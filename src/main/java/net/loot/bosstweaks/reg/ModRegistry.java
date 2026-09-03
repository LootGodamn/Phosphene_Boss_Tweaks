package net.loot.bosstweaks.reg;

import java.util.function.Supplier;

import net.loot.bosstweaks.PhospheneBossTweaks;
import net.loot.bosstweaks.block.BossAltarBlock;
import net.loot.bosstweaks.block.BossAltarBlockEntity;
import net.loot.bosstweaks.drops.BossDrops;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRegistry {
    private static final String MODID = PhospheneBossTweaks.MODID;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<BossAltarBlock> BOSS_ALTAR = BLOCKS.register("boss_altar",
            () -> new BossAltarBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3_600_000.0F) // bedrock-tier: unbreakable in survival
                    .noLootTable()
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK)
                    .sound(SoundType.AMETHYST)));

    public static final DeferredItem<BlockItem> BOSS_ALTAR_ITEM =
            ITEMS.registerSimpleBlockItem("boss_altar", BOSS_ALTAR);

    public static final Supplier<BlockEntityType<BossAltarBlockEntity>> BOSS_ALTAR_BE =
            BLOCK_ENTITIES.register("boss_altar",
                    () -> BlockEntityType.Builder.of(BossAltarBlockEntity::new, BOSS_ALTAR.get()).build(null));

    public static final Supplier<CreativeModeTab> TAB = TABS.register("bosses", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.phosphene_boss_tweaks.bosses"))
            .icon(() -> altarStack(null, BossAltarBlockEntity.DEFAULT_TINT))
            .displayItems((parameters, output) -> {
                output.accept(new ItemStack(BOSS_ALTAR_ITEM.get())); // blank, configure by hand
                BossDrops.ALTAR_TINT.forEach((bossId, tint) -> output.accept(altarStack(bossId, tint)));
            })
            .build());

    /** A boss_altar item pre-loaded with {@code Boss}/{@code Tint} block-entity data. */
    public static ItemStack altarStack(ResourceLocation boss, int tint) {
        ItemStack stack = new ItemStack(BOSS_ALTAR_ITEM.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("id", MODID + ":boss_altar");
        if (boss != null) {
            tag.putString(BossAltarBlockEntity.KEY_BOSS, boss.toString());
        }
        tag.putInt(BossAltarBlockEntity.KEY_TINT, tint & 0xFFFFFF);
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
        return stack;
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        TABS.register(bus);
    }

    private ModRegistry() {
    }
}
