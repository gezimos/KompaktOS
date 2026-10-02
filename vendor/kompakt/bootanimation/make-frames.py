# Boot animation frames: LineageOS's motion redrawn with the boot logo's rings.
#
#     python3 make-frames.py boot-logo.svg track.json out/
#     cd out && zip -0 -r ../bootanimation.zip desc.txt part0 part1 part2
#
# The zip must be stored (-0).
import io, json, math, os, re, sys
import cairosvg
from PIL import Image

SRC, TRACK, OUT = sys.argv[1], sys.argv[2], sys.argv[3]
svg = open(SRC).read()
track = json.load(open(TRACK))
C = (240.19, 400.44); RBIG = 64.75; R = 28.78; SW = 14.57
LEFT = (96.3, 436.41); RIGHT = (384.08, 436.41)
LINES = re.search(r'<path class="cls-1" d="([^"]+)"/>', svg).group(1)
base = re.sub(r'<circle[^>]*/>', '', svg)
base = re.sub(r'<path class="cls-1"[^>]*/>', '', base)
base = base.replace('</defs>', '</defs><rect width="480" height="800" fill="#fff"/>', 1)

# LineageOS's final side rings sit at (190.8, 88.3) and (288.2, 88.3); ours at
# LEFT and RIGHT. That pair fixes the scale and the offset.
S = (RIGHT[0] - LEFT[0]) / (288.2 - 190.8)
mx = lambda x: C[0] + (x - 239.5) * S
my = lambda y: LEFT[1] + (y - 88.3) * S
A0 = 13.45                  # the original ring's settled outer radius
OUTER = R + SW / 2          # ours

def ellipse(cx, cy, a, b, ang):
    # A tilted ring keeps the logo's full stroke: its outline follows the
    # original's ellipse, and the stroke stays 14.57 all the way round.
    b = max(b, 0.55 * a)    # never flatter than this: the hole stays open
    rx = max(OUTER * a / A0 - SW / 2, 2)
    ry = max(OUTER * b / A0 - SW / 2, 2)
    return (f'<ellipse class="cls-1" cx="{cx:.2f}" cy="{cy:.2f}" rx="{rx:.2f}" ry="{ry:.2f}" '
            f'transform="rotate({ang:.1f} {cx:.2f} {cy:.2f})"/>')

def ring(x, y, r=R):
    return f'<circle class="cls-1" cx="{x:.2f}" cy="{y:.2f}" r="{r:.2f}"/>'

def save(png, path):
    g = Image.open(io.BytesIO(png)).convert('RGBA')
    bg = Image.new('RGBA', g.size, (255, 255, 255, 255)); bg.alpha_composite(g)
    bg.convert('L').point(lambda v: round(v / 17) * 17).convert('RGB').save(path)

def render(marks, path, whole=None):
    s = whole or base.replace('</svg>', ''.join(marks) + '</svg>')
    save(cairosvg.svg2png(bytestring=s.encode(), output_width=480, output_height=800), path)

def tracked(key, centre_to=None):
    out = []
    for c in track[key]:
        if key.startswith('part2') and c['hole'] == 0 and c['b'] > 3:   # the dot
            continue
        x, y = mx(c['cx']), my(c['cy'])
        if centre_to is not None and abs(c['cx'] - 239.5) < 2 and c['a'] > 14:
            # the growing centre ring heads for our big ring's centre
            t = min((c['a'] - 13.45) / (RBIG / OUTER * A0 - 13.45), 1)
            y = y + (C[1] - y) * t
        out.append(ellipse(x, y, c['a'], c['b'], c['ang']))
    return out

for d in ('part0', 'part1', 'part2'):
    os.makedirs(f'{OUT}/{d}', exist_ok=True)

STEP = 6                    # 60 fps source -> 10 fps
n = 0
for i in range(12, 60, STEP):   # 0-11: a blank frame and a sliver
    render(tracked(f'part0/{i:03d}.png'), f'{OUT}/part0/{n:03d}.png'); n += 1
n = 0
for i in range(60, 150, STEP):
    render(tracked(f'part1/{i:03d}.png'), f'{OUT}/part1/{n:03d}.png'); n += 1
n = 0
for i in range(150, 228, STEP):
    render(tracked(f'part2/{i:03d}.png', centre_to=C), f'{OUT}/part2/{n:03d}.png'); n += 1
# From here LineageOS joins the rings with the lines and pops the dot in;
# drawn on our exact geometry so it lands on the logo.
lines = f'<path class="cls-1" d="{LINES}"/>'
for r in (RBIG * 0.86, RBIG):
    render([ring(*C, r), ring(*LEFT), ring(*RIGHT), lines], f'{OUT}/part2/{n:03d}.png'); n += 1
for k in (0.45, 0.8, 1.12):
    dot = f'<circle cx="{C[0]}" cy="{C[1]}" r="{R * k:.2f}"/>'
    render([ring(*C, RBIG), ring(*LEFT), ring(*RIGHT), lines, dot], f'{OUT}/part2/{n:03d}.png'); n += 1
# Hold the finished logo with repeated frames; part pauses are skipped at exit.
HOLD = 15
logo = svg.replace('</defs>', '</defs><rect width="480" height="800" fill="#fff"/>', 1)
for _ in range(1 + HOLD):
    render(None, f'{OUT}/part2/{n:03d}.png', whole=logo); n += 1
open(f'{OUT}/desc.txt', 'w').write('480 800 10\nc 1 0 part0\nc 0 0 part1\nc 1 0 part2\n')
print('scale', round(S, 3), 'part2 frames', n)
