package gay.solonovamax.beaconsoverhaul.util

import me.shedaniel.clothconfig2.api.ConfigBuilder

var ConfigBuilder.transparentBackground: Boolean
    get() = hasTransparentBackground()
    set(value) {
        setTransparentBackground(value)
    }
