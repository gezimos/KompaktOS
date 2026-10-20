# Changelog

The system image. Each release ships as one full update package together with
the boot and vendor images from
[KompaktDevice](https://github.com/gezimos/KompaktDevice), listed per release.

## 1.2 (2026-10-20)

System build 379, with boot 275 and vendor 297 (Global) / 297-usa (USA).

### Modem

- **Every package carries its region's modem firmware**, as MuditaOS updates do.
  - Global: V236.P8. USA: V236.P6 (MuditaOS 1.6.1, unmodified).
  - The phone keeps one modem per A/B slot. Global MuditaOS shipped P6 until
    1.4.0, so many Global phones still had P6 in one slot.
  - An install landing on that slot ran Global software on the old modem, and
    calls failed (IMS never registered).
  - The build checks the modem in each finished package against its vendor's
    region.

### Calls

- **Call volume goes properly quiet, in six steps.** `patches/kompakt/device_phh_treble/0014`,
  `vendor/kompakt/kompakt.mk`, KompaktDevice's vendor.
  - Six steps (five before): -28, -21, -15, -8, -2 and +5 dB. The loudest is
    unchanged; a new phone starts on the fourth.
  - phh's MtkInCallService is gone. It pushed the volume to MediaTek's audio
    HAL on a scale this phone does not have, fighting the HAL's own volume.
- **One volume slider during a call.** The second, unlabelled media slider is
  gone. `patches/kompakt/frameworks_base/0106`.
- **Call button labels are whole** (Record call, Add call, Hold).
  `patches/kompakt/packages_apps_Dialer/0004`.
- **Create IMS APN adds nothing when the carrier already has an IMS APN.** A
  second one could break calls and data. `patches/kompakt/treble_app/0004`.

### Mobile network

- **The modem hears what MuditaOS told it.** `patches/kompakt/frameworks_opt_telephony/0002`,
  `vendor/kompakt/mtkradioex`.
  - MuditaOS runs MediaTek's own telephony layer, which keeps the modem
    informed and answers its requests.
  - The modem's data retry count is reset at each new attempt, so it cannot
    run out and block an APN.
  - Android acts when the modem reports that reset or asks for the attach APN
    again.
  - The modem gets the service state, mobile data, roaming and the data SIM.
  - Aimed at networks lost on Verizon, T-Mobile US and Orange France.
- **Verizon: data, SMS and calls work.** `patches/kompakt/packages_apps_CarrierConfig/0001`.
  - No wait for VoLTE provisioning, as on MuditaOS.
  - A new carrier config version, so a phone updating from 1.1 loads it
    without clearing Phone Services storage.
- **Orange France** attaches with IPv4 and IPv6, not IPv6 only.
- **The modem's power saving matches MuditaOS**: it no longer switches with every
  screen and charger change. `patches/kompakt/frameworks_opt_telephony/0004`.
- **Mobile data reconnects when the network ends it**, on Wi-Fi too, so MMS
  goes out. The modem's report that a connection ended now reaches Android, as
  on MuditaOS; before, the connection stayed shown as connected until mobile
  data was turned off and on. `patches/kompakt/frameworks_opt_telephony/0005`.
- **Mobile data after Wi-Fi**: when mobile data takes over and its connection has
  gone dead underneath, it reconnects by itself instead of staying dead until
  mobile data is turned off and on. It is checked again every 30 seconds while
  in use, so a connection that dies in use reconnects too.
  `patches/kompakt/frameworks_opt_telephony/0003`.
  `patches/kompakt/vendor_apn/0002`.
- **SIMs opens with no SIM inserted.** The row reads No SIM and opens the
  SIMs page, where the SIM 2 slot switch and Add eSIM are; before, it jumped
  into the eSIM download or was greyed out, so the eSIM could not be selected
  without a physical SIM. `patches/kompakt/packages_apps_Settings/0004`.
- **eSIM on the SIMs page, in MuditaOS's words.** A row reads **Switch to eSIM
  mode** (or **Switch to SIM 2 mode**) and asks before it restarts the phone. In
  eSIM mode one row, **Add eSIM** or **eSIM** with the carrier, opens OpenEUICC. Android's Add SIM row is gone: it
  appeared only on some visits, stayed after an eSIM was on, and left a new
  eSIM off. `patches/kompakt/packages_apps_Settings/0005`.
- **A downloaded eSIM is turned on.** OpenEUICC enables the new profile when its
  download finishes, as Android's own flow does. `patches/openeuicc`.

### Sound

- **Quieter lowest steps for ring and notifications.** Step 1 goes 45.7 dB
  below full volume (Mudita's curve: 29.7 dB). Full volume is unchanged.
  KompaktDevice's vendor.
- **30 media volume steps** instead of 15. The lowest step is half as loud.
  `ro.config.media_vol_steps` in `vendor/kompakt/kompakt.mk`.

### Notification light

- **The notification light is LineageOS's own.** `patches/kompakt/hardware_lineage_interfaces/0001`,
  `0002`, `lineage_sdk/0002`, `packages_apps_LineageParts/0004`, `0005`,
  `vendor/kompakt/KompaktService/NOTIFICATION-LED.md`.
  - Settings → Notifications → Notification light: colour per app,
    brightness, pulse length and speed.
  - Colours mix (orange shows as orange, not red), and a solid light is solid.
  - The brightness preview lights the LED at the level it shows.
  - Brightness dims "Always on" only. A pulsing light runs at full
    brightness: the PMIC's breath mode cannot be dimmed on this LED.
  - LineageOS drives the LED through its lights HAL; the Kompakt app no
    longer does. LED colours set in the Kompakt app on 1.1 are gone.
  - Needs KompaktDevice's 1.2 boot and vendor images.

### Lockscreen and always-on display

- **The AOD clock is the lockscreen clock**: same font style, no animation
  when tapped or when a charger is plugged in. `patches/kompakt/frameworks_base/0048`.
- **The lockscreen clock digits are sharp.** Google Sans Flex's roundness was
  turned up by default, which blurred the digits. `patches/kompakt/frameworks_base/0091`.
- **AOD shows what is current when it starts.** `patches/kompakt/frameworks_base/0062`.
  - A missing notification icon or the quick access buttons could stay until
    the next minute.
  - AOD now refreshes the whole lockscreen when it starts.
- **AOD updates when its content changes**, at most every 10 seconds: a late
  notification icon, a battery step, an expired fingerprint message.
  `patches/kompakt/frameworks_base/0063`.
- **The lockscreen shows only the notifications that fit.** The next one was a
  thin sliver with a small box holding a dot. `patches/kompakt/frameworks_base/0054`.
- **Lockscreen notifications have round bottom corners.** `patches/kompakt/frameworks_base/0076`.
- **4x4 and larger patterns unlock.** The lock screen always drew a 3x3 grid.
  `patches/kompakt/frameworks_base/0077`, `0078`.
- **The big clock no longer fades** when a notification arrives and the small
  clock takes its place, or back. `patches/kompakt/frameworks_base/0089`.
- **With no lock screen, its clock no longer shows over the home screen** for a
  moment on every wake. `patches/kompakt/frameworks_base/0112`.
- **The PIN pad and the lockscreen switch without animation.** The status bar,
  shortcuts and Emergency button came back late, and the message faded.
  `patches/kompakt/frameworks_base/0090`.
- **Black lockscreen text on any mostly light wallpaper.**
  `persist.wallpapercolors.threshold=50`, `max_dark_area=100` in `vendor/kompakt/kompakt.mk`.
  - Android allowed dark text only when under 5% of the wallpaper is dark, so
    ink drawings got white text.
  - Set the wallpaper again once after updating.
- **No widget hub on the lockscreen.** Its Add widgets button did nothing.
  `patches/kompakt/vendor_lineage/0003`.
- **Prevent accidental wake-up** (Settings → Display), on by default.
  `patches/kompakt/frameworks_base/0052`, `0056`, `0057`, `vendor/kompakt/overlay`.
  - A covered proximity sensor keeps Home from waking the phone, and AOD stays
    on. Turn it off in the same place.
  - Back and Recents no longer vibrate while the screen is off.
- **Warm flashlight.** `patches/kompakt/frameworks_base/0100`, `0101`, `vendor/kompakt/KompaktService`.
  - A Warm flashlight lock screen shortcut (Wallpaper & style → Lock screen →
    Shortcuts), beside the cold flashlight.
  - The power button's torch can switch the warm light: Kompakt app →
    Settings → Power button light.
- **An unlock can no longer freeze the home screen.** If Android is never told
  an unlock finished, it ends by itself after 3 seconds.
  `patches/kompakt/frameworks_base/0103`.

### Notifications and the shade

- **The shade turns pages.** `patches/kompakt/frameworks_base/0082`-`0084`, `0086`.
  - The list stays still under the finger and jumps a page on release,
    breaking between notifications.
  - No shelf: the next notification shows past the edge, and Clear all,
    history and settings stay pinned at the bottom.
  - Dragging down on a notification turns the page; the chevron expands it.
- **Lock screen notifications that fit are shown.** A grouped notification
  (Google's weather, a Signal group) under another one was hidden although
  there was room. `patches/kompakt/frameworks_base/0113`.
- **The shade stays at the top after a notification is closed over the lock
  screen.** It dropped to the middle, with Clear all under the rows.
  `patches/kompakt/frameworks_base/0114`.
- **The PIN pad is quicker.** A key no longer turns black when pressed, and a
  digit shows as a dot at once instead of a shape that turns into one.
  `patches/kompakt/frameworks_base/0116`.
- **The PIN pad opens whole on a swipe.** The keys no longer show over the
  lock screen before the white background. `patches/kompakt/frameworks_base/0117`.
- **The last lock screen notification shows when all of them fit.** It was
  hidden whenever nothing overflowed, so two or three notifications showed one
  fewer. `patches/kompakt/frameworks_base/0118`.
- **Lock screen notifications appear in place on a wake.** They showed under
  the always-on icons first and then moved up under the clock.
  `patches/kompakt/frameworks_base/0115`.
- **The shade opens whole.** `patches/kompakt/frameworks_base/0079`-`0081`, `0096`.
  - The notifications and Clear all arrive together.
  - The first pull after unlocking no longer grows and reorders the list.
- **Swiping a notification away moves nothing.** `patches/kompakt/frameworks_base/0085`,
  `vendor/kompakt/overlay` (`config_showNotificationGear`).
  - The card stays put and its outline turns to dashes in eight steps.
  - Let go when it is all dashes to dismiss it; earlier, it turns back.
  - No fade, no magnetic swipe, no gear and snooze menu behind the card (long
    press still opens its settings).
- **Notifications open to show their buttons** (Reply, Mark as read, Dismiss).
  The top notification still does not open by itself. `patches/kompakt/frameworks_base/0060`.
- **The expand and reply arrows are visible**: a white pill with an ink
  outline, and a solid send arrow. `patches/kompakt/frameworks_base/0065`.
- **Monochrome notification icons** where the app has a monochrome icon.
  `patches/kompakt/frameworks_base/0066`.
- **A heads-up sits 16dp from the top**, the same as from the sides; it was 8dp, which
  looked cramped where the status bar is hidden.
- **No lines above a heads-up.** Hidden notifications behind it left their
  outlines. `patches/kompakt/frameworks_base/0083`.
- **The Media settings are gone.** They only applied to the shade's media
  player, which was already off in 1.1. `patches/kompakt/packages_apps_Settings/0003`.
- **Borders are one weight: 2 px**, in quick settings, the brightness slider,
  the footer buttons and Messaging. A selected message bubble is 4 px.
  `patches/kompakt/frameworks_base/0097`, `packages_apps_Messaging/0001`,
  `vendor/kompakt/overlay-static` (Messaging).

### Status bar and Quick Settings

- **The status bar starts and ends 16dp from the edges**, like the
  lockscreen (was 26dp and 28dp). `device_phh_treble/0013`, `frameworks_base/0073`,
  `vendor/kompakt/overlay`.
- **The status bar stays still when the shade opens or closes.**
  `patches/kompakt/frameworks_base/0069`-`0072`, `0093`, `0094`, `0105`.
  - It no longer empties out and comes back in steps.
  - The shade header draws the clock, icons and battery where the status bar
    does, at the same clock weight, after a boot too.
  - The header, its Wi-Fi icon and Clear all arrive with the panel.
- **Camera and microphone show only the dot.** `patches/kompakt/frameworks_base/0107`,
  `system_animation_ongoing_dot.xml` in the SystemUI overlay.
  - No chip sliding in first.
  - Black in light mode, white in dark mode, with a thin ring of the other
    ink so it shows on the camera's viewfinder.
  - A little further from the edge.
- **A Quick Settings tile per e-ink mode.** `vendor/kompakt/KompaktService`.
  - Fast, Fast soft, Fast + dither, Text, Quality, Auto, beside the E-ink tile
    that steps through them.
  - A tap switches to that mode; the current mode's tile is on.
- **The e-ink mode tiles have their own group** in the tile editor, E-ink
  modes. `patches/kompakt/frameworks_base/0108`.
- **The tile editor's header is one compact row.** The title sat in a 64dp
  bar with padding, and the hint centred in 48dp below it: air above and
  below both. `patches/kompakt/frameworks_base/0109`.
- **A Bars battery style**, as on MuditaOS: up to four bars, one per 25%. It
  takes the Dotted circle slot in Battery status style.
  `patches/kompakt/frameworks_base/0088`, `vendor/kompakt/overlay-static` (LineageParts).

### Volume dialog

- **The older volume dialog is back.** `patches/kompakt/frameworks_base/0061`, `0064`, `0067`, `0068`.
  - One outlined pill with a pill slider, instead of Android 16's boxed slider.
  - The empty part of the slider is outlined, not filled, with no shadow.
  - A sixth smaller, with a longer slider.

### E-ink

- **The same default temperature as MuditaOS (+11).** `vendor/kompakt/KompaktService`.
  - The temperature correction defaults to +11, the driver's and MuditaOS's
    value. 1.1 set 0 at every boot.
  - A phone with 0 saved moves to +11 once.
  - A changed offset applies straight away and is kept after a reboot.
- **Every lock clears the panel**, as on stock; it ran at every eighth lock,
  leaving imprints on the sleep screen. `LockFlash.kt`.
- **A custom sleep picture is back right after a reboot**, not only after the
  first unlock. `SleepImage.kt`.

### Animations

- **Animations off covers more.** `patches/kompakt/frameworks_base/0049`, `0053`,
  `0058`, `packages_apps_Launcher3/0002`, `vendor/kompakt/overlay-static` (Dialer).
  - SystemUI and Recents animations built on androidx animators.
  - Older view animations in every app: the dialpad, sliding menus, pages
    that snap into place.
- **Going home no longer flashes black** with animations off.
  `patches/kompakt/frameworks_base/0098`.
- **No touch ripples**, in any app. D-pad focus is a black or white outline
  and shows only when moving with the D-pad. `patches/kompakt/frameworks_base/0051`, `0059`.
- **No wallpaper zoom** when the shade, Recents or an app opens.
  `config_wallpaperMaxScale` in `vendor/kompakt/overlay`.
- **Recents no longer leaves an app card on the home screen.**
  `patches/kompakt/packages_apps_Launcher3/0005`.

### Speed

- **Precompiled apps are used again.** `vendor/kompakt/kompakt.mk`.
  - The GSI compiled for the userfaultfd GC, which the 4.19 kernel cannot
    run, so ART threw that code away and apps ran interpreted.
  - The build now compiles for the collector the phone uses.
  - Settings, Phone, Contacts, Messaging, the keyboard, Calendar, Clock and the
    Kompakt app are compiled in full.
  - SystemUI is about a quarter faster, and apps launch 5-15% faster.
- **Swiping and scrolling are smoother.** KompaktDevice's vendor
  (`vendor/scripts/apply-power-hints.sh`).
  - Each touch holds the CPU at 1.075 GHz or more for a second, as MuditaOS
    does while scrolling.
  - Swiping in Settings: the CPU was below that 46% of the time, now 11%;
    janky frames 26% before, 20% now.

### Battery

- **Battery usage shows real numbers.** `vendor/kompakt/overlay` (`power_profile.xml`).
  - Mudita's vendor ships Android's placeholder profile, so the page was
    empty on stock too.
  - The profile uses the Infinix X626's figures for the same chip and this
    phone's gauge for the screen and sleep.
  - Capacity 3037 mAh, what the phone charges the cell to (rated 3300 mAh at
    4.45 V, charged to 4.34 V).
- **Battery saver no longer turns on dark theme.** `vendor/kompakt/overlay`
  (`config_batterySaver_full_enableNightMode`).

### Fonts

- **A system font choice.** `patches/kompakt/frameworks_base/0087`, `vendor/kompakt/fonts`.
  - Google Sans (the default), Roboto, Manrope, Noto Serif, Source Serif, Lato
    or JetBrains Mono.
  - Kompakt app → Style → Font. The phone restarts to apply.
  - The Font list shows each name in its own font.
  - Settings, the shade and every app without its own font follow it.
  - Google Sans is now Google Sans everywhere, not LineageOS's mix of Google
    Sans and Roboto.
  - Titles keep the title weight, body text stays regular.
  - Letters a font lacks come from the font it replaces.
  - The lock screen clock follows it, and the clock's width slider (Wallpaper &
    style → Clock) sets its weight in every font: each step picks the nearest
    weight the font has. `patches/kompakt/frameworks_base/0111`.
  - Lato is Lato 2 with Medium and SemiBold. Source Serif 4 is the text cut.
    Noto Serif gains Medium and SemiBold.
  - Greek and Cyrillic: Google Sans is not offered, and Roboto is the default.
  - Chinese, Japanese, Korean, Arabic, Hindi and other languages no font
    covers: the Font row is hidden and Android's own fonts are used.
- **TrebleDroid's Font family option is gone.** It changed only some screens.
  `patches/kompakt/treble_app/0005`.

### Emoji

- **Black and white emoji by default**: Google's Noto Emoji, following the
  text colour in dark mode. `patches/kompakt/frameworks_base/0074`,
  `external_noto-fonts/0001`, `vendor/kompakt/fonts`.
  - Colour emoji: Kompakt app → Style → Emoji. The phone restarts to apply.
  - Symbols like ☺️ ❤️ ☀️ are black and white too (all 371).
    `vendor/kompakt/fonts/add-emoji-vs.py`.
  - Flags and the newest emoji stay in colour; the black and white font lacks
    them.

### Settings

- **Every page turns a page per swipe**: the Compose pages (All apps), the
  LineageOS pages, Default apps, Device diagnostics and the emergency alert
  settings. `patches/kompakt/frameworks_base/0099`, `0104`.
- **Every page has a fixed header**, no growing title.
  `patches/kompakt/frameworks_base/0102`.
- **The main page's pages line up.** The same space above the first row on
  every page, the last one included. `vendor/kompakt/overlay-static`,
  `patches/kompakt/packages_apps_Settings/0001`.
- **A search result opens at its setting**, at the top of the page.
  `patches/kompakt/packages_apps_Settings/0001`.
- **Monochrome app icons** in All apps, app info, battery usage and the other
  app lists, where the app has one. `patches/kompakt/frameworks_base/0095`,
  `packages_apps_Settings/0001`.
- **All apps scrolls** without tapping the search box first.
  `patches/kompakt/frameworks_base/0055`.
- **The search bar has a 2px outline.** `vendor/kompakt/overlay-static` (Settings, `search_bar.xml`).
- **"Passwords & accounts"** fits one line. `vendor/kompakt/overlay-static`.
- **Hidden: Night Light and Extra dim** (they only turned whites grey) and
  **Lift to wake** (the sensor hub has no lift algorithm).
  `vendor/kompakt/overlay`, `patches/kompakt/packages_apps_Settings/0002`.
- **Hidden on the Lock screen page: Widgets on lock screen** (the feature is
  gone) and the **When to show** heading, which had no rows under it.
  `patches/kompakt/vendor_lineage/0004`,
  `patches/kompakt/packages_apps_Settings/0006`.

### Dialogs

- **Material dialogs get the dialog border** (Calendar and other Material 3
  apps). `patches/kompakt/frameworks_base/0050`.
- **Permission prompts in dark mode** are black with a white icon.
  `vendor/kompakt/KompaktPermissionControllerOverlay`.

### Apps

- **Phone:** `vendor/kompakt/overlay-static` (Dialer), `patches/kompakt/packages_apps_Dialer/0001`, `0003`, `0005`.
  - Mute, Speaker and the other call buttons show when they are on.
  - The answer button and the dialpad button are outlined, with no shadow;
    the dialpad button is a rounded square, as the Calendar and Messaging
    buttons.
  - The selected tab's name is black; the search hint is solid.
  - Recents, Contacts and search results turn a page per swipe.
- **Contacts:** `patches/kompakt/packages_apps_Contacts/0002`-`0007`, `vendor/kompakt/overlay-static`.
  - The add button is a rounded square, as in Calendar and Messaging.
  - The list turns a page per swipe.
  - A contact opens full screen, with a status bar in the page colour.
  - Without a photo: the placeholder icon is drawn in ink, so the name and buttons stay readable.
  - Over a photo: the name on a white strip, outlined icon circles.
  - The menu: no grey, no separators, the selected item a pill.
  - Letter avatars and dialogs follow the mode.
- **Messaging:** `patches/kompakt/packages_apps_Messaging/0002`, `0003`.
  - Dialog buttons are visible in both modes.
  - Start chat is the same outlined square as Calendar's add button.
- **Calendar:** `patches/kompakt/packages_apps_Etar/0002`-`0008`, `vendor/kompakt/overlay-static` (Etar).
  - Dialogs and pickers are white or black, not grey.
  - Events are black (white in dark mode), not the account colour.
  - A swipe changes the month, week or day without scrolling; up or down in day and
    week views moves the hours a page.
  - The agenda turns a page per swipe; past days are not grey.
  - The menu is full width and opens and closes without animation.
- **Clock:** `patches/kompakt/packages_apps_DeskClock/0003`-`0006`, `vendor/kompakt/overlay-static`.
  - The alarm screen is readable in light mode.
  - Repeat days, the alarm label, the sound picker and settings are black and
    white.
  - A paused stopwatch no longer blinks.
  - The selected tab has a pill; the add button is outlined.
- **Camera:** `patches/kompakt/packages_apps_Aperture/0002`-`0005`, `vendor/kompakt/overlay-static` (Aperture).
  - Styles: Normal, B&W or Film for photos; Normal, B&W or VHS for videos.
    Applied after saving; the original is kept if a style fails.
  - VHS videos are 4:3 at 480 lines.
  - Save to SD card (Camera settings).
  - The status bar hides again after a swipe.
  - Labels follow the system font.
  - The settings page has paged scrolling under a fixed header.
- **Music:** `patches/kompakt/packages_apps_Twelve/0002`-`0011`, `0013`, `vendor/kompakt/overlay-static` (Twelve).
  - Outlined buttons, covers and mini player, with no shadows.
  - Paged scrolling in every list, the Activity rows of covers included (a
    sideways swipe); tabs change without sliding.
  - The settings page has paged scrolling under a fixed header.
  - Album and playlist covers hide on a swipe up and return at the top.
  - The player's title, artist and album use the full width.
  - Settings → Floating buttons hides Play random and the play buttons.
  - No visualizer.
  - Albums, Artists, Genres and Playlists are the tabs at the bottom, and the
    header names the page; search and a menu button for settings; the
    provider and Activity are in Settings.
  - Albums is a list with small covers, four to a page.
  - Settings → Dither covers shows album art in black and white with an
    ordered dither; covers that are not square fill their square.
  - The mini player sits right above the tabs, which get shorter while it
    shows; it appears without sliding.
  - Pages show as soon as they are built, complete, with the mini player in
    place in the same frame.
  - Coming back to the library shows the page of the lit tab; it showed
    Albums under another tab.
  - Pages change in one step, with nothing of the old page under the new one.
  - The playing song on an album page shows its title and a playing icon;
    both were white.
  - Genre, artist and playlist pages (Favorites too) have a plain header with
    no cover image, and list their albums, playlists and songs as rows; album covers are shorter,
    with a stronger fade under the name.
  - The tab labels use the system font, as in the Clock app.
  - Thinner 2px outlines, less space around the sort filter, and a thin line
    for the rest of the progress bar.
    `patches/kompakt/packages_apps_Twelve/0013`.
- **Files:** `patches/kompakt/packages_apps_DocumentsUI/0002`-`0005`.
  - Turns a page per swipe; the path and filter chips hide on the first page
    down.
  - Files, folders and chips have a 2px outline.
  - The selected place in the menu is a pill.
- **Gallery:** the selected tab has a pill. `vendor/kompakt/overlay-static`.
- **Wallpaper & style:** `patches/kompakt/packages_apps_WallpaperPicker2/0001`-`0006`.
  - Lock and home previews side by side, outlined.
  - Tap a preview or its label for that screen's options.
  - Turns a page per swipe; the first swipe up collapses the previews.
  - Tapping the clock preview does nothing; it switched between the round and
    sharp digits (pinned to sharp) and crashed the picker.
    `patches/kompakt/packages_apps_ThemePicker/0001`.
  - A swipe anywhere on the page turns a page, not only on the options.
    `patches/kompakt/packages_apps_WallpaperPicker2/0007`.
  - The Shortcuts page has no previews, and the shortcuts are four to a row.
    `patches/kompakt/packages_apps_ThemePicker/0002`.
- **Keyboard:** emoji pages stay put on a swipe, and the emoji tabs are in
  full ink. `patches/kompakt/packages_inputmethods_LatinIME/0002`.
- **eSIM manager (OpenEUICC):** the add button is the rounded square of the
  other apps, outlined, with no shadow; it was the Material accent disc with a
  shadow. `patches/openeuicc/0001`.

- **Katapult 1.7** (versionCode 8, same signing key). `vendor/kompakt/prebuilt/Katapult.apk`.
- **inkOS 0.7, build 101015** (same signing key). `vendor/kompakt/prebuilt/inkOS.apk`.

### Kompakt app

- **A new layout**, like the system apps.
  - Four tabs: Display, Offline, Style, Settings.
  - Style holds Font, Emoji and Display width.
  - The ⋮ menu opens Advanced: Temperature offset and Diagnostics.
  - An app's e-ink sheet marks Follow master mode when it has no mode of its own.
- **Hold Recents for quick settings** (Settings tab, off by default). Holding
  it again closes them. `patches/kompakt/frameworks_base/0092`, `0110`.
- **Brightness on/off for fingerprint gestures**: switch the front light off
  and back to its last level, shared with inkOS's brightness gesture.
- **Fingerprint Switch mode goes through every enabled mode**, not only the
  top two.
- **Refresh on unlock works reliably.** The flash lasts longer (it was 180 ms
  and often did not show); the long-press refresh uses it too.
- **Security & privacy no longer flags Per-app e-ink mode.** It declares
  itself an accessibility tool.
- **The app shows the current e-ink mode when brought back.** A mode changed
  from a Quick Settings tile or the fingerprint sensor while the app was in the
  background left the old mode highlighted; the page is rebuilt on resume.
  `KompaktActivity.kt`.
- **An app's own e-ink mode comes back after an unlock.** meink switches to A2
  on every wake and the screen-on re-apply used the main mode, while the
  per-app watcher saw the same app again and did nothing, so a reader in Text
  stayed in Fast until it was reopened. A fingerprint unlock sends `USER_PRESENT`
  before `SCREEN_ON`, so both now ask `isKeyguardLocked`: locked gets the main
  mode, unlocked the app in front its own. The refresh flash (long press home, refresh on unlock) is
  a window of ours that the watcher took for an app switch, leaving the app in
  the Kompakt app's mode; the watcher now ignores it. `EinkAppWatcher.kt`,
  `OfflineWatcherService.kt`, `Flash.kt`.
- **An app keeps its own e-ink mode while the keyboard is open.** The watcher
  took the newest window for the app in front, and the keyboard's window (its
  package has no mode of its own) switched the browser from Fast dither to Text
  three seconds after it opened, as the log showed. The app in front is now the
  top activity, so the keyboard or a dialog of another app is not an app switch;
  per-app invert had the same flaw. `EinkAppWatcher.kt`.
- **Fingerprint taps and holds keep the screen on.** Swipes are injected input
  and count as use, but the tap, double-tap, triple-tap and hold actions (and
  brightness swipes) ran without input, so the screen timeout kept counting.
  They now call `PowerManager.userActivity` (`DEVICE_POWER`). `FingerScroll.kt`.

### Apps that check the bootloader

- **Securize is on by default**, so banking apps see a locked bootloader.
  `vendor/kompakt/etc/init/kompakt-securize.rc`.
  - 1.0 and 1.1 switched it off.
  - Switch it off in TrebleDroid Settings → Misc.

## 1.1 (2026-10-03)

System build 288, with boot 269 and vendor 264 (Global) / 264-usa (USA).

### Calls

- **Wi-Fi calling.** An IMS or XCAP APN without `network_type_bitmask` goes to
  the modem with every network type, and an IMS APN without `profile_id` as
  profile 2. The MediaTek modem read the empty bearer bitmap as no network and
  refused the IMS PDN over Wi-Fi.
  `patches/kompakt/frameworks_opt_telephony/0001`.
- **IMS data call through the RIL.** `config_wlan_data_service_package` is
  `com.android.phone`, so the modem builds the tunnel through its own ePDG
  daemon instead of Google Iwlan. `patches/kompakt/device_phh_treble/0012`.
- **IMS and XCAP APNs** from the MuditaOS list for carriers that had none:
  955 entries, IMS for 641 networks, XCAP for 340.
  `patches/kompakt/vendor_apn/0001`.
- **4G calling forced on by default** in the Treble app, so VoLTE and Wi-Fi
  calling are available from the first boot. `patches/kompakt/treble_app/0003`.
- **Create IMS APN** shows a toast for each outcome.
  `patches/kompakt/treble_app/0002`.
- **Airplane mode keeps the radio off.** KompaktService's offline switch no
  longer powers the radio on while airplane mode is on.
  `vendor/kompakt/KompaktService`.

## 1.0

System build 281, with boot 269 and vendor 263 (Global) / 263-usa (USA).

- First release.
