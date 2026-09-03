package net.loot.bosstweaks.block;

import net.loot.bosstweaks.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Holds the two NBT knobs an altar is configured with:
 * <ul>
 *   <li>{@code Boss} - the entity id to summon (e.g. {@code "cataclysm:maledictus"})</li>
 *   <li>{@code Tint} - 0xRRGGBB colour for the crystal group</li>
 * </ul>
 * Set them with {@code /data merge block <x> <y> <z> {Boss:"...",Tint:...}} or via the preset creative items.
 */
public class BossAltarBlockEntity extends BlockEntity {
    public static final String KEY_BOSS = "Boss";
    public static final String KEY_TINT = "Tint";
    public static final int DEFAULT_TINT = 0xFFFFFF;

    @Nullable
    private ResourceLocation boss;
    private int tint = DEFAULT_TINT;

    public BossAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.BOSS_ALTAR_BE.get(), pos, state);
    }

    @Nullable
    public ResourceLocation getBoss() {
        return boss;
    }

    public int getTint() {
        return tint;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.boss = tag.contains(KEY_BOSS, Tag.TAG_STRING) ? ResourceLocation.tryParse(tag.getString(KEY_BOSS)) : null;
        this.tint = tag.contains(KEY_TINT, Tag.TAG_ANY_NUMERIC) ? (tag.getInt(KEY_TINT) & 0xFFFFFF) : DEFAULT_TINT;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (boss != null) {
            tag.putString(KEY_BOSS, boss.toString());
        }
        tag.putInt(KEY_TINT, tint);
    }

    // --- client sync so the block colour handler can read the tint ---

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
