Local manifest additions beyond MisterZtr's treble_manifest. `build.sh` copies
every XML file here into `.repo/local_manifests/`.

- `overrides.xml`: named to sort after MisterZtr's `manifest.xml`, since repo
  reads local manifests in name order and an `extend-project` must come after
  the project it extends.
  - pins Twelve to the commit our patch series and its overlay-static layouts
    are ported to. Upstream keeps rewriting the screens we restyle, so a move
    of the pin is a port: rebase the series and three-way merge every
    overlay-static layout upstream changed.
  - pins the GSI projects (LineageOS_gsi, TrebleDroid, phhusson, MindTheGapps)
    to the commits the TrebleDroid layer and our series are tested on. Their
    manifest tracks branches, so without the pins a later push changes what the
    series applies to.

`build.sh` also checks MisterZtr's treble_manifest out at a fixed commit, and
applies `LineageOS_gsi/patches/apply-patches.sh` before `patches/apply-patches.sh`.

vendor/kompakt ships as source in this repository and build.sh copies it into
the tree, so it needs no manifest entry.
