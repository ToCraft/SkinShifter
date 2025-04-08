import java.util.*

plugins {
    id("dev.tocraft.modmaster.neoforge")
}

tasks.withType<ProcessResources> {
    @Suppress("UNCHECKED_CAST") val modMeta = parent!!.ext["mod_meta"]!! as Map<String, Any>

    filesMatching("META-INF/mods.toml") {
        expand(modMeta)
    }

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(modMeta)
    }


    outputs.upToDateWhen { false }
}

val ccversion = (parent!!.ext["props"] as Properties)["craftedcore"] as String
dependencies {
    modApi("dev.tocraft:craftedcore-neoforge:${parent!!.name}-${ccversion}") {
        exclude("me.shedaniel.cloth")
    }
}