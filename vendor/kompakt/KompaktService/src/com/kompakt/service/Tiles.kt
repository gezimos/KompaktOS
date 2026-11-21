package com.kompakt.service

import android.app.PendingIntent
import android.content.Intent
import android.app.StatusBarManager
import android.content.Context
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.util.Log
import android.service.quicksettings.TileService

internal const val PREFS = "kompakt"
private const val KEY_MODE = "eink_mode"
private const val KEY_CARD = "sim_card"

object Prefs {
    /** The master mode. */
    fun mode(c: Context): String {
        val v = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_MODE, null)
        return if (v != null && Eink.ORDER.contains(v)) v else Eink.DEFAULT
    }

    /** Which smallest-width step the tile is on. See Width. */
    fun width(c: Context): String {
        val v = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("width", null)
        return if (v != null && Width.STEPS.containsKey(v)) v else Width.DEFAULT
    }

    fun setWidth(c: Context, v: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("width", v).apply()

    /** Once: the old Temperature offset and Fast speed rows become the two speed rows. */
    fun migrateSpeeds(c: Context) {
        val p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (p.contains("fast_steps")) return
        val warm = p.getInt("temp_offset", 11) >= 20
        val old = p.getInt("fast_speed", 0)
        val fast = when {
            old == 5 || old == 6 -> old
            !warm -> Eink.STOCK_FAST            // at +11 the old 9, 8 and 7 all ran 11
            old == 8 || old == 9 -> old
            else -> 7
        }
        p.edit().putInt("fast_steps", fast)
            .putInt("text_steps", if (warm) 28 else Eink.STOCK_TEXT).apply()
    }

    /** Pushes per change in the fast modes, one of Eink.FAST_STEPS. */
    fun fastSteps(c: Context): Int =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("fast_steps", Eink.STOCK_FAST)
            .takeIf { Eink.FAST_STEPS.contains(it) } ?: Eink.STOCK_FAST

    fun setFastSteps(c: Context, v: Int) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("fast_steps", v).apply()

    /** Pushes per change in Text and Quality, a key of Eink.TEXT_STEPS. */
    fun textSteps(c: Context): Int =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("text_steps", Eink.STOCK_TEXT)
            .takeIf { Eink.TEXT_STEPS.containsKey(it) } ?: Eink.STOCK_TEXT

    fun setTextSteps(c: Context, v: Int) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("text_steps", v).apply()

    /** Small updates between full refreshes, one of Eink.CLEAN_STEPS. */
    fun cleanEvery(c: Context): Int =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("clean_every", Eink.CLEAN_STOCK)
            .takeIf { Eink.CLEAN_STEPS.contains(it) } ?: Eink.CLEAN_STOCK

    fun setCleanEvery(c: Context, v: Int) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("clean_every", v).apply()

    /** The dither pattern, see Eink.PATTERNS. */
    fun pattern(c: Context): Int =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("dither_pattern", Eink.STOCK_PATTERN)
            .takeIf { Eink.PATTERNS.containsKey(it) } ?: Eink.STOCK_PATTERN

    fun setPattern(c: Context, v: Int) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("dither_pattern", v).apply()

    /** How far the dithers' greys are lifted, see Eink.LIGHTS. */
    fun ditherLight(c: Context): Int =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("dither_light", Eink.STOCK_LIGHT)

    fun setDitherLight(c: Context, v: Int) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("dither_light", v).apply()

    private fun patternKey(pkg: String) = "app_pattern_" + pkg

    /** The package's own dither pattern, or null while it follows the master one. */
    fun appPatternOwn(c: Context, pkg: String): Int? =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(patternKey(pkg), -1)
            .takeIf { Eink.PATTERNS.containsKey(it) }

    fun appPattern(c: Context, pkg: String): Int = appPatternOwn(c, pkg) ?: pattern(c)


    fun setAppPattern(c: Context, pkg: String, v: Int?) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().apply {
            if (v == null) remove(patternKey(pkg)) else putInt(patternKey(pkg), v)
        }.apply()

    private fun appKey(pkg: String) = "app_mode_" + pkg

    /** The mode for one package: the user's explicit choice if there is one, otherwise the master. */
    fun appMode(c: Context, pkg: String): String {
        val p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val explicit = p.getString(appKey(pkg), null)
        return if (explicit != null && Eink.MODES.containsKey(explicit)) explicit
               else mode(c)
    }

    /** Whether the package has a mode of its own, rather than following the master. */
    fun appModeIsOwn(c: Context, pkg: String): Boolean =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(appKey(pkg), null)
            ?.let { Eink.MODES.containsKey(it) } ?: false

    fun setAppMode(c: Context, pkg: String, v: String?) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().apply {
            if (v == null) remove(appKey(pkg)) else putString(appKey(pkg), v)
        }.apply()

    fun perAppEnabled(c: Context): Boolean =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("per_app", true)

    fun setPerAppEnabled(c: Context, v: Boolean) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("per_app", v).apply()

    fun setMode(c: Context, v: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_MODE, v).apply()

    /* The SIM/eSIM mux, remembered locally. */
    fun card(c: Context): String? =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_CARD, null)

    fun setCard(c: Context, v: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_CARD, v).apply()
}

/** Tap cycles Fast, Fast + dither, Text, Quality. The icon is the mode's own. */
class EinkTileService : TileService() {

    override fun onStartListening() = render(Prefs.mode(this))

    override fun onClick() {
        val cur = Prefs.mode(this)
        // Only the modes this phone can do.
        val modes = Eink.ORDER.filter { Eink.usable(it) }
        val idx = modes.indexOf(cur).coerceAtLeast(0)
        val next = modes[(idx + 1) % modes.size]
        Prefs.setMode(this, next)
        Eink.setMode(next)
        render(next)
        // A mode change repaints with the new waveform but leaves whatever the previous mode ghosted in.
        collapseShade()
        Flash.show(applicationContext)
    }

    private fun render(mode: String) {
        val tile = qsTile ?: return
        tile.label = getString(R.string.tile_eink)
        tile.subtitle = Eink.label(mode)
        tile.icon = Icon.createWithResource(this, Eink.icon(mode))
        // Unavailable rather than silently doing nothing, so a missing sepolicy rule is visible in the UI instead of.
        tile.state = if (Eink.available()) Tile.STATE_ACTIVE else Tile.STATE_UNAVAILABLE
        tile.updateTile()
    }
}

/** One tile per e-ink mode: a tap switches straight to it, and the current mode's tile is on. */
abstract class EinkModeTileService : TileService() {
    abstract val mode: String

    override fun onStartListening() = render()

    override fun onClick() {
        if (!Eink.available() || !Eink.usable(mode)) return
        if (Prefs.mode(this) != mode) {
            Prefs.setMode(this, mode)
            Eink.setMode(mode)
        }
        render()
        // As the E-ink tile: clear what the previous mode ghosted in.
        collapseShade()
        Flash.show(applicationContext)
    }

    private fun render() {
        val tile = qsTile ?: return
        tile.label = Eink.label(mode)
        tile.subtitle = getString(R.string.tile_eink)
        tile.icon = Icon.createWithResource(this, Eink.icon(mode))
        tile.state = when {
            !Eink.available() || !Eink.usable(mode) -> Tile.STATE_UNAVAILABLE
            Prefs.mode(this) == mode -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }
}

class EinkFastTileService : EinkModeTileService() { override val mode = "fast" }
class EinkSoftTileService : EinkModeTileService() { override val mode = "soft" }
class EinkDitherTileService : EinkModeTileService() { override val mode = "fastdither" }
class EinkTextTileService : EinkModeTileService() { override val mode = "text" }
class EinkQualityTileService : EinkModeTileService() { override val mode = "quality" }

/** One tile per flash LED. */
abstract class TorchTileService : TileService() {
    abstract val node: String
    abstract val labelRes: Int

    override fun onStartListening() = render()

    override fun onClick() {
        Torch.set(applicationContext, node, !Torch.isOn(node))
        // The write is queued, so read back rather than assuming it landed.
        Sysfs.onIo { render() }
    }

    private fun render() {
        val tile = qsTile ?: return
        tile.label = getString(labelRes)
        tile.state = when {
            !Torch.available(node) -> Tile.STATE_UNAVAILABLE
            Torch.isOn(node) -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }
}

class WarmTorchTileService : TorchTileService() {
    override val node = Torch.WARM
    override val labelRes = R.string.tile_torch_warm
}

// No ColdTorchTileService.


/** Full-panel refresh as a tile. */
class RefreshTileService : TileService() {
    /** Hand off to RefreshActivity rather than writing clear directly: the flash is what actually removes ghosting. */
    override fun onClick() {
        collapseShade()
        Flash.show(applicationContext)
    }
}

/** The eSIM slot switch, as a tile. */
class ESimTileService : TileService() {
    override fun onStartListening() = render()

    /** Flip the mux and tell the user to reboot. */
    override fun onClick() {
        // Toggle off what we last wrote, not off the read-back. See Prefs.card().
        val current = Prefs.card(this) ?: ESim.state()
        val next = if (current == ESim.ESIM) ESim.SIM else ESim.ESIM
        ESim.select(next)
        Prefs.setCard(this, next)
        Sysfs.onIo {
            render()
            Log.i(Sysfs.TAG, "sim mux -> $next (takes effect on reboot)")
        }
    }

    private fun render() {
        val tile = qsTile ?: return
        val s = ESim.state() ?: Prefs.card(this)
        tile.label = when (s) {
            ESim.ESIM -> "eSIM"
            ESim.SIM -> "SIM"
            else -> "SIM ?"
        }
        tile.subtitle = "reboot to apply"
        tile.state = if (s == ESim.ESIM) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}




/** Close the shade after a tile does its work. */
private fun TileService.collapseShade() {
    try {
        getSystemService(StatusBarManager::class.java)?.collapsePanels()
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "cannot collapse the shade (${t.javaClass.simpleName})")
    }
}

/** Smallest width, in three steps. */
object Width {

    /** label to density. Order is the cycle order. */
    val STEPS: LinkedHashMap<String, Int> = linkedMapOf(
        "360" to 213,
        "400" to 192,
        "440" to 174,
    )

    val ORDER: List<String> = STEPS.keys.toList()

    const val DEFAULT = "360"

    /** Push a density to the window manager. */
    fun apply(step: String) {
        val density = STEPS[step] ?: return
        try {
            val wm = android.view.IWindowManager.Stub.asInterface(
                android.os.ServiceManager.getService(Context.WINDOW_SERVICE))
            wm.setForcedDisplayDensityForUser(
                android.view.Display.DEFAULT_DISPLAY, density, android.os.UserHandle.myUserId())
            Log.i(Sysfs.TAG, "width: $step dp (density $density)")
        } catch (t: Throwable) {
            Log.e(Sysfs.TAG, "width: cannot set density", t)
        }
    }

    /** Drop the forced density altogether: the phone's own again, whoever set one. */
    fun clear() {
        try {
            val wm = android.view.IWindowManager.Stub.asInterface(
                android.os.ServiceManager.getService(Context.WINDOW_SERVICE))
            wm.clearForcedDisplayDensityForUser(
                android.view.Display.DEFAULT_DISPLAY, android.os.UserHandle.myUserId())
            Log.i(Sysfs.TAG, "width: forced density cleared")
        } catch (t: Throwable) {
            Log.e(Sysfs.TAG, "width: cannot clear density", t)
        }
    }
}

/** Cycles smallest width. */
class WidthTileService : TileService() {

    override fun onStartListening() = render(Prefs.width(this))

    override fun onClick() {
        val cur = Prefs.width(this)
        val next = Width.ORDER[(Width.ORDER.indexOf(cur).coerceAtLeast(0) + 1) % Width.ORDER.size]
        Prefs.setWidth(this, next)
        Width.apply(next)
        render(next)
    }

    private fun render(step: String) {
        qsTile?.apply {
            label = getString(R.string.tile_width, step)
            state = if (step == Width.DEFAULT) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
            updateTile()
        }
    }
}
