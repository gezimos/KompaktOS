package com.kompakt.service

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.provider.Settings
import android.util.Log
import java.io.File
import java.util.concurrent.Executors

/** Direct sysfs access. */
object Sysfs {
    const val TAG = "Kompakt"

    private val io = Executors.newSingleThreadExecutor()

    /** Queue a write off the main thread. Tiles call this from onClick. */
    fun put(path: String, value: String) = io.execute { putBlocking(path, value) }

    fun putBlocking(path: String, value: String): Boolean = try {
        File(path).writeText(value)
        true
    } catch (t: Throwable) {
        Log.w(TAG, "write failed: $path = $value (${t.javaClass.simpleName})")
        false
    }

    fun read(path: String): String? = try {
        File(path).readText().trim()
    } catch (t: Throwable) {
        null
    }

    fun writable(path: String): Boolean = File(path).canWrite()

    fun onIo(block: () -> Unit) = io.execute(block)
}

/** Which of our images the phone runs; each probe reads a mechanism, not a version string. */
object Ours {
    /** Our meink_hal has module parameters; Mudita's declares none. */
    fun vendor(): Boolean = File("/sys/module/meink_hal/parameters/partial").exists()

    /** Ours reports about 3000 mAh design capacity; Mudita's divides by ten. */
    fun kernel(): Boolean =
        (Sysfs.read("/sys/class/power_supply/battery/charge_full_design")?.toIntOrNull()
            ?: 0) >= 1_000_000
}

/** Build number of each image, for the Installed images rows; null where none is stamped. */
object Builds {
    /** BUILD_NUMBER via ro.build.version.incremental; a ten digit value is a timestamp, not a build. */
    fun system(): Long? =
        android.os.SystemProperties.get("ro.build.version.incremental", "")
            .takeIf { it.length <= 6 }?.toLongOrNull()

    /** This app's own version, the installed copy (an update in /data wins). */
    fun app(c: android.content.Context): String? = try {
        c.packageManager.getPackageInfo(c.packageName, 0).versionName
    } catch (t: Throwable) {
        null
    }

    /** GApps or Vanilla, from the Lineage version string (23.2-20260926-GAPPS-EXT4-GSI). */
    fun edition(): String? {
        val v = android.os.SystemProperties.get("ro.lineage.version", "")
        return when {
            v.contains("GAPPS") -> "GApps"
            v.contains("VANILLA") -> "Vanilla"
            else -> null
        }
    }

    /** Our kernel: /proc/version reads "(kompakt@...) ... #<build> SMP". */
    fun kernel(): Long? {
        val v = Sysfs.read("/proc/version") ?: return null
        if (!v.contains("(kompakt@")) return null
        return Regex(" #(\\d+) ").find(v)?.groupValues?.get(1)?.toLongOrNull()
    }

    /** The day the running kernel was compiled, "26 Sep", from the end of /proc/version. */
    fun kernelDate(): String? {
        val v = Sysfs.read("/proc/version") ?: return null
        val m = Regex("(\\w{3}) (\\w{3}) +(\\d{1,2}) [\\d:]+ \\w+ (\\d{4})$").find(v) ?: return null
        return "${m.groupValues[3]} ${m.groupValues[2]} ${m.groupValues[4]}"
    }

    /** Our vendor stamps ro.vendor.build.version.incremental as kompakt-258; Mudita's is a date. */
    fun vendor(): Long? =
        Regex("^kompakt-(\\d+)$").find(
            android.os.SystemProperties.get("ro.vendor.build.version.incremental", ""))
            ?.groupValues?.get(1)?.toLongOrNull()
}

/** What a feature needs beyond the system image. */
enum class Needs(val vendor: Boolean, val kernel: Boolean) {
    VENDOR(true, false),
    KERNEL(false, true),
    BOTH(true, true),
}

/** The e-ink panel, via /sys/einkinfo. */
object Eink {
    private const val E = "/sys/einkinfo"

    /** One display preset. */
    data class Mode(
        val waveform: String,
        val bpp: Int,
        val gamma: Int,
        val brightness: Float,
        val contrast: Float,
        val dither: Boolean,
        /** TCON timings, see TIMINGS. Defaults to clean: the register persists until rewritten. */
        val timings: String = "clean",
        /** GL16 leaves unchanged pixels undriven (gu16). */
        val faster: Boolean = false,
    )

    /** enum EInkDitheringType (Mudita's eink_types.h); 0 turns dithering off. */
    const val DITHER_OFF = 0
    const val DITHER_FS_OPT1 = 4

    /** Mudita's modes, in their order, less Auto. */
    val MODES: LinkedHashMap<String, Mode> = linkedMapOf(
        "fast" to Mode("A2", 1, 1, 1.0f, 1.0f, false),
        // Kompakt: soft. Gamma 3 is the 1.25 curve.
        "soft" to Mode("A2", 1, 3, 1.00f, 0.8f, false),
        "fastdither" to Mode("A2", 1, 1, 1.0f, 1.2f, true),
        "text" to Mode("GL16", 4, 1, 1.0f, 1.2f, false, faster = true),
        "quality" to Mode("GL16", 4, 2, 1.0f, 1.0f, false, faster = true),
    )

    /** TCON timing sets; keys are the strings the driver's decoder accepts. */
    val TIMINGS: LinkedHashMap<String, String> = linkedMapOf(
        "clean" to "Clean",
        "text" to "High contrast",
        "fast" to "Fast",
    )

    /** What an app with no entry of its own gets. */
    const val DEFAULT = "fastdither"

    /** Every mode, device and per app alike. No custom dials: they can leave the panel black. */
    val ORDER: List<String> = MODES.keys.toList()

    val APP_ORDER: List<String> = ORDER

    private val LABELS = mapOf(
        "fast" to "Fast",
        "soft" to "Fast, soft",
        "fastdither" to "Fast + dither",
        "text" to "Text",
        "quality" to "Quality",
    )

    /** One icon per mode, so the E-ink tile shows which one it is on. */
    private val ICONS = mapOf(
        "fast" to R.drawable.ic_mode_fast,
        "soft" to R.drawable.ic_mode_soft,
        "fastdither" to R.drawable.ic_mode_dither,
        "text" to R.drawable.ic_mode_text,
        "quality" to R.drawable.ic_mode_quality,
    )

    /** Modes that need our images; none since Auto was dropped. */
    val NEEDS: Map<String, Needs> = emptyMap()

    /** Whether a mode does what it says on this phone, rather than falling back. */
    fun usable(key: String): Boolean = NEEDS[key]?.let { !it.vendor || Ours.vendor() } ?: true

    fun label(key: String): String = LABELS[key] ?: key

    fun icon(key: String): Int = ICONS[key] ?: R.drawable.ic_eink

    fun available(): Boolean = Sysfs.writable("$E/waveform_mode")

    /** The last mode actually written, so a caller can skip a redundant re-apply. */
    @Volatile
    var applied: String? = null
        private set

    /** Apply a preset. */
    fun setMode(key: String) = Sysfs.onIo {
        val m = MODES[key] ?: MODES[DEFAULT] ?: return@onIo
        applied = key
        write("waveform_mode", m.waveform)
        write("pixelbits", m.bpp.toString())
        write("gamma", m.gamma.toString())
        write("brightness", Math.round(m.brightness * 100f).toString())
        write("contrast", Math.round(m.contrast * 10f).toString())
        if (m.timings.isNotEmpty() && TIMINGS.containsKey(m.timings)) {
            write("timings_mode", m.timings)
        }
        // The type sets Mudita's own cut-off (160); dither_param is not written.
        write("dither_type", (if (m.dither) DITHER_FS_OPT1 else DITHER_OFF).toString())
        setFaster(m.faster)
    }

    /** Our modules only; each gu16 write uploads the waveform again, so only on a change. */
    private const val GU16 = "kompakt_wf_gu16"
    private const val CLEAN_EVERY = "/sys/module/meink_hal/parameters/partial_full_every"
    const val CLEAN_STOCK = 24

    /** Small updates before meink redraws the whole frame; the slider's stops. */
    val CLEAN_STEPS: List<Int> = listOf(6, 12, 24, 48)

    /** A meink_hal module parameter, back to 24 at every boot, so BootReceiver calls this. */
    fun setCleanEvery(n: Int) {
        if (!Ours.vendor()) return
        Sysfs.onIo { Sysfs.putBlocking(CLEAN_EVERY, "$n\n") }
    }

    @Volatile
    private var faster: Boolean? = null

    private fun setFaster(on: Boolean) {
        if (faster == on || !Ours.vendor()) return
        write(GU16, if (on) "1" else "0")
        faster = on
    }


    /** Pushes per change in the fast modes; 11 is Mudita's, its table at the +11 offset. */
    val FAST_STEPS: List<Int> = listOf(11, 9, 8, 7, 6, 5)
    const val STOCK_FAST = 11

    /** Pushes per change in Text and Quality, and the temperature offset that picks each from the panel's table. */
    val TEXT_STEPS: LinkedHashMap<Int, Int> = linkedMapOf(46 to 7, 38 to 11, 33 to 13, 28 to 18)
    const val STOCK_TEXT = 38

    /** For kompakt_wf_a2frames: 0, the table's own count, when both rows are on their defaults. */
    private fun a2frames(fast: Int, text: Int): Int =
        if (fast == STOCK_FAST && text == STOCK_TEXT) 0 else fast

    /** Both speed rows. The offset goes first: the fast count uploads the waveform, which reads it. */
    fun setSpeeds(fast: Int, text: Int) = Sysfs.onIo {
        write("eink_temperature", (TEXT_STEPS[text] ?: TEXT_STEPS.getValue(STOCK_TEXT)).toString())
        write("kompakt_wf_a2frames", a2frames(fast, text).toString())
        // meink reads the offset only when it loads a waveform, so load the current one again.
        val m = MODES[applied ?: return@onIo] ?: return@onIo
        write("waveform_mode", m.waveform)
    }

    /**
     * For Flash, on the IO thread: in a Fast mode set below 7 pushes, A2 at 7 (true) or back to
     * the user's speed (false), so the flush's black is real black. Returns whether it changed anything.
     */
    fun flushSpeed(c: Context, on: Boolean): Boolean {
        val fast = Prefs.fastSteps(c)
        if (!Ours.vendor() || fast >= 7) return false
        // Boost only in a Fast mode; the way back always runs, the mode may have changed meanwhile.
        if (on && MODES[applied ?: ""]?.waveform != "A2") return false
        write("kompakt_wf_a2frames", (if (on) 7 else a2frames(fast, Prefs.textSteps(c))).toString())
        return true
    }

    /** How dithering places its dots: 0 Mudita's diffusion, 1 an 8x8 grid, 3 diffusion along the grid, 4 blue noise. */
    val PATTERNS: LinkedHashMap<Int, String> = linkedMapOf(0 to "Normal", 1 to "Pattern", 3 to "Hybrid", 4 to "Noise")
    const val STOCK_PATTERN = 0

    /** The last pattern written, so an app switch skips an unchanged one: each write repaints. */
    @Volatile
    var appliedPattern: Int? = null
        private set

    fun setPattern(p: Int) {
        appliedPattern = p
        Sysfs.onIo { write("kompakt_wf_dither", p.toString()) }
    }

    /** Dither brightness: 0 is the mode's own curve, 3 the lightest; Pattern, Hybrid and Noise only. */
    val LIGHTS: List<Int> = listOf(0, 1, 2, 3)
    const val STOCK_LIGHT = 0

    fun setDitherLight(v: Int) = Sysfs.onIo {
        if (Ours.vendor()) write("kompakt_wf_dither_light", v.toString())
    }

    /** Whether a mode shows a pattern: Fast + dither. */
    fun dithers(key: String): Boolean {
        val m = MODES[key] ?: return false
        return m.dither
    }

    /** The pattern for the app in front, its own while its mode dithers; null is the lock screen. */
    fun usePattern(c: Context, pkg: String?) {
        if (!Ours.vendor()) return
        val p = if (pkg != null && Prefs.perAppEnabled(c) && dithers(Prefs.appMode(c, pkg)))
            Prefs.appPattern(c, pkg) else Prefs.pattern(c)
        if (p != appliedPattern) setPattern(p)
    }

    /** Full-panel flush. A2 is 1bpp and never fully settles, so ghosting accumulates. */
    fun clear() = Sysfs.put("$E/clear", "1")

    /** Newline terminated, which is how MuditaService writes every one of these. */
    private fun write(node: String, value: String) = Sysfs.putBlocking("$E/$node", value + "\n")
}

/** Kompakt: battery history and charge limit. See docs/Battery.md. */
object Battery {
    private const val B = "/sys/kernel/battery_monitor"
    private const val PSY = "/sys/class/power_supply/battery"

    val LIMITS = listOf(80, 85, 90, 95, 100)

    data class State(
        val cycles: Int?,
        val firstUse: String?,
        val chargeFull: Int?,
        val chargeFullDesign: Int?,
        val limit: Int?,
        val hysteresis: Boolean,
        val status: String?,
        val level: Int?,
    )

    fun read(): State = State(
        cycles = num("$B/cycle_counter"),
        firstUse = Sysfs.read("$B/first_use_date")
            ?.takeIf { it.isNotEmpty() }
            ?.substringBefore(' '),
        chargeFull = num("$PSY/charge_full"),
        chargeFullDesign = num("$PSY/charge_full_design"),
        limit = num("$B/charge_limit"),
        hysteresis = num("$B/hysteresis_enable") == 1,
        status = Sysfs.read("$PSY/status"),
        level = num("$PSY/capacity"),
    )

    /** Plugged in but the HAL cut charging. "Discharging" means unplugged. */
    fun heldAtLimit(s: State): Boolean = s.status == "Not charging"

    fun setLimit(percent: Int) = Sysfs.put("$B/charge_limit", percent.toString())

    fun setHysteresis(on: Boolean) =
        Sysfs.put("$B/hysteresis_enable", if (on) "1" else "0")

    fun writable(): Boolean = File("$B/charge_limit").canWrite()

    /** Null when either figure is missing or the ratio is out of range. */
    fun healthPercent(s: State): Int? {
        val full = s.chargeFull ?: return null
        val design = s.chargeFullDesign ?: return null
        if (design <= 0 || full <= 0) return null
        val pct = (full.toLong() * 100 / design).toInt()
        return if (pct in 1..100) pct else null
    }

    private fun num(path: String): Int? = Sysfs.read(path)?.trim()?.toIntOrNull()
}

/** Kompakt: The hardware offline switch. */
object OfflineSwitch {
    private const val STATE = "/sys/bus/platform/drivers/pmic-codec-accdet/irqkey_state"

    /** True when the switch is engaged, i.e. the phone is physically offline. */
    fun engaged(): Boolean = Sysfs.read(STATE) == "1"

    fun readable(): Boolean = File(STATE).canRead()
}

/** Kompakt: The eSIM slot switch. */
object ESim {
    /** Kompakt: The SIM / eSIM mux. */
    private const val NODE = "/sys/devices/virtual/nvrom_class/nvrom_write/nvrom_write"

    const val SIM = "sim"
    const val ESIM = "esim"

    fun available(): Boolean = Sysfs.writable(NODE)

    /** SIM, ESIM, or null. The node reads back as "****esim***" / "****sim***". */
    fun state(): String? = Sysfs.read(NODE)?.let {
        when {
            it.startsWith("****esim***") -> ESIM
            it.startsWith("****sim***") -> SIM
            else -> null
        }
    }

    /** Select a card. */
    fun select(card: String) = Sysfs.onIo { Sysfs.putBlocking(NODE, card) }

    /** The same write, blocking, for callers that have to act on the result. */
    fun selectBlocking(card: String): Boolean = Sysfs.putBlocking(NODE, card)
}

/** Kompakt: The two flash LEDs. */
object Torch {
    private const val B = "/sys/devices/platform/11016000.i2c5/i2c-5/5-0034"
    const val WARM = "$B/mt6370_pmu_fled.0/rt-flash-led.0/flashlight/mt-flash-led1/flashlight_led1_onoff"
    const val COLD = "$B/mt6370_pmu_fled.1/rt-flash-led.1/flashlight/mt-flash-led2/flashlight_led2_onoff"

    /** The warm LED's state, for SystemUI's lock screen shortcut, which cannot read the LED. */
    const val WARM_SETTING = "kompakt_torch_warm"
    const val ACTION_TOGGLE_WARM = "com.kompakt.service.action.TORCH_WARM"

    /** Switches an LED, then publishes the warm one's state, so every switch keeps the shortcut right. */
    fun set(c: Context, node: String, on: Boolean) = Sysfs.onIo {
        Sysfs.putBlocking(node, if (on) "1" else "0")
        if (node == WARM) publish(c)
    }

    fun publish(c: Context) {
        Settings.Global.putInt(c.applicationContext.contentResolver, WARM_SETTING, if (isOn(WARM)) 1 else 0)
    }

    fun isOn(node: String): Boolean = Sysfs.read(node) == "1"

    fun available(node: String): Boolean = Sysfs.writable(node)

    const val ACTION_TOGGLE_BOTH = "com.kompakt.service.action.TORCH_BOTH"

    /** Both LEDs as one: either on turns both off. Cold goes through the camera torch, so the system flashlight follows. */
    fun toggleBoth(c: Context) = Sysfs.onIo {
        val cr = c.applicationContext.contentResolver
        val coldOn = Settings.Secure.getInt(cr, Settings.Secure.FLASHLIGHT_ENABLED, 0) == 1 || isOn(COLD)
        val on = !(isOn(WARM) || coldOn)
        Sysfs.putBlocking(WARM, if (on) "1" else "0")
        publish(c)
        try {
            val cm = c.getSystemService(CameraManager::class.java) ?: error("no camera service")
            val id = cm.cameraIdList.firstOrNull { id ->
                val ch = cm.getCameraCharacteristics(id)
                ch.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                    ch.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            } ?: error("no rear torch")
            cm.setTorchMode(id, on)
        } catch (t: Throwable) {
            Log.w("KompaktTorch", "camera torch failed (${t.message}), writing the LED")
            Sysfs.putBlocking(COLD, if (on) "1" else "0")
        }
        // A cold light switched on through sysfs is unknown to the camera torch, so off writes the LED too.
        if (!on) Sysfs.putBlocking(COLD, "0")
    }
}
