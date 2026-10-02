OpenEUICC is an upstream checkout and is deliberately not vendored here.

build.sh clones it to vendor/kompakt/OpenEUICC at the commit below, applies the
two local changes in patches/openeuicc, and copies the prebuilt AAR from this
directory in (upstream resolves that through Gradle, which this build cannot).

  upstream commit: 9a537a2
  apply:  git apply ../../../patches/openeuicc/0001-Kompakt-openeuicc-local-changes.diff
  extras: cp -a prebuilts/openeuicc/app-deps/prebuilts vendor/kompakt/OpenEUICC/app-deps/

**This lives outside vendor/ on purpose.** build.sh copies vendor/kompakt into
the tree wholesale, so while these files sat at vendor/kompakt/OpenEUICC-extra
the tree ended up with two copies of app-deps/prebuilts/Android.bp and soong
failed with "module prebuilt_OpenEUICC_com.journeyapps_zxing-android-embedded
already defined". Anything that is build input rather than build output belongs
here, not under vendor/.

Without OpenEUICC the build still completes; PRODUCT_PACKAGES loses it and the
device has no eSIM manager.
