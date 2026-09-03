package net.loot.bosstweaks;

import com.mojang.logging.LogUtils;

import net.loot.bosstweaks.reg.ModRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * Phosphene Boss Tweaks - one jar that:
 *  - replaces the death drops of 8 named bosses with an exact list (waystone + resource key + 3 skill orbs),
 *    wiping their signature weapons / forced drops;
 *  - adds an unbreakable "boss altar" block that summons a chosen boss on right-click then removes itself;
 *  - bundles a datapack that strips every worldgen feature/structure/biome from the boss + waystone + ars
 *    nouveau mods and disables the vanilla summon routes (recipes; the rest is handled by ItemObliterator).
 */
@Mod(PhospheneBossTweaks.MODID)
public final class PhospheneBossTweaks {
    public static final String MODID = "phosphene_boss_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PhospheneBossTweaks(IEventBus modEventBus, ModContainer modContainer) {
        ModRegistry.register(modEventBus);
        modEventBus.addListener(Config::onLoad);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
