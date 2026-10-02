# Renames kpoc_charger's DT_NEEDED libcutils.so to libkpoc.so (kpoc_shim.cpp).
# LD_PRELOAD cannot do it: init's domain transition drops it.
#
#     python3 patch-needed.py kpoc_charger.mudita kpoc_charger
import hashlib, sys

src, dst = sys.argv[1], sys.argv[2]
data = open(src, 'rb').read()
assert hashlib.md5(data).hexdigest() == '601d987bf92a77ba33cceaf53f663b64', 'not MuditaOS K 1.6.0 kpoc_charger'
old, new = b'libcutils.so\0', b'libkpoc.so\0'
assert data.count(old) == 1
new = new.ljust(len(old), b'\0')
open(dst, 'wb').write(data.replace(old, new))
print(hashlib.md5(data.replace(old, new)).hexdigest())
