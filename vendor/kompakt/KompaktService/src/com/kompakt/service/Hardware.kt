package com.kompakt.service

import android.util.Log
import java.io.File
import java.util.concurrent.ConcurrentHashMap
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
        /** How much of the image is dithered, 0 to 255. -1 leaves the preset's own choice. */
        val ditherColors: Int = -1,
        /** enum EInkDitheringType, read only when the amount is above zero. */
        val ditherType: Int = DITHER_FS_OPT1,
        /** TCON timings, see TIMINGS. Defaults to clean: the register persists until rewritten. */
        val timings: String = "clean",
    )

    /** enum EInkDitheringType (Mudita's eink_types.h); 0 turns dithering off. */
    const val DITHER_OFF = 0
    const val DITHER_FS_OPT1 = 4

    /** Auto. Stock meink ignores this name; autoWorks detects that by reading it back. */
    const val AUTO_WAVEFORM = "KAUTO"

    /** Null until auto has been tried once; false on a kernel without it. */
    @Volatile
    var autoWorks: Boolean? = null
        private set

    /** Mudita's modes, in their order, less Auto. */
    val MODES: LinkedHashMap<String, Mode> = linkedMapOf(
        "fast" to Mode("A2", 1, 1, 1.0f, 1.0f, false),
        // Kompakt: soft. Gamma 3 is the 1.25 curve.
        "soft" to Mode("A2", 1, 3, 1.00f, 0.8f, false, 0),
        // 96 dither levels, not 160: chosen on the panel as the point where the
        // diffused dots stop crawling while scrolling.
        "fastdither" to Mode("A2", 1, 1, 1.0f, 1.2f, true, 96, DITHER_FS_OPT1),
        "text" to Mode("GL16", 4, 1, 1.0f, 1.2f, false),
        "quality" to Mode("GL16", 4, 2, 1.0f, 1.0f, false),
        // Kompakt: kernel switches A2 and GL16 as the screen moves; the rest is quality's.
        // Needs our e-ink modules: see autoWorks.
        "auto" to Mode(AUTO_WAVEFORM, 4, 2, 1.0f, 1.0f, false),
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
        "auto" to "Auto",
    )

    /** One icon per mode, so the E-ink tile shows which one it is on. */
    private val ICONS = mapOf(
        "fast" to R.drawable.ic_mode_fast,
        "soft" to R.drawable.ic_mode_soft,
        "fastdither" to R.drawable.ic_mode_dither,
        "text" to R.drawable.ic_mode_text,
        "quality" to R.drawable.ic_mode_quality,
        "auto" to R.drawable.ic_mode_auto,
    )

    /** Modes that need our images: auto needs our e-ink modules. */
    val NEEDS: Map<String, Needs> = mapOf("auto" to Needs.VENDOR)

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
        var m = MODES[key] ?: MODES[DEFAULT] ?: return@onIo
        applied = key

        // Auto only exists in our kernel: probe once per boot, else fall back to quality.
        if (m.waveform == AUTO_WAVEFORM) {
            if (autoWorks == null) {
                write("waveform_mode", AUTO_WAVEFORM)
                autoWorks = Sysfs.read("$E/waveform_mode")?.trim() == AUTO_WAVEFORM
                if (autoWorks != true) {
                    Log.w(Sysfs.TAG, "eink: kernel has no $AUTO_WAVEFORM; using quality")
                }
            }
            if (autoWorks != true) {
                m = MODES["quality"] ?: m
                applied = "quality"
            }
        }
        write("waveform_mode", m.waveform)
        write("pixelbits", m.bpp.toString())
        write("gamma", m.gamma.toString())
        write("brightness", Math.round(m.brightness * 100f).toString())
        write("contrast", Math.round(m.contrast * 10f).toString())
        val colors = if (m.ditherColors >= 0) m.ditherColors else if (m.dither) 96 else 0
        // Never dither_colors: that shim forces FloydSteinbergOpt1 for any value.
        if (m.timings.isNotEmpty() && TIMINGS.containsKey(m.timings)) {
            write("timings_mode", m.timings)
        }
        if (colors <= 0) {
            write("dither_type", DITHER_OFF.toString())
        } else {
            write("dither_param", colors.toString())
            write("dither_type", m.ditherType.toString())
        }
    }

    /** Offset in degrees on the panel's reported temperature. */
    val TEMPS: List<Int> = listOf(0, 5, 10, 20, 30, 40)

    /** eink_temperature, not temperature_offset: same labelling reason as update_mode. */
    fun setTemperature(offset: Int) = Sysfs.onIo {
        write("eink_temperature", offset.toString())
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

/** The notification LED: the MT6370 PMU red, green and blue sinks. */
object Leds {
    private const val DIR = "/sys/class/leds"
    private val COLOURS = listOf("red", "green", "blue")

    /** Hardware modes, chosen by writing the trigger. Writing `none` leaves the mode as it was. */
    private const val BREATH = "breath_mode"
    private const val SOLID = "cc_mode"

    /** The driver applies the enable bits 100 ms after the last brightness write. */
    private const val SETTLE_MS = 250L

    /** Cached because it cannot change, and every notification would re-read it. */
    private val maxCache = ConcurrentHashMap<String, Int>()

    /** max_brightness reads 6 on this device. */
    private const val MAX_FALLBACK = 6

    private fun maxBrightness(colour: String): Int = maxCache.getOrPut(colour) {
        Sysfs.read("$DIR/$colour/max_brightness")?.toIntOrNull()?.takeIf { it > 0 } ?: MAX_FALLBACK
    }

    /** Which channels are lit, and whether they breathe. Null is off. */
    private data class Shown(val r: Boolean, val g: Boolean, val b: Boolean, val breathing: Boolean)

    @Volatile private var shown: Shown? = null

    /** False until the first write: at start the channels hold whatever boot left there. */
    @Volatile private var known = false

    /** A channel is on or off, so it lights when it carries at least half the strongest component. */
    private fun lit(component: Int, strongest: Int): Boolean =
        component > 0 && component * 2 >= strongest

    /** Show a colour Lineage resolved. Timings are ignored: it always breathes. */
    fun apply(argb: Int, onMs: Int, offMs: Int) = colour(argb, breathing = true)

    private fun colour(argb: Int, breathing: Boolean) {
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        val top = maxOf(r, g, b)
        if (top == 0) {
            off()
            return
        }
        show(Shown(lit(r, top), lit(g, top), lit(b, top), breathing))
    }

    fun off() = show(null)

    /** All channels off, then the lit ones on together, so their breaths stay in phase. */
    private fun show(want: Shown?) {
        if (known && want == shown) {
            Log.i(Sysfs.TAG, "leds: $want unchanged, left alone")
            return
        }
        for (c in COLOURS) {
            Sysfs.putBlocking("$DIR/$c/trigger", "none")
            Sysfs.putBlocking("$DIR/$c/brightness", "0")
        }
        shown = want
        known = true
        if (want == null) {
            Log.i(Sysfs.TAG, "leds: off")
            return
        }
        val on = listOf("red" to want.r, "green" to want.g, "blue" to want.b)
            .filter { it.second }
            .map { it.first }
        // The off above has to reach the chip before the channels come back on,
        // or a channel that stays lit is never restarted.
        sleep(SETTLE_MS)
        val mode = if (want.breathing) BREATH else SOLID
        for (c in on) Sysfs.putBlocking("$DIR/$c/trigger", mode)
        for (c in on) Sysfs.putBlocking("$DIR/$c/brightness", maxBrightness(c).toString())
        Log.i(Sysfs.TAG, "leds: ${on.joinToString("+")} $mode")
    }

    private fun sleep(ms: Long) {
        try {
            Thread.sleep(ms)
        } catch (t: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    /** What is there, as the app sees it. */
    fun probe() {
        for (c in COLOURS) {
            Log.i(
                Sysfs.TAG,
                "leds: $c writable=${Sysfs.writable("$DIR/$c/brightness")}/" +
                    "${Sysfs.writable("$DIR/$c/trigger")} " +
                    "brightness=${Sysfs.read("$DIR/$c/brightness")} " +
                    "max=${Sysfs.read("$DIR/$c/max_brightness")} " +
                    "trigger=${Sysfs.read("$DIR/$c/trigger")}",
            )
        }
    }

    /** Every colour the LED can make, solid, then white breathing. */
    private val TEST_COLOURS = listOf(
        "white" to 0xFFFFFF,
        "red" to 0xFF0000,
        "green" to 0x00FF00,
        "blue" to 0x0000FF,
        "yellow" to 0xFFFF00,
        "cyan" to 0x00FFFF,
        "magenta" to 0xFF00FF,
    )

    /** Stop a test. Caller's thread, not Sysfs.onIo: that queue is serial. */
    @Volatile private var testCancel = false

    fun stopTest() {
        testCancel = true
    }

    /** Slept in slices, so a cancel is acted on within one of them, not one step. */
    private fun hold(ms: Long): Boolean {
        var left = ms
        while (left > 0) {
            if (testCancel) return false
            val slice = if (left < CANCEL_SLICE_MS) left else CANCEL_SLICE_MS
            try {
                Thread.sleep(slice)
            } catch (t: InterruptedException) {
                Thread.currentThread().interrupt()
                return false
            }
            left -= slice
        }
        return !testCancel
    }

    private const val CANCEL_SLICE_MS = 100L

    fun selfTest(holdMs: Long = 1200L) {
        testCancel = false
        Log.i(Sysfs.TAG, "leds: self test starting")
        probe()
        for ((name, rgb) in TEST_COLOURS) {
            Log.i(Sysfs.TAG, "leds: -> $name")
            colour(rgb, breathing = false)
            if (!hold(holdMs)) break
        }
        if (!testCancel) {
            Log.i(Sysfs.TAG, "leds: breathing white for 12s")
            colour(0xFFFFFF, breathing = true)
            hold(12_000L)
        }
        off()
        Log.i(Sysfs.TAG, "leds: self test done")
    }
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

    fun set(node: String, on: Boolean) = Sysfs.put(node, if (on) "1" else "0")

    fun isOn(node: String): Boolean = Sysfs.read(node) == "1"

    fun available(node: String): Boolean = Sysfs.writable(node)
}
