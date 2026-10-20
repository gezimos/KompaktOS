# The notification LED

The Kompakt app does not drive the LED. LineageOS's own notification light
does, through a standard lights HAL, with nothing of ours in between. What is
left here is `LedMigration.kt` (carries pre-Lineage per-app colours into
LineageOS's settings, once) and the read-only LED rows on the Diagnostics page.

## The chain

- **Settings**: LineageParts' Notification light page: per-app colours,
  default colour, pulse length and speed, brightness, DND and screen-on rules.
  `config_deviceLightCapabilities` is 41 (RGB 1, pulsing 8, HAL brightness 32;
  lineage_sdk 0002 and the static overlay).
- **Framework**: `NotificationAttentionHelper` with `LineageNotificationLights`
  picks the colour and the on/off times and calls `LightsService`, which sends
  the brightness level as the colour's alpha. `config_intrusiveNotificationLed`
  stays true (Settings gates the row on it);
  `config_notificationLedHandledExternally` is false.
- **HAL** (vendor image): LineageOS's `android.hardware.light-service`, built
  for this vendor as `android.hardware.light-service.kompakt` (KompaktOS patch
  hardware_lineage_interfaces 0001) and installed over MediaTek's binary by
  KompaktDevice's `apply-lights-hal.sh`. It scales each channel to
  `max_brightness` and uses the timed (QCOM LPG) nodes when they exist.
- **Kernel** (boot image): `mt6370_pmu_rgbled.c`, KompaktDevice kernel patch
  0027. The red, green and blue sinks take levels 1 to 6 (4 to 24 mA), and
  carry `blink start_idx duty_pcts pause_lo pause_hi ramp_step_ms`. blink=1
  turns the pattern into the PMIC's breath timing and breathes with no CPU and
  no wakelock; blink=0 is a solid light (constant current). Vendor init chowns
  the nodes to system.

## Behaviour worth knowing

- **Colours mix.** Each channel has six levels, so orange and the other
  in-between colours show as themselves. A dark app colour comes out dim, and a
  channel below about a sixth of full is off in a solid light.
- **Channels breathe in phase.** The HAL writes brightness 0 to every channel
  before a new pattern, which disables it at once; the driver's shared enable
  work then switches all lit channels on in one register write. A channel lit
  across a colour change therefore restarts with the others. (Breathing out of
  phase is the "rainbow".)
- **Timing is quantised.** Rise, fall and hold are 125 ms + 200 ms steps, the
  pause 250 ms + 400 ms steps, nearest value. A 500 ms on time is about
  250 ms up, 125 ms held, 250 ms down.
- **Brightness costs battery.** Full brightness is 24 mA per channel; the old
  fixed level was 4 mA. The brightness slider sets it.

## On Mudita's stock images

- Stock vendor: MediaTek's HAL throws `ServiceSpecificException(-7)`.
  frameworks_base 0019 catches `RuntimeException` in `LightsService`, so this
  costs a log line, not system_server. The LED stays dark.
- Kernel without 0027: the HAL finds no timed nodes and falls back to the
  `timer` trigger (our kernels since 0020) or to a solid light at one level.

## Why system_server cannot write sysfs itself

`system/sepolicy/private/coredomain.te` has
`neverallow { coredomain ... } sysfs_leds:file *` under `full_treble_only()`.
The HAL runs in the vendor domain `mtk_hal_light`, which is in
`hal_light_server`, and the platform policy lets `hal_light` read and write
`sysfs_leds`.
