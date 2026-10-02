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

/** Settings.Secure key the framework reads before vibrating for the keys under the screen. */
private const val KEY_VIBRATION = "kompakt_key_vibration"

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
    private val tabs = ArrayList<View>()
    private val underlines = ArrayList<View>()
    private var current = 0

    private val handler = Handler(Looper.getMainLooper())

    // Two inks, swapped in dark mode. No greys: this panel dithers anything between them.
    private val night by lazy {
        (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
    private val INK by lazy { if (night) Color.WHITE else Color.BLACK }
    private val PAPER by lazy { if (night) Color.BLACK else Color.WHITE }

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
        root.addView(tabBar())

        body = FrameLayout(this)
        root.addView(body, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))

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

    override fun onResume() {
        super.onResume()
        if (current == PAGE_DIAGNOSTICS) refreshDiagnostics()
    }

    // ------------------------------------------------------------------ chrome

    /** The app's own title, since the action bar is gone. */
    private fun titleBar(): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(6))
        }
        row.addView(TextView(this).apply {
            text = getString(R.string.app_name).uppercase()
            textSize = 19f
            setTextColor(INK)
            setTypeface(Typeface.DEFAULT_BOLD)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        return row
    }

    /** Three tabs marked by rule weight, not a fill; the last is the cog icon. */
    private fun tabBar(): View {
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val labels = listOf(
            getString(R.string.tab_display),
            getString(R.string.tab_offline),
            null,
        )
        labels.forEachIndexed { i, label ->
            // Cell = label (or icon) over its own rule.
            val cell = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setOnClickListener { select(i) }
            }
            val head: View
            if (label != null) {
                val tab = TextView(this).apply {
                    text = label
                    textSize = 16f
                    gravity = Gravity.CENTER
                    setTextColor(INK)
                }
                tabs.add(tab)
                head = tab
            } else {
                val cog = ImageView(this).apply {
                    setImageResource(R.drawable.ic_settings)
                    imageTintList = ColorStateList.valueOf(INK)
                    contentDescription = getString(R.string.tab_settings)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setPadding(dp(2), dp(9), dp(2), dp(9))
                }
                tabs.add(cog)
                head = cog
            }
            val underline = View(this).apply { setBackgroundColor(INK) }
            // One fixed height for every head, text or icon: padding alone leaves
            // the icon cell a different height, and then its rule sits off the line.
            cell.addView(head, LinearLayout.LayoutParams(MATCH_PARENT, dp(TAB_HEIGHT)))
            cell.addView(underline, LinearLayout.LayoutParams(MATCH_PARENT, dp(1)))
            underlines.add(underline)
            bar.addView(cell, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        }
        return bar
    }

    /** The tab a page belongs to, or -1 for a tab; this is the whole back stack. */
    private fun parentOf(index: Int): Int = when (index) {
        PAGE_DIAGNOSTICS -> TAB_SETTINGS
        PAGE_BATTERY -> TAB_SETTINGS
        PAGE_APP_MODES -> TAB_DISPLAY
        PAGE_SLEEP -> TAB_DISPLAY
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
        current = index
        // A page marks the tab it came from, so the app never looks tab-less.
        val marked = if (index < tabs.size) index else parentOf(index)
        tabs.forEachIndexed { i, tab ->
            val on = i == marked
            if (tab is TextView) tab.setTypeface(if (on) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
            underlines[i].layoutParams =
                LinearLayout.LayoutParams(MATCH_PARENT, dp(if (on) 3 else 1))
            underlines[i].requestLayout()
        }
        body.removeAllViews()
        val page = PagedColumn(this)
        // Clear of the tab rule, and the same gap at the bottom.
        page.setPadding(0, dp(8), 0, dp(8))
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
                })
                page.add(valueRow(getString(R.string.unlock_refresh_row), unlockRefreshValue()) {
                    unlockRefreshSheet()
                })
                page.add(sectionWithInfo(getString(R.string.temperature_section)) {
                    temperatureNeeds()
                })
                page.add(if (Ours.vendor()) temperatureSteps()
                         else blockedRow(getString(R.string.temperature_row), Needs.VENDOR) {
                             temperatureNeeds()
                         })
                page.add(section(getString(R.string.per_app_section)))
                page.add(valueRow(getString(R.string.display_app_modes), "›") {
                    select(PAGE_APP_MODES)
                })
                page.add(sectionWithInfo(getString(R.string.sleep_section)) { sleepNeeds() })
                page.add(if (SleepImage.available())
                             valueRow(getString(R.string.sleep_row), "›") { select(PAGE_SLEEP) }
                         else blockedRow(getString(R.string.sleep_row), Needs.BOTH) {
                             sleepNeeds()
                         })
            }
            PAGE_SLEEP -> sleepPage().forEach { page.add(it) }
            TAB_SETTINGS -> {
                page.add(section(getString(R.string.width_section)))
                page.add(widthSteps())
                page.add(section(getString(R.string.settings_section)))
                page.add(if (FingerScroll.available())
                             valueRow(getString(R.string.finger_section), "›") { select(PAGE_FINGER) }
                         else blockedRow(getString(R.string.finger_section), Needs.VENDOR) {
                             fingerNeeds()
                         })
                // Read by the framework (PhoneWindowManager): 0 stops the keys under the screen vibrating.
                page.add(toggleRow(getString(R.string.key_vibration),
                    Settings.Secure.getInt(contentResolver, KEY_VIBRATION, 1) != 0) { on ->
                    Settings.Secure.putInt(contentResolver, KEY_VIBRATION, if (on) 1 else 0)
                })
                page.add(valueRow(getString(R.string.battery_section), "›") {
                    select(PAGE_BATTERY)
                })
                page.add(valueRow(getString(R.string.diagnostics_section), "›") {
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
                page.add(lockedRow(getString(R.string.offline_modem)))
                page.add(lockedRow(getString(R.string.offline_mic)))
                page.add(offlineRow(OfflineExtras.Extra.WIFI, R.string.offline_wifi))
                page.add(offlineRow(OfflineExtras.Extra.BLUETOOTH, R.string.offline_bt))
                page.add(offlineRow(OfflineExtras.Extra.CAMERA, R.string.offline_camera))
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
            setPadding(dp(12), dp(2), dp(12), dp(8))
        }
        // Sized in pixels, not dp: a circle has to be exactly as tall as it is
        // wide, and a weighted width would leave it an oval.
        val gap = dp(4)
        val size = ((resources.displayMetrics.widthPixels - dp(24) - gap * (keys.size - 1)) /
            keys.size).coerceAtLeast(dp(44))
        keys.forEachIndexed { i, key ->
            val on = key == chosen
            // A mode this phone cannot do is dashed, and explains itself instead of being picked.
            val usable = Eink.usable(key)
            val cell = FrameLayout(this).apply {
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
            }, FrameLayout.LayoutParams(size / 2, size / 2, Gravity.CENTER))
            val lp = LinearLayout.LayoutParams(size, size)
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
                setPadding(dp(16), dp(6), dp(16), dp(6))
            }
            lines.addView(TextView(this).apply {
                text = Eink.label(key)
                textSize = 15f
                setTextColor(INK)
                setTypeface(Typeface.DEFAULT_BOLD)
            })
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
        "auto" -> R.string.eink_note_auto
        else -> R.string.eink_note_quality
    })

    /** A sheet against the bottom edge: a heading, a note, then one choice row per item. */
    private fun sheet(title: String, note: String, items: List<SheetItem>) {
        val column = sheetColumn(title, note)
        val dialog = showSheet(column)
        items.forEach { item ->
            column.addView(choiceRow(item.label, item.sub, item.selected) {
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
            setPadding(dp(8), 0, dp(8), dp(16))
        }
        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(24), dp(16), dp(8))
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
    private fun offlineRow(extra: OfflineExtras.Extra, label: Int): View =
        toggleRow(getString(label), OfflineExtras.enabled(this, extra)) { on ->
            OfflineExtras.setEnabled(this, extra, on)
        }

    /** Smallest width, in the steps the tile cycles through. */
    private fun widthSteps(): View = chipRow(
        Width.ORDER,
        Prefs.width(this),
        { it.toString() },
    ) { step ->
        Prefs.setWidth(this, step)
        Width.apply(step)
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
            setPadding(dp(16), dp(4), dp(16), dp(4))
        }
        val chips = ArrayList<Pair<T, TextView>>()
        val mark = { picked: T ->
            chips.forEach { (item, chip) ->
                val on = item == picked
                chip.setTypeface(if (on) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
                chip.background = outline(if (on) 3 else 1, BUTTON_RADIUS)
            }
        }
        items.forEachIndexed { i, item ->
            val chip = TextView(this).apply {
                text = label(item)
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(INK)
                setTypeface(if (item == chosen) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
                background = outline(if (item == chosen) 3 else 1, BUTTON_RADIUS)
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
            out.add(dualRow(
                getString(R.string.battery_cycles),
                s.cycles?.takeIf { it >= 0 }?.toString() ?: unknown,
                getString(R.string.battery_health),
                Battery.healthPercent(s)?.let { getString(R.string.battery_percent, it) } ?: unknown))
            out.add(factRow(getString(R.string.battery_capacity),
                if (s.chargeFull != null && s.chargeFullDesign != null)
                    getString(R.string.battery_capacity_pair,
                        s.chargeFull / 1000, s.chargeFullDesign / 1000)
                else unknown))
        } else {
            // The cycle count is the vendor HAL's and holds on either kernel; health
            // and capacity divide by Mudita's 294 mAh and would read about 1000%.
            out.add(factRow(getString(R.string.battery_cycles),
                s.cycles?.takeIf { it >= 0 }?.toString() ?: unknown))
            out.add(blockedRow(getString(R.string.battery_health_title), Needs.KERNEL) {
                healthNeeds()
            })
        }
        out.add(factRow(getString(R.string.battery_first_use), s.firstUse ?: unknown))

        // The write lands on either vendor. Only ours lets the HAL act on it: see
        // docs/Battery.md section 5 in KompaktDevice, the orphaned proc_battery_cmd type.
        val limitNeeds = {
            needsSheet(getString(R.string.battery_limit_section),
                getString(R.string.battery_limit_note), Needs.VENDOR)
        }
        out.add(sectionWithInfo(getString(R.string.battery_limit_section)) { limitNeeds() })
        if (!Ours.vendor()) {
            out.add(blockedRow(getString(R.string.battery_limit_row), Needs.VENDOR) {
                limitNeeds()
            })
        } else if (Battery.writable()) {
            out.add(limitSteps(s.limit))
            out.add(factRow(getString(R.string.battery_now),
                when {
                    Battery.heldAtLimit(s) -> getString(R.string.battery_now_held)
                    s.status == "Charging" -> getString(R.string.battery_now_charging)
                    s.status == "Full" -> getString(R.string.battery_now_full)
                    else -> getString(R.string.battery_now_unplugged)
                }))
            out.add(toggleRow(getString(R.string.battery_hysteresis), s.hysteresis) { on ->
                Battery.setHysteresis(on)
            })
        } else {
            out.add(lockedRow(getString(R.string.battery_limit_unavailable)))
        }
        return out
    }

    /** Two facts on one line, half-width boxes that line up with the rows around them. */
    private fun dualRow(nameA: String, valueA: String, nameB: String, valueB: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16), dp(4), dp(16), dp(4))
        }
        listOf(nameA to valueA, nameB to valueB).forEachIndexed { i, (name, value) ->
            val lp = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            if (i == 0) lp.marginEnd = dp(8)
            row.addView(factBox(name, value), lp)
        }
        return row
    }

    /** A row that only reports. No outline change, no tap target. */
    private fun factRow(name: String, value: String): View = pad(factBox(name, value))

    /** The box itself: the name on the left, the value against the right. */
    private fun factBox(name: String, value: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = outline(2)
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
        row.addView(TextView(this).apply {
            text = name
            textSize = 15f
            maxLines = 1
            setTextColor(INK)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        row.addView(TextView(this).apply {
            text = value
            textSize = 13f
            maxLines = 1
            setTextColor(INK)
            setTypeface(Typeface.DEFAULT_BOLD)
        })
        return row
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
        out.add(toggleRow(getString(R.string.finger_row), on) { checked ->
            FingerScroll.setEnabled(this, checked)
            select(PAGE_FINGER)
        })
        if (!on) return out
        val modes = FingerScroll.cycle(this)
        out.add(section(getString(R.string.finger_swipe_section)))
        out.add(valueRow(getString(R.string.finger_cycle_row),
            resources.getQuantityString(R.plurals.finger_modes_count, modes.size, modes.size)) {
            fingerCycleSheet()
        })
        out.add(section(getString(R.string.finger_gestures_section)))
        out.add(gestureRow(R.string.finger_tap_row, FingerScroll.tapAction(this), Buttons.TAP) {
            FingerScroll.setTapAction(this, it)
        })
        out.add(gestureRow(R.string.finger_triple_row, FingerScroll.tripleAction(this), Buttons.TRIPLE) {
            FingerScroll.setTripleAction(this, it)
        })
        out.add(gestureRow(R.string.finger_hold_row, FingerScroll.holdAction(this), Buttons.HOLD) {
            FingerScroll.setHoldAction(this, it)
        })
        // Natural and Sideways steer scrolling and the D-pad; nothing else reads them.
        if ("swipe" in modes || "dpad" in modes) {
            out.add(section(getString(R.string.finger_scroll_section)))
            out.add(toggleRow(getString(R.string.finger_natural), FingerScroll.natural(this)) {
                FingerScroll.setNatural(this, it)
            })
            out.add(toggleRow(getString(R.string.finger_horizontal), FingerScroll.horizontal(this)) {
                FingerScroll.setHorizontal(this, it)
            })
        }
        return out
    }

    private fun gestureRow(title: Int, action: String, button: Int, onPick: (String) -> Unit) =
        valueRow(getString(title), actionLabel(action, button)) {
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
            setPadding(0, dp(6), dp(12), dp(6))
            minimumHeight = dp(56)
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
        lines.addView(TextView(this).apply {
            text = styleLabel(style)
            textSize = 16f
            setTextColor(INK)
        })
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
                }, emptyList(), framed = false))
        }
        dialog.setOnDismissListener { if (current == TAB_DISPLAY) select(TAB_DISPLAY) }
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
            "torch_warm" -> R.string.finger_act_torch_warm
            "torch_cold" -> R.string.finger_act_torch_cold
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

    /** eink_temperature is root's and read-only on Mudita's vendor; ours adds the store and the chmod. */
    private fun temperatureNeeds() = needsSheet(getString(R.string.temperature_section),
        getString(R.string.temperature_note), Needs.VENDOR)

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
                val value = TextView(this).apply {
                    text = valueOf(pkg)
                    textSize = 13f
                    maxLines = 1
                    setTextColor(INK)
                    setTypeface(Typeface.DEFAULT_BOLD)
                }
                valueRow(name, value) { onPick(pkg, name) { value.text = valueOf(pkg) } }
            }
    }

    /** Mode chooser for one package; invert lives here because it is not a panel mode. */
    private fun pickMode(pkg: String, name: String, refresh: () -> Unit) {
        // The heading says which mode is on, the way the Display heading does --
        // the circles are icons, so without it the sheet never names the choice.
        val heading = sheetLabel(
            getString(R.string.app_mode_title, name, Eink.label(Prefs.appMode(this, pkg))))
        val column = sheetColumnOf(heading, onInfo = { einkInfo(Eink.APP_ORDER) })
        // The circles are redrawn in place when one is tapped, so the sheet stays
        // up and the invert row below it is still reachable.
        val holder = FrameLayout(this)
        fun drawCircles() {
            holder.removeAllViews()
            holder.addView(modeCircles(Eink.APP_ORDER, Prefs.appMode(this, pkg)) { key ->
                Prefs.setAppMode(this, pkg, key)
                heading.text = getString(R.string.app_mode_title,
                    name, Eink.label(key)).uppercase()
                refresh()
                drawCircles()
            })
        }
        drawCircles()
        column.addView(holder)
        column.addView(switchRow(getString(R.string.app_invert_row),
            AppInvert.wanted(this, pkg), { on ->
                AppInvert.setWanted(this, pkg, on)
                refresh()
            }, emptyList(), framed = false))
        val dialog = showSheet(column)
        column.addView(choiceRow(getString(R.string.per_app_default), "", false) {
            Prefs.setAppMode(this, pkg, null)
            refresh()
            dialog.dismiss()
        })
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

    /** What a feature does, in one paragraph under its heading. */
    private fun note(text: String) = TextView(this).apply {
        this.text = text
        textSize = 13f
        setTextColor(INK)
        setPadding(dp(16), 0, dp(16), dp(8))
    }

    /** A section heading in inkOS's shape: bullet, capitals, dashed rule to the margin. */
    private fun section(text: String): View = sectionRow(sectionLabel(text), null)

    /** The heading's own text view, kept when a caller needs to rewrite it. */
    private fun sectionLabel(text: String) = TextView(this).apply {
        this.text = "• " + text.uppercase()
        textSize = 12f
        setTextColor(INK)
        setTypeface(Typeface.DEFAULT_BOLD)
    }

    /** Label, dashed rule out to the margin, and optionally the info glyph. */
    private fun sectionRow(label: TextView, onInfo: (() -> Unit)?): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(6))
        }
        row.addView(label)
        row.addView(DashedRule(this, INK),
            LinearLayout.LayoutParams(0, dp(1), 1f).apply { marginStart = dp(8) })
        if (onInfo != null) {
            row.addView(ImageView(this).apply {
                setImageResource(R.drawable.ic_info)
                imageTintList = ColorStateList.valueOf(INK)
                contentDescription = getString(R.string.eink_info)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setOnClickListener { onInfo() }
            }, LinearLayout.LayoutParams(dp(28), dp(24)).apply { marginStart = dp(8) })
        }
        return row
    }

    /** The same heading, with the info glyph against the right margin. */
    private fun sectionWithInfo(text: String, onInfo: () -> Unit): View =
        sectionRow(sectionLabel(text), onInfo)

    /** One choice in a sheet, as inkOS's SingleChoiceSheet: radio ring, label, whole row the target. */
    private fun choiceRow(label: String, sub: String, selected: Boolean, onClick: () -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setOnClickListener { onClick() }
        }
        row.addView(RadioMark(this, INK, selected))
        val lines = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        lines.addView(TextView(this).apply {
            text = label
            textSize = 18f
            setTextColor(INK)
        })
        if (sub.isNotEmpty()) {
            lines.addView(TextView(this).apply {
                text = sub
                textSize = 13f
                setTextColor(INK)
            })
        }
        row.addView(lines, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
            marginStart = dp(16)
        })
        return row
    }

    /** A fact inside a sheet: name and value, without the page rows' box. */
    private fun sheetFactRow(name: String, value: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(10), dp(16), dp(10))
        }
        row.addView(TextView(this).apply {
            text = name
            textSize = 16f
            setTextColor(INK)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        row.addView(TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(INK)
            setTypeface(Typeface.DEFAULT_BOLD)
        })
        return row
    }

    /** An outlined row with a name on the left and its current value on the right. */
    private fun valueRow(name: String, value: String, onClick: () -> Unit): View =
        valueRow(name, TextView(this).apply {
            text = value
            textSize = 13f
            maxLines = 1
            setTextColor(INK)
            setTypeface(Typeface.DEFAULT_BOLD)
        }, onClick)

    /** The same row, with a value view the caller keeps so it can update it in place. */
    private fun valueRow(name: String, value: TextView, onClick: () -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = outline(2)
            setPadding(dp(16), dp(6), dp(16), dp(6))
            minimumHeight = dp(ROW_HEIGHT)
            setOnClickListener { onClick() }
        }
        row.addView(TextView(this).apply {
            text = name
            textSize = 15f
            maxLines = 1
            setTextColor(INK)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        if (value.text == "›") {
            value.text = ""
            row.addView(ImageView(this).apply {
                setImageResource(R.drawable.ic_chevron)
                imageTintList = ColorStateList.valueOf(INK)
                val s = dp(20)
                layoutParams = LinearLayout.LayoutParams(s, s)
            })
        } else {
            row.addView(value)
        }
        return pad(row)
    }

    /** A toggle row with buttons before the switch; the row itself still toggles. */
    private fun toggleRowWith(label: String, on: Boolean, onChange: (Boolean) -> Unit,
                              vararg buttons: Pair<String, () -> Unit>): View =
        switchRow(label, on, onChange, buttons.toList())

    /** Callers pass the change as a trailing lambda, so it stays the last parameter here. */
    private fun toggleRow(label: String, on: Boolean, onChange: (Boolean) -> Unit): View =
        switchRow(label, on, onChange, emptyList())

    /** Switch at the right, label filling the rest, buttons between; framed = false inside a sheet. */
    private fun switchRow(label: String, on: Boolean, onChange: (Boolean) -> Unit,
                          buttons: List<Pair<String, () -> Unit>>, framed: Boolean = true): View {
        val switch = InkToggle(this, INK).apply {
            preset(on)
            this.onChange = onChange
        }
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            if (framed) background = outline(2)
            setPadding(dp(16), dp(6), dp(12), dp(6))
            if (framed) minimumHeight = dp(ROW_HEIGHT)
            setOnClickListener { switch.toggle() }
        }
        row.addView(TextView(this).apply {
            text = label
            textSize = 15f
            setTextColor(INK)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        buttons.forEach { (text, action) ->
            row.addView(TextView(this).apply {
                this.text = text
                textSize = 13f
                gravity = Gravity.CENTER
                setTextColor(INK)
                setTypeface(Typeface.DEFAULT_BOLD)
                background = outline(2, BUTTON_RADIUS)
                setPadding(dp(10), dp(6), dp(10), dp(6))
                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    marginEnd = dp(6)
                }
                setOnClickListener { action() }
            })
        }
        row.addView(switch)
        return if (framed) pad(row) else row
    }

    /** A switch that cannot be cleared: the thing is always switched off. */
    private fun lockedRow(label: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = outline(2)
            setPadding(dp(16), dp(6), dp(12), dp(6))
            minimumHeight = dp(ROW_HEIGHT)
        }
        row.addView(TextView(this).apply {
            text = label
            textSize = 15f
            setTextColor(INK)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        row.addView(TextView(this).apply {
            text = getString(R.string.offline_always)
            textSize = 13f
            setTextColor(INK)
            setTypeface(Typeface.DEFAULT_BOLD)
            setPadding(0, 0, dp(8), 0)
        })
        // Checked and unreachable: enabled so it draws in ink, not clickable so it stays.
        row.addView(InkToggle(this, INK).apply { preset(true) })
        return pad(row)
    }


    private fun buttonRow(vararg items: Pair<String, () -> Unit>): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16), dp(4), dp(16), dp(4))
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

    /** Side margins and a gap, applied outside the border so rows do not touch. */
    private fun pad(v: View): View {
        val holder = FrameLayout(this).apply {
            setPadding(dp(16), dp(4), dp(16), dp(4))
        }
        holder.addView(v, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        return holder
    }

    /** Paper fill, ink border, rounded; dashed marks a control this phone cannot use. */
    private fun outline(strokeDp: Int = 2, radiusDp: Int = ROW_RADIUS,
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
            column.addView(sheetFactRow(getString(R.string.needs_vendor), getString(when {
                vendorOk -> R.string.needs_installed
                Ours.vendor() -> R.string.needs_older
                else -> R.string.needs_missing
            })))
        }
        if (needs.kernel) {
            column.addView(sheetFactRow(getString(R.string.needs_boot), getString(
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

    /** A feature this phone cannot use: dashed, names what it needs, explains on tap. */
    private fun blockedRow(name: String, needs: Needs, onTap: () -> Unit): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = outline(2, dashed = true)
            setPadding(dp(16), dp(6), dp(12), dp(6))
            minimumHeight = dp(ROW_HEIGHT)
            setOnClickListener { onTap() }
        }
        row.addView(TextView(this).apply {
            text = name
            textSize = 15f
            maxLines = 1
            setTextColor(INK)
        }, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f))
        row.addView(TextView(this).apply {
            text = needsValue(needs)
            textSize = 13f
            maxLines = 1
            setTextColor(INK)
            setTypeface(Typeface.DEFAULT_BOLD)
        })
        row.addView(ImageView(this).apply {
            setImageResource(R.drawable.ic_info)
            imageTintList = ColorStateList.valueOf(INK)
            contentDescription = getString(R.string.eink_info)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }, LinearLayout.LayoutParams(dp(28), dp(24)).apply { marginStart = dp(6) })
        return pad(row)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    /** One choice in a sheet. */
    private class SheetItem(
        val label: String,
        val sub: String,
        val selected: Boolean,
        val action: () -> Unit,
    )

    companion object {

        private const val TAB_DISPLAY = 0
        private const val TAB_OFFLINE = 1
        private const val TAB_SETTINGS = 2

        /** Pages one level below a tab; Back walks up through parentOf. */
        private const val PAGE_DIAGNOSTICS = 3
        private const val PAGE_APP_MODES = 4
        private const val PAGE_BATTERY = 5
        private const val PAGE_SLEEP = 6
        private const val PAGE_FINGER = 7

        private const val REQ_SLEEP_IMAGE = 1

        /** Every tab head is this tall, so the rules under them form one line. */
        private const val TAB_HEIGHT = 42

        /** Every page row is at least this tall, whatever it ends in: switch, value or chevron. */
        private const val ROW_HEIGHT = 52

        /** Corner radii, in dp: rows and boxes, pill-shaped buttons, and the sheet's top edge. */
        private const val ROW_RADIUS = 28
        private const val BOX_RADIUS = 8
        private const val BUTTON_RADIUS = 24
        private const val SHEET_RADIUS = 28
    }
}

class DashedRule(context: Context, private val ink: Int) : View(context) {

    private val paint = android.graphics.Paint().apply {
        color = ink
        strokeWidth = context.resources.displayMetrics.density
        alpha = 217          // inkOS draws it at 0.85
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        val y = height / 2f
        var x = 0f
        while (x < width) {
            canvas.drawLine(x, y, minOf(x + 4f, width.toFloat()), y, paint)
            x += 8f
        }
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
