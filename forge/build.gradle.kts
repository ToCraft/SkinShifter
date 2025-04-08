plugins {
    id("dev.tocraft.modmaster.forge")
}

tasks.withType<ProcessResources> {
    @Suppress("UNCHECKED_CAST")val modMeta = parent!!.ext["mod_meta"]!! as Map<String, Any>

    filesMatching("META-INF/mods.toml") {
        expand(modMeta)
    }

    outputs.upToDateWhen { false }
}

loom {
    forge {
        mixinConfigs.add("skinshifter.mixins.json")
    }
}

val ccversion = (parent!!.ext["props"] as Properties)["craftedcore"] as String
dependencies {
    modApi("dev.tocraft:craftedcore-forge:${parent!!.name}-${ccversion}") {
        exclude("me.shedaniel.cloth")
    }
}