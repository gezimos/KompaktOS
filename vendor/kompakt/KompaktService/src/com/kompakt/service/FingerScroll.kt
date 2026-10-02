package com.kompakt.service

import android.app.KeyguardManager
import android.app.StatusBarManager
import android.content.Context
import android.hardware.input.InputManager
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/** Fingerprint sensor navigation, decoded from the HAL's "Redir" zone values in logcat. */
object FingerScroll {
    private const val TAG = "KompaktFinger"
    /** The event-log tag written for every power key down and up. */
    private const val POWER_TAG = "intercept_power"
    private const val INI = "/vendor/etc/fpsensor_nav.ini"
    private const val KEY_ON = "finger_scroll"
    private const val KEY_NATURAL = "finger_scroll_natural"
    private const val KEY_HORIZONTAL = "finger_scroll_horizontal"

    /** Short swipe length as a share of the screen; paged apps turn a page per threshold passed. */
    private const val SHORT_SHARE = 0.15f

    /** Swipe modes, user ordered; Switch mode cycles the enabled ones, the first is each wake's default. */
    val STYLES = listOf("swipe", "dpad", "brightness", "keys", "off")
    /** The mode swipes are in now. */
    private const val KEY_STYLE = "finger_scroll_style"
    /** Legacy default, read once to seed the order. */
    private const val KEY_HOME = "finger_scroll_home"
    private const val KEY_CYCLE = "finger_mode_cycle"
    /** Every mode, comma separated, in the user's order. */
    private const val KEY_ORDER = "finger_mode_order"
    /** Enabled until chosen otherwise: every mode but Keys. */
    private val DEFAULT_CYCLE = STYLES - "keys"

    /** What a double tap, a triple tap, or a finger held still, on the sensor does. */
    val ACTIONS = listOf("off", "mode", "enter", "recents", "refresh", "shade", "qs",
        "torch_warm", "torch_cold", "orient", "key")

    private const val CLICK_MS = 50L
    private const val KEY_TAP = "finger_double_tap"
    private const val KEY_TRIPLE = "finger_triple_tap"
    private const val KEY_HOLD = "finger_hold"
    /** From the end of one tap to the start of the next. */
    private const val DOUBLE_TAP_MS = 500L
    /** Wait for a third tap when a triple tap is set; past the window to cover log delay. */
    private const val THIRD_WAIT_MS = DOUBLE_TAP_MS + 150L

    /** Our vendor ships the ini that turns the HAL's navigation loop on. */
    fun available(): Boolean = File(INI).exists()

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    fun enabled(c: Context) = prefs(c).getBoolean(KEY_ON, false)
    /** As on a trackpad: the content follows the finger. Off turns it around. */
    fun natural(c: Context) = prefs(c).getBoolean(KEY_NATURAL, true)
    /** Sideways instead of up and down: page turns in readers. */
    fun horizontal(c: Context) = prefs(c).getBoolean(KEY_HORIZONTAL, false)

    fun setEnabled(c: Context, on: Boolean) {
        val edit = prefs(c).edit().putBoolean(KEY_ON, on)
        // Fix the double tap's default the first time the switch is set, so a
        // later change of the switch cannot change what it does.
        if (!prefs(c).contains(KEY_TAP)) edit.putString(KEY_TAP, tapAction(c))
        edit.apply()
        sync(c)
    }
    fun setNatural(c: Context, on: Boolean) = prefs(c).edit().putBoolean(KEY_NATURAL, on).apply()
    fun setHorizontal(c: Context, on: Boolean) =
        prefs(c).edit().putBoolean(KEY_HORIZONTAL, on).apply()
    fun style(c: Context): String =
        prefs(c).getString(KEY_STYLE, null)?.takeIf { it in STYLES } ?: home(c)
    private fun setStyle(c: Context, v: String) = prefs(c).edit().putString(KEY_STYLE, v).apply()

    /** Every mode in the user's order; until one is saved, the legacy mode comes first. */
    fun order(c: Context): List<String> {
        val saved = prefs(c).getString(KEY_ORDER, null)?.split(',')?.filter { it in STYLES }
        if (saved != null) return saved.distinct() + (STYLES - saved.toSet())
        val first = (prefs(c).getString(KEY_HOME, null) ?: prefs(c).getString(KEY_STYLE, null))
            ?.takeIf { it in STYLES }
        return if (first == null) STYLES else listOf(first) + (STYLES - first)
    }

    fun setOrder(c: Context, modes: List<String>) =
        prefs(c).edit().putString(KEY_ORDER, modes.joinToString(",")).apply()

    private fun enabledSet(c: Context): Set<String> =
        prefs(c).getStringSet(KEY_CYCLE, null)?.filter { it in STYLES }?.toSet()
            ?.takeIf { it.isNotEmpty() } ?: DEFAULT_CYCLE.toSet()

    /** The modes switched on, in the user's order; never none. */
    fun cycle(c: Context): List<String> {
        val on = enabledSet(c)
        return order(c).filter { it in on }
    }

    fun isOn(c: Context, style: String) = style in enabledSet(c)

    /** False, and nothing saved, if it would leave no mode on. */
    fun setInCycle(c: Context, style: String, on: Boolean): Boolean {
        val next = enabledSet(c).toMutableSet().apply { if (on) add(style) else remove(style) }
        if (next.isEmpty()) return false
        prefs(c).edit().putStringSet(KEY_CYCLE, next).apply()
        return true
    }

    /** The default: the first mode that is on. */
    fun home(c: Context): String = cycle(c).first()

    /** Swipes go back to the default, as when the screen wakes. */
    fun goHome(c: Context) = setStyle(c, home(c))

    private fun action(c: Context, key: String, default: String): String {
        val v = prefs(c).getString(key, null) ?: return default
        // Legacy per-gesture mode names map to Switch mode.
        if (v.startsWith("to:")) return "mode"
        return v.takeIf { it in ACTIONS } ?: default
    }

    /** Defaults: double tap refreshes (switches mode for existing users), triple switches mode, hold is Enter. */
    fun tapAction(c: Context) = action(c, KEY_TAP, if (prefs(c).contains(KEY_ON)) "mode" else "refresh")
    fun setTapAction(c: Context, v: String) = prefs(c).edit().putString(KEY_TAP, v).apply()
    fun tripleAction(c: Context) = action(c, KEY_TRIPLE, "mode")
    fun setTripleAction(c: Context, v: String) = prefs(c).edit().putString(KEY_TRIPLE, v).apply()
    fun holdAction(c: Context) = action(c, KEY_HOLD, "enter")
    fun setHoldAction(c: Context, v: String) = prefs(c).edit().putString(KEY_HOLD, v).apply()

    @Volatile private var reader: Process? = null

    /** Read while the screen is on and the feature is; nothing otherwise. */
    @Synchronized
    fun sync(c: Context) {
        val ctx = c.applicationContext
        val screenOn = ctx.getSystemService(PowerManager::class.java)?.isInteractive == true
        if (enabled(ctx) && available() && screenOn) {
            start(ctx)
        } else {
            stop()
            // A held key must not outlive the reader that would have released it.
            inject.execute { holdEnd(ctx) }
            // Each wake starts in the default mode.
            if (!screenOn) setStyle(ctx, home(ctx))
        }
    }

    @Synchronized
    private fun start(ctx: Context) {
        if (reader?.isAlive == true) return
        val p = try {
            // -T 1 never replays an old swipe; monotonic stamps time the TA's reads.
            // The events buffer carries intercept_power for the power key.
            ProcessBuilder("logcat", "-b", "main,events", "-v", "monotonic", "-T", "1",
                "-s", "Redir:I", "$POWER_TAG:I")
                .redirectErrorStream(true)
                .start()
        } catch (t: Throwable) {
            Log.e(TAG, "cannot start logcat", t)
            return
        }
        reader = p
        thread(name = "finger-scroll", isDaemon = true) {
            // The reader only decodes; injecting takes a swipe's length of real
            // time, and on this thread it would hold back the next readings.
            val decoder = Decoder(
                onStep = { towardHigh, strength ->
                    inject.execute { onStep(ctx, towardHigh, strength) }
                },
                onDoubleTap = { inject.execute { doubleTap(ctx) } },
                onTripleTap = { inject.execute { tripleTap(ctx) } },
                onNextTouch = { inject.execute { if (doubleWaiting != null) nextTouchDown = true } },
                onNextTouchDone = { inject.execute { if (doubleWaiting != null) fireDouble(ctx) } },
                onHold = { inject.execute { holdStart(ctx) } },
                onHoldEnd = { inject.execute { holdEnd(ctx) } },
            )
            try {
                p.inputStream.bufferedReader().forEachLine { decoder.feed(it) }
            } catch (t: Throwable) {
                // Destroyed by stop(), or logcat died; sync() starts a new one.
            }
            inject.execute { holdEnd(ctx) }
        }
        Log.i(TAG, "reading the sensor")
    }

    @Synchronized
    fun stop() {
        reader?.destroy()
        reader = null
    }

    private val inject = Executors.newSingleThreadScheduledExecutor()

    // Only touched on the inject thread.
    /** A double tap held back while a third tap may still come. */
    private var doubleWaiting: ScheduledFuture<*>? = null
    /** A touch began inside the window: the wait runs out without firing, the touch decides. */
    private var nextTouchDown = false

    private fun doubleTap(ctx: Context) {
        if (tripleAction(ctx) == "off") {
            act(ctx, tapAction(ctx), Buttons.TAP)
            return
        }
        nextTouchDown = false
        doubleWaiting = inject.schedule({ if (!nextTouchDown) fireDouble(ctx) },
            THIRD_WAIT_MS, TimeUnit.MILLISECONDS)
    }

    private fun fireDouble(ctx: Context) {
        doubleWaiting?.cancel(false)
        doubleWaiting = null
        act(ctx, tapAction(ctx), Buttons.TAP)
    }

    /** Only after a double tap that waited for it; one already done is not undone. */
    private fun tripleTap(ctx: Context) {
        val waiting = doubleWaiting ?: return
        waiting.cancel(false)
        doubleWaiting = null
        act(ctx, tripleAction(ctx), Buttons.TRIPLE)
    }

    // Only touched on the inject thread.
    private var lastStepAt = 0L
    private var lastTowardHigh = false
    private var streak = 0
    /** Steps closer than this in the same direction accelerate; each adds this much. */
    private const val STREAK_MS = 700L
    private const val STREAK_MAX = 3

    /** [strength] 0 to 1: under a half a fixed short step, from a half up a long one scaled by it. */
    private fun onStep(ctx: Context, towardHigh: Boolean, strength: Float) {
        val long = strength >= 0.5f
        val more = if (long) (strength - 0.5f) * 2f else 0f
        // Acceleration: quick long swipes the same way do more; a pause, turn or short swipe resets it.
        val at = SystemClock.uptimeMillis()
        streak = if (long && at - lastStepAt <= STREAK_MS && towardHigh == lastTowardHigh)
            minOf(streak + 1, STREAK_MAX) else 0
        lastStepAt = at
        lastTowardHigh = towardHigh
        if (streak > 0) Log.i(TAG, "  accelerated: swipe %d in a row".format(streak + 1))
        // The HAL authenticates rather than navigates on the lockscreen; this is belt and braces.
        if (ctx.getSystemService(KeyguardManager::class.java)?.isKeyguardLocked == true) return
        val style = style(ctx)
        if (style == "off") return
        if (style == "keys") {
            // One click a swipe, short or long: Key Mapper binds the key, not a count.
            // Up is towards zone 0, as for the light below.
            Buttons.click(if (towardHigh) Buttons.SWIPE_DOWN else Buttons.SWIPE_UP)
            return
        }
        if (style == "brightness") {
            // Up (towards zone 0) brightens; Natural and Sideways do not apply.
            val steps = streak +
                if (long) LONG_KEYS_MIN + Math.round(more * (LONG_KEYS_MAX - LONG_KEYS_MIN)) else 1
            FrontLight.step(ctx, if (towardHigh) -steps else steps)
            return
        }
        // Top is taken as towards zone 0 (not settled); Natural turns it round.
        val forward = !towardHigh == natural(ctx)
        val sideways = horizontal(ctx)
        if (style == "dpad") {
            val code = when {
                sideways && forward -> KeyEvent.KEYCODE_DPAD_RIGHT
                sideways -> KeyEvent.KEYCODE_DPAD_LEFT
                forward -> KeyEvent.KEYCODE_DPAD_DOWN
                else -> KeyEvent.KEYCODE_DPAD_UP
            }
            val keys = streak +
                if (long) LONG_KEYS_MIN + Math.round(more * (LONG_KEYS_MAX - LONG_KEYS_MIN)) else 1
            repeat(keys) { i ->
                if (i > 0) Thread.sleep(KEY_GAP_MS)
                key(ctx, code)
            }
        } else {
            scroll(ctx, forward, sideways, long, more)
        }
    }

    /** One of ACTIONS, for a tap gesture or a hold; button is the one "key" presses for it. */
    private fun act(ctx: Context, action: String, button: Int) {
        if (ctx.getSystemService(KeyguardManager::class.java)?.isKeyguardLocked == true) return
        when (action) {
            // The confirm key: presses whatever holds focus, the element the D-pad is on.
            "enter" -> key(ctx, KeyEvent.KEYCODE_DPAD_CENTER)
            "recents" -> key(ctx, KeyEvent.KEYCODE_APP_SWITCH)
            "key" -> Buttons.click(button)
            "refresh" -> Flash.show(ctx)
            "torch_warm" -> Torch.set(Torch.WARM, !Torch.isOn(Torch.WARM))
            "torch_cold" -> Torch.set(Torch.COLD, !Torch.isOn(Torch.COLD))
            // Switch the scrolling itself, from wherever the phone is.
            "orient" -> {
                val sideways = !horizontal(ctx)
                setHorizontal(ctx, sideways)
                say(ctx, if (sideways) R.string.finger_now_sideways else R.string.finger_now_vertical)
            }
            "mode" -> {
                // Through the enabled modes; from any other, the first of them.
                val modes = cycle(ctx)
                val next = modes[(modes.indexOf(style(ctx)) + 1) % modes.size]
                setStyle(ctx, next)
                say(ctx, when (next) {
                    "dpad" -> R.string.finger_now_dpad
                    "brightness" -> R.string.finger_now_brightness
                    "keys" -> R.string.finger_now_keys
                    "off" -> R.string.finger_now_off
                    else -> R.string.finger_now_swipe
                })
            }
            "shade", "qs" -> try {
                val bar = ctx.getSystemService(StatusBarManager::class.java)
                // The notification shade toggles; quick settings opens, and the
                // same gesture again (or Back) closes it.
                if (action == "shade") bar?.togglePanel()
                else if (qsOpen(ctx)) bar?.collapsePanels() else bar?.expandSettingsPanel()
            } catch (t: Throwable) {
                Log.w(TAG, "$action failed (${t.javaClass.simpleName})")
            }
        }
    }

    /** A switch made with the sensor happens out of sight of the settings, so say it. */
    private fun say(ctx: Context, text: Int) = Handler(Looper.getMainLooper()).post {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show()
    }

    // Only touched on the inject thread.
    private var qsOpenedAt = 0L

    /** Whether we opened quick settings in the last 30 s; the shade state itself is not readable here. */
    private fun qsOpen(ctx: Context): Boolean {
        val now = SystemClock.uptimeMillis()
        val open = qsOpenedAt != 0L && now - qsOpenedAt < 30_000L
        qsOpenedAt = if (open) 0L else now
        return open
    }

    /** A click: down, then up a moment later, as a real button sends it. */
    private fun key(ctx: Context, code: Int) {
        val down = SystemClock.uptimeMillis()
        if (!sendKey(ctx, code, KeyEvent.ACTION_DOWN, down)) return
        SystemClock.sleep(CLICK_MS)
        sendKey(ctx, code, KeyEvent.ACTION_UP, down)
    }

    private fun sendKey(ctx: Context, code: Int, action: Int, downTime: Long): Boolean {
        val im = ctx.getSystemService(InputManager::class.java) ?: return false
        val ev = KeyEvent(downTime, SystemClock.uptimeMillis(), action, code, 0, 0,
            KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD)
        return try {
            im.injectInputEvent(ev, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)
            true
        } catch (t: Throwable) {
            Log.w(TAG, "key injection failed (${t.javaClass.simpleName})")
            false
        }
    }

    /** A hold is keeping Button 2 down. Touched on the inject thread only. */
    private var heldKeyDown = false

    /** A hold set to "key" holds Button 2 until the finger lifts; other actions run once. */
    private fun holdStart(ctx: Context) {
        if (holdAction(ctx) != "key") {
            act(ctx, holdAction(ctx), Buttons.HOLD)
            return
        }
        if (ctx.getSystemService(KeyguardManager::class.java)?.isKeyguardLocked == true) return
        Buttons.down(Buttons.HOLD)
        heldKeyDown = true
    }

    private fun holdEnd(ctx: Context) {
        if (!heldKeyDown) return
        heldKeyDown = false
        Buttons.up(Buttons.HOLD)
    }

    /** An injected touchscreen swipe through the middle of the screen, as a thumb makes. */
    private fun scroll(ctx: Context, forward: Boolean, sideways: Boolean, long: Boolean, more: Float) {
        val im = ctx.getSystemService(InputManager::class.java) ?: return
        val dm = ctx.resources.displayMetrics
        val span = if (sideways) dm.widthPixels else dm.heightPixels
        // Line length follows the swipe's strength, not its speed.
        val factor = (if (long) LONG_FACTOR_MIN + more * (LONG_FACTOR_MAX - LONG_FACTOR_MIN) else 1f) *
            (1f + SWIPE_STREAK_GAIN * streak)
        val len = minOf(SHORT_SHARE * factor, 0.8f) * span
        val swipeMs = SWIPE_MS
        val mid = span / 2f
        // Showing what is further on is a finger moving up the screen, or to the left.
        val from = if (forward) mid + len / 2 else mid - len / 2
        val to = if (forward) mid - len / 2 else mid + len / 2
        val down = SystemClock.uptimeMillis()
        val onward = if (to > from) 1f else -1f
        fun tail(i: Int) = to + onward * TAIL_PX_S * (TAIL_MS * i / TAIL_STEPS) / 1000f
        fun send(action: Int, t: Long, along: Float): Boolean {
            val x = if (sideways) along else dm.widthPixels / 2f
            val y = if (sideways) dm.heightPixels / 2f else along
            val ev = MotionEvent.obtain(down, t, action, x, y, 0)
            ev.source = InputDevice.SOURCE_TOUCHSCREEN
            return try {
                im.injectInputEvent(ev, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)
            } catch (t: Throwable) {
                Log.w(TAG, "swipe injection failed (${t.javaClass.simpleName})")
                false
            } finally {
                ev.recycle()
            }
        }
        if (!send(MotionEvent.ACTION_DOWN, down, from)) {
            // A swipe cut off before its UP leaves a stuck finger that refuses every DOWN;
            // a CANCEL clears it.
            send(MotionEvent.ACTION_CANCEL, down, from)
            if (!send(MotionEvent.ACTION_DOWN, down, from)) return
        }
        try {
            for (i in 1..SWIPE_STEPS) {
                // Real time between the moves too: some views read the clock, not the event.
                Thread.sleep(swipeMs / SWIPE_STEPS)
                val along = from + (to - from) * i / SWIPE_STEPS
                send(MotionEvent.ACTION_MOVE, down + swipeMs * i / SWIPE_STEPS, along)
            }
            // Slow tail before the lift, not a stop: some launchers only take a fling.
            for (i in 1..TAIL_STEPS) {
                Thread.sleep(TAIL_MS / TAIL_STEPS)
                send(MotionEvent.ACTION_MOVE, down + swipeMs + TAIL_MS * i / TAIL_STEPS, tail(i))
            }
        } finally {
            send(MotionEvent.ACTION_UP, down + swipeMs + TAIL_MS, tail(TAIL_STEPS))
        }
    }

    /** The line drawn for a swipe, and the slow tail it lifts from. */
    private const val SWIPE_MS = 200L
    private const val TAIL_MS = 120L
    private const val TAIL_STEPS = 4
    private const val TAIL_PX_S = 150f
    /** A long swipe: 2.8 to 3.2 times as far, by strength. */
    private const val LONG_FACTOR_MIN = 2.8f
    private const val LONG_FACTOR_MAX = 3.2f
    /** Each long swipe in a row adds this much to the line; the D-pad adds a key. */
    private const val SWIPE_STREAK_GAIN = 0.25f
    private const val SWIPE_STEPS = 10
    /** Arrow presses for a long swipe, and the gap between them. */
    private const val LONG_KEYS_MIN = 2
    private const val LONG_KEYS_MAX = 4
    private const val KEY_GAP_MS = 40L

    /** The sensor read as a capacitive slider: the centroid of 12 zone values, 0 to 11. */
    class Decoder(
        private val onStep: (towardHigh: Boolean, strength: Float) -> Unit,
        private val onDoubleTap: () -> Unit,
        /** A tap soon after a double tap: a triple, if the double waited for it. */
        private val onTripleTap: () -> Unit,
        /** A touch soon after a double tap, then its outcome if it was not the third tap. */
        private val onNextTouch: () -> Unit,
        private val onNextTouchDone: () -> Unit,
        private val onHold: () -> Unit,
        private val onHoldEnd: () -> Unit,
    ) {
        private var touching = false
        /** A hold fired during this touch: its lift is the button's release. */
        private var holding = false
        private var navigating = false
        private var armed = false
        private var stepped = false
        private var touchStart = 0L
        private var lastTapEnd = 0L
        private var lastTouchEnd = 0L
        private var lastDoubleEnd = 0L
        /** This touch began inside the window after a double tap. */
        private var followUp = false
        /** An end crossing waiting for the next reading, or the lift, to confirm it. */
        private var pending = 0
        private var crossing = 0f
        private var deepest = 0f
        /** Readings that moved at least MOVE from the one before. */
        private var moves = 0
        private val times = ArrayList<Long>()
        private val places = ArrayList<Float>()
        /** The sensor is the power key: a touch overlapping a press counts for nothing. */
        private var powerHeld = false
        private var ignored = false

        fun feed(line: String) {
            if (line.contains(POWER_TAG)) {
                if (line.contains("ACTION_DOWN")) {
                    powerHeld = true
                    ignored = true
                    pending = 0
                    lastTapEnd = 0
                    lastDoubleEnd = 0
                    Log.i(TAG, "power key down: ignoring the sensor")
                } else if (line.contains("ACTION_UP")) {
                    powerHeld = false
                }
                return
            }
            val now = stamp(line) ?: return
            if (line.contains("finger has leaved")) {
                release(now)
                return
            }
            if (line.contains("device_get_relative_coords")) {
                if (touching) navigating = true
                return
            }
            val at = line.indexOf(VALUES)
            if (at < 0) return
            val parts = line.substring(at + VALUES.length).trim().split(' ')
            if (parts.size < ZONES) return
            var total = 0
            var moment = 0f
            for (i in 0 until ZONES) {
                val v = (parts[i].removePrefix("0x").toIntOrNull(16) ?: return) - BASE
                if (v > 0) {
                    total += v
                    moment += i * v
                }
            }
            if (total < MIN_TOTAL) {
                release(now)
                return
            }
            if (!touching) {
                touching = true
                navigating = false
                armed = false
                stepped = false
                pending = 0
                moves = 0
                touchStart = now
                times.clear()
                places.clear()
                ignored = powerHeld
                followUp = lastDoubleEnd != 0L && now - lastDoubleEnd <= DOUBLE_TAP_MS
                lastDoubleEnd = 0
                if (followUp) onNextTouch()
            }
            if (ignored) return
            val c = moment / total
            if (places.isNotEmpty() && Math.abs(c - places.last()) >= MOVE) moves++
            times.add(now)
            places.add(c)
            // A finger resting still: fire while it is still down, once, and
            // let nothing else come of this touch.
            if (!stepped && pending == 0 && navigating && now - touchStart >= HOLD_MS) {
                var lo = places[0]
                var hi = places[0]
                for (p in places) { lo = minOf(lo, p); hi = maxOf(hi, p) }
                if (hi - lo < HOLD_STILL) {
                    stepped = true
                    lastTapEnd = 0
                    Log.i(TAG, "hold at %.1f".format(places[0]))
                    holding = true
                    settleFollowUp()
                    onHold()
                    return
                }
            }
            if (pending != 0) {
                val dir = pending
                // A finger wobbling back from an end is not a step; one still
                // past it, or gone (release), is.
                val still = (dir < 0 && c <= END_LOW + CONFIRM) || (dir > 0 && c >= END_HIGH - CONFIRM)
                if (!still) {
                    pending = 0
                } else {
                    if (dir > 0) deepest = maxOf(deepest, c)
                    when {
                        // A tap lifting off an end moves in its last two
                        // readings only; wait for the lift to tell them apart.
                        moves < MOVES_TO_FIRE -> Unit
                        // Low-end exits are measured by speed, known already.
                        dir < 0 -> { pending = 0; step(dir, strength(dir), "") }
                        // High-end ones by how deep they go, known at the lift,
                        // or now if as deep as any went.
                        deepest >= DEEP_MAX -> { pending = 0; step(dir, 1f, "went deep") }
                    }
                    return
                }
            }
            when {
                c in MIDDLE_LOW..MIDDLE_HIGH -> armed = true
                armed && navigating && (c <= END_LOW || c >= END_HIGH) -> {
                    armed = false
                    pending = if (c <= END_LOW) -1 else 1
                    crossing = speed()
                    deepest = c
                }
            }
        }

        private fun step(dir: Int, strength: Float, why: String) {
            stepped = true
            lastTapEnd = 0
            Log.i(TAG, "step %s %s %.2f: speed %.0f, started %.1f, reached %.1f %s".format(
                if (dir > 0) "high" else "low", if (strength >= 0.5f) "long" else "short",
                strength, crossing, places.firstOrNull() ?: 0f,
                if (dir > 0) places.maxOrNull() ?: 0f else places.minOrNull() ?: 0f, why))
            settleFollowUp()
            onStep(dir > 0, strength)
        }

        /** The touch after a double tap was not its third tap. */
        private fun settleFollowUp() {
            if (!followUp) return
            followUp = false
            onNextTouchDone()
        }

        /** 0 to 1, a half between short and long: low-end by speed, high-end by depth. */
        private fun strength(dir: Int): Float = if (dir < 0) {
            if (crossing < LONG_SPEED) 0.5f * crossing / LONG_SPEED
            else 0.5f + 0.5f * minOf(1f, (crossing - LONG_SPEED) / (FASTEST - LONG_SPEED))
        } else {
            if (deepest < DEEP_HIGH) 0.5f * maxOf(0f, deepest - END_HIGH) / (DEEP_HIGH - END_HIGH)
            else 0.5f + 0.5f * minOf(1f, (deepest - DEEP_HIGH) / (DEEP_MAX - DEEP_HIGH))
        }

        /** Zones a second over the last three readings: 4 to 55 for a short swipe, 87 to 126 for a long one. */
        private fun speed(): Float {
            val n = places.size
            val j = maxOf(0, n - 4)
            val dt = maxOf(1L, times[n - 1] - times[j])
            return Math.abs(places[n - 1] - places[j]) * 1000f / dt
        }

        /** A short swipe too slow to reach an end: a steady slide off the middle. */
        private fun slide(): Int {
            val n = places.size
            if (n < 3) return 0
            val dir = if (places[n - 1] > places[n - 2]) 1 else -1
            var k = n - 1
            var pauses = 0
            while (k > 0) {
                val move = (places[k] - places[k - 1]) * dir
                if (move > 0.02f) k-- else if (move >= -0.05f && pauses == 0) { pauses++; k-- } else break
            }
            val from = places[k]
            val far = (places[n - 1] - from) * dir
            val took = times[n - 1] - times[k]
            val fromMiddle = from in (MIDDLE_LOW - 0.6f)..(MIDDLE_HIGH + 0.6f)
            return if (far >= SLIDE_MIN && took >= SLIDE_MS && fromMiddle) dir else 0
        }

        private fun release(now: Long) {
            if (!touching) return
            touching = false
            lift(now)
            settleFollowUp()
        }

        private fun lift(now: Long) {
            if (holding) {
                holding = false
                Log.i(TAG, "hold released")
                onHoldEnd()
            }
            val restedBefore = touchStart - lastTouchEnd
            lastTouchEnd = now
            if (ignored) {
                ignored = powerHeld
                pending = 0
                lastTapEnd = 0
                Log.i(TAG, "touch ignored: the power key was pressed")
                return
            }
            if (!navigating) {
                pending = 0
                lastTapEnd = 0
                return
            }
            // A tap first: it can end past an end threshold as it lifts.
            if (!stepped && now - touchStart <= TAP_MAX_MS && tapShaped()) {
                pending = 0
                when {
                    followUp -> {
                        followUp = false
                        lastTapEnd = 0
                        Log.i(TAG, "triple tap")
                        onTripleTap()
                    }
                    lastTapEnd != 0L && touchStart - lastTapEnd <= DOUBLE_TAP_MS -> {
                        lastTapEnd = 0
                        lastDoubleEnd = now
                        Log.i(TAG, "double tap")
                        onDoubleTap()
                    }
                    // Mid-scroll a stray tap can follow a swipe closely; a real
                    // double tap starts from rest.
                    restedBefore >= REST_MS -> {
                        lastTapEnd = now
                        Log.i(TAG, "tap at %.1f".format(places.first()))
                    }
                    else -> {
                        lastTapEnd = 0
                        Log.i(TAG, "tap at %.1f ignored: only %d ms after the last touch".format(
                            places.first(), restedBefore))
                    }
                }
                return
            }
            if (pending != 0) {
                val dir = pending
                pending = 0
                step(dir, strength(dir), "at the lift")
                return
            }
            if (stepped) {
                lastTapEnd = 0
                return
            }
            lastTapEnd = 0
            val slid = slide()
            if (slid != 0) {
                crossing = 0f
                step(slid, 0f, "slid off slowly")
                return
            }
            // A quick swipe from just outside the middle never armed; still a swipe.
            val ran = places.last() - places.first()
            val pastEnd = places.last() >= END_HIGH + 0.4f || places.last() <= END_LOW - 0.2f
            if (Math.abs(ran) >= RAN_MIN && pastEnd) {
                crossing = 0f
                step(if (ran > 0) 1 else -1, 0f, "ran off an end")
                return
            }
            val held = now - touchStart
            Log.i(TAG, "touch did nothing: %d ms, %d readings, %.1f to %.1f, %s".format(
                held, places.size, places.minOrNull() ?: 0f, places.maxOrNull() ?: 0f,
                when {
                    held > TAP_MAX_MS -> "too long for a tap"
                    places.last() <= FLICK_LOW -> "lifted off the low end"
                    else -> "moved too much for a tap, too little for a swipe"
                }))
        }

        /** A tap sits still where it lands, then lifts quickly in two readings at most; a far lift is a flick. */
        private fun tapShaped(): Boolean {
            val n = places.size
            var k = 0
            var lo = places[0]
            var hi = places[0]
            while (k + 1 < n && maxOf(hi, places[k + 1]) - minOf(lo, places[k + 1]) < TAP_STILL) {
                k++
                lo = minOf(lo, places[k])
                hi = maxOf(hi, places[k])
            }
            var sum = 0f
            for (i in 0..k) sum += places[i]
            val sat = sum / (k + 1)
            val dir = if (places[n - 1] >= sat) 1 else -1
            var advances = 0
            for (i in k + 1 until n) if ((places[i] - places[i - 1]) * dir >= ADVANCE) advances++
            val lift = times[n - 1] - times[k]
            return advances <= TAP_ADVANCES && lift <= TAP_LIFT_MS &&
                places[n - 1] > FLICK_LOW && Math.abs(places[n - 1] - sat) < FLICK_AWAY
        }

        private companion object {
            const val VALUES = "finger_detect_value[12]:"
            const val ZONES = 12
            /** An uncovered zone reads from 0x4b0, a covered one up to 0x3600, a partly covered one between. */
            const val BASE = 0x600
            /** Below this in all, nothing is on the sensor. */
            const val MIN_TOTAL = 6000
            /** A resting finger sits at 4.3 to 6.3; the band that arms a step. */
            const val MIDDLE_LOW = 4.0f
            const val MIDDLE_HIGH = 6.2f
            const val END_LOW = 2.5f
            const val END_HIGH = 6.6f
            /** A reading this far from the last is movement. */
            const val MOVE = 0.3f
            /** How far a tap may wander before its lift, and how low its lift may reach. */
            const val TAP_STILL = 0.6f
            const val FLICK_LOW = 2.8f
            /** A lift this far from where the tap sat is a flick. */
            const val FLICK_AWAY = 2.8f
            /** A tap's lift: at most this many readings advancing this much, within this long. */
            const val ADVANCE = 0.15f
            const val TAP_ADVANCES = 2
            const val TAP_LIFT_MS = 45L
            /** A hold: still for this long. The duration is what rejects palm grips. */
            const val HOLD_MS = 1200L
            const val HOLD_STILL = 0.8f
            /** Movement in this many readings first: under it, it may be a tap lifting. */
            const val MOVES_TO_FIRE = 3
            const val TAP_MAX_MS = 250L
            /** Untouched for this long before the first tap of a double. */
            const val REST_MS = 300L
            /** How far back from an end a wobble may come and still confirm it. */
            const val CONFIRM = 0.2f
            /** Low-end exits at or above this crossing speed are long. */
            const val LONG_SPEED = 75f
            /** High-end exits are slow either way, so depth decides: at or past this, long. */
            const val DEEP_HIGH = 8.6f
            /** The fastest and deepest long swipes measured: full strength. */
            const val FASTEST = 130f
            const val DEEP_MAX = 9.6f
            /** A slow short swipe: this far from the middle, over at least this long. */
            const val SLIDE_MIN = 1.8f
            const val SLIDE_MS = 80L
            /** An unarmed touch that ran this far to past an end is still a swipe. */
            const val RAN_MIN = 1.8f

            /** logcat -v monotonic: seconds since boot, to the millisecond, first on the line. */
            fun stamp(line: String): Long? {
                val s = line.trimStart()
                val end = s.indexOf(' ')
                if (end <= 0) return null
                return s.substring(0, end).toDoubleOrNull()?.let { (it * 1000).toLong() }
            }
        }
    }
}
