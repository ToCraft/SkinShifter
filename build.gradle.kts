plugins {
    id("dev.tocraft.modmaster.root") version ("2.5-SNAPSHOT")
}

subprojects {
    repositories {
        maven("https://maven.fabricmc.net/") // fabric api
        maven {
            name = "Minecraft Libraries"
            url = uri("https://libraries.minecraft.net")
        }
    }
}
