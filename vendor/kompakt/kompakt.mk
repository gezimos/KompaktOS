# Kompakt additions.

# Static framework overlay: the monochrome palette plus the framework configs.
PRODUCT_PACKAGE_OVERLAYS += vendor/kompakt/overlay

# Product identity: Mudita / Kompakt. With Securize on (the default, see
# etc/init/kompakt-securize.rc), phh copies the vendor fingerprint over it at boot.
PRODUCT_BRAND := Mudita
PRODUCT_MANUFACTURER := Mudita
PRODUCT_MODEL := Kompakt

# The 4.19 kernel cannot run the userfaultfd GC, so ART uses the read barrier one; precompile for it.
OVERRIDE_ENABLE_UFFD_GC := false

# Fully precompiled, so the apps opened most start fast from the first boot after an update.
PRODUCT_DEXPREOPT_SPEED_APPS += \
    Settings \
    Dialer \
    Contacts \
    messaging \
    LatinIME \
    Etar \
    DeskClock \
    KompaktService \
    Aperture \
    Glimpse \
    DocumentsUI \
    Twelve \
    ExactCalculator \
    ThemePicker \
    Recorder \
    Jelly \
    KompaktFMRadio \
    TeleService \
    Katapult

# Securize on by default: see etc/init/kompakt-securize.rc.
PRODUCT_COPY_FILES += \
    vendor/kompakt/etc/init/kompakt-securize.rc:$(TARGET_COPY_OUT_SYSTEM)/etc/init/kompakt-securize.rc

# Signing: vendor/lineage-priv/keys/keys.mk. Set the cert in one place only.

# Static overlay: merged INTO the target APK at build time rather than turned into an RRO.
PRODUCT_PACKAGE_OVERLAYS += vendor/kompakt/overlay-static
PRODUCT_ENFORCE_RRO_EXCLUDED_OVERLAYS += vendor/kompakt/overlay-static

PRODUCT_PACKAGES += \
    Katapult \
    inkOS

# The userspace half of the Kompakt's hardware, replacing com.mudita.service.
PRODUCT_PACKAGES += KompaktService

# Google's setup wizard is prebuilt, so product overlays never reach it; its
# dark-mode buttons get the light ink from this overlay instead. Unused on Vanilla.
PRODUCT_PACKAGES += KompaktGoogleSetupWizardOverlay
PRODUCT_PACKAGES += KompaktPermissionControllerOverlay

# aee_core_forwarder -- see vendor/kompakt/aee/aee_core_forwarder.c.
PRODUCT_PACKAGES += aee_core_forwarder

# Mudita's power-off charging screen -- see vendor/kompakt/charger/kpoc_charger.rc.
PRODUCT_PACKAGES += kpoc_charger

# Boot animation: black on white at 10 fps, re-enabled over the vendor's nobootanimation.
TARGET_BOOTANIMATION := vendor/kompakt/bootanimation/bootanimation.zip
TARGET_BOOTANIMATION_HALF_RES := false
PRODUCT_PRODUCT_PROPERTIES += debug.sf.nobootanimation=0

# Monochrome emoji by default; the Kompakt app switches to colour (persist.sys.kompakt.emoji).
PRODUCT_PACKAGES += NotoEmoji-Regular.ttf

# JetBrains Mono, Lato, Manrope, Noto Serif and Source Serif, system font choices in the Kompakt app (persist.sys.kompakt.font).
PRODUCT_PACKAGES += \
    KompaktLato-Light.ttf \
    KompaktLato-LightItalic.ttf \
    KompaktLato-Regular.ttf \
    KompaktLato-Italic.ttf \
    KompaktLato-Medium.ttf \
    KompaktLato-MediumItalic.ttf \
    KompaktLato-SemiBold.ttf \
    KompaktLato-SemiBoldItalic.ttf \
    KompaktLato-Bold.ttf \
    KompaktLato-BoldItalic.ttf \
    KompaktLato-Heavy.ttf \
    KompaktLato-HeavyItalic.ttf \
    KompaktLato-Black.ttf \
    KompaktLato-BlackItalic.ttf \
    KompaktNotoSerif-Regular.ttf \
    KompaktNotoSerif-Italic.ttf \
    KompaktNotoSerif-Medium.ttf \
    KompaktNotoSerif-MediumItalic.ttf \
    KompaktNotoSerif-SemiBold.ttf \
    KompaktNotoSerif-SemiBoldItalic.ttf \
    KompaktNotoSerif-Bold.ttf \
    KompaktNotoSerif-BoldItalic.ttf \
    KompaktSourceSerif-Regular.ttf \
    KompaktSourceSerif-Italic.ttf \
    KompaktSourceSerif-Medium.ttf \
    KompaktSourceSerif-MediumItalic.ttf \
    KompaktSourceSerif-SemiBold.ttf \
    KompaktSourceSerif-SemiBoldItalic.ttf \
    KompaktSourceSerif-Bold.ttf \
    KompaktSourceSerif-BoldItalic.ttf \
    KompaktManrope-Regular.ttf \
    KompaktManrope-Medium.ttf \
    KompaktManrope-SemiBold.ttf \
    KompaktManrope-Bold.ttf \
    JetBrainsMono-Regular.ttf \
    JetBrainsMono-Italic.ttf \
    JetBrainsMono-Medium.ttf \
    JetBrainsMono-MediumItalic.ttf \
    JetBrainsMono-SemiBold.ttf \
    JetBrainsMono-SemiBoldItalic.ttf \
    JetBrainsMono-Bold.ttf \
    JetBrainsMono-BoldItalic.ttf

# Lockscreen text is black on any mostly light wallpaper, ink drawings included.
PRODUCT_SYSTEM_PROPERTIES += \
    persist.wallpapercolors.threshold=50 \
    persist.wallpapercolors.max_dark_area=100

# 30 media volume steps instead of 15; the lowest is half as loud.
PRODUCT_SYSTEM_PROPERTIES += ro.config.media_vol_steps=30
# Six call volume steps: the audio HAL makes speech levels 1 to 6 from the call volume, (int)(v*5)+1.
PRODUCT_SYSTEM_PROPERTIES += \
    ro.config.vc_call_vol_steps=6 \
    ro.config.vc_call_vol_default=4

# VoLTE and Wi-Fi calling switches on by default (PHH's override properties).
PRODUCT_SYSTEM_PROPERTIES += \
    persist.dbg.volte_avail_ovr=1 \
    persist.dbg.wfc_avail_ovr=1 \
    persist.dbg.allow_ims_off=1

# The keys under the screen vibrate on press, like any Android key with touch
# feedback on. Android only does that for keys marked VIRTUAL in their layout.
PRODUCT_COPY_FILES += \
    vendor/kompakt/usr/keylayout/mtk-tpd.kl:$(TARGET_COPY_OUT_SYSTEM)/usr/keylayout/mtk-tpd.kl

# MediaTek's codec service seccomp policy for the current libraries; rw-system.sh
# mounts it over the vendor's. See seccomp/mtk-c2.policy.
PRODUCT_COPY_FILES += \
    vendor/kompakt/seccomp/mtk-c2.policy:$(TARGET_COPY_OUT_SYSTEM)/etc/kompakt/mtk-c2.policy

# USB playback on the standard USB audio HAL instead of MediaTek's primary one;
# rw-system.sh mounts the policy over the vendor's. See audio/.
PRODUCT_COPY_FILES += \
    vendor/kompakt/audio/audio_policy_configuration.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/kompakt/audio_policy_configuration.xml \
    vendor/kompakt/audio/usb_audio_output_policy_configuration.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/kompakt/usb_audio_output_policy_configuration.xml

# eSIM.
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.se.omapi.uicc.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/android.hardware.se.omapi.uicc.xml \
    frameworks/native/data/etc/android.hardware.se.omapi.ese.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/android.hardware.se.omapi.ese.xml \
    frameworks/native/data/etc/android.hardware.se.omapi.sd.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/android.hardware.se.omapi.sd.xml

# NFC.
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.nfc.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/android.hardware.nfc.xml \
    frameworks/native/data/etc/android.hardware.nfc.hce.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/android.hardware.nfc.hce.xml \
    frameworks/native/data/etc/android.hardware.nfc.hcef.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/android.hardware.nfc.hcef.xml \
    frameworks/native/data/etc/android.hardware.nfc.ese.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/android.hardware.nfc.ese.xml \
    frameworks/native/data/etc/com.nxp.mifare.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/com.nxp.mifare.xml

# The Local Profile Assistant itself.
PRODUCT_PACKAGES += OpenEUICC

# Release version: About phone shows BP4A.251205.006.<version>. The build
# number stays in ro.build.version.incremental, which Diagnostics reads.
KOMPAKT_VERSION := 1.2
# The product build.prop repeats the build info and is read last, so it needs
# the version too.
PRODUCT_SYSTEM_PROPERTIES += ro.build.display.id=$(BUILD_ID).$(KOMPAKT_VERSION)
PRODUCT_PRODUCT_PROPERTIES += ro.build.display.id=$(BUILD_ID).$(KOMPAKT_VERSION)

# Updates: the latest GitHub release carries one list per edition and region.
# Named for 1.2, the first build that may update over the air; 1.1 reads the old name.
PRODUCT_SYSTEM_PROPERTIES += \
    lineage.updater.uri=https://github.com/gezimos/KompaktOS/releases/latest/download/{type}-{region}-from-1.2.json

# FM radio.
PRODUCT_PACKAGES += \
    KompaktFMRadio \
    libmtkfmjni
