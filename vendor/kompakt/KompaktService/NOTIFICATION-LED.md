# The notification LED

Reference for `Hardware.kt` (`object Leds`), `NotifLight.kt` and `LedMigration.kt`.
Read this before changing any of them.

## What the hardware is

Three dies, red, green and blue, behind one window, on the RGB current sinks of
the **MT6370 charger PMU**. The driver is
`drivers/misc/mediatek/pmic/mt6370/mt6370_pmu_rgbled.c`, the same file in our
kernel and Mudita's. `leds-mt6357.c` and `mtk_leds_drv.c` are not what drives
these LEDs; the first is not even built. `/sys/class/leds/{red,green,blue}/`.

What the driver does with each write:

- **Brightness is on or off.** `mt6370_pmu_led_bright_set()` overwrites the value
  with 1 before programming the current (an ODM edit), so every non-zero write
  lights the channel at the same current. `max_brightness` still reads 6. A
  channel therefore shows one of seven colours: red, green, blue, yellow, cyan,
  magenta, white.
- **The enable bits are written together.** A brightness write only schedules
  a work item, 100 ms later, which writes all three channels' enable bits in one
  register write from each channel's stored brightness.
- **Each channel has a hardware mode**, chosen by writing its `trigger`:
  `pwm_mode` (the boot default), `breath_mode` (the PMIC ramps the current by
  itself, with no CPU and no wakelock) and `cc_mode` (constant current, solid).
- **Writing `none` does not change the mode.** Every trigger's `deactivate`
  only removes its sysfs files, so a channel last armed with `breath_mode` goes
  on breathing after `none`. The trigger file cannot tell you what the chip is
  doing. To change the behaviour, write the other mode's trigger.
- **The breath timing is the driver's default**, about four seconds a cycle.
  `tr1 tr2 tf1 tf2 ton toff` appear when `breath_mode` is armed, but they are
  created at that moment, owned by root, 0644 and labelled `sysfs_leds`; the app
  can neither write nor read them. Nothing relies on them.
- `adb shell` cannot read `/sys/class/leds`; the shell domain is denied. The
  app logs what it sees with `Leds.probe()`.

## Who drives it, and why it is not the framework

`NotificationManagerService` must never reach the lights HAL on this device.
Mudita's HAL has no notification-light mapping and throws
`ServiceSpecificException(-7)`, which reached system_server's main looper and
restarted the runtime. Posting a notification, or locking the screen with one
pending, killed the phone.

Fixed at the cause by `patches/kompakt/frameworks_base/0014`, two guards:

1. `config_notificationLedHandledExternally` leaves `mHasLight` true but never
   acquires the light, so `updateLightsLocked()` returns at its first line.
2. `LightsService.setLightUnchecked()` catches `RuntimeException`.
   `ServiceSpecificException` is one and was slipping through the old
   `RemoteException | UnsupportedOperationException`.

The second guard is what keeps a device alive if the first is lost in a rebase,
and it covers the battery light and any future call site too.

**`config_intrusiveNotificationLed` stays `true`.** Setting it false looks like
the obvious fix and is wrong twice: it disables `NOTIFICATION_LIGHT_PULSE`, and
`packages/apps/Settings/res/xml/configure_notification_settings.xml` gates the
"Notification lights" row on it through `requiresConfig`, so Settings would show
no entry at all.

**Why the app and not a lights HAL of our own:** a HAL lives in `/vendor`, and
the system image must also run on Mudita's stock vendor, where ours would not
exist. system_server cannot do it either --
`system/sepolicy/private/coredomain.te` has a
`neverallow { coredomain ... } sysfs_leds:file *` under `full_treble_only()`,
which fails the build. `KompaktService` reaches the nodes because vendor's own
`init.mt6761.rc` chowns them to `system` and vendor sepolicy allows `system_app`.

## Settings come from LineageOS

`NotifLight` calls `LineageNotificationLights.calcLights()` (lineage-sdk,
linked with `static_libs: ["org.lineageos.platform.internal"]`). That gives
per-app colours, defaults, auto-colour from the app icon, DND and the screen-on
gate for free.

Three things it does not cover and we do:

- **Zen mode.** In NMS it arrives via a `ZenModeHelper` callback. From an app it
  must be read from `Settings.Global.ZEN_MODE` and pushed in with `setZenMode()`,
  or `ZEN_ALLOW_LIGHTS` silently never fires.
- **The colour picker's live preview.** It posts an *ongoing* notification, which
  the ongoing filter would drop. Detected with `isForcedOn()` and allowed to
  outrank everything.
- **Per-channel lights.** Drop candidates where `channel.shouldShowLights()` is
  false. That is NMS's own test, and the replacement for the old per-app "none".

`LineageNotificationLights`' constructor calls the `LedUpdater` callback
**synchronously**, before the field holding the instance is assigned. Guard it.

## How a colour is shown

`Leds.apply(argb, onMs, offMs)`:

- **Colour to channels.** A channel lights when its component is at least half
  the strongest one. Lineage's auto colours are often dark (`#0b0d20` from an
  icon): scaling them to levels made every channel 1, i.e. white. The half rule
  keeps the hue: that one is blue.
- **Always a breath.** `breath_mode`, whatever timing Lineage passes, the colour
  picker's preview included: the LED is declared on/off only, LineageParts
  labels every light "Blinking", and the preview should look like the real
  thing. `cc_mode` (solid) is used only by the self test.
- **Every change re-arms all three channels in phase.** All three go to `none`
  and brightness 0, the app waits 250 ms for the driver's enable work to switch
  them off, then writes the mode to each lit channel and switches them on. The
  enable work lands after the last write, so they start breathing at the same
  instant.

Why the last point matters: a channel that stays lit across a colour change is
never restarted, while the newly lit ones start from the beginning of the
cycle. Two or three channels breathing at the same rate but out of phase pass
through every mix of themselves: the light cycles like a rainbow.

## State

One record, `Leds.Shown`: which channels are lit and whether they breathe. The
same value again is left alone, since re-arming restarts the breath and a
System notification re-posts constantly. `known` is false until the first
write, so the first call after the app starts always writes: the channels hold
whatever boot or a previous process left.

## The self test must be cancellable

`adb shell am broadcast -a com.kompakt.service.action.LED_TEST` runs
`Leds.selfTest()`: every colour solid, then white breathing for 12 s, then off.

`Sysfs.onIo` is a **single-threaded** executor. A cancel queued behind a running
test cannot run until the test finishes. `stopTest()` sets a volatile flag from the caller's thread and `hold()`
checks it in 100 ms slices.

## Wakelock

`NotifLight` holds `"kompakt:led-update"` across the handover to the IO thread.
Delivery's own wakelock is gone as soon as the callback returns, and with AOD in
`DOZE_SUSPEND` the SoC can suspend before the queued write runs, leaving the LED
dark until something else wakes it.

## What Lineage's per-app model cannot express

- **No "system" bucket.** Its add-app list is launchable packages only, so
  iconless packages (Android, SystemUI, GMS) cannot be added and there is no
  "everything else" row. They fall to the default colour. The replacement is the
  stock per-channel lights switch, which `NotifLight` honours.
- **No per-app "none".** Colour `0` is a sentinel for "generate one from the
  icon", not "off". Same replacement.

`NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES` format:

    <pkg>=<signedDecimalARGB>;<onMs>;<offMs>|<pkg>=...

A negative `onMs`/`offMs` means "use the default". `LedMigration` carries the old
per-app colours across with `-1;-1` so the colour is preserved without inventing
a timing.
