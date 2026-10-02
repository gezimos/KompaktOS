#!/bin/bash
# Apply the Kompakt patch series to a synced LineageOS tree.
#
#     bash patches/apply-patches.sh /path/to/lineage
#
# Each directory under kompakt/ is one upstream project, and the numbered files
# inside it are a git format-patch series, so `git am` keeps the messages and the
# authorship. That is the whole point of shipping them this way: when LineageOS
# moves, a series can be rebased and a conflicting patch tells you which change
# it belongs to, which a single squashed diff cannot.
set -u

TREE=${1:-$PWD}
HERE=$(cd "$(dirname "$0")" && pwd)

# Directory name -> path in the tree. Spelled out rather than derived, because
# underscores are not reliably path separators: treble_app is one directory.
declare -A PROJECT=(
  [bionic]=bionic
  [frameworks_base]=frameworks/base
  [frameworks_native]=frameworks/native
  [packages_apps_Launcher3]=packages/apps/Launcher3
  [packages_apps_Settings]=packages/apps/Settings
  [packages_apps_Dialer]=packages/apps/Dialer
  [packages_apps_Contacts]=packages/apps/Contacts
  [packages_apps_DeskClock]=packages/apps/DeskClock
  [packages_apps_Messaging]=packages/apps/Messaging
  [packages_apps_DocumentsUI]=packages/apps/DocumentsUI
  [packages_apps_Etar]=packages/apps/Etar
  [packages_apps_AudioFX]=packages/apps/AudioFX
  [packages_apps_Jelly]=packages/apps/Jelly
  [packages_apps_LineageParts]=packages/apps/LineageParts
  [packages_apps_Aperture]=packages/apps/Aperture
  [packages_apps_Twelve]=packages/apps/Twelve
  [packages_apps_Updater]=packages/apps/Updater
  [packages_inputmethods_LatinIME]=packages/inputmethods/LatinIME
  [packages_modules_Connectivity]=packages/modules/Connectivity
  [system_sepolicy]=system/sepolicy
  [device_phh_treble]=device/phh/treble
  [build_release]=build/release
  [frameworks_av]=frameworks/av
  [lineage_sdk]=lineage-sdk
  [vendor_lineage]=vendor/lineage
  [treble_app]=treble_app
  [vendor_hardware_overlay]=vendor/hardware_overlay
)

fail=0
applied=0

for dir in "$HERE"/kompakt/*/; do
  name=$(basename "$dir")
  proj=${PROJECT[$name]:-}

  if [ -z "$proj" ]; then
    echo "SKIP  $name (no project mapping)"
    fail=$((fail + 1))
    continue
  fi
  if [ ! -d "$TREE/$proj/.git" ]; then
    echo "SKIP  $proj (not in the tree; sync it first)"
    fail=$((fail + 1))
    continue
  fi

  patches=("$dir"[0-9]*.patch)
  [ -e "${patches[0]}" ] || continue

  echo "APPLY $proj  (${#patches[@]} patch(es))"
  if ! git -C "$TREE/$proj" am --keep-non-patch "${patches[@]}"; then
    echo "FAIL  $proj -- resolve by hand, then: git -C $TREE/$proj am --continue"
    git -C "$TREE/$proj" am --abort 2>/dev/null
    fail=$((fail + 1))
    continue
  fi
  applied=$((applied + ${#patches[@]}))
done

echo
echo "applied $applied patch(es)"
if [ "$fail" -ne 0 ]; then
  echo "$fail project(s) did not apply cleanly"
  exit 1
fi
echo "ALL_OK"
