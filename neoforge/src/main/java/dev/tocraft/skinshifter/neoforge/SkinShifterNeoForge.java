package dev.tocraft.skinshifter.neoforge;

import dev.tocraft.craftedcore.permission.neoforge.PermissionCheckerImpl;
import dev.tocraft.skinshifter.SkinShifter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Mod(SkinShifter.MODID)
public class SkinShifterNeoForge {
    public SkinShifterNeoForge() {
        NeoForge.EVENT_BUS.addListener(PermissionGatherEvent.Nodes.class, SkinShifterNeoForge::registerNodesEvent);
        new SkinShifter().initialize();
    }

    public static void registerNodesEvent(PermissionGatherEvent.@NotNull Nodes event) {
        List<PermissionNode<Boolean>> nodes = new ArrayList<>();
        
        // Core permissions
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));

        // Command permissions (basic)
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));

        // Command permissions (.self variants)
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));

        // Command permissions (.others variants)
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));
        nodes.add(PermissionCheckerImpl.createNode("skinshifter", "morph"));

        // Entity type permissions for all registered entities
        BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            ResourceLocation key = EntityType.getKey(entityType);
            nodes.add(PermissionCheckerImpl.createNode("skinshifter", "type." + key));
        });


        // register all
        event.addNodes(nodes.toArray(PermissionNode[]::new));
    }
}
