package net.loot.bosstweaks.drops;

import java.util.ArrayList;
import java.util.List;

import net.loot.bosstweaks.Config;
import net.loot.bosstweaks.PhospheneBossTweaks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

/**
 * Replaces the death loot of the {@link BossDrops#TABLE} bosses with an exact list.
 *
 * <p>For the 7 mob bosses this rides {@link LivingDropsEvent}, whose collection is NeoForge's captured set
 * of the entire death-loot sequence (loot table + {@code dropCustomDeathLoot} + equipment) - clearing it
 * removes signature weapons and any forced drops. The Ender Dragon skips that path (its own death
 * animation awards loot), so it is handled from {@link LivingDeathEvent} instead.
 */
@EventBusSubscriber(modid = PhospheneBossTweaks.MODID)
public final class BossDropHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!Config.enableBossDrops) {
            return;
        }
        Entity entity = event.getEntity();
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (id.equals(BossDrops.ENDER_DRAGON)) {
            return; // handled in onLivingDeath
        }
        if (BossDrops.SUPPRESS_DROPS.contains(id)) {
            event.getDrops().clear(); // e.g. Pale Monarch's cutscene corpse dropping pale_axe
            return;
        }
        List<BossDrops.Drop> drops = BossDrops.TABLE.get(id);
        if (drops == null) {
            return;
        }
        event.getDrops().clear();
        Level level = entity.level();
        for (ItemStack stack : resolve(drops)) {
            event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), stack));
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!Config.enableBossDrops) {
            return;
        }
        Entity entity = event.getEntity();
        if (!BossDrops.ENDER_DRAGON.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()))) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        for (ItemStack stack : resolve(BossDrops.TABLE.get(BossDrops.ENDER_DRAGON))) {
            ItemEntity item = new ItemEntity(level, entity.getX(), entity.getY() + 1.0, entity.getZ(), stack);
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
        }
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!Config.zeroBossXp) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType());
        if (BossDrops.TABLE.containsKey(id) || BossDrops.SUPPRESS_DROPS.contains(id)) {
            event.setDroppedExperience(0);
        }
    }

    /** Resolve drop lines to real stacks, split so no stack exceeds the item's max size. */
    private static List<ItemStack> resolve(List<BossDrops.Drop> drops) {
        List<ItemStack> out = new ArrayList<>();
        if (drops == null) {
            return out;
        }
        for (BossDrops.Drop drop : drops) {
            Item item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(drop.item())).orElse(Items.AIR);
            if (item == Items.AIR) {
                PhospheneBossTweaks.LOGGER.debug("Boss drop item not present, skipping: {}", drop.item());
                continue;
            }
            int remaining = drop.count();
            int max = Math.max(1, new ItemStack(item).getMaxStackSize());
            while (remaining > 0) {
                int n = Math.min(remaining, max);
                out.add(new ItemStack(item, n));
                remaining -= n;
            }
        }
        return out;
    }

    private BossDropHandler() {
    }
}
