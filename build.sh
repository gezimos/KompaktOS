#!/bin/bash
# Build the KompaktOS system image from a clean machine.
#
#     STOCK_SYSTEM=<MuditaOS K 1.6.0 system.img> bash build.sh [/path/to/tree]
#
# Defaults to ./lineage. Needs about 250 GB of disk.
set -eu

TREE=${1:-$PWD/lineage}
HERE=$(cd "$(dirname "$0")" && pwd)
JOBS=$(nproc 2>/dev/null || sysctl -n hw.ncpu)

BRANCH=lineage-23.2
TARGET=lineage_arm64_bgN4-bp4a-userdebug     # GApps, ext4. bvN4 is the vanilla one.

mkdir -p "$TREE"
cd "$TREE"

if [ ! -d .repo ]; then
  repo init -u https://github.com/LineageOS/android.git -b "$BRANCH" --git-lfs
fi

# Device projects come from MisterZtr's manifest, used unforked.
if [ ! -d .repo/local_manifests ]; then
  git clone https://github.com/MisterZtr/treble_manifest.git .repo/local_manifests -b "$BRANCH"
fi

repo sync --force-sync --optimized-fetch --no-tags --no-clone-bundle --prune -j"$JOBS"

bash "$HERE/patches/apply-patches.sh" "$TREE"

# vendor/kompakt ships as source, copied into the tree.
rm -rf "$TREE/vendor/kompakt"
cp -a "$HERE/vendor/kompakt" "$TREE/vendor/kompakt"

# Mudita's charging screen is not in this repository: take it from their image.
if [ ! -f "$TREE/vendor/kompakt/charger/kpoc_charger" ]; then
  bash "$TREE/vendor/kompakt/charger/extract-from-stock.sh" \
      "${STOCK_SYSTEM:?set STOCK_SYSTEM to the MuditaOS K 1.6.0 system.img}"
fi

# OpenEUICC: pinned upstream clone, recursive, with our patch and prebuilt AAR.
OPENEUICC_URL=https://gitea.angry.im/PeterCxy/OpenEUICC
OPENEUICC_REV=9a537a25163c5159899260fb6191a5da35a692bd

if [ ! -d "$TREE/vendor/kompakt/OpenEUICC/.git" ]; then
  echo "cloning OpenEUICC at ${OPENEUICC_REV:0:12}"
  if git clone "$OPENEUICC_URL" "$TREE/vendor/kompakt/OpenEUICC"; then
    (
      cd "$TREE/vendor/kompakt/OpenEUICC"
      git checkout -q "$OPENEUICC_REV"
      git submodule update --init --recursive
      git apply "$HERE/patches/openeuicc/0001-Kompakt-openeuicc-local-changes.diff"
      # prebuilts/ stays outside vendor/: a second copy of its Android.bp breaks soong.
      cp -a "$HERE/prebuilts/openeuicc/app-deps/prebuilts" app-deps/
    )
  else
    echo
    echo "warning: could not clone OpenEUICC, so this build will have no eSIM"
    echo "         manager. Everything else is unaffected."
    echo
  fi
fi

# Signing: see signing.md. Keys live in vendor/lineage-priv/keys, never here.

( cd "$TREE/device/phh/treble" && bash generate.sh lineage )

# TrebleApp.apk is not committed upstream; build it from the patched source.
( cd "$TREE/treble_app" && bash build.sh release )

# generate.sh sets the brand after the ROM inherit, so rewrite the generated file.
for mk in "$TREE"/device/phh/treble/lineage_arm64_*.mk; do
  [ -f "$mk" ] || continue
  python3 - "$mk" <<'PY'
import re, sys
p = sys.argv[1]
s = open(p).read()
for var, val in (("PRODUCT_BRAND", "Mudita"), ("PRODUCT_SYSTEM_BRAND", "Mudita"),
                 ("PRODUCT_MODEL", "Kompakt"), ("PRODUCT_SYSTEM_MODEL", "Kompakt"),
                 ("PRODUCT_MANUFACTURER", "Mudita"),
                 ("PRODUCT_SYSTEM_MANUFACTURER", "Mudita")):
    line = "%s := %s" % (var, val)
    if re.search(r"^%s\s*:=.*$" % var, s, re.M):
        s = re.sub(r"^%s\s*:=.*$" % var, line, s, flags=re.M)
    else:
        s = s.rstrip("\n") + "\n" + line + "\n"
open(p, "w").write(s)
PY
done
grep -H -E '^PRODUCT_(SYSTEM_)?(BRAND|MODEL|MANUFACTURER)' "$TREE"/device/phh/treble/lineage_arm64_*.mk

set +u
source build/envsetup.sh
set -u
lunch "$TARGET"

# Compile check SystemUI and services before the full image.
m SystemUI services -j"$JOBS"

make systemimage -j"$JOBS"

IMG=$(ls -t "$TREE"/out/target/product/*/system.img | head -1)
echo
echo "image:  $IMG"
echo "md5:    $(md5sum "$IMG" | cut -d' ' -f1)"
echo "size:   $(stat -c %s "$IMG") bytes"
echo
echo "Pack it into an update package with KompaktDevice's tools/make-full-ota.sh."
