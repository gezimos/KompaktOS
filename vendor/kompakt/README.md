# vendor/kompakt

Ours outright, copied into the tree as source: KompaktService, the overlays, the
boot animation, the charging screen and the build settings in `kompakt.mk`. The
comments in these files are short titles; the reasons behind them are here.

## Build settings (`kompakt.mk`)

- **Release version**: About phone shows `BP4A.251205.006.<KOMPAKT_VERSION>`.
  The build number stays in `ro.build.version.incremental`, which Diagnostics
  reads and which must never repeat.
- **Product identity**: without `PRODUCT_BRAND`, `MANUFACTURER` and `MODEL` the
  GSI declares itself google/generic_arm64. phh's securize copies only the vendor
  fingerprint over the system one, an inconsistency Play flags as uncertified, so
  securize is disabled through `/metadata/securize_disable`
  (`etc/init/kompakt-securize.rc`). `PRODUCT_DEVICE` stays generic_arm64: it
  selects the device config.
- **Plain product variables**: `PRODUCT_BUILD_PROP_OVERRIDES` accepts only keys in
  soong's product_config.json and fails with `Key "PRODUCT_BRAND" isn't a valid
  prop override`.
- **Securize marker order**: rw-system.sh reads the marker at post-fs, and init
  parses the directory alphabetically, so `kompakt-` must sort before `vndk-`.
- **Boot animation**: LineageOS's is a 480x160 strip at 60 fps that the panel
  smears into grey; ours is its frames redrawn black on white at 10 fps around the
  boot logo. Mudita's vendor sets `debug.sf.nobootanimation=1`; product
  properties load after the vendor's, so our `=0` turns it back on.
- **Calls over 4G**: the three `persist.dbg` properties are what PHH Settings'
  "Force the presence of 4G Calling setting" writes. They make the 4G Calling
  switch appear for carriers the carrier config does not list. The user still
  installs MediaTek's IMS app from PHH Settings.

## Build script (`build.sh`)

- **Device manifest**: device/phh/treble, treble_app and the vendor overlays come
  from MisterZtr's treble_manifest, used unforked.
- **OpenEUICC**: cloned recursively (its submodules carry lpac and cJSON), pinned,
  patched, with the prebuilt AAR copied in, since upstream resolves it through
  Gradle.
- **prebuilts/ outside vendor/**: everything under vendor/kompakt is copied
  wholesale; a second copy of the OpenEUICC prebuilts' Android.bp would make
  soong fail with the module already defined.
- **Brand rewrite**: phh's generate.sh writes `PRODUCT_BRAND` and `PRODUCT_MODEL`
  after the ROM inherit, so build.sh rewrites the generated makefiles.
  `PRODUCT_SYSTEM_*` is the family that shows on a system-only GSI.

## KompaktService

- **versionCode** must rise every build: Android replaces an installed /data copy
  only when the system copy's versionCode is higher.
- **Static lineage platform**: LineageNotificationLights is linked statically, as
  LineageParts and Settings do; there is no shared library declaration for
  org.lineageos.platform.
- **BootReceiver**: installing a new APK kills the process and nothing else
  restarts it; the watcher is the only reader of the offline switch and the
  fingerprint sensor.

### Detecting our images (`Hardware.kt`)

- Each probe reads the mechanism a feature depends on, not a version string. Our
  `meink_hal` has module parameters; our kernel reports about 3000 mAh design
  capacity where Mudita's divides by ten (294 mAh).
- `ro.build.version.incremental` holds `BUILD_NUMBER`; a ten digit value is a
  timestamp from a build without one. Our kernel's /proc/version reads
  `(kompakt@...) ... #<build> SMP`; our vendor stamps
  `ro.vendor.build.version.incremental` as `kompakt-<build>`.

### E-ink (`Hardware.kt`, `EinkAppWatcher.kt`)

- **Auto mode detection**: stock meink does not know the `KAUTO` waveform name
  and ignores the write, so reading `waveform_mode` back tells the kernels apart
  without a dedicated node. On failure the mode falls back to Quality.
- **Gamma**: enum EInkGammaCorrectionLevel: -1 is 1.00, then 0 to 6 are 0.25,
  0.50, 0.75, 1.25, 1.50, 2.00, 2.20.
- **Dithering**: EInkDitheringType comes from Mudita's `eink_types.h`; 0 turns
  dithering off and only FloydSteinbergOpt1 (4) is used. Never write
  `dither_colors`: it forces FloydSteinbergOpt1 for any value 1 to 255. Use
  `dither_param` and `dither_type`.
- **TCON timings**: `timings_mode` is a panel register, not per-app state; it
  keeps the last written value, so every mode writes it.
- **No custom dials**: a bad set can leave the panel all black with nothing on
  screen to undo it.
- **Skip unchanged modes**: writing `waveform_mode` re-sends the LUT and forces a
  full repaint, so switching between apps that share a mode must not write it.

### Sleep image (`SleepImage.kt`)

- An array inside meink.ko, exposed by our meink_loader as
  `/sys/einkinfo/sleep_image`. It comes back from the vendor partition on every
  boot, so BootReceiver writes ours again.
- 480x800, 4 bytes a pixel, R = G = B with 0xff last, stored rotated 180 degrees.
- Fitted on white, the panel's rest state; greyed with luma weights and
  Floyd-Steinberg to 16 levels, since smooth 8 bit grey posterises into bands.
- Written in one open from offset 0 to the end; the kernel replaces the picture
  only when the last page arrives.
- The picker uses ACTION_OPEN_DOCUMENT: the photo picker behind GET_CONTENT
  serves a cached copy, so a replaced image comes back as the old one.

### Fingerprint gestures (`FingerScroll.kt`)

- **Signal**: the sensor is a strip of 12 zones read by the Chipone TA. With our
  vendor the HAL runs its navigation loop every 12 ms (`fpsensor_nav.ini`, all
  key codes 0) and logs the per-zone values under "Redir"; the TA's own dx/dy is
  too noisy. Only lines with `device_get_relative_coords` count, which excludes
  enrolment and authentication.
- **logcat**: `-T 1` so an old swipe is never replayed; `-v monotonic` so speeds
  come from when the TA read. uid system is exempt from logd's consent prompt.
- **Power key is the sensor**: a touch overlapping a power press counts for
  nothing.
- **Decoder**: a fingertip covers the whole strip, so the centroid moves only as
  the finger slides off an end. A step fires when it leaves the middle band and
  passes an end threshold; returning to the middle re-arms it.
- **Short vs long**: low-end exits by crossing speed, high-end exits by depth,
  since high-end exits are slow either way.
- **Taps and holds**: a tap sits still and lifts within about 45 ms; a palm reads
  like a thumb but rarely stays still, which `HOLD_MS` trades against missed
  holds. With a triple tap set, a double tap waits `THIRD_WAIT_MS`.
- **Injected swipes**: injected as touch, since touch-only apps ignore a mouse
  wheel. Length follows strength; the swipe ends in a slow tail because
  launchers such as inkOS accept only a fling. A swipe cut off before its UP
  leaves a finger down that refuses every later DOWN; ACTION_CANCEL clears it.
- **Keys for Key Mapper**: injected through uinput, because keys an app injects
  never reach accessibility services. Double tap KEY_RED, hold KEY_GREEN, triple
  tap KEY_YELLOW, Keys-mode swipes F13 and F14.

### Settings screen (`KompaktActivity.kt`)

- **Back**: targeting SDK 36, Back no longer reaches onBackPressed; without an
  OnBackInvokedCallback every page closes the app.
- **Invert is not a mode**: per-app inversion happens in SurfaceFlinger before the
  driver sees the frame, so it lives in the per-app sheet.
- **Battery on Mudita's kernel**: health and capacity divide by its 294 mAh design
  figure and would read about 1000%, so they are hidden there.
- **E-ink UI**: disabled is a dashed outline, since the panel cannot show grey;
  sheets have no window animation and pages scroll with scrollTo, never
  smoothScrollTo.
- **inkOS parity**: headings, sheets, radio marks and the toggle follow inkOS so
  both apps read as one system.

### Other services

- **GSF id** (`GsfId.kt`): Play Services serves it under
  `com.google.android.gsf.gservices` to callers holding READ_GSERVICES, which
  adb's shell cannot hold, plus a `<queries>` entry on Android 11 and up. It is
  text: up to 20 digits, past a signed Long.
  `adb shell am broadcast -n com.kompakt.service/.GsfIdReceiver` prints it.
- **IMS** (`Ims.kt`): carriers without 2G or 3G carry every call over IMS.
- **Front light** (`FrontLight.kt`): a step is a tenth of the slider on Android's
  perceptual scale, copied from SettingsLib's BrightnessUtils.
- **Notification light** (`NotifLight.kt`, `Hardware.kt`): see
  `KompaktService/NOTIFICATION-LED.md`. DND is `matchesInterruptionFilter()`; an
  unreadable ranking lights the LED rather than silencing it.
- **Tether offload** (`TetherOffload.kt`): kept off. The vendor's offload is
  MediaTek's modem direct path over /dev/mddp, which neither kernel has, so
  clients connect and nothing is forwarded. Settings writes 0 back when developer
  options are switched off, so it is watched.

## Charging screen (`charger/`)

- Mudita's power-off charging screen lives in their system image, not the vendor,
  so a GSI loses it. `extract-from-stock.sh` takes it from MuditaOS K 1.6.0's
  system image; this repository does not carry Mudita's binaries.
- TrebleDroid's AOSP charger draws for an LCD and never triggers an e-ink
  repaint; kpoc_charger sets `/sys/einkinfo/waveform_mode` itself.
- Its DT_NEEDED `libcutils.so` becomes `libkpoc.so` (`kpoc_shim.cpp`).
  LD_PRELOAD cannot do it: init's domain transition sets AT_SECURE and bionic
  drops LD_PRELOAD for secure processes.

## Media

- **Codec2 seccomp** (`seccomp/mtk-c2.policy`): the vendor policy was written for
  Android 12 libraries; with Android 16's the service is killed with SIGSYS a
  second into playback. This allows every ARM syscall bionic knows except those
  no codec needs. SELinux still confines it.
- **USB audio** (`audio/`): MediaTek's primary HAL cannot open USB DACs, so USB
  output moves to the standard USB audio HAL; input stays on the primary module.
- **Boot animation** (`bootanimation/`): `track.json` holds every ring of the
  LineageOS animation, frame by frame; `make-frames.py` redraws them around our
  logo at 480x800, 16 greys, 10 fps. The zip is stored, since bootanimation maps
  frames uncompressed. The final hold is repeated frames: a pause on the last
  part is skipped once boot completes.

## Overlays

- **Doze** (`kompakt_doze.xml`): with doze display states on, the AOD state is
  DOZE_SUSPEND, the display suspend blocker is released and the driver gets its
  AOD power mode; at the upstream false the SoC never sleeps. The proximity
  sensor chatters with nothing in front of it, so pulses do not gate on it.
- **IPsec** (`kompakt_ipsec.xml`): Android enables these algorithms by itself
  only at vendor API level 31; this vendor declares 30, although both kernels
  build them. IWLAN needs AES-XCBC.
- **Text size** (SettingsProvider defaults): the third stop makes status bar
  icons overflow 480 px.
- **AOD icons**: 18 dp in doze only; the status bar keeps 15 dp.
- **Setup wizard**: Google's is prebuilt, so product overlays never reach it;
  `KompaktGoogleSetupWizardOverlay` gives its dark mode buttons the light ink.
- **Colours** (`overlay/.../values/colors.xml`): generated by gen-colors.py, which
  is not in this repository; edit the generator, not the file.
- **Toasts**: a border in the text ink, since e-ink has no elevation shadow.
- **Cycle count**: LineageOS hides the Settings row; our kernel publishes the
  count through the vendor health HAL.
