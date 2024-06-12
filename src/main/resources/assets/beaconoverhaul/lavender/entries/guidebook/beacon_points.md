```json
{
    "$schema":  "https://raw.githubusercontent.com/solonovamax/lavender/1.20.3/lavender-entry.json",
    "title":    "Beacon Formulas",
    "icon":     "minecraft:comparator",
    "category": "beaconoverhaul:beacon",
    "ordinal":  4
}
```

What are points?

Points are used to calculate different properties of a beacon, such as their range, the duration of effects, and the level of effects.
Different block types add more or less points, based on the formula associated with it.

;;;;;

The range of the beacon (in blocks) is computed according to:

[TODO]

---

Where 'pts' is the number of points the beacon has.

;;;;;

The duration of the beacon effects (in seconds) is computed according to:

[TODO]

---

Where 'pts' is the number of points the beacon has.

;;;;;


The level of the primary effect from beacons is computed according to:

[TODO]

The level of the secondary effect from beacons is computed according to:

[TODO]

---

Where 'pts' is the number of points the beacon has, and 'isPotent' 1 if no secondary effect is selected, and 0 if a secondary effect is
selected, at tier 4 or higher.
