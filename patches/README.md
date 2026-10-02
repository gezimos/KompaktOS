# Patches

One directory per upstream project, each a `git format-patch` series applied in
order with `git am` by `apply-patches.sh`. Commit messages are titles only; what
a patch does and why, where it is not obvious from the code, is below.

Projects with no entry here carry only changes their titles explain.

## bionic (`bionic`)

- **0001 posix_spawn close-on-exec before Linux 5.11**: `POSIX_SPAWN_CLOEXEC_DEFAULT` uses `close_range(CLOSE_RANGE_CLOEXEC)`, which needs Linux 5.11; on the Kompakt's 4.19 kernel it fails and the vfork child exits 127 before exec without logging anything. That broke netd's dnsmasq (no DNS for hotspot clients) and clatd. On ENOSYS/EINVAL the patch walks `/proc/self/fd` with raw syscalls; it runs in the vfork child, so it must not allocate or use stdio, and its buffer stays small because bionic caps a stack frame at 2 KiB.

## build_release (`build/release`)

- **0001 Kompakt flag values**: enables the SystemUI `scene_container` aconfig flag (flexiglass), which the SystemUI changes depend on.

## device_phh_treble (`device/phh/treble`)

- **0001 goodix sysfs guard**: the goodix `support_pen` write is guarded by an existence check. This is a manual re-application of a trebledroid-staging patch that no longer applies, because a Samsung seccomp block now follows the goodix block.
- **0002 battery cmd roletypes**: the Kompakt vendor policy declares `proc_battery_cmd` and `proc_battery_current_cmd` but binds only the versioned aliases to `object_r`. Any GSI forces a policy recompile, and secilc then aborts ("Type ... is invalid for role object_r"), so init reboot-loops. The fix must go in `31.0.compat.cil`, not `31.0.cil`: the latter is post-processed by combine_maps.py, which silently drops `roletype`.
- **0003 frontlight off in doze**: `config_screenBrightnessDoze` is 0. The e-ink panel keeps its image unpowered, so AOD needs no frontlight.
- **0004 inherit vendor/kompakt**: `inherit-product-if-exists`, so the tree still builds without `vendor/kompakt`.
- **0005 boot animation size**: `TARGET_SCREEN_WIDTH/HEIGHT` are set to 480x800 so the generated boot animation matches the panel (the default is 1080x1920). They must be set in `lineage.mk` itself: its body runs before the inherited `common.mk`, so `:=` lands before that file's `?=` and before `BoardConfigSoong.mk` reads the value. `TARGET_BOOT_ANIMATION_RES` was removed because nothing reads it.
- **0006 sepolicy for the Kompakt app (dumbita.te, later kompakt_service.te)**: the app runs as `system_app`. The node types (`sysfs_eink_waveform`, `fm_device`, ...) are declared only in vendor policy, which system_ext policy cannot name at build time. Declaring them again breaks the policy merge at boot. The rules therefore use the bare `sysfs_type` and `dev_type` attributes, which secilc resolves at boot. Keep the attribute bare: any subtraction (`sysfs_type -foo`) makes checkpolicy expand the set at build time to plat types only, which compiles cleanly but grants nothing.
- **0007 FM rename**: the comment follows the DumbitaFMRadio rename.
- **0008 kpoc_charger**: Mudita's power-off charging screen is MediaTek's `kpoc_charger`, which lives in their system image; `vendor/kompakt/charger` ships it. This patch adds the system half of its policy (Mudita's own rules, minus types Android 16 dropped) and disables the AOSP charger, which draws for an LCD. Every Kompakt vendor image already grants `kpoc_charger_31_0` the logo partition, DRM, the e-ink waveform node and the battery nodes. The compat mapping points that name at the new type.
- **0009 nvdata scan only on OPPO**: rw-system.sh grepped all of nvdata (64 MB on the Kompakt) for an OPPO model name. The script runs as an exec at post-fs, so it held up init and display start by about 40 s on every boot.
- **0010 Kompakt properties**: `persist.sys.phh.mainkeys=1` means no on-screen navigation bar. `persist.sys.phh.disable_display_doze_suspend` must stay 0: at 1, phh's DisplayPowerController patch turns DOZE_SUSPEND into DOZE, and on this MediaTek driver DOZE holds `pri_disp_wakelock`, so the SoC never sleeps during AOD. `ro.vendor.mtk_aod_support=1` lets the MediaTek HWC issue its AOD power mode. Mudita left it off, so the display pipeline stayed powered in doze.
- **0011 build identity**: brand, model and manufacturer (and their `PRODUCT_SYSTEM_` twins) say Mudita/Kompakt, so the derived fingerprint is self-consistent, which Play checks.
- **0013 MediaTek codecs**: MediaTek's Codec2 seccomp policy predates the current libraries, so the service is killed with SIGSYS during playback. rw-system.sh bind-mounts `/system/etc/kompakt/mtk-c2.policy` over it. It also drops `c2.mtk.mp3.decoder` from the vendor codec list: that decoder dereferences a null input buffer on empty work items (EOS, seek, flush) and takes the whole service down. MP3 then goes to `c2.android.mp3.decoder`.
- **0014 USB DACs**: MediaTek's primary audio HAL claims USB playback but never opens the DAC ("mPcm is NULL"). A replacement audio policy moves USB output to `audio.usb.default`. It is mounted only when the vendor policy's md5 matches the file it was derived from; USB input stays on the primary module.
- **0015 Wi-Fi calling data call**: `config_wlan_data_service_package` is `com.android.phone`, so the RIL sets up the IWLAN data call and the modem builds the tunnel through its own ePDG daemon, as MediaTek's IwlanDataService does on MuditaOS. With phh's value, Google Iwlan built the tunnel, and the MediaTek IMS stack could not send through it. This directory is listed first in PRODUCT_PACKAGE_OVERLAYS, so the value has to change here; `vendor/kompakt/overlay` repeats it with the class name.

## frameworks_av (`frameworks/av`)

- **0001 front camera alias**: the Kompakt has one rear sensor. CameraService exposes it a second time as id "1", facing front, while `persist.kompakt.front_alias` is on (the default) and the HAL reports exactly one camera "0". The alias resolves to "0" in `resolveCameraId()`, so open, torch and characteristics all follow and the sensor is never opened twice. It must also appear in `addListenerHelper()`'s status list, because camera2's `getCameraIdList()` is built from that list. Leave `SENSOR_ORIENTATION` as the sensor reports it: apps mirror a front camera, and using the complementary angle turned the picture upside down.

## frameworks_base (`frameworks/base`)

- **0001 lockscreen scrim**: KEYGUARD_SCRIM_ALPHA is 0 and mDarkenWhileDragging is forced false. A grey wash dithers instead of shading and forces a full refresh on every shade drag.
- **0003 marquee stragglers**: controls_base_item.xml spaces its marqueeRepeatLimit attribute and styles.xml declares it as a style item, so an attribute-form search for marquee misses both.
- **0004 AOD clock and frontlight**: config_screenBrightnessDoze is 0 so the frontlight stays off in doze, and getAodColor() always returns DOZE_COLOR because system_accent1_100 maps to white on this palette. The AOD background is left to the palette overlay (system_under_surface_light) on purpose; hardcoding it here would miss every other consumer.
- **0005 config values**: device values belong in device/phh/treble/overlay, not in AOSP config.xml, or every upstream bump conflicts.
- **0006 bars and QQS**: the bars are opaque 0xFFFFFFFE because the semi-transparent value is a framework ColorStateList the palette overlay cannot reach. QuickQSPanel is forced GONE in setVisibility because QSAnimator and QSFragment set its visibility at runtime. QS tile colours already come from the palette through customColorShade*, so QSTileViewImpl is not patched.
- **0008 legacy brightness icon**: on Android 16 the brightness icon is a drawable layer (@id/slider_icon in brightness_progress_full_drawable), not a view, so it is fixed by tint rather than by hiding a view.
- **0009 QS pulldown fallback**: the fallback value 1 matches the LineageSettingsProvider default and is only used when the setting was never written; an explicit 0 still wins.
- **0010 animations the scales miss**: the animator scales do not reach scene container transitions (their own durationScale) or NumPadAnimator (AnimatorSet.setDuration overrides its children), so both are set to zero directly.
- **0011 dual shade and QQS**: the QQS row is emptied with take(0) inside QuickQuickSettings and must never be skipped: its Box emits GridAnchor, which the QQS to QS transition anchors on, and removing it crashes SystemUI on every shade open. The shade fling duration comes from FlingAnimationUtils, not the animator scale, so setDuration(0) is placed after every branch. DUAL_SHADE_ENABLED_DEFAULT is set in the interactor because Settings.Secure.DUAL_SHADE has no SettingsProvider default, and it is only read with SceneContainerFlag on.
- **0012 keyguard icons and battery**: dark intensity is pinned to 1 because the status bar is always paper; BatteryMeterView.onDarkChangedLegacy ignores the tint colour and derives its own from the intensity alone. updateBatteryVisibility is null guarded because with no Compose battery it would call addView(null, -1) and crash-loop SystemUI.
- **0013 dims**: the theme default dim behind dialogs is 0 while an explicit dimAmount is still honoured; dialogs get their edge from a border instead. ShutdownUi paints colorBackground, so a restart no longer floods the panel black.
- **0014 QS tile state**: the icon_refresh_2025 on and off drawables are animated vectors over the same base vector, so with animations off they look identical; this is why KompaktTileIcons supplies real filled and outlined pairs.
- **0015 PIN dots**: APPEAR and DISAPPEAR durations are 0, but TEXT_VISIBILITY_DURATION and TEXT_REST_DURATION_AFTER_APPEAR are kept on purpose: they are how long a typed digit stays readable, which is behaviour rather than animation.
- **0016 long press home**: the panel flush is started directly, not through Action.SEARCH and the assistant role, which would cost the user a real assistant and flush on every assistant route. It falls back to the configured key action when the Kompakt app is absent.
- **0017 cursor blink**: a zero blink interval returns false from shouldBlink(), otherwise makeBlink() reposts at zero delay and the panel repaints on every main loop pass while a field has focus.
- **0017 lockscreen layout**: LockscreenSceneLayout asserts exactly six measurables and AOSP registers no AmbientIndicationArea off Pixel, so a zero-height AmbientIndicationElementProvider is bound or the Compose lockscreen crashes SystemUI.
- **0017 charger detection**: mtk_charger_type latches the last charger (online=1, USB_CDP) after unplug, which keeps the charging icon and stay_on_while_plugged_in alive. plugType() treats BATTERY_STATUS_DISCHARGING as unplugged; only DISCHARGING, because a full battery reports NOT_CHARGING while genuinely plugged in.
- **0017 night mode traps**: the device runs in night mode with white surfaces, so anything keyed off UI_MODE_NIGHT_MASK or isLightTheme picks white ink on white paper. Keyguard status bar icons are fixed to dark_mode_icon_color_single_tone (black) and the intensity to 1.
- **0017 shade header padding**: the header uses status_bar_padding_start and end in both updateResources() and the configuration listener; the code overwrites any value set in the layout, and the two sites must read the same resources or the header drifts on configuration change.
- **0017 scrims and power menu**: ScrimController durations are 0 because scrims run their own animator outside the animation scales. GlobalActionsDialogLite no longer calls setBackgroundTintList, which tints a shape drawable whole, stroke included, and erased the overlay border.
- **0017 QS tile icons**: KompaktTileIcons.kt is generated by vendor/kompakt/tools/gen_symbols.py from Material Symbols (which has a real FILL axis) and must not be hand edited. A null return leaves any unmapped or third party tile on its own drawable.
- **0017 USB default**: an unset default USB configuration is mtp, so an unlocked phone presents mtp, adb and acm for Mudita Center.
- **0018 lockscreen notification stack**: the stack composes inside the upper region's nested SceneTransitionLayout, whose currentScene is never Scenes.Lockscreen, so the gate is "nested layout idle" and the last position is remembered and applied when it turns idle. Without it setStackTop is never called and the first card is clipped by the screen edge.
- **0018 clock ink**: the keyguard clock inks by dozeAmount, white over AOD black and black on the lockscreen, because isLightTheme is always dark here and REGION_SAMPLING is off. handleDoze runs every transition frame, so colours are only updated when the threshold is crossed.
- **0019 notification LED**: LightsService catches RuntimeException because the lights HAL throws ServiceSpecificException(-7), which otherwise restarts system_server; do not narrow it back. config_notificationLedHandledExternally, when true, stops the framework acquiring the light while leaving mHasLight and the Settings row alone.
- **0020 wake sequence**: the panel paints every frame after unblank and ignores brightness, so an OFF to ON transition is applied only once the window manager policy unblocks the screen. The ColorFade is disabled and the display is not powered down on the way into doze.
- **0021 navigation bar property**: qemu.hw.mainkeys comes from persist.sys.phh.mainkeys in /data and survives reboots, so only the branch that hides the bar is honoured, in both DisplayPolicy and DisplayLayout.
- **0022 LED and animator scale**: config_intrusiveNotificationLed=false keeps NotificationAttentionHelper away from the lights HAL; the LED is driven from KompaktService. SettingsProvider also seeds ANIMATOR_DURATION_SCALE, which AOSP leaves unseeded.
- **0023 unlock transitions**: the light reveal scrim is pinned open because its animator is androidx and never sees the animator scale. Notification alpha is binary and stays 0 on every transition to unlocked, and burn-in offsets are 0 since every offset is a repaint.
- **0024 bouncer**: the PIN pad reveal is never gated on a suspend call, which left it at alpha 0 on the first lockscreen after boot. The falsing swipe distance is a quarter of the screen, since the inch based threshold demanded four fifths of a 480x800 panel at 213dpi.
- **0025 status bar**: the icon group is added once and never removed; re-adding it on attach made the clock appear late. The Compose battery shows the percentage inside the icon and does not read config_defaultBatteryPercentageSetting. While Offline+ (Settings.Global HWSwitch_lock) cuts the modem, the signal bars show no signal.
- **0026 battery glyphs**: with two inks the fill and the foreground are the same colour, so the bolt and percentage are composited from white through BlendMode.DIFFERENCE: black on paper, white on the fill, correct across the fill edge.
- **0028 AOD painting**: the panel is bistable, so STATE_DOZE paints (eink_aod_func(1)) and DOZE_SUSPEND sleeps (eink_aod_func(0)); AOD enters DOZE, waits AOD_PAINT_MS, then drops to DOZE_SUSPEND. Never hold DOZE (the phh disable_display_doze_suspend property): meink stays in AOD and the panel freezes. Each tick bumps to DOZE before pushing frames, only while in AOD, and logs the machine state so a GPU fence deadlock (tick fires, main thread stuck) can be told apart from a skipped bump.
- **0030 AOD icon row width**: fillMaxWidth is needed on both the AnimatedVisibility modifier and the AndroidView; otherwise NotificationIconContainer wrap-measures and shows N-1 icons plus an overflow dot.
- **0031 AOD proximity**: proximity never pauses AOD; nothing burns in, and the flapping sensor pulled the display out of DOZE_SUSPEND and raced meink so the power button stopped waking it.
- **0032 shelf icons**: NotificationShelf is the only caller of setOverrideIconColor and its colour is pinned black, so on the keyguard over a dark surface it uses the inverse override colour.
- **0033 dialog border**: DecorView strokes the outline of every floating window background after the content, so AppCompat, Material and SystemUI dialogs all get an edge with a matching radius. persist.sys.kompakt.dialog_frame=0 disables it, read once per process.
- **0034 detached launch**: a lockscreen shortcut (Wallet) launches after the keyguard is dismissed, when its view is detached and has no transition container; the ClassCastException is avoided by launching without animation.
- **0035 boot dialog and Offline+ switch**: the boot dialog uses theme 0, the context's dialogTheme as ShutdownThread does; naming Theme_DeviceDefault_Light_Dialog_Alert gives a black content panel because ProgressDialog paints colorBackgroundFloating. The Offline+ switch hangs up any call before the modem is cut, records HWSwitch_lock and announces the change.
- **0036 AOD ink**: the AOD clock, icons and indication text ask KompaktInk.isDarkSurface(), which reads the lock wallpaper's dark text hint, because AOD shows that wallpaper.
- **0037 theme**: two inks, no greys, no translucency: surfaces are black or white with an outline where a fill would have been. A 2dp kompakt_dialog_outline_width is the one weight for dialog edges, and the default wallpaper is KompaktPaperWallpaper, white or black with the ui mode.
- **0037 status bar autohide**: the bar mode never changes on this device, so touchAutoHide is scheduled from autoHideUpdate and CentralSurfacesImpl rather than on a bar mode change.
- **0039 unlock hold**: SystemUI hides the keyguard as soon as its scene reaches Gone, before the launcher draws, which this panel paints as a black page. KompaktUnlockHold screenshots the display onto its overlay layer while the keyguard goes away from a lit panel and releases it on the frame after the transitions settle, or after a timeout. persist.sys.kompakt.unlock_hold=0 turns it off.
- **0040 focus ring**: borderOnFocus draws only in keyboard input mode, because a touch focuses some QS elements; the fingerprint d-pad moves focus with key events and still shows it.
- **0041 tile names**: a custom tile counts as a switch only if its app declares TOGGLEABLE_TILE, so every icon only tile now shows its name in the toolbar.
- **0042 key vibration**: the keys under the screen are VIRTUAL in the mtk-tpd key layout; Settings.Secure kompakt_key_vibration=0, set by the Kompakt app, turns their haptics off.
- **0043 unlock broadcast**: com.kompakt.service.action.UNLOCKED is sent once the unlocked screen is drawn, posted because it runs under the window manager lock. USER_PRESENT is not sent for a fingerprint unlock and arrives late.
- **0044 SIM PIN confirm key**: auto-confirm only works for a PIN of known length, so the SIM pad always keeps its confirm and backspace keys.
- **0046 heads-up status bar**: only the legacy shade released the status bar forced visible by a pinned heads-up; under the scene container it is released when the heads-up unpins.
- **0047 SPA toolbar**: Compose Settings pages and apps built on SettingsLib SPA, such as the Updater, get the one row toolbar instead of the large collapsing title, the same as the view based pages after 0007. The large title left a band of empty space above every page. The scaffold no longer hands scrolling to the collapse behaviour: without the large bar its limit is never laid out, and it took every scroll away from the page.

## frameworks_native (`frameworks/native`)

- **0001 hover inconsistency**: an unexpected hover action in InputDispatcher is logged as an error, with the dispatcher state dumped once, instead of aborting system_server.

## frameworks_opt_telephony (`frameworks/opt/telephony`)

- **0001 IMS APN for the MediaTek modem**: this is the Wi-Fi calling fix. The modem builds the ePDG tunnel itself and only uses an IMS APN whose bearer bitmap allows Wi-Fi. An APN without `network_type_bitmask` means every network to Android, but it reaches the modem as an empty bitmap, which the modem reads as none: with the radio off it refused every IMS connection (`pdn_count=0`, cause 31) and never asked epdg_wod for a tunnel. IMS and XCAP APNs without a bitmask are sent with every network type, as MuditaOS's APN list sets them. An IMS APN without `profile_id` is sent as profile 2, because the MediaTek RIL names the APN type after the profile and otherwise sends the IMS APN as `"default"`; Android 12 derived the profile from the APN type.

## lineage_sdk (`lineage-sdk`)

- **0001 quick pulldown**: `def_qs_quick_pulldown` defaults to 1 (right side). The notification shade carries no tiles, so the right quarter is the one-finger route to the full QS grid.
- **0002 LED capabilities**: `config_deviceLightCapabilities` is 513 (RGB notification LED + no brightness control). It must not include 8 (`LIGHTS_PULSATING_LED`): the light HAL rejects pulsing with UNSUPPORTED_OPERATION, which crashes system_server and restarts the lockscreen on every notification.

## packages_apps_Aperture (`packages/apps/Aperture`)

- **0001 camera facing label**: the Kompakt overlay hides the lens selector and puts a Front/Back label in its place, because the single sensor is offered as both cameras. The view itself lives in vendor/kompakt's Aperture overlay.

## packages_apps_LineageParts (`packages/apps/LineageParts`)

- **0001 Blinking, no brightness**: the LED is declared TOGGLE, and on this device it only breathes, so the caption says Blinking. The brightness section is removed because a breathing channel cannot be dimmed. The capability stays RGB so colours are still offered.
- **0003 no caption**: TOGGLE rows hide the on/off caption, because there is only one way to light.

## packages_apps_Twelve (`packages/apps/Twelve`)

- **0001 QUERY_ALL_PACKAGES**: Media3 resolves the package of every app that sends a transport command. Without package visibility the lookup fails and the command is dropped, so launcher widgets could not play, pause or skip.

## packages_apps_Updater (`packages/apps/Updater`)

- **0001 GitHub releases**: the update list URL takes a `{region}` placeholder, filled from the modem version in `gsm.version.baseband`. `V236.P6` gives `usa` and anything else gives `global`, because Global and North American phones need different vendors and the system image cannot tell them apart. With no modem version yet, the check fails instead of guessing. The list is fetched with HTTPS redirects followed, because GitHub serves `releases/latest/download/` files through one. The URL itself is `lineage.updater.uri` in `vendor/kompakt/kompakt.mk`.
- **0002 updates page**: the version card is the page colour with a 2dp outline instead of the teal brand fill and its shader pattern, and Check for updates is an outlined button, because its tonal fill is the page colour on this palette. Recent changes and the View on GitHub menu item open our GitHub releases, and Report issues opens our GitHub issues.
- **0003 stale entries**: when the server offers nothing newer, the list used to return before removing saved entries the server no longer advertises, so a build already installed, or an abandoned download of it, stayed on screen with an Install button. The refresh now always prunes them.

## packages_modules_Connectivity (`packages/modules/Connectivity`)

- **0001 hotspot DNS**: the GSI default starts tethering without netd's DNS proxy, but DHCP still hands out the phone as the DNS server and nothing answers, so clients get no DNS. Tethering starts with the proxy and retries without it only if that fails.

## system_sepolicy (`system/sepolicy`)

- **0001 su permissive**: adbd enters `su` for adb root (`--root_seclabel=u:r:su:s0`). With `su` enforcing and its denials dontaudited, root adbd cannot listen on its own socket and restarts in a loop, which kills USB and wireless debugging.

## treble_app (`treble_app`)

- **0001 IMS receivers**: the app runs as the system uid, so Android 16 requires every runtime-registered receiver to declare an export flag. Without one, the IMS download crashed.
- **0002 Create IMS APN feedback**: the button shows a toast for each outcome: added, already added, no SIM, or failed. The insert can return null although the row was written, so failure is reported only when the APN is not in the table.
- **0003 4G calling forced on by default**: "Force the presence of 4G Calling setting" writes `persist.dbg.volte_avail_ovr`, `wfc_avail_ovr` and `allow_ims_off`. It defaulted to off, and the first visit to the IMS page saved that default, setting all three to 0 over the build's 1. Android then hid the Wi-Fi calling switch and, on carriers whose config leaves VoLTE unavailable (Telia LT), kept VoLTE off until the VoLTE switch was toggled.

## vendor_apn (`vendor/apn`)

- **0001 IMS and XCAP APNs**: `KOMPAKT.xml` adds 955 IMS and XCAP entries from the MuditaOS APN list, for carriers whose country files have none (Telia LT, Vodafone UK and about 640 others). Without an IMS APN, VoLTE and Wi-Fi calling need Create IMS APN in the Phh settings; without XCAP, call forwarding and waiting over IMS are unavailable. Entries that also carry `ia` are left out, so no carrier's attach APN changes.

## vendor_hardware_overlay (`vendor/hardware_overlay`)

- **0001 Updater**: the prebuilt Treble app declared that it overrides `Updater`, which kept the LineageOS Updater out of the image.

## vendor_lineage (`vendor/lineage`)

- **0001 white wallpaper**: the default wallpaper is a flat #fffffe PNG, in both drawable-nodpi and drawable-hdpi; nodpi alone was not enough. It is not pure #ffffff because the e-ink driver special-cases pure white.
- **0002 light mode**: `config_defaultNightMode` is 1 (light). A full black screen costs contrast and ghosts on the panel.
