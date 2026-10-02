# Signing

The keys are **not** in this repository and never will be. `.gitignore` blocks
`vendor/lineage-priv/`, `vendor/kompakt/security/`, `*.pk8`, `*.pem`,
`*.keystore` and `*.jks`. Without keys the tree builds and falls back to the
AOSP test keys, which is how a clone of this repo behaves.

Keep the master copy outside any git working tree. `git clean -xdf` deletes
ignored files, so a gitignored directory is a convenience, not a backup.

## Where they go

```
vendor/lineage-priv/keys/
├── keys.mk
├── releasekey.{pk8,x509.pem}      platform.{pk8,x509.pem}
├── media.* shared.* networkstack.* nfc.* bluetooth.* sdk_sandbox.* testkey.*
└── com.android.<module>.pem  +  com.android.<module>.avbpubkey   (35 of them)
```

`keys.mk` is two lines, and is the **only** place the certificate is set:

```make
PRODUCT_DEFAULT_DEV_CERTIFICATE := vendor/lineage-priv/keys/releasekey
PRODUCT_MAINLINE_BLUETOOTH_SEPOLICY_DEV_CERTIFICATES := vendor/lineage-priv/keys
```

`vendor/lineage/config/common.mk` already ends with
`-include vendor/lineage-priv/keys/keys.mk`, so nothing has to reference it.

## Why that exact path

LineageOS grants `release-keys` by a **literal string check on the variable's
text**, in both `build/make/core/config.mk` and `build/soong/android/config.go`:

```make
ifeq ($(DEFAULT_SYSTEM_DEV_CERTIFICATE),build/make/target/product/security/testkey)
BUILD_KEYS := test-keys
else ifneq ($(filter vendor/lineage-priv/%,$(DEFAULT_SYSTEM_DEV_CERTIFICATE)),)
BUILD_KEYS := release-keys
else
BUILD_KEYS := dev-keys
endif
```

Keys anywhere else are genuine release keys reported as `dev-keys`. That is
cosmetic — nothing in AOSP gates behaviour on `ro.build.tags`, and the root
detection libraries that read it only look for `test-keys` — but it is free to
get right.

**Set the certificate in exactly one place.** `PRODUCT_DEFAULT_DEV_CERTIFICATE`
is single-valued and the first-inherited value wins **silently**, so a second
setter in `vendor/kompakt/kompakt.mk` is a coin flip rather than a build error.

## APEX payload keys, the part that actually matters

`build/soong/apex/key.go` looks for each APEX's payload key in the certificate
directory and, when it is missing, **silently falls back to the module's own
directory** — no error, no warning. Those in-tree keys are committed to AOSP
source, so the private half is public.

The same payload key with the AOSP fallback and with ours:

```
com.android.tzdata.apex payload pubkey (sha1)
  fallback fb4f42cd084c4752908ae6df1d2d6b6ee7b3db8d   AOSP test key
  release  bb144b97d643d388c62a2ff235e207a92e9fa82d   ours
```

APEX accepts only SHA256_RSA4096, so these are 4096-bit. To regenerate:

```sh
O=out/target/product/generic_arm64
K=vendor/lineage-priv/keys
ls $O/system/apex/ | sed 's/\.capex$//; s/\.apex$//' | sort -u | while read m; do
    [ -f $K/$m.pem ] && continue
    openssl genrsa -f4 -out $K/$m.pem 4096
    python3 external/avb/avbtool.py extract_public_key \
        --key $K/$m.pem --output $K/$m.avbpubkey
done
```

Only generate for APEXes that declare an `apex_key` module; `com.android.apex.cts.shim`
does not and is skipped. Verify afterwards on a rebuilt APEX:

```sh
unzip -p $O/system/apex/com.android.tzdata.apex apex_pubkey | sha1sum
sha1sum vendor/lineage-priv/keys/com.android.tzdata.avbpubkey   # must match
```

## Mainline APKs

Ten APKs declare their own `certificate:` module, so the default cert never
reaches them and they ship signed with an AOSP in-tree key. `keys.mk` overrides
them against an `android_app_certificate` module declared in the same directory:

```make
PRODUCT_CERTIFICATE_OVERRIDES += ServiceWifiResources:kompakt-releasekey ...
```

The format is `<module_name>:<certificate_module_name>` — the **app** module on
the left, not the certificate module. The wrong way round is silently ignored,
not a build error. Find them all with:

```sh
find $OUT/apex $OUT/system -name '*.apk' | while read a; do
  unzip -p "$a" 'META-INF/*.RSA' | openssl pkcs7 -inform DER -print_certs -noout \
    | grep -q 'android@android.com' && basename "$a"
done | sort -u
```

That must print nothing.

## Not done

- **Re-signing target files.** `sign_target_files_apks` is not used:
  `PRODUCT_DEFAULT_DEV_CERTIFICATE` already signs every APK and APEX container
  at build time. The update package itself is signed with the release key by
  KompaktDevice's `tools/make-full-ota.sh`, and the LineageOS recovery accepts
  only packages signed with that key.
- **`-user` builds.** We build `-userdebug`. Signing is independent of the
  variant. `-user` would set `ro.debuggable=0`, costing `adb root` and
  `adb remount` while leaving `logcat`, `settings`, `dumpsys` and
  `reboot fastboot` working.
- **AVB hashtree signing of `system.img`** (`BOARD_AVB_SYSTEM_KEY_PATH`) is a
  separate mechanism from everything above and has not been reviewed.

## Proving the platform key without reading it

```sh
adb shell pm list packages -U com.kompakt.service   # must report uid:1000
```

A different platform key would deny the app the shared system uid and it would
not run at all. Use this instead of touching key material.
