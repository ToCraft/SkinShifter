plugins {
    id("dev.tocraft.modmaster.fabric")
}

dependencies {
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric")}")

    implementation("dev.tocraft:craftedcore-fabric:${property("craftedcore_version")}") {
        exclude("net.fabricmc.fabric-api")
        exclude(group = "me.shedaniel.cloth")
        exclude(group = "com.terraformersmc")
    }
}

tasks.processResources {
    val mcVersion = project.property("minecraft")
    val craftedcoreVersion = project.property("craftedcore_version")
    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to project.version,
            "minecraft_version" to mcVersion,
            "craftedcore_version" to craftedcoreVersion
        ))
    }
}
