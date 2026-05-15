plugins {
    id("dev.tocraft.modmaster.common")
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")

    compileOnly("dev.tocraft:craftedcore:${property("craftedcore_version")}") {
        exclude(group = "me.shedaniel.cloth")
    }
    compileOnly("org.ow2.asm:asm:9.8")
}
