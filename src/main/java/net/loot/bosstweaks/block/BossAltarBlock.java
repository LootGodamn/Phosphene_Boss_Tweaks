package net.loot.bosstweaks.block;

import com.mojang.serialization.MapCodec;

import net.loot.bosstweaks.PhospheneBossTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Unbreakable altar. Right-click on the server: read {@link BossAltarBlockEntity#getBoss()}, summon that
 * entity just above the block, then delete the block with no drop. One use only.
 */
public class BossAltarBlock extends BaseEntityBlock {
    public static final MapCodec<BossAltarBlock> CODEC = simpleCodec(BossAltarBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
            box(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D),
            box(4.0D, 7.0D, 4.0D, 12.0D, 16.0D, 12.0D));

    public BossAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BossAltarBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel) || !(level.getBlockEntity(pos) instanceof BossAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }
        ResourceLocation bossId = altar.getBoss();
        if (bossId == null) {
            player.displayClientMessage(Component.translatable("message.phosphene_boss_tweaks.altar.unset"), true);
            return InteractionResult.CONSUME;
        }
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(bossId).orElse(null);
        if (type == null) {
            player.displayClientMessage(Component.translatable("message.phosphene_boss_tweaks.altar.unknown", bossId.toString()), true);
            return InteractionResult.CONSUME;
        }
        Entity spawned = type.spawn(serverLevel, pos.above(), MobSpawnType.SPAWNER);
        if (spawned == null) {
            PhospheneBossTweaks.LOGGER.warn("Boss altar at {} failed to spawn {}", pos, bossId);
            return InteractionResult.CONSUME;
        }
        serverLevel.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 1.4F, 0.6F);
        serverLevel.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.6F, 0.5F);
        level.destroyBlock(pos, false); // gone, no drop
        return InteractionResult.CONSUME;
    }
}
