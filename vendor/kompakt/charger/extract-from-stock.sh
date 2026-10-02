#!/bin/bash
# Takes Mudita's charging screen from MuditaOS K 1.6.0's system image, which
# this repository does not carry, and makes kpoc_charger with patch-needed.py.
#   extract-from-stock.sh <MuditaOS K 1.6.0 system.img>
set -euo pipefail
IMG=${1:?usage: extract-from-stock.sh <MuditaOS K 1.6.0 system.img>}
HERE=$(cd "$(dirname "$0")" && pwd)

take() {
    debugfs -R "cat $1" "$IMG" 2>/dev/null > "$HERE/$2"
    test "$(md5sum < "$HERE/$2" | cut -d' ' -f1)" = "$3" || { echo "$2: not MuditaOS K 1.6.0's"; exit 1; }
}
take /system/bin/kpoc_charger kpoc_charger.mudita 601d987bf92a77ba33cceaf53f663b64
take /system/lib64/libshowlogo.so libshowlogo.so 7add5c805eba7a24b32dffdbad3e5c52
python3 "$HERE/patch-needed.py" "$HERE/kpoc_charger.mudita" "$HERE/kpoc_charger" > /dev/null
echo CHARGER_OK
