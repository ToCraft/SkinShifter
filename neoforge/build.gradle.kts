plugins {
    id("dev.tocraft.modmaster.neoforge")
}

dependencies {
    implementation("dev.tocraft:craftedcore-neoforge:${property("craftedcore_version")}") {
        exclude(group = "me.shedaniel.cloth")
    }
}

tasks.processResources {
    val mcVersion = project.property("minecraft")
    val craftedcoreVersion = project.property("craftedcore_version")
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(mapOf(
            "version" to project.version,
            "minecraft_version" to mcVersion,
            "craftedcore_version" to craftedcoreVersion
        ))
    }
}
