package com.kompakt.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

/** Settings.Secure key the framework reads before vibrating for the keys under the screen. */
private const val KEY_VIBRATION = "kompakt_key_vibration"
/** Settings.Secure key DozeUi reads: minutes between always-on display repaints, 0 for auto. */
private const val KEY_AOD_REFRESH = "kompakt_aod_refresh"
private const val RECENTS_LONG_PRESS_QS = "kompakt_recents_long_press_qs"
private const val POWER_TORCH = "kompakt_power_torch"
// Read by SystemFonts at boot: "color" drops the monochrome emoji font.
private const val EMOJI_PROP = "persist.sys.kompakt.emoji"
// Read by SystemFonts at boot: the picked family replaces Roboto and Google Sans everywhere.
private const val FONT_PROP = "persist.sys.kompakt.font"
// Read by Choreographer in every app as it starts: a divisor of the panel's frame rate (frameworks_base 0130).
private const val FPS_PROP = "persist.sys.kompakt.fps_divisor"
// Google Sans is the default, so nothing picked reads as it (SystemFonts does the same).
private val FONTS = listOf("google-sans", "roboto", "manrope", "serif", "source-serif", "lato", "jetbrains-mono")
// The scripts the fonts cover; Google Sans has Latin only (SystemFonts makes the same choice).
private val FONT_SCRIPTS = setOf("Latn", "Grek", "Cyrl")

/** Kompakt: The Kompakt hardware that Android has no settings for. */
class KompaktActivity : Activity() {

    private class Diag(
        val eink: List<Pair<String, String>>,
        val leds: List<Pair<String, String>>,
        val calls: List<Pair<String, String>>,
        val google: String,
    )

    private var diag: Diag? = null
    /** What the last sleep screen action did, shown on its page until it is left. */
    private var sleepStatus: String? = null
    private var sleepBusy = false
    private lateinit var body: FrameLayout
    private lateinit var menuButton: ImageView
    private val tabs = ArrayList<View>()
    private val tabPills = ArrayList<View>()
    private val tabIcons = ArrayList<ImageView>()
    private val tabLabels = ArrayList<TextView>()
    private var current = 0
    /** The tab Advanced was opened from, so Back returns there. */
    private var advancedFrom = 0

    private val handler = Handler(Looper.getMainLooper())

    // Two inks, swapped in dark mode. No greys: this panel dithers anything between them.
    private val night by lazy {
        (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
    private val INK by lazy { if (night) Color.WHITE else Color.BLACK }
    private val PAPER by lazy { if (night) Color.BLACK else Color.WHITE }
    /** App icons in the two inks: greys pushed to black or white, and the inks swapped at night. */
    private val MONO by lazy {
        val m = android.graphics.ColorMatrix().apply { setSaturation(0f) }
        val c = 3f
        m.postConcat(android.graphics.ColorMatrix(floatArrayOf(
            c, 0f, 0f, 0f, 128f * (1 - c),
            0f, c, 0f, 0f, 128f * (1 - c),
            0f, 0f, c, 0f, 128f * (1 - c),
            0f, 0f, 0f, 1f, 0f)))
        if (night) m.postConcat(android.graphics.ColorMatrix(floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f)))
        android.graphics.ColorMatrixColorFilter(m)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.app_name)
        // Idempotent; brings the watcher back if anything stopped it.
        startService(Intent(this, OfflineWatcherService::class.java))

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(PAPER)
        }

        root.addView(titleBar())

        body = FrameLayout(this)
        root.addView(body, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
        root.addView(tabBar())

        // Android 16 enforces edge-to-edge, so without this the window starts at y=0: the title sits under the status.
        root.setOnApplyWindowInsetsListener { v, insets ->
            val bars = insets.getInsets(WindowInsets.Type.systemBars())
            v.setPadding(0, bars.top, 0, bars.bottom)
            insets
        }

        setContentView(root)

        // Targeting 36, the framework no longer routes Back through onBackPressed:
        // without this callback every page would close the app instead of going up.
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { if (!goBack()) finish() }
        }

        select(0)
    }

    /** False until the first resume, so the page built in onCreate is not built twice. */
    private var shownOnce = false

    override fun onResume() {
        super.onResume()
        // Kompakt: a tile or the sensor may have changed the mode while the app was in the background.
        if (current == PAGE_DIAGNOSTICS) refreshDiagnostics() else if (shownOnce) select(current)
        shownOnce = true
    }

    override fun onPause() {
        super.onPause()
        unwatchLux()
    }

    // ------------------------------------------------------------------ chrome

    /** The app's own title, since the action bar is gone. */
    private fun titleBar(): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            // The right gutter gives up the 8 dp the 40 dp button adds around its 24 dp glyph, so the glyph ends on the gutter as the info glyphs do.
            setPadding(dp(GUTTER), dp(4), dp(GUTTER) - dp(8), 0)
        }
        // As the system apps head a page: the name, plain and large.
        row.addView(TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 26f
            setTextColor(INK)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        // The overflow menu, as the system apps carry one: Advanced. On Advanced it is a close button.
        menuButton = ImageView(this).apply {
            imageTintList = ColorStateList.valueOf(INK)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        row.addView(menuButton, LinearLayout.LayoutParams(dp(40), dp(40)))
        return row
    }

    /** ⋮ on a tab, × on Advanced and Diagnostics, so those never pass for the tab they came from. */
    private fun setMenuButton() {
        val inAdvanced = current == PAGE_ADVANCED || current == PAGE_DIAGNOSTICS
        menuButton.setImageResource(if (inAdvanced) R.drawable.ic_close else R.drawable.ic_more_vert)
        menuButton.contentDescription = getString(if (inAdvanced) R.string.close else R.string.more_options)
        if (inAdvanced) {
            menuButton.setOnClickListener { select(advancedFrom) }
        } else {
            // Kompakt: the app's own sheet, which has a frame; a PopupMenu is a borderless box on this panel.
            menuButton.setOnClickListener {
                val column = sheetColumn(getString(R.string.more_options), "")
                val dialog = showSheet(column)
                column.addView(valueRow(getString(R.string.advanced_section), "›",
                    getString(R.string.advanced_short), R.drawable.ic_tune) {
                    dialog.dismiss()
                    // From Advanced or Diagnostics, keep the tab it was first opened from.
                    if (current != PAGE_ADVANCED && current != PAGE_DIAGNOSTICS) {
                        advancedFrom = if (current < tabs.size) current else parentOf(current)
                    }
                    select(PAGE_ADVANCED)
                })
            }
        }
    }

    /** Bottom tabs as the system apps draw them: an icon over a label, the current one in an ink pill. */
    private fun tabBar(): View {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            isBaselineAligned = false
            setPadding(0, dp(10), 0, dp(10))
        }
        val heads = listOf(
            R.string.tab_display to R.drawable.ic_eink,
            R.string.tab_offline to R.drawable.ic_offline,
            R.string.tab_look to R.drawable.ic_brush,
            R.string.tab_settings to R.drawable.ic_settings,
        )
        heads.forEachIndexed { i, (label, icon) ->
            val cell = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setOnClickListener { select(i) }
            }
            val pill = FrameLayout(this)
            val image = ImageView(this).apply {
                setImageResource(icon)
                contentDescription = getString(label)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }
            pill.addView(image, FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER))
            cell.addView(pill, LinearLayout.LayoutParams(dp(64), dp(32)))
            val text = TextView(this).apply {
                setText(label)
                textSize = 13f
                gravity = Gravity.CENTER
                setTextColor(INK)
                setPadding(0, dp(4), 0, 0)
            }
            cell.addView(text, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
            tabs.add(cell)
            tabPills.add(pill)
            tabIcons.add(image)
            tabLabels.add(text)
            bar.addView(cell, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        }
        return bar
    }

    /** The tab a page belongs to, or -1 for a tab; this is the whole back stack. */
    private fun parentOf(index: Int): Int = when (index) {
        PAGE_DIAGNOSTICS -> PAGE_ADVANCED
        PAGE_ADVANCED -> advancedFrom
        PAGE_BATTERY -> TAB_SETTINGS
        PAGE_APP_MODES -> TAB_DISPLAY
        PAGE_SLEEP -> TAB_DISPLAY
        PAGE_LIGHT_CURVE -> TAB_DISPLAY
        PAGE_FINGER -> TAB_SETTINGS
        else -> -1
    }

    /** Back goes up a level, and only leaves the app from a tab. */
    private fun goBack(): Boolean {
        val parent = parentOf(current)
        if (parent < 0) return false
        select(parent)
        return true
    }

    @Deprecated("Kept for the pre-33 path; 33 and up arrive through the dispatcher.")
    override fun onBackPressed() {
        if (!goBack()) @Suppress("DEPRECATION") super.onBackPressed()
    }

    private fun select(index: Int) {
        if (index != PAGE_SLEEP) sleepStatus = null
        unwatchLux()
        current = index
        setMenuButton()
        // A page marks the tab it came from, so the app never looks tab-less.
        val marked = if (index < tabs.size) index else parentOf(index)
        tabs.indices.forEach { i ->
            val on = i == marked
            tabPills[i].background = if (on) GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                setColor(INK)
            } else null
            tabIcons[i].imageTintList = ColorStateList.valueOf(if (on) PAPER else INK)
            tabLabels[i].setTypeface(if (on) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
        }
        body.removeAllViews()
        val page = PagedColumn(this)
        // The section heading's own space is enough under the title; a gap above the tabs.
        page.setPadding(0, 0, 0, dp(8))
        when (index) {
            TAB_DISPLAY -> {
                // The heading carries the name, since the circles only carry icons.
                // A saved mode this phone cannot do runs as quality (Eink.setMode), so say that.
                val mode = Prefs.mode(this).let { if (Eink.usable(it)) it else "quality" }
                page.add(sectionWithInfo(getString(R.string.eink_section_named,
                    Eink.label(mode))) { einkInfo() })
                page.add(modeCircles(Eink.ORDER, mode) { key ->
                    Prefs.setMode(this, key)
                    Eink.setMode(key)
                    select(TAB_DISPLAY)
                    // As the e-ink tiles: flush what the previous mode left.
                    Flash.show(applicationContext)
                })
                page.add(valueRow(getString(R.string.display_app_modes), "›",
                    getString(R.string.app_modes_short), R.drawable.ic_apps) {
                    select(PAGE_APP_MODES)
                })
                page.add(valueRow(getString(R.string.unlock_refresh_row), unlockRefreshValue(),
                    getString(R.string.unlock_refresh_short), R.drawable.ic_lock_open) {
                    unlockRefreshSheet()
                })
                page.add(sectionWithInfo(getString(R.string.sleep_light_section)) { sleepNeeds() })
                page.add(if (SleepImage.available())
                             valueRow(getString(R.string.sleep_row), "›",
                                 getString(R.string.sleep_short), R.drawable.ic_sleep) { select(PAGE_SLEEP) }
                         else blockedRow(getString(R.string.sleep_row), Needs.BOTH, R.drawable.ic_sleep) {
                             sleepNeeds()
                         })
                page.add(valueRow(getString(R.string.aod_refresh_row),
                    aodRefreshLabel(aodRefresh()), getString(R.string.aod_refresh_short),
                    R.drawable.ic_update) {
                    aodRefreshSheet()
                })
                page.add(valueRow(getString(R.string.light_curve_row), "›",
                    getString(R.string.light_curve_short), R.drawable.ic_brightness_auto) {
                    select(PAGE_LIGHT_CURVE)
                })
            }
            PAGE_SLEEP -> sleepPage().forEach { page.add(it) }
            PAGE_LIGHT_CURVE -> lightCurvePage().forEach { page.add(it) }
            TAB_LOOK -> {
                page.add(sectionWithInfo(getString(R.string.look_section)) {
                    showSheet(sheetColumn(getString(R.string.look_section),
                        getString(R.string.look_note)))
                })
                // No font here covers the language's letters, so there is nothing to choose.
                if (script() in FONT_SCRIPTS) {
                    page.add(valueRow(getString(R.string.font_row), fontLabel(font()),
                        getString(R.string.font_short), R.drawable.ic_font) {
                        fontSheet()
                    })
                }
                page.add(valueRow(getString(R.string.emoji_row), getString(
                    if (emojiColour()) R.string.emoji_colour else R.string.emoji_mono),
                    getString(R.string.emoji_short), R.drawable.ic_emoji) {
                    emojiSheet()
                })
                page.add(valueRow(getString(R.string.width_section), Prefs.width(this),
                    getString(R.string.width_short), R.drawable.ic_width) {
                    widthSheet()
                })
            }
            TAB_SETTINGS -> {
                page.add(section(getString(R.string.settings_section)))
                page.add(if (FingerScroll.available())
                             valueRow(getString(R.string.finger_section), "›",
                                 getString(R.string.finger_short), R.drawable.ic_fingerprint) {
                                 select(PAGE_FINGER)
                             }
                         else blockedRow(getString(R.string.finger_section), Needs.VENDOR,
                                 R.drawable.ic_fingerprint) {
                             fingerNeeds()
                         })
                // Read by the framework (PhoneWindowManager): 0 stops the keys under the screen vibrating.
                page.add(toggleRow(getString(R.string.key_vibration),
                    Settings.Secure.getInt(contentResolver, KEY_VIBRATION, 1) != 0,
                    getString(R.string.key_vibration_note), R.drawable.ic_vibration) { on ->
                    Settings.Secure.putInt(contentResolver, KEY_VIBRATION, if (on) 1 else 0)
                })
                // Read by the framework (PhoneWindowManager): holding Recents opens quick settings.
                page.add(toggleRow(getString(R.string.recents_long_press_qs),
                    Settings.Secure.getInt(contentResolver, RECENTS_LONG_PRESS_QS, 0) != 0,
                    getString(R.string.recents_long_press_qs_note), R.drawable.ic_recents) { on ->
                    Settings.Secure.putInt(contentResolver, RECENTS_LONG_PRESS_QS, if (on) 1 else 0)
                })
                page.add(valueRow(getString(R.string.power_torch_row), powerTorchLabel(),
                    getString(R.string.power_torch_short), R.drawable.ic_torch) {
                    powerTorchSheet()
                })
                page.add(valueRow(getString(R.string.battery_section), "›",
                    getString(R.string.battery_short), R.drawable.ic_battery) {
                    select(PAGE_BATTERY)
                })
            }
            PAGE_ADVANCED -> {
                // Every setting here is a row: its name, one line on what it does, the info glyph, then its chips.
                page.add(section(getString(R.string.advanced_section)))
                page.add(settingRow(getString(R.string.temperature_section),
                    getString(R.string.temperature_short), R.drawable.ic_thermostat) { temperatureInfo() })
                page.add(temperatureSteps())
                page.add(settingRow(getString(R.string.speed_section),
                    getString(R.string.speed_short), R.drawable.ic_speed) { speedInfo() })
                page.add(speedSteps())
                page.add(settingRow(getString(R.string.pattern_section),
                    getString(R.string.pattern_short), R.drawable.ic_grain) { patternInfo() })
                page.add(patternSteps())
                // Normal is Mudita's own dither, which the brightness does not reach.
                if (Prefs.pattern(this) != Eink.STOCK_PATTERN) {
                    page.add(settingRow(getString(R.string.light_section),
                        getString(R.string.light_short), R.drawable.ic_light) { lightInfo() })
                    page.add(lightSteps())
                }
                page.add(settingRow(getString(R.string.clean_section),
                    getString(R.string.clean_short), R.drawable.ic_clean) { cleanInfo() })
                page.add(cleanSteps())
                page.add(toggleRow(getString(R.string.refresh_on_app), Prefs.refreshOnApp(this),
                    getString(R.string.refresh_on_app_note), R.drawable.ic_autorenew) { on ->
                    Prefs.setRefreshOnApp(this, on)
                })
                page.add(valueRow(getString(R.string.frame_rate_row),
                    getString(R.string.frame_rate_fps, frameRateNow()),
                    getString(R.string.frame_rate_short), R.drawable.ic_timer) {
                    frameRateSheet()
                })
                page.add(valueRow(getString(R.string.diagnostics_section), "›",
                    getString(R.string.diagnostics_short), R.drawable.ic_bug) {
                    select(PAGE_DIAGNOSTICS)
                })
            }
            PAGE_BATTERY -> batteryPage().forEach { page.add(it) }
            PAGE_FINGER -> fingerPage().forEach { page.add(it) }
            PAGE_APP_MODES -> {
                page.add(sectionWithInfo(getString(R.string.display_app_modes)) {
                    showSheet(sheetColumn(getString(R.string.display_app_modes),
                        getString(R.string.app_modes_note)))
                })
                page.add(toggleRow(
                    getString(R.string.per_app_enable),
                    Prefs.perAppEnabled(this),
                    getString(R.string.per_app_enable_note),
                    R.drawable.ic_apps,
                ) { on ->
                    Prefs.setPerAppEnabled(this, on)
                    if (!on) Eink.setMode(Prefs.mode(this))
                })
                appRows({ pkg -> appModeValue(pkg) }) { pkg, name, refresh ->
                    pickMode(pkg, name, refresh)
                }.forEach { page.add(it) }
            }
            TAB_OFFLINE -> {
                page.add(sectionWithInfo(getString(R.string.offline_section)) {
                    showSheet(sheetColumn(getString(R.string.offline_info_title),
                        getString(R.string.offline_info_note)))
                })
                page.add(lockedRow(getString(R.string.offline_modem), getString(R.string.offline_locked_note),
                    R.drawable.ic_sim))
                page.add(lockedRow(getString(R.string.offline_mic), getString(R.string.offline_locked_note),
                    R.drawable.ic_mic))
                page.add(offlineRow(OfflineExtras.Extra.WIFI, R.string.offline_wifi, R.string.offline_extra_note,
                    R.drawable.ic_wifi))
                page.add(offlineRow(OfflineExtras.Extra.BLUETOOTH, R.string.offline_bt, R.string.offline_extra_note,
                    R.drawable.ic_bluetooth))
                page.add(offlineRow(OfflineExtras.Extra.CAMERA, R.string.offline_camera, R.string.offline_camera_note,
                    R.drawable.ic_camera))
            }
            PAGE_DIAGNOSTICS -> {
                page.add(section(getString(R.string.diagnostics_section)))
                page.add(buttonRow(
                    getString(R.string.refresh_diagnostics) to { refreshDiagnostics() },
                ))
                // Which build each image is, since several features depend on
                // boot and vendor, and a report is only useful with the numbers.
                page.add(section(getString(R.string.diag_images)))
                page.add(factRow(getString(R.string.diag_system), systemBuild()))
                page.add(factRow(getString(R.string.diag_boot), bootBuild()))
                page.add(factRow(getString(R.string.diag_vendor), vendorBuild()))
                page.add(factRow(getString(R.string.diag_app), Builds.app(this) ?: "?"))
                val d = diag
                if (d == null) {
                    page.add(note(getString(R.string.diag_reading)))
                    refreshDiagnostics()
                } else {
                    // Sixteen driver values: one row, and the values in a sheet.
                    page.add(section(getString(R.string.diag_eink)))
                    page.add(valueRow(getString(R.string.diag_eink_values), "›") {
                        showSheet(sheetColumn(getString(R.string.diag_eink), "").also { column ->
                            d.eink.forEach { (n, v) -> column.addView(factRow(n, v)) }
                        })
                    })
                    page.add(section(getString(R.string.diag_calls)))
                    d.calls.forEach { (n, v) -> page.add(factRow(n, v)) }
                    page.add(section(getString(R.string.diag_leds)))
                    d.leds.forEach { (n, v) -> page.add(factRow(n, v)) }
                    page.add(section(getString(R.string.diag_google)))
                    page.add(note(d.google))
                }
            }
        }
        body.addView(page, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    /** "KompaktOS 258, GApps". */
    private fun systemBuild(): String {
        val n = Builds.system()
        val base = if (n != null) getString(R.string.diag_ours_build, n) else getString(R.string.diag_ours)
        return Builds.edition()?.let { "$base, $it" } ?: base
    }

    /** "KompaktOS 258", or for a kernel from before the numbers, the day it was built. */
    private fun bootBuild(): String {
        if (!Ours.kernel()) return getString(R.string.diag_mudita)
        Builds.kernel()?.let { return getString(R.string.diag_ours_build, it) }
        return Builds.kernelDate()?.let { getString(R.string.diag_ours_built, it) }
            ?: getString(R.string.diag_ours)
    }

    private fun vendorBuild(): String = when {
        !Ours.vendor() -> getString(R.string.diag_mudita)
        Builds.vendor() != null -> getString(R.string.diag_ours_build, Builds.vendor())
        SleepImage.available() -> getString(R.string.diag_ours)
        else -> getString(R.string.diag_ours_older)
    }

    // ----------------------------------------------------------------- e-ink

    /** The modes as a row of icon circles, the whole picker; the heading names the choice. */
    private fun modeCircles(keys: List<String>, chosen: String?, onPick: (String) -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(GUTTER), dp(4), dp(GUTTER), dp(8))
        }
        // Circles of one size, the first and last on the gutters; they only shrink when the row cannot hold them.
        val content = resources.displayMetrics.widthPixels - 2 * dp(GUTTER)
        val gap = maxOf(dp(8), (content - keys.size * dp(CIRCLE)) / (keys.size - 1).coerceAtLeast(1))
        keys.forEachIndexed { i, key ->
            val on = key == chosen
            // A mode this phone cannot do is dashed, and explains itself instead of being picked.
            val usable = Eink.usable(key)
            val cell = SquareCell(this).apply {
                background = circle(if (on) 4 else 2, dashed = !usable)
                setOnClickListener {
                    if (usable) onPick(key)
                    else needsSheet(Eink.label(key), einkNote(key), Eink.NEEDS.getValue(key))
                }
            }
            cell.addView(ImageView(this).apply {
                setImageResource(Eink.icon(key))
                imageTintList = ColorStateList.valueOf(INK)
                contentDescription = Eink.label(key)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            val lp = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            if (i < keys.size - 1) lp.marginEnd = gap
            row.addView(cell, lp)
        }
        return row
    }

    /** What the modes are: an explanation sheet, not a second picker. */
    private fun einkInfo(keys: List<String> = Eink.ORDER) {
        val column = sheetColumn(getString(R.string.eink_section), getString(R.string.eink_note))
        keys.forEach { key ->
            val lines = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(GUTTER), dp(8), dp(GUTTER), dp(8))
            }
            lines.addView(titleView(Eink.label(key)))
            lines.addView(TextView(this).apply {
                text = einkNote(key)
                textSize = 13f
                setTextColor(INK)
            })
            // Said whether or not it is installed, so the dependency is never a surprise.
            if (Eink.NEEDS.containsKey(key)) {
                lines.addView(TextView(this).apply {
                    text = getString(if (Eink.usable(key)) R.string.needs_line_vendor_ok
                                     else R.string.needs_line_vendor_missing)
                    textSize = 13f
                    setTextColor(INK)
                    setTypeface(Typeface.DEFAULT_BOLD)
                })
            }
            column.addView(lines)
        }
        showSheet(column)
    }

    /** What each mode is for, in a few words. */
    private fun einkNote(key: String): String = getString(when (key) {
        "fast" -> R.string.eink_note_fast
        "soft" -> R.string.eink_note_soft
        "fastdither" -> R.string.eink_note_dither
        "text" -> R.string.eink_note_text
        else -> R.string.eink_note_quality
    })

    /** A sheet against the bottom edge: a heading, a note, then one choice row per item. */
    private fun sheet(title: String, note: String, items: List<SheetItem>) {
        val column = sheetColumn(title, note)
        val dialog = showSheet(column)
        items.forEach { item ->
            column.addView(choiceRow(item.label, item.sub, item.selected, item.typeface) {
                item.action()
                dialog.dismiss()
            })
        }
    }

    /** A sheet's paper, heading and note; onInfo adds the info glyph to the heading. */
    private fun sheetColumn(title: String, note: String,
                            onInfo: (() -> Unit)? = null): LinearLayout =
        sheetColumnOf(sheetLabel(title), onInfo, note)

    /** A sheet's title, as inkOS's SheetTitle: the words in capitals, bold, nothing else. */
    private fun sheetLabel(text: String) = TextView(this).apply {
        this.text = text.uppercase()
        textSize = 15f
        setTextColor(INK)
        setTypeface(Typeface.DEFAULT_BOLD)
    }

    /** The same with a caller-kept heading. Returns the scrolling list; showSheet finds the sheet via its tag. */
    private fun sheetColumnOf(heading: TextView, onInfo: (() -> Unit)? = null,
                              noteText: String = ""): LinearLayout {
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = SheetFrame(PAPER, INK, dp(SHEET_RADIUS).toFloat(), dp(2).toFloat())
            setPadding(0, 0, 0, dp(16))
        }
        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(GUTTER), dp(24), dp(GUTTER), dp(8))
        }
        titleRow.addView(heading, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        if (onInfo != null) {
            titleRow.addView(ImageView(this).apply {
                setImageResource(R.drawable.ic_info)
                imageTintList = ColorStateList.valueOf(INK)
                contentDescription = getString(R.string.eink_info)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setOnClickListener { onInfo() }
            }, LinearLayout.LayoutParams(dp(28), dp(24)).apply { marginStart = dp(8) })
        }
        column.addView(titleRow)
        if (noteText.isNotEmpty()) column.addView(note(noteText))
        // A list longer than the screen scrolls inside the sheet, capped so the
        // whole sheet stays under three quarters of the screen.
        val cap = (resources.displayMetrics.heightPixels * 0.6f).toInt()
        val scroller = object : ScrollView(this) {
            override fun onMeasure(w: Int, h: Int) =
                super.onMeasure(w, MeasureSpec.makeMeasureSpec(cap, MeasureSpec.AT_MOST))
        }
        scroller.isVerticalScrollBarEnabled = false
        scroller.overScrollMode = View.OVER_SCROLL_NEVER
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroller.addView(list, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        column.addView(scroller, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        list.tag = column
        return list
    }

    /** Put a sheet's column on screen, and hand back the dialog holding it. */
    private fun showSheet(column: LinearLayout): android.app.Dialog {
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        // The list's own sheet (sheetColumnOf), which holds the list's scroller.
        dialog.setContentView(column.tag as? View ?: column)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(MATCH_PARENT, WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            // No slide-in: an animated sheet is a partial-refresh storm on this panel.
            setWindowAnimations(0)
        }
        dialog.show()
        return dialog
    }

    // --------------------------------------------------------------- offline

    /** A choice for the offline switch. Applied at once if the switch is engaged. */
    private fun offlineRow(extra: OfflineExtras.Extra, label: Int, note: Int, glyph: Int): View =
        toggleRow(getString(label), OfflineExtras.enabled(this, extra), getString(note), glyph) { on ->
            OfflineExtras.setEnabled(this, extra, on)
        }

    /** Smallest width: the steps the tile cycles through, and a way back to the phone's own. */
    private fun widthSheet() {
        val now = Prefs.width(this)
        val column = sheetColumn(getString(R.string.width_section), getString(R.string.width_note))
        val dialog = showSheet(column)
        Width.ORDER.forEach { step ->
            column.addView(choiceRow(step,
                if (step == Width.DEFAULT) getString(R.string.width_default) else "", step == now) {
                Prefs.setWidth(this, step)
                Width.apply(step)
                dialog.dismiss()
            })
        }
        // Clears the forced density itself, so the phone is back to its own whatever set one.
        column.addView(buttonRow(getString(R.string.width_reset) to {
            Prefs.setWidth(this, Width.DEFAULT)
            Width.clear()
            dialog.dismiss()
        }))
        dialog.setOnDismissListener { if (current == TAB_LOOK) select(TAB_LOOK) }
    }

    /** Equal-share chips. Restyled in place; a reread would race the IO write. */
    private fun <T> chipRow(
        items: List<T>,
        chosen: T?,
        label: (T) -> String,
        onPick: (T) -> Unit,
    ): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            // Baselines would drop the regular chips 1 px below the bold one, past the clip.
            isBaselineAligned = false
            setPadding(dp(GUTTER), dp(4), dp(GUTTER), dp(8))
        }
        val chips = ArrayList<Pair<T, TextView>>()
        val mark = { picked: T ->
            chips.forEach { (item, chip) ->
                val on = item == picked
                chip.setTypeface(if (on) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
                chip.background = outline(if (on) 3 else 2, BUTTON_RADIUS)
            }
        }
        items.forEachIndexed { i, item ->
            val chip = TextView(this).apply {
                text = label(item)
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(INK)
                setTypeface(if (item == chosen) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
                background = outline(if (item == chosen) 3 else 2, BUTTON_RADIUS)
                setPadding(0, dp(10), 0, dp(10))
                setOnClickListener {
                    onPick(item)
                    mark(item)
                }
            }
            chips.add(item to chip)
            val lp = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            if (i < items.size - 1) lp.marginEnd = dp(4)
            row.addView(chip, lp)
        }
        return row
    }

    /** Temperature offset. A warmer claim means fewer waveform frames. */
    private fun temperatureSteps(): View = chipRow(
        Eink.TEMPS,
        Prefs.temperature(this),
        { if (it > 0) "+$it" else it.toString() },
    ) { step ->
        Prefs.setTemperature(this, step)
        Eink.setTemperature(step)
        Flash.show(applicationContext)  // as a mode change: flush what the old setting left
    }

    /** A2 steps in the fast modes, 9 to 5. Fewer steps: sooner, with more ghosting. */
    private fun speedSteps(): View {
        val keys = Eink.SPEEDS.keys.toList()
        return StepSlider(this, Eink.SPEEDS.values.toList(),
            keys.indexOf(Prefs.speed(this)).coerceAtLeast(0), INK, PAPER) { i ->
            Prefs.setSpeed(this, keys[i])
            Eink.setSpeed(keys[i])
            Flash.show(applicationContext)  // as a mode change: flush what the old setting left
        }.apply { setPadding(dp(GUTTER), dp(4), dp(GUTTER), dp(8)) }
    }

    /** Small updates before the whole frame is redrawn. Fewer: ghosts clear sooner, with more redraws. */
    private fun cleanSteps(): View {
        val keys = Eink.CLEAN_STEPS
        return StepSlider(this, keys.map { it.toString() },
            keys.indexOf(Prefs.cleanEvery(this)).coerceAtLeast(0), INK, PAPER) { i ->
            Prefs.setCleanEvery(this, keys[i])
            Eink.setCleanEvery(keys[i])
        }.apply { setPadding(dp(GUTTER), dp(4), dp(GUTTER), dp(8)) }
    }

    /** How far the dithers lift dark greys. */
    private fun lightSteps(): View = chipRow(
        Eink.LIGHTS,
        Prefs.ditherLight(this),
        { if (it > 0) "+$it" else it.toString() },
    ) { v ->
        Prefs.setDitherLight(this, v)
        Eink.setDitherLight(v)
    }

    /** How Fast + dither places its dots. */
    private fun patternSteps(): View = chipRow(
        Eink.PATTERNS.keys.toList(),
        Prefs.pattern(this),
        { Eink.PATTERNS.getValue(it) },
    ) { p ->
        val brightnessShown = Prefs.pattern(this) != Eink.STOCK_PATTERN
        Prefs.setPattern(this, p)
        Eink.setPattern(p)
        if (brightnessShown != (p != Eink.STOCK_PATTERN)) select(PAGE_ADVANCED)
    }

    // --------------------------------------------------------------- battery

    private fun batteryPage(): List<View> {
        val s = Battery.read()
        val out = ArrayList<View>()

        val unknown = getString(R.string.battery_unknown)
        val healthNeeds = {
            needsSheet(getString(R.string.battery_health_title),
                getString(R.string.battery_health_note), Needs.KERNEL)
        }
        out.add(sectionWithInfo(getString(R.string.battery_section)) { healthNeeds() })
        if (Ours.kernel()) {
            out.add(factRow(getString(R.string.battery_cycles),
                s.cycles?.takeIf { it >= 0 }?.toString() ?: unknown, getString(R.string.battery_cycles_short)))
            out.add(factRow(getString(R.string.battery_health),
                Battery.healthPercent(s)?.let { getString(R.string.battery_percent, it) } ?: unknown,
                getString(R.string.battery_health_short)))
            out.add(factRow(getString(R.string.battery_capacity),
                if (s.chargeFull != null && s.chargeFullDesign != null)
                    getString(R.string.battery_capacity_pair,
                        s.chargeFull / 1000, s.chargeFullDesign / 1000)
                else unknown, getString(R.string.battery_capacity_short)))
        } else {
            // The cycle count is the vendor HAL's and holds on either kernel; health
            // and capacity divide by Mudita's 294 mAh and would read about 1000%.
            out.add(factRow(getString(R.string.battery_cycles),
                s.cycles?.takeIf { it >= 0 }?.toString() ?: unknown, getString(R.string.battery_cycles_short)))
            out.add(blockedRow(getString(R.string.battery_health_title), Needs.KERNEL) {
                healthNeeds()
            })
        }
        out.add(factRow(getString(R.string.battery_first_use), s.firstUse ?: unknown,
            getString(R.string.battery_first_use_short)))

        // The write lands on either vendor. Only ours lets the HAL act on it: see
        // docs/Battery.md section 5 in KompaktDevice, the orphaned proc_battery_cmd type.
        val limitNeeds = {
            needsSheet(getString(R.string.battery_limit_section),
                getString(R.string.battery_limit_note), Needs.VENDOR)
        }
        if (!Ours.vendor()) {
            out.add(blockedRow(getString(R.string.battery_limit_section), Needs.VENDOR) {
                limitNeeds()
            })
        } else if (Battery.writable()) {
            out.add(settingRow(getString(R.string.battery_limit_section),
                getString(R.string.battery_limit_short)) { limitNeeds() })
            out.add(limitSteps(s.limit))
            out.add(factRow(getString(R.string.battery_now),
                when {
                    Battery.heldAtLimit(s) -> getString(R.string.battery_now_held)
                    s.status == "Charging" -> getString(R.string.battery_now_charging)
                    s.status == "Full" -> getString(R.string.battery_now_full)
                    else -> getString(R.string.battery_now_unplugged)
                }, getString(R.string.battery_now_short)))
            out.add(toggleRow(getString(R.string.battery_hysteresis), s.hysteresis,
                getString(R.string.battery_hysteresis_note)) { on ->
                Battery.setHysteresis(on)
            })
        } else {
            out.add(lockedRow(getString(R.string.battery_limit_unavailable)))
        }
        return out
    }

    private fun limitSteps(chosen: Int?): View = chipRow(
        Battery.LIMITS,
        chosen,
        { getString(R.string.battery_percent, it) },
    ) { step -> Battery.setLimit(step) }

    // ---------------------------------------------------------- sleep screen

    /** Picture, Fit or Fill, then actions; the picture is read back from the kernel before drawing. */
    private fun sleepPage(): List<View> {
        val out = ArrayList<View>()
        out.add(sectionWithInfo(getString(R.string.sleep_section)) { sleepNeeds() })
        if (!SleepImage.available()) {
            out.add(blockedRow(getString(R.string.sleep_row), Needs.BOTH) { sleepNeeds() })
            return out
        }
        SleepImage.current()?.let { bmp ->
            val shot = ImageView(this).apply {
                setImageBitmap(bmp)
                scaleType = ImageView.ScaleType.FIT_XY
                background = outline(2, BOX_RADIUS)
                setPadding(dp(3), dp(3), dp(3), dp(3))
                contentDescription = getString(R.string.sleep_section)
            }
            val holder = FrameLayout(this).apply { setPadding(0, dp(4), 0, dp(8)) }
            // 3:5, the panel's own shape, at a size that leaves the buttons on screen.
            holder.addView(shot, FrameLayout.LayoutParams(dp(156), dp(260), Gravity.CENTER_HORIZONTAL))
            out.add(holder)
        }
        // Fit and Fill say it themselves; a row over them pushed the buttons off the screen.
        out.add(chipRow(listOf(false, true), SleepImage.fill(this),
            { getString(if (it) R.string.sleep_fill else R.string.sleep_fit) }) { fill ->
            SleepImage.setFill(this, fill)
            if (SleepImage.isCustom(this)) sleepWork { SleepImage.apply(this) }
        })
        out.add(buttonRow(
            getString(R.string.sleep_choose) to { chooseSleepImage() },
            getString(R.string.sleep_reset) to {
                sleepWork(R.string.sleep_reset_later) { SleepImage.reset(this) }
            },
        ))
        sleepStatus?.let { out.add(note(it)) }
        return out
    }

    /** Needs our meink_loader and kernel; the node's own probe stands for the vendor. */
    private fun sleepNeeds() = needsSheet(getString(R.string.sleep_section),
        getString(R.string.sleep_note), Needs.BOTH, SleepImage.available())

    /** Switch, swipe modes, gestures, and direction while a scrolling mode is enabled. */
    private fun fingerPage(): List<View> {
        val out = ArrayList<View>()
        // The explanation lives behind the info glyph; the page is only controls.
        out.add(sectionWithInfo(getString(R.string.finger_section)) { fingerNeeds() })
        val on = FingerScroll.enabled(this)
        out.add(toggleRow(getString(R.string.finger_row), on, getString(R.string.finger_row_note)) { checked ->
            FingerScroll.setEnabled(this, checked)
            select(PAGE_FINGER)
        })
        if (!on) return out
        val modes = FingerScroll.cycle(this)
        out.add(section(getString(R.string.finger_swipe_section)))
        out.add(valueRow(getString(R.string.finger_cycle_row),
            resources.getQuantityString(R.plurals.finger_modes_count, modes.size, modes.size),
            getString(R.string.finger_cycle_short)) {
            fingerCycleSheet()
        })
        out.add(section(getString(R.string.finger_gestures_section)))
        out.add(gestureRow(R.string.finger_tap_row, R.string.finger_tap_short,
            FingerScroll.tapAction(this), Buttons.TAP) {
            FingerScroll.setTapAction(this, it)
        })
        out.add(gestureRow(R.string.finger_triple_row, R.string.finger_triple_short,
            FingerScroll.tripleAction(this), Buttons.TRIPLE) {
            FingerScroll.setTripleAction(this, it)
        })
        out.add(gestureRow(R.string.finger_hold_row, R.string.finger_hold_short,
            FingerScroll.holdAction(this), Buttons.HOLD) {
            FingerScroll.setHoldAction(this, it)
        })
        // Natural and Sideways steer scrolling and the D-pad; nothing else reads them.
        if ("swipe" in modes || "dpad" in modes) {
            out.add(section(getString(R.string.finger_scroll_section)))
            out.add(toggleRow(getString(R.string.finger_natural), FingerScroll.natural(this),
                getString(R.string.finger_natural_note)) {
                FingerScroll.setNatural(this, it)
            })
            out.add(toggleRow(getString(R.string.finger_horizontal), FingerScroll.horizontal(this),
                getString(R.string.finger_horizontal_note)) {
                FingerScroll.setHorizontal(this, it)
            })
        }
        return out
    }

    private fun gestureRow(title: Int, note: Int, action: String, button: Int, onPick: (String) -> Unit) =
        valueRow(getString(title), actionLabel(action, button), getString(note)) {
            pickAction(getString(title), action, button, onPick)
        }

    private fun styleLabel(style: String): String = getString(when (style) {
        "dpad" -> R.string.finger_dpad
        "brightness" -> R.string.finger_brightness
        "keys" -> R.string.finger_keys
        "off" -> R.string.finger_style_off
        else -> R.string.finger_swipe
    })

    /** Swipe modes sheet: drag to reorder, switch on or off; the first one on is the default. */
    private fun fingerCycleSheet() {
        val column = sheetColumn(getString(R.string.finger_cycle_row),
            getString(R.string.finger_cycle_note))
        val order = FingerScroll.order(this).toMutableList()
        val subs = HashMap<String, TextView>()
        fun relabel() {
            val home = FingerScroll.home(this)
            subs.forEach { (style, sub) ->
                sub.visibility = if (style == home) View.VISIBLE else View.GONE
            }
        }
        val rows = DragList(this) { from, to ->
            order.add(to, order.removeAt(from))
            FingerScroll.setOrder(this, order)
            relabel()
        }
        column.addView(rows, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        val dialog = showSheet(column)
        order.forEach { style ->
            val sub = TextView(this).apply {
                text = getString(R.string.finger_default_mode)
                textSize = 13f
                setTextColor(INK)
            }
            subs[style] = sub
            rows.addView(modeRow(style, sub) { on ->
                if (!FingerScroll.setInCycle(this, style, on)) {
                    Toast.makeText(this, R.string.finger_cycle_keep_one, Toast.LENGTH_SHORT).show()
                    // Reopened so the last switch shows on again.
                    dialog.setOnDismissListener(null)
                    dialog.dismiss()
                    fingerCycleSheet()
                } else relabel()
            })
        }
        relabel()
        dialog.setOnDismissListener {
            FingerScroll.goHome(this)
            if (current == PAGE_FINGER) select(PAGE_FINGER)
        }
    }

    /** A mode in the sheet: the drag handle, its name over "Default" when it is, its switch. */
    private fun modeRow(style: String, sub: TextView, onChange: (Boolean) -> Unit): View {
        val switch = InkToggle(this, INK).apply {
            preset(FingerScroll.isOn(this@KompaktActivity, style))
            this.onChange = onChange
        }
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), dp(GUTTER), dp(8))
            minimumHeight = dp(ROW_HEIGHT)
            setOnClickListener { switch.toggle() }
        }
        row.addView(TextView(this).apply {
            text = "\u2261"
            textSize = 26f
            gravity = Gravity.CENTER
            setTextColor(INK)
            contentDescription = getString(R.string.finger_drag)
        }, LinearLayout.LayoutParams(dp(DragList.HANDLE_DP), WRAP_CONTENT))
        val lines = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        lines.addView(titleView(styleLabel(style)))
        lines.addView(sub)
        row.addView(lines, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        row.addView(switch)
        return row
    }

    private fun unlockLabel(source: String, short: Boolean): String = getString(when (source) {
        UnlockRefresh.AOD -> if (short) R.string.unlock_short_aod else R.string.unlock_from_aod
        UnlockRefresh.SLEEP -> if (short) R.string.unlock_short_sleep else R.string.unlock_from_sleep
        else -> if (short) R.string.unlock_short_lockscreen else R.string.unlock_from_lockscreen
    })

    private fun unlockRefreshValue(): String {
        val on = UnlockRefresh.SOURCES.filter { UnlockRefresh.enabled(this, it) }
        return when (on.size) {
            0 -> getString(R.string.unlock_refresh_off)
            UnlockRefresh.SOURCES.size -> getString(R.string.all)
            else -> on.joinToString(", ") { unlockLabel(it, short = true) }
        }
    }

    /** Which unlocks run the refresh: any, all or none of the three, all off by default. */
    private fun unlockRefreshSheet() {
        val column = sheetColumn(getString(R.string.unlock_refresh_row),
            getString(R.string.unlock_refresh_note))
        val dialog = showSheet(column)
        UnlockRefresh.SOURCES.forEach { source ->
            column.addView(switchRow(unlockLabel(source, short = false),
                UnlockRefresh.enabled(this, source), { on ->
                    UnlockRefresh.setEnabled(this, source, on)
                }))
        }
        dialog.setOnDismissListener { if (current == TAB_DISPLAY) select(TAB_DISPLAY) }
    }

    private fun aodRefresh() = Settings.Secure.getInt(contentResolver, KEY_AOD_REFRESH, 0)

    private fun aodRefreshLabel(minutes: Int) =
        if (minutes in AOD_REFRESH_MINUTES) getString(R.string.aod_refresh_min, minutes)
        else getString(R.string.aod_refresh_auto)

    /** How often the always-on display repaints; DozeUi reads the setting at each repaint. */
    private fun aodRefreshSheet() {
        val now = aodRefresh()
        sheet(getString(R.string.aod_refresh_row), getString(R.string.aod_refresh_note),
            (listOf(0) + AOD_REFRESH_MINUTES).map { minutes ->
                SheetItem(aodRefreshLabel(minutes), "", minutes == now) {
                    Settings.Secure.putInt(contentResolver, KEY_AOD_REFRESH, minutes)
                    select(TAB_DISPLAY)
                }
            })
    }

    /** What the stored divisor works out to in frames a second. */
    private fun frameRateNow(): Int {
        val divisor = android.os.SystemProperties.getInt(FPS_PROP, 1).coerceAtLeast(1)
        return Math.round((display?.refreshRate ?: 60f) / divisor)
    }

    private fun frameRateSheet() {
        val now = frameRateNow()
        sheet(getString(R.string.frame_rate_row), getString(R.string.frame_rate_note),
            listOf(60, 30, 15, 10).map { fps ->
                SheetItem(getString(R.string.frame_rate_fps, fps), "", fps == now) { setFrameRate(fps) }
            })
    }

    /** Every app reads the divisor as it starts, SystemUI included, so a change restarts the phone. */
    private fun setFrameRate(fps: Int) {
        val divisor = Math.round((display?.refreshRate ?: 60f) / fps).coerceAtLeast(1)
        try {
            android.os.SystemProperties.set(FPS_PROP, divisor.toString())
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "frame rate: cannot set $FPS_PROP (${t.javaClass.simpleName})")
            return
        }
        getSystemService(android.os.PowerManager::class.java)?.reboot(null)
    }

    private fun emojiColour() = android.os.SystemProperties.get(EMOJI_PROP, "") == "color"

    // Read by the framework (PhoneWindowManager): which LED a long press of the power button switches.
    private fun powerTorch() = when (Settings.Global.getString(contentResolver, POWER_TORCH)) {
        "warm" -> "warm"
        "both" -> "both"
        else -> "cold"
    }

    private fun powerTorchLabel() = getString(when (powerTorch()) {
        "warm" -> R.string.power_torch_warm
        "both" -> R.string.power_torch_both
        else -> R.string.power_torch_cold
    })

    private fun powerTorchSheet() {
        val now = powerTorch()
        val pick = { value: String ->
            Settings.Global.putString(contentResolver, POWER_TORCH, value)
            select(TAB_SETTINGS)
        }
        sheet(getString(R.string.power_torch_row), getString(R.string.power_torch_note), listOf(
            SheetItem(getString(R.string.power_torch_cold), "", now == "cold") { pick("cold") },
            SheetItem(getString(R.string.power_torch_warm), "", now == "warm") { pick("warm") },
            SheetItem(getString(R.string.power_torch_both), "", now == "both") { pick("both") },
        ))
    }

    private fun emojiSheet() {
        val colour = emojiColour()
        sheet(getString(R.string.emoji_row), getString(R.string.emoji_note), listOf(
            SheetItem(getString(R.string.emoji_mono), "", !colour) { setEmoji(false) },
            SheetItem(getString(R.string.emoji_colour), "", colour) { setEmoji(true) },
        ))
    }

    /** Fonts load once at boot, so a change restarts the phone. */
    private fun setEmoji(colour: Boolean) {
        if (colour == emojiColour()) return
        try {
            android.os.SystemProperties.set(EMOJI_PROP, if (colour) "color" else "mono")
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "emoji: cannot set $EMOJI_PROP (${t.javaClass.simpleName})")
            return
        }
        getSystemService(android.os.PowerManager::class.java)?.reboot(null)
    }

    private fun script(): String = android.icu.util.ULocale.addLikelySubtags(
        android.icu.util.ULocale.forLocale(resources.configuration.locales[0])).script

    private fun fonts(): List<String> = if (script() == "Latn") FONTS else FONTS - "google-sans"

    private fun font() = android.os.SystemProperties.get(FONT_PROP, "")
        .takeIf { it in fonts() } ?: fonts()[0]

    /** Each choice in its own face, from its file: the named families already follow the pick. */
    private fun fontPreview(font: String): android.graphics.Typeface? {
        val file = when (font) {
            "roboto" -> "/system/fonts/Roboto-Regular.ttf"
            "manrope" -> "/system/fonts/KompaktManrope-Regular.ttf"
            "google-sans" -> "/product/fonts/GoogleSansFlex-Regular.ttf"
            "serif" -> "/system/fonts/KompaktNotoSerif-Regular.ttf"
            "source-serif" -> "/system/fonts/KompaktSourceSerif-Regular.ttf"
            "lato" -> "/system/fonts/Lato-Regular.ttf"
            "jetbrains-mono" -> "/system/fonts/JetBrainsMono-Regular.ttf"
            else -> return null
        }
        return try {
            android.graphics.Typeface.Builder(java.io.File(file))
                .setFontVariationSettings("'wght' 400").build()
        } catch (t: Throwable) {
            null
        }
    }

    private fun fontLabel(font: String) = getString(when (font) {
        "jetbrains-mono" -> R.string.font_jetbrains_mono
        "lato" -> R.string.font_lato
        "source-serif" -> R.string.font_source_serif
        "roboto" -> R.string.font_roboto
        "manrope" -> R.string.font_manrope
        "serif" -> R.string.font_serif
        else -> R.string.font_google_sans
    })

    private fun fontSheet() {
        val current = font()
        sheet(getString(R.string.font_row), getString(R.string.font_note),
            fonts().map { f ->
                SheetItem(fontLabel(f), "", f == current, fontPreview(f)) { setFont(f) }
            })
    }

    /** Fonts load once at boot, so a change restarts the phone. */
    private fun setFont(font: String) {
        if (font == font()) return
        try {
            android.os.SystemProperties.set(FONT_PROP, font)
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "font: cannot set $FONT_PROP (${t.javaClass.simpleName})")
            return
        }
        getSystemService(android.os.PowerManager::class.java)?.reboot(null)
    }

    /** The name Key Mapper shows for the key a gesture sends. */
    private fun keyName(button: Int) = getString(when (button) {
        Buttons.TAP -> R.string.finger_key_red
        Buttons.TRIPLE -> R.string.finger_key_yellow
        else -> R.string.finger_key_green
    })

    private fun actionLabel(action: String, button: Int): String = when {
        action == "key" -> getString(R.string.finger_act_key_named, keyName(button))
        else -> getString(when (action) {
            "enter" -> R.string.finger_act_enter
            "recents" -> R.string.finger_act_recents
            "refresh" -> R.string.finger_act_refresh
            "shade" -> R.string.finger_act_shade
            "qs" -> R.string.finger_act_qs
            "light" -> R.string.finger_act_light
            "torch_warm" -> R.string.finger_act_torch_warm
            "torch_cold" -> R.string.finger_act_torch_cold
            "torch_both" -> R.string.finger_act_torch_both
            "orient" -> R.string.finger_act_orient
            "mode" -> R.string.finger_act_mode
            else -> R.string.finger_act_off
        })
    }

    /** One sheet for any gesture: every action, the current one marked. */
    private fun pickAction(title: String, current: String, button: Int, onPick: (String) -> Unit) =
        sheet(title, "", FingerScroll.ACTIONS.map { action ->
            SheetItem(actionLabel(action, button), "", action == current) {
                onPick(action)
                select(PAGE_FINGER)
            }
        })

    /** The ini that starts the HAL's navigation loop comes with our vendor; its probe is that file. */
    private fun fingerNeeds() = needsSheet(getString(R.string.finger_section),
        getString(R.string.finger_note), Needs.VENDOR, FingerScroll.available())

    private fun temperatureInfo() = resetSheet(getString(R.string.temperature_section),
        getString(R.string.temperature_note)) {
        Prefs.setTemperature(this, Eink.STOCK_TEMP)
        Eink.setTemperature(Eink.STOCK_TEMP)
    }

    private fun speedInfo() = resetSheet(getString(R.string.speed_section),
        getString(R.string.speed_note)) {
        Prefs.setSpeed(this, Eink.STOCK_SPEED)
        Eink.setSpeed(Eink.STOCK_SPEED)
    }

    private fun cleanInfo() = resetSheet(getString(R.string.clean_section),
        getString(R.string.clean_note)) {
        Prefs.setCleanEvery(this, Eink.CLEAN_STOCK)
        Eink.setCleanEvery(Eink.CLEAN_STOCK)
    }

    private fun patternInfo() = resetSheet(getString(R.string.pattern_section),
        getString(R.string.pattern_note)) {
        Prefs.setPattern(this, Eink.STOCK_PATTERN)
        Eink.setPattern(Eink.STOCK_PATTERN)
    }

    private fun lightInfo() = resetSheet(getString(R.string.light_section),
        getString(R.string.light_note)) {
        Prefs.setDitherLight(this, Eink.STOCK_LIGHT)
        Eink.setDitherLight(Eink.STOCK_LIGHT)
    }

    /** A setting's sheet: what it does, and a way back to its default. The page redraws with it. */
    private fun resetSheet(title: String, note: String, onReset: () -> Unit) {
        val column = sheetColumn(title, note)
        lateinit var dialog: android.app.Dialog
        column.addView(buttonRow(getString(R.string.reset_default) to {
            onReset()
            dialog.dismiss()
            select(current)
        }))
        dialog = showSheet(column)
    }

    private fun chooseSleepImage() {
        // OPEN_DOCUMENT, not GET_CONTENT: the photo picker can serve a stale copy.
        val pick = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        try {
            startActivityForResult(pick, REQ_SLEEP_IMAGE)
        } catch (e: android.content.ActivityNotFoundException) {
            sleepStatus = getString(R.string.sleep_no_picker)
            select(PAGE_SLEEP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_SLEEP_IMAGE || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        sleepWork { SleepImage.set(this, uri) }
    }

    /** Decoding and dithering take a moment: off the main thread, one at a time, one repaint at the end. */
    private fun sleepWork(failText: Int = R.string.sleep_failed, work: () -> Boolean) {
        if (sleepBusy) return
        sleepBusy = true
        Sysfs.onIo {
            val ok = work()
            runOnUiThread {
                sleepBusy = false
                if (isDestroyed) return@runOnUiThread
                sleepStatus = getString(if (ok) R.string.sleep_done else failText)
                if (current == PAGE_SLEEP) select(PAGE_SLEEP)
            }
        }
    }

    // ----------------------------------------------------------- front light

    private var luxWatch: android.hardware.SensorEventListener? = null

    /** The auto brightness curve: a front light number per kind of light, and what the sensor sees now. */
    private fun lightCurvePage(): List<View> {
        val out = ArrayList<View>()
        // The note and Reset to default live in the info sheet; Reset forgets what the slider taught.
        out.add(sectionWithInfo(getString(R.string.light_curve_row)) {
            val column = sheetColumn(getString(R.string.light_curve_row),
                getString(R.string.light_curve_note))
            lateinit var dialog: android.app.Dialog
            column.addView(buttonRow(getString(R.string.reset_default) to {
                LightCurve.reset(this)
                dialog.dismiss()
                select(PAGE_LIGHT_CURVE)
            }))
            dialog = showSheet(column)
        })
        val now = note(getString(R.string.diag_reading))
        out.add(now)
        // What the slider has taught it: the light at each point, read only; the slider is how it changes.
        val (x, y) = LightCurve.points(this)
        x.indices.forEach { i ->
            out.add(factRow(getString(R.string.light_curve_lux, x[i].roundToInt()),
                LightCurve.label(this, y[i].roundToInt())))
        }
        watchLux { value ->
            now.text = getString(R.string.light_curve_now, value,
                LightCurve.label(this, LightCurve.at(this, value.toFloat())))
        }
        return out
    }

    /** Follows the light sensor while a page shows its reading; select and onPause stop it. */
    private fun watchLux(onLux: (Int) -> Unit) {
        unwatchLux()
        val sm = getSystemService(android.hardware.SensorManager::class.java) ?: return
        val sensor = sm.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT) ?: return
        val watch = object : android.hardware.SensorEventListener {
            override fun onSensorChanged(e: android.hardware.SensorEvent) {
                onLux(e.values[0].toInt())
            }
            override fun onAccuracyChanged(s: android.hardware.Sensor?, accuracy: Int) {}
        }
        sm.registerListener(watch, sensor, android.hardware.SensorManager.SENSOR_DELAY_NORMAL)
        luxWatch = watch
    }

    private fun unwatchLux() {
        luxWatch?.let {
            getSystemService(android.hardware.SensorManager::class.java)?.unregisterListener(it)
        }
        luxWatch = null
    }

    // --------------------------------------------------------------- per app

    /** A row per launchable app, showing the value it will get: its e-ink mode, invert or light colour. */
    private fun appRows(
        valueOf: (String) -> String,
        onPick: (String, String, () -> Unit) -> Unit,
    ): List<View> {
        val pm = packageManager
        return pm.getInstalledApplications(0)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }
            .map { app ->
                val pkg = app.packageName
                val name = app.loadLabel(pm).toString()
                // The row updates itself: rebuilding the page would throw the list back to the top.
                val mode = noteView(valueOf(pkg))
                row(name, null, null, { onPick(pkg, name) { mode.text = valueOf(pkg) } },
                    appIcon(app.loadIcon(pm)), mode)
            }
    }

    /** An app's icon as its symbol in ink: the monochrome layer when it has one, else the foreground layer, else the whole icon, in two inks. */
    private fun appIcon(icon: android.graphics.drawable.Drawable): android.graphics.drawable.Drawable {
        val adaptive = icon as? android.graphics.drawable.AdaptiveIconDrawable
        val mono = adaptive?.monochrome
        return when {
            mono != null -> IconLayer(mono.mutate()).apply { setTint(INK) }
            adaptive?.foreground != null -> IconLayer(adaptive.foreground.mutate()).apply { colorFilter = MONO }
            else -> icon.mutate().apply { colorFilter = MONO }
        }
    }

    /** Mode chooser for one package; invert lives here, last, because it is not a panel mode. */
    private fun pickMode(pkg: String, name: String, refresh: () -> Unit) {
        // The heading says which mode is on, the way the Display heading does --
        // the circles are icons, so without it the sheet never names the choice.
        val heading = sheetLabel(
            getString(R.string.app_mode_title, name, Eink.label(Prefs.appMode(this, pkg))))
        val column = sheetColumnOf(heading, onInfo = { einkInfo(Eink.APP_ORDER) })
        lateinit var dialog: android.app.Dialog
        // Follow master mode first, with the modes: it is the choice of no mode of its own.
        val follow = FrameLayout(this)
        fun drawFollow() {
            follow.removeAllViews()
            follow.addView(choiceRow(getString(R.string.per_app_default), "",
                !Prefs.appModeIsOwn(this, pkg)) {
                Prefs.setAppMode(this, pkg, null)
                refresh()
                dialog.dismiss()
            })
        }
        // The circles are redrawn in place when one is tapped, so the sheet stays up.
        val holder = FrameLayout(this)
        // The pattern row, only while the app's mode has dots to place.
        val pattern = FrameLayout(this)
        fun drawPattern() {
            pattern.removeAllViews()
            if (!Ours.vendor() || !Eink.dithers(Prefs.appMode(this, pkg))) return
            val own = Prefs.appPatternOwn(this, pkg)
            pattern.addView(valueRow(getString(R.string.pattern_section),
                own?.let { Eink.PATTERNS[it] } ?: getString(R.string.app_pattern_default)) {
                pickPattern(pkg) { drawPattern() }
            })
        }
        fun drawCircles() {
            holder.removeAllViews()
            holder.addView(modeCircles(Eink.APP_ORDER, Prefs.appMode(this, pkg)) { key ->
                Prefs.setAppMode(this, pkg, key)
                heading.text = getString(R.string.app_mode_title,
                    name, Eink.label(key)).uppercase()
                refresh()
                drawCircles()
                drawFollow()
                drawPattern()
            })
        }
        drawFollow()
        column.addView(follow)
        drawCircles()
        column.addView(holder)
        drawPattern()
        column.addView(pattern)
        // A rule between the e-ink choices and invert, which is not one of them.
        column.addView(View(this).apply { setBackgroundColor(INK) },
            LinearLayout.LayoutParams(MATCH_PARENT, dp(1)).apply { setMargins(dp(GUTTER), dp(8), dp(GUTTER), dp(8)) })
        column.addView(switchRow(getString(R.string.app_invert_row),
            AppInvert.wanted(this, pkg), { on ->
                AppInvert.setWanted(this, pkg, on)
                refresh()
            }))
        dialog = showSheet(column)
    }

    /** One app's dither pattern: its own, or the master one's. */
    private fun pickPattern(pkg: String, done: () -> Unit) {
        val own = Prefs.appPatternOwn(this, pkg)
        val pick = { p: Int? ->
            Prefs.setAppPattern(this, pkg, p)
            done()
        }
        sheet(getString(R.string.pattern_section), getString(R.string.pattern_note),
            listOf(SheetItem(getString(R.string.app_pattern_default),
                Eink.PATTERNS[Prefs.pattern(this)] ?: "", own == null) { pick(null) }) +
            Eink.PATTERNS.map { (p, label) -> SheetItem(label, "", own == p) { pick(p) } })
    }

    /** What one app's row reads: its mode, and whether it is flipped. */
    private fun appModeValue(pkg: String): String {
        val mode = Eink.label(Prefs.appMode(this, pkg))
        return if (AppInvert.wanted(this, pkg))
            getString(R.string.app_mode_inverted, mode) else mode
    }

    // ----------------------------------------------------------- diagnostics

    /** Read every node we drive, and say whether it is writable. */
    private fun refreshDiagnostics() {
        Sysfs.onIo {
            val eink = ArrayList<Pair<String, String>>()
            eink.add("mode pref" to Prefs.mode(this))
            // Every node setMode writes, so a dial can be checked against the panel.
            for (n in listOf("manual_mode", "waveform_mode", "pixelbits", "refresh_mode",
                    "timings_mode", "contrast", "gamma", "brightness",
                    "dither_type", "dither_colors", "dither_param",
                    "fps_limit", "eink_temperature", "temperature_offset", "power",
                    "refresh_time")) {
                val v = Sysfs.read("/sys/einkinfo/$n")?.trim() ?: "?"
                eink.add(n to
                    if (File("/sys/einkinfo/$n").canWrite()) v
                    else getString(R.string.diag_read_only, v))
            }
            val leds = listOf("red", "green", "blue").map { c ->
                val b = Sysfs.read("/sys/class/leds/$c/brightness")?.trim() ?: "?"
                val max = Sysfs.read("/sys/class/leds/$c/max_brightness")?.trim() ?: "?"
                val trig = Sysfs.read("/sys/class/leds/$c/trigger")
                    ?.let { t -> Regex("\\[(\\w+)]").find(t)?.groupValues?.get(1) } ?: "?"
                val v = "$b / $max  $trig"
                c to if (File("/sys/class/leds/$c/brightness").canWrite()) v
                     else getString(R.string.diag_read_only, v)
            }
            val snapshot = Diag(eink, leds, Ims.diagnose(this), gsfLines())
            handler.post {
                diag = snapshot
                if (current == PAGE_DIAGNOSTICS) select(PAGE_DIAGNOSTICS)
            }
        }
    }

    /** The GSF check-in id in decimal, for google.com/android/uncertified; the reason when missing. */
    private fun gsfLines(): String {
        val r = GsfId.read(this)
        return if (r.decimal != null) "  android_id = ${r.decimal}\n" else "  android_id: ${r.why}\n"
    }

    // ------------------------------------------------------------- the parts

    /** Words under a heading or at the end of a page; the one place text may wrap. */
    private fun note(text: String) = TextView(this).apply {
        this.text = text
        textSize = 13f
        setTextColor(INK)
        setPadding(dp(GUTTER), 0, dp(GUTTER), dp(8))
    }

    /** A section heading as AOSP Settings draws one: small capitals in bold, nothing around them. */
    private fun section(text: String): View = sectionRow(sectionLabel(text), null)

    /** The heading's own text view, kept when a caller needs to rewrite it. */
    private fun sectionLabel(text: String) = TextView(this).apply {
        this.text = text.uppercase()
        textSize = 13f
        setTextColor(INK)
        setTypeface(Typeface.DEFAULT_BOLD)
    }

    /** The heading as inkOS draws one: the title, then dashes to the right edge, or to the info glyph when it has a sheet. */
    private fun sectionRow(label: TextView, onInfo: (() -> Unit)?): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(GUTTER), dp(16), dp(GUTTER), dp(16))
        }
        row.addView(label)
        // inkOS's separator: 4 px dash, 4 px gap, 1 px thick; dp(3) is 4 px on this panel.
        row.addView(View(this).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.LINE
                setStroke(dp(1), INK, dp(3).toFloat(), dp(3).toFloat())
            }
        }, LinearLayout.LayoutParams(0, dp(3), 1f).apply { marginStart = dp(16) })
        if (onInfo != null) row.addView(infoGlyph(onInfo), glyphParams())
        return row
    }

    /** The same heading, with the info glyph against the right margin. */
    private fun sectionWithInfo(text: String, onInfo: () -> Unit): View =
        sectionRow(sectionLabel(text), onInfo)

    private fun infoGlyph(onInfo: () -> Unit) = ImageView(this).apply {
        setImageResource(R.drawable.ic_info)
        imageTintList = ColorStateList.valueOf(INK)
        contentDescription = getString(R.string.eink_info)
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        setOnClickListener { onInfo() }
    }

    private fun glyphParams() = LinearLayout.LayoutParams(dp(28), dp(24)).apply { marginStart = dp(8) }

    /** A setting's name: one weight up from the body, so it leads the row. */
    private fun titleView(text: String) = TextView(this).apply {
        this.text = text
        textSize = 16f
        setTextColor(INK)
        setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }

    /** What a setting does, in one line; a longer story belongs behind the info glyph. */
    private fun noteView(text: String) = TextView(this).apply {
        this.text = text
        textSize = 13f
        maxLines = 1
        ellipsize = android.text.TextUtils.TruncateAt.END
        setTextColor(INK)
        setPadding(0, dp(2), 0, 0)
    }

    /** A row's value, at the right, in bold. */
    private fun valueView(text: String) = TextView(this).apply {
        textSize = 14f
        maxLines = 1
        setTextColor(INK)
        setTypeface(Typeface.DEFAULT_BOLD)
        this.text = text
    }

    private fun chevron() = ImageView(this).apply {
        setImageResource(R.drawable.ic_chevron)
        imageTintList = ColorStateList.valueOf(INK)
        scaleType = ImageView.ScaleType.FIT_CENTER
        val s = dp(28)
        layoutParams = LinearLayout.LayoutParams(s, s)
    }

    /**
     * The one row every setting is drawn as, on a page or in a sheet: the name over its note
     * at the left, a control at the right, nothing drawn around them.
     */
    private fun row(title: String, note: String? = null, control: View? = null,
                    onClick: (() -> Unit)? = null, icon: android.graphics.drawable.Drawable? = null,
                    noteLine: TextView? = null, glyph: Int = 0): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(GUTTER), dp(8), dp(GUTTER), dp(8))
            minimumHeight = dp(ROW_HEIGHT)
            if (onClick != null) setOnClickListener { onClick() }
        }
        if (icon != null || glyph != 0) {
            // One slot for both: an app's symbol already in ink (appIcon), or a Material glyph in ink.
            row.addView(ImageView(this).apply {
                if (icon != null) {
                    setImageDrawable(icon)
                } else {
                    setImageResource(glyph)
                    imageTintList = ColorStateList.valueOf(INK)
                    scaleType = ImageView.ScaleType.CENTER
                }
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }, LinearLayout.LayoutParams(dp(32), dp(32)).apply { marginEnd = dp(16) })
        }
        val lines = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        lines.addView(titleView(title))
        (noteLine ?: note?.let { noteView(it) })?.let { lines.addView(it) }
        row.addView(lines, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
            if (control != null) marginEnd = dp(16)
        })
        if (control != null) row.addView(control)
        return row
    }

    /** One choice in a sheet, as inkOS's SingleChoiceSheet: radio ring, label, whole row the target. */
    private fun choiceRow(label: String, sub: String, selected: Boolean,
                          face: android.graphics.Typeface? = null, onClick: () -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(GUTTER), dp(8), dp(GUTTER), dp(8))
            minimumHeight = dp(ROW_HEIGHT)
            setOnClickListener { onClick() }
        }
        row.addView(RadioMark(this, INK, selected))
        val lines = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        lines.addView(titleView(label).apply { if (face != null) typeface = face })
        if (sub.isNotEmpty()) lines.addView(noteView(sub))
        row.addView(lines, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
            marginStart = dp(16)
        })
        return row
    }

    /** A name on the left and its current value on the right; "›" draws the chevron instead. */
    private fun valueRow(name: String, value: String, note: String? = null, glyph: Int = 0,
                         onClick: () -> Unit): View =
        row(name, note, if (value == "›") chevron() else valueView(value), onClick, glyph = glyph)

    /** The same row, with a value view the caller keeps so it can update it in place. */
    private fun valueRow(name: String, value: TextView, onClick: () -> Unit): View =
        row(name, null, value, onClick)

    /** A row that only reports: name and value, no tap target. */
    private fun factRow(name: String, value: String, note: String? = null): View =
        row(name, note, valueView(value))

    /** A setting whose control sits below it (chips, a slider): the name, its note and the info glyph. */
    private fun settingRow(title: String, note: String, glyph: Int = 0, onInfo: () -> Unit): View =
        row(title, note, infoGlyph(onInfo).apply { layoutParams = glyphParams() }, glyph = glyph)

    /** Callers pass the change as a trailing lambda, so it stays the last parameter here. */
    private fun toggleRow(label: String, on: Boolean, note: String? = null, glyph: Int = 0,
                          onChange: (Boolean) -> Unit): View =
        switchRow(label, on, onChange, note, glyph)

    /** A switch as inkOS draws one: the row, with the switch at the right. */
    private fun switchRow(label: String, on: Boolean, onChange: (Boolean) -> Unit,
                          note: String? = null, glyph: Int = 0): View {
        val switch = InkToggle(this, INK).apply {
            preset(on)
            this.onChange = onChange
        }
        return row(label, note, switch, onClick = { switch.toggle() }, glyph = glyph)
    }

    /** A switch that cannot be cleared: the thing is always switched off, and the note says so. */
    private fun lockedRow(label: String, note: String? = null, glyph: Int = 0): View =
        // Checked and unreachable: enabled so it draws in ink, not clickable so it stays.
        row(label, note, InkToggle(this, INK).apply { preset(true) }, glyph = glyph)

    private fun buttonRow(vararg items: Pair<String, () -> Unit>): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(GUTTER), dp(8), dp(GUTTER), dp(8))
        }
        items.forEachIndexed { i, (label, action) ->
            val b = TextView(this).apply {
                text = label
                textSize = 15f
                gravity = Gravity.CENTER
                setTextColor(INK)
                setTypeface(Typeface.DEFAULT_BOLD)
                background = outline(2, BUTTON_RADIUS)
                setPadding(dp(8), dp(12), dp(8), dp(12))
                setOnClickListener { action() }
            }
            val lp = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            if (i < items.size - 1) lp.marginEnd = dp(8)
            row.addView(b, lp)
        }
        return row
    }

    /** Paper fill, ink border, rounded: a chip, a button, a picture frame; dashed marks one this phone cannot use. */
    private fun outline(strokeDp: Int = 2, radiusDp: Int = BUTTON_RADIUS,
                        dashed: Boolean = false): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(PAPER)
            if (dashed) setStroke(dp(strokeDp), INK, dp(6).toFloat(), dp(4).toFloat())
            else setStroke(dp(strokeDp), INK)
            cornerRadius = dp(radiusDp).toFloat()
        }

    /** The same shape as a round outline, for the mode circles. */
    private fun circle(strokeDp: Int, dashed: Boolean = false): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(PAPER)
            if (dashed) setStroke(dp(strokeDp), INK, dp(6).toFloat(), dp(4).toFloat())
            else setStroke(dp(strokeDp), INK)
        }

    // ------------------------------------------------------ our boot, vendor

    /** What a feature needs and whether this phone has it; vendorOk is the feature's own vendor probe. */
    private fun needsSheet(title: String, note: String, needs: Needs,
                           vendorOk: Boolean = Ours.vendor()) {
        val column = sheetColumn(title, note)
        if (needs.vendor) {
            column.addView(factRow(getString(R.string.needs_vendor), getString(when {
                vendorOk -> R.string.needs_installed
                Ours.vendor() -> R.string.needs_older
                else -> R.string.needs_missing
            })))
        }
        if (needs.kernel) {
            column.addView(factRow(getString(R.string.needs_boot), getString(
                if (Ours.kernel()) R.string.needs_installed else R.string.needs_missing)))
        }
        showSheet(column)
    }

    /** The short form, for the right-hand side of a row. */
    private fun needsValue(needs: Needs): String = getString(when (needs) {
        Needs.VENDOR -> R.string.needs_value_vendor
        Needs.KERNEL -> R.string.needs_value_boot
        Needs.BOTH -> R.string.needs_value_both
    })

    /** A feature this phone cannot use: the same row, naming what it needs, and the sheet on tap. */
    private fun blockedRow(name: String, needs: Needs, glyph: Int = 0, onTap: () -> Unit): View {
        val control = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        control.addView(valueView(needsValue(needs)))
        control.addView(infoGlyph(onTap), glyphParams())
        return row(name, null, control, onTap, glyph = glyph)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    /** One choice in a sheet. */
    private class SheetItem(
        val label: String,
        val sub: String,
        val selected: Boolean,
        val typeface: android.graphics.Typeface? = null,
        val action: () -> Unit,
    )

    companion object {

        private const val TAB_DISPLAY = 0
        private const val TAB_OFFLINE = 1
        private const val TAB_LOOK = 2
        private const val TAB_SETTINGS = 3

        /** Pages one level below a tab; Back walks up through parentOf. */
        private const val PAGE_DIAGNOSTICS = 4
        private const val PAGE_APP_MODES = 5
        private const val PAGE_BATTERY = 6
        private const val PAGE_SLEEP = 7
        private const val PAGE_FINGER = 8
        private const val PAGE_ADVANCED = 9
        private const val PAGE_LIGHT_CURVE = 10

        private const val REQ_SLEEP_IMAGE = 1

        /** Where every row, heading, chip and circle starts and ends, in dp from the edge. */
        private const val GUTTER = 20

        /** Every row is at least this tall, whatever it ends in: switch, value or chevron. */
        private const val ROW_HEIGHT = 48

        /** A mode circle's diameter, in dp. */
        private const val CIRCLE = 48

        /** Minutes the always-on display can repaint every, besides auto (0). */
        private val AOD_REFRESH_MINUTES = listOf(1, 5, 10, 60)

        /** Corner radii, in dp: the picture frame, pill-shaped chips and buttons, and the sheet's top edge. */
        private const val BOX_RADIUS = 8
        private const val BUTTON_RADIUS = 24
        private const val SHEET_RADIUS = 28
    }
}

/** One layer of an adaptive icon, drawn as a launcher draws it: grown by the extra inset and cut to the icon mask. */
class IconLayer(layer: android.graphics.drawable.Drawable) : android.graphics.drawable.InsetDrawable(
    layer, -android.graphics.drawable.AdaptiveIconDrawable.getExtraInsetFraction()) {
    private val mask = android.graphics.drawable.AdaptiveIconDrawable(
        android.graphics.drawable.ColorDrawable(Color.BLACK), null)
    override fun draw(canvas: android.graphics.Canvas) {
        mask.bounds = bounds
        val saved = canvas.save()
        canvas.clipPath(mask.iconMask)
        super.draw(canvas)
        canvas.restoreToCount(saved)
    }
}

/** As tall as it is wide, with its icon at two fifths of that: a mode circle. */
class SquareCell(context: Context) : FrameLayout(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val side = MeasureSpec.getSize(widthMeasureSpec)
        val inset = side * 3 / 10
        getChildAt(0)?.setPadding(inset, inset, inset, inset)
        val exact = MeasureSpec.makeMeasureSpec(side, MeasureSpec.EXACTLY)
        super.onMeasure(exact, exact)
    }
}

/** A column that moves a page at a time and never splits a row. */
class PagedColumn(context: Context) : ScrollView(context) {

    private val column = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }

    private var starts: List<Int> = listOf(0)
    private var page = 0
    private var downY = 0f

    /** How far a finger must travel before it counts as a page turn. */
    private val slop = 24 * context.resources.displayMetrics.density

    /** Reports (page, total), both one-based, whenever either changes. */
    var onPageChanged: ((Int, Int) -> Unit)? = null

    init {
        isVerticalScrollBarEnabled = false
        overScrollMode = View.OVER_SCROLL_NEVER
        isFillViewport = true
        addView(column, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    fun add(v: View) {
        column.addView(v, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        recompute()
    }

    /** Where each page begins, in scroll coordinates. */
    private fun recompute() {
        val viewport = height - paddingTop - paddingBottom
        if (viewport <= 0) return
        val out = ArrayList<Int>()
        out.add(0)
        var pageTop = 0
        for (i in 0 until column.childCount) {
            val c = column.getChildAt(i)
            if (c.visibility == View.GONE) continue
            if (c.bottom - pageTop > viewport && c.top > pageTop) {
                pageTop = c.top
                out.add(pageTop)
            }
        }
        starts = out
        if (page >= starts.size) page = starts.size - 1
        onPageChanged?.invoke(page + 1, starts.size)
    }

    /** Let children have taps; take the gesture the moment it becomes a drag. */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
                return false
            }
            MotionEvent.ACTION_MOVE ->
                return abs(ev.y - downY) > slop
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> downY = ev.y
            MotionEvent.ACTION_UP -> {
                val dy = ev.y - downY
                if (abs(dy) > slop) {
                    // Dragging up reveals what is below, so it goes forward.
                    if (dy < 0) next() else prev()
                }
            }
        }
        return true
    }

    fun next() = go(page + 1)

    fun prev() = go(page - 1)

    private fun go(target: Int) {
        val clamped = target.coerceIn(0, starts.size - 1)
        if (clamped == page) return
        page = clamped
        // scrollTo, never smoothScrollTo: the animation would be the partial refresh storm this whole class exists to.
        scrollTo(0, starts[page])
        onPageChanged?.invoke(page + 1, starts.size)
    }
}

/** A column whose rows reorder by dragging from the left-edge handle. */
class DragList(context: Context,
               private val onMove: (from: Int, to: Int) -> Unit) : LinearLayout(context) {
    companion object {
        const val HANDLE_DP = 48
    }

    private val handle = HANDLE_DP * context.resources.displayMetrics.density
    private var held: View? = null

    init {
        orientation = VERTICAL
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked != MotionEvent.ACTION_DOWN || ev.x > handle) return false
        val row = rowAt(ev.y) ?: return false
        held = row
        row.background = GradientDrawable().apply {
            setStroke((2 * resources.displayMetrics.density).toInt(), currentInk())
        }
        // The sheet's scroller would take a vertical drag for itself.
        parent?.requestDisallowInterceptTouchEvent(true)
        return true
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val row = held ?: return false
        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val from = indexOfChild(row)
                val to = when {
                    from > 0 && ev.y < getChildAt(from - 1).let { it.top + it.height / 2 } -> from - 1
                    from < childCount - 1 && ev.y > getChildAt(from + 1).let { it.top + it.height / 2 } -> from + 1
                    else -> from
                }
                if (to != from) {
                    removeViewAt(from)
                    addView(row, to)
                    onMove(from, to)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                row.background = null
                held = null
            }
        }
        return true
    }

    private fun rowAt(y: Float): View? =
        (0 until childCount).map { getChildAt(it) }.firstOrNull { y >= it.top && y < it.bottom }

    /** The ink of the first line of text in the list, so the outline matches the mode. */
    private fun currentInk(): Int {
        fun find(v: View): Int? = when (v) {
            is TextView -> v.currentTextColor
            is ViewGroup -> (0 until v.childCount).firstNotNullOfOrNull { find(v.getChildAt(it)) }
            else -> null
        }
        return find(this) ?: Color.BLACK
    }
}

/** inkOS's toggle (CustomToggleSwitch): ring then dash off, dash then dot on; nothing slides. */
class InkToggle(context: Context, private val ink: Int) : View(context) {

    private val d = context.resources.displayMetrics.density

    var isChecked = false
        set(value) {
            if (field == value) return
            field = value
            invalidate()
            onChange?.invoke(value)
        }

    var onChange: ((Boolean) -> Unit)? = null

    fun toggle() { isChecked = !isChecked }

    /** Set without telling the listener, for the initial state. */
    fun preset(on: Boolean) {
        val listener = onChange
        onChange = null
        isChecked = on
        onChange = listener
    }

    private val ring = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2.5f * d
    }
    private val solid = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.FILL
    }

    /** A little room round the 24 x 12 dp drawing, so neither circle is cut flat. */
    private val margin = 2 * d

    override fun onMeasure(widthSpec: Int, heightSpec: Int) = setMeasuredDimension(
        Math.ceil((24 * d + 2 * margin).toDouble()).toInt(),
        Math.ceil((12 * d + 2 * margin).toDouble()).toInt())

    override fun onDraw(canvas: android.graphics.Canvas) {
        ring.color = ink
        solid.color = ink
        // The ring and the dot share one outer radius, 6 dp, as in inkOS: the
        // ring's 2.5 dp line is drawn inside that edge, the dot fills it.
        val r = 6 * d
        val cy = height / 2f
        val x0 = margin
        val line = 2.5f * d
        if (isChecked) {
            canvas.drawRect(x0, cy - line / 2, x0 + 2 * r, cy + line / 2, solid)
            canvas.drawCircle(x0 + 3 * r, cy, r, solid)
        } else {
            canvas.drawCircle(x0 + r, cy, r - ring.strokeWidth / 2, ring)
            canvas.drawRect(x0 + 2 * r, cy - line / 2, x0 + 4 * r, cy + line / 2, solid)
        }
    }
}

/** inkOS's radio: a ring, with a dot inside when it is the chosen one. */
class RadioMark(context: Context, ink: Int, private val selected: Boolean) : View(context) {

    private val density = context.resources.displayMetrics.density

    private val ring = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2 * density
        color = ink
    }

    private val dot = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.FILL
        color = ink
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val size = (20 * density).toInt()
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        val r = width / 2f
        canvas.drawCircle(r, r, r - ring.strokeWidth / 2, ring)
        if (selected) canvas.drawCircle(r, r, 5 * density, dot)
    }
}

/** A sheet's paper, as inkOS draws it: rounded top, ink on top and sides, open at the bottom. */
class SheetFrame(paper: Int, ink: Int, private val radius: Float, stroke: Float) :
    android.graphics.drawable.Drawable() {

    private val fill = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.FILL
        color = paper
    }

    private val line = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = stroke
        color = ink
    }

    override fun draw(canvas: android.graphics.Canvas) {
        val b = bounds
        val h = line.strokeWidth / 2
        val l = b.left + h
        val t = b.top + h
        val r = b.right - h
        val bottom = b.bottom.toFloat()
        val shape = android.graphics.Path().apply {
            moveTo(l, bottom)
            lineTo(l, t + radius)
            arcTo(android.graphics.RectF(l, t, l + 2 * radius, t + 2 * radius), 180f, 90f, false)
            lineTo(r - radius, t)
            arcTo(android.graphics.RectF(r - 2 * radius, t, r, t + 2 * radius), 270f, 90f, false)
            lineTo(r, bottom)
        }
        canvas.drawPath(android.graphics.Path(shape).apply { close() }, fill)
        canvas.drawPath(shape, line)
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
}
