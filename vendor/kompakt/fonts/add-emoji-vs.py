#!/usr/bin/env python3
# Gives NotoEmoji-Regular.ttf the emoji variation sequences (U+FE0F) that NotoColorEmoji declares,
# so Android picks the monochrome font for symbols like U+263A U+FE0F too.
# Usage: add-emoji-vs.py NotoEmoji-Regular.ttf <out/.../system/fonts/NotoColorEmoji.ttf>
import sys
from fontTools.ttLib import TTFont
from fontTools.ttLib.tables._c_m_a_p import CmapSubtable

mono_path, colour_path = sys.argv[1], sys.argv[2]
colour = TTFont(colour_path)
wanted = {cp for t in colour["cmap"].tables if t.format == 14 for cp, _ in t.uvsDict.get(0xFE0F, [])}

mono = TTFont(mono_path)
drawn = set(mono.getBestCmap())
mono["cmap"].tables = [t for t in mono["cmap"].tables if t.format != 14]
vs = CmapSubtable.newSubtable(14)
vs.platformID, vs.platEncID, vs.language, vs.cmap = 0, 5, 0xFF, {}
vs.uvsDict = {0xFE0F: [(cp, None) for cp in sorted(wanted & drawn)]}
mono["cmap"].tables.append(vs)
mono.save(mono_path)
print(f"{len(wanted & drawn)} of {len(wanted)} sequences added, {len(wanted - drawn)} not drawn")
