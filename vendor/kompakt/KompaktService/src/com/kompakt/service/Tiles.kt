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
private const val KEY_PENDING_ESIM = "pending_esim"

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

    /** Temperature offset for testing how far each mode can be pushed. */
    fun temperature(c: Context): Int =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("temp_offset", 0)

    fun setTemperature(c: Context, v: Int) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("temp_offset", v).apply()

    private fun appKey(pkg: String) = "app_mode_" + pkg

    /** The mode for one package: the user's explicit choice if there is one, otherwise the master. */
    fun appMode(c: Context, pkg: String): String {
        val p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val explicit = p.getString(appKey(pkg), null)
        return if (explicit != null && Eink.MODES.containsKey(explicit)) explicit
               else mode(c)
    }

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

    /* Set when we write the mux to esim, cleared once the eSIM add flow has been opened after the reboot that. */
    fun pendingEsim(c: Context): Boolean =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PENDING_ESIM, false)

    fun setPendingEsim(c: Context, v: Boolean) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_PENDING_ESIM, v).apply()
}

/** Tap cycles Fast, Fast + dither, Text, Quality. The icon is the mode's own. */
class EinkTileService : TileService() {

    override fun onStartListening() = render(Prefs.mode(this))

    override fun onClick() {
        val cur = Prefs.mode(this)
        // Only the modes this phone can do: on Mudita's vendor, auto would just be quality again.
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

/** One tile per flash LED. */
abstract class TorchTileService : TileService() {
    abstract val node: String
    abstract val labelRes: Int

    override fun onStartListening() = render()

    override fun onClick() {
        Torch.set(node, !Torch.isOn(node))
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
