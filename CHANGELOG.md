# Changelog

The system image. Each release ships as one full update package together with
the boot and vendor images from
[KompaktDevice](https://github.com/gezimos/KompaktDevice), listed per release.

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
  daemon instead of Google Iwlan. `patches/kompakt/device_phh_treble/0015`.
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
