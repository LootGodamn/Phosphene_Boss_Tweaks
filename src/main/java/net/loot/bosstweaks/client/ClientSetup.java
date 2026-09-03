package net.loot.bosstweaks.client;

import net.loot.bosstweaks.PhospheneBossTweaks;
import net.loot.bosstweaks.block.BossAltarBlockEntity;
import net.loot.bosstweaks.reg.ModRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * Client-only entrypoint. Registers the crystal-group tint, driven by the block entity's {@code Tint}
 * in the world and by the item's {@code BLOCK_ENTITY_DATA} for the creative-tab presets.
 */
@Mod(value = PhospheneBossTweaks.MODID, dist = Dist.CLIENT)
public final class ClientSetup {

    private static final int NO_TINT = 0xFFFFFFFF;

    public ClientSetup(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(ClientSetup::registerBlockColors);
        modEventBus.addListener(ClientSetup::registerItemColors);
    }

    private static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0 || level == null || pos == null) {
                return NO_TINT;
            }
            return level.getBlockEntity(pos) instanceof BossAltarBlockEntity altar
                    ? 0xFF000000 | altar.getTint()
                    : NO_TINT;
        }, ModRegistry.BOSS_ALTAR.get());
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return NO_TINT;
            }
            CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data == null || !data.contains(BossAltarBlockEntity.KEY_TINT)) {
                return NO_TINT;
            }
            return 0xFF000000 | (data.copyTag().getInt(BossAltarBlockEntity.KEY_TINT) & 0xFFFFFF);
        }, ModRegistry.BOSS_ALTAR_ITEM.get());
    }
}
