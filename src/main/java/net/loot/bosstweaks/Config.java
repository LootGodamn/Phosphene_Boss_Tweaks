package net.loot.bosstweaks;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Small COMMON config: master toggle + boss-XP zeroing. */
public final class Config {
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.BooleanValue ENABLE_BOSS_DROPS;
    private static final ModConfigSpec.BooleanValue ZERO_BOSS_XP;

    public static volatile boolean enableBossDrops = true;
    public static volatile boolean zeroBossXp = true;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        ENABLE_BOSS_DROPS = b
                .comment("Master switch for the custom boss drops. When false, the 8 handled bosses drop their vanilla/mod loot again.")
                .define("enableBossDrops", true);
        ZERO_BOSS_XP = b
                .comment("Zero the experience dropped by the 8 handled bosses.",
                        "Note: the Ender Dragon's XP is awarded by its own death animation, not the drop event, so it is not affected.")
                .define("zeroBossXp", true);
        SPEC = b.build();
    }

    public static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        enableBossDrops = ENABLE_BOSS_DROPS.get();
        zeroBossXp = ZERO_BOSS_XP.get();
    }

    private Config() {
    }
}
