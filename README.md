[![Build](https://img.shields.io/github/actions/workflow/status/ToCraft/SkinShifter/build_current.yml?style=for-the-badge)](https://github.com/ToCraft/SkinShifter/actions/workflows/build_current.yml)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.tocraft.dev%2Fpublic%2Fdev%2Ftocraft%2Fskinshifter%2Fmaven-metadata.xml&style=for-the-badge&label=SkinShifter)](https://maven.tocraft.dev/#/public/dev/tocraft/skinshifter)
[![Patreon](https://img.shields.io/badge/Patreon-F96854?style=for-the-badge&logo=patreon&logoColor=white)](https://patreon.com/ToCraft)
[![Discord](https://img.shields.io/discord/1183373613508857906?style=for-the-badge&label=Discord)](https://discord.gg/Y3KqxWDUYy)

# SkinShifter

*SkinShifter* lets you change your Player Skin in game!

## Getting Started

This mod is intended to be used in modpacks or as library for other mods.
You can change your skin by using the command `/skinshifter set @s xyz`, where `xyz` is the name or uuid of the player you want to morph into.

If you're a coder, you can use the following methods:
~~~java
SkinShifter.setSkin(serverPlayer, playerUuidForSkin); // Sets the current skin
SkinShifter.getCurrentSkin(serverPlayer); // returns the UUID of owner of the current skin. Might be the own UUID!
~~~

To change config values, you can do the following:
~~~java
class Test {
    public static void initialize() {
        SkinShifter.CONFIG.changeCape = true;
        SkinShifter.CONFIGchangeNameTag = true;
        // required to save the changes into the config file
        SkinShifter.CONFIG.save();
        // when changing the config after the first player has joined, use:
        // SkinShifter.CONFIG.sendToAllPlayers(serverLevel);
    }
}
~~~

### License

SkinShifter is licensed under Apache 2.0. 
