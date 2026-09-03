package net.loot.bosstweaks.drops;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;

/**
 * The single source of truth for the 8 handled bosses: what each one drops, and the crystal tint of its
 * preset altar (matched to its resource key's colour). Every boss also drops 3 {@code phospheneitems:skill_orb}.
 * The Kraken and The Leviathan share an entry.
 */
public final class BossDrops {

    /** One drop line: an item id and a stack count. Resolved to an ItemStack at drop time (missing item = skipped). */
    public record Drop(String item, int count) {}

    /** boss entity id -> ordered drop list (already includes the 3 skill orbs). */
    public static final Map<ResourceLocation, List<Drop>> TABLE = new LinkedHashMap<>();

    /** boss entity id -> ARGB-less crystal tint (0xRRGGBB) for its preset creative-tab altar. */
    public static final Map<ResourceLocation, Integer> ALTAR_TINT = new LinkedHashMap<>();

    public static final ResourceLocation ENDER_DRAGON = ResourceLocation.withDefaultNamespace("ender_dragon");

    /**
     * Helper entities whose death loot must be wiped but which award nothing themselves. Pale Monarch's
     * cutscene corpse ({@code pale_monarch:pale_monarch_death_entity}) spawns {@code pale_monarch:pale_axe}
     * from its {@code dropCustomDeathLoot}; that entity is never in {@link #TABLE}, so without this it slips
     * past the drop replacement. Their drops are cleared and nothing is added; their XP is zeroed with the
     * bosses' when {@code zeroBossXp} is on.
     */
    public static final Set<ResourceLocation> SUPPRESS_DROPS = Set.of(
            ResourceLocation.fromNamespaceAndPath("pale_monarch", "pale_monarch_death_entity"));

    static {
        boss("pale_monarch:pale_monarch", 0x4BEDDB,
                "waystones:waystone", "phospheneitems:diamond_key");
        boss("legendary_monsters:posessed_paladin", 0x724B43,
                "waystones:mossy_waystone", "phospheneitems:netherite_key");
        boss("cataclysm:maledictus", 0x2DDA65,
                "waystones:red_nether_bricks_waystone", "phospheneitems:totemic_key");
        boss("block_factorys_bosses:underworld_knight", 0x5229DD,
                "waystones:blackstone_waystone", "phospheneitems:eternal_key");
        boss("legendary_monsters:cloud_golem", 0x29DFEB,
                "waystones:deepslate_waystone", "deeperdarker:heart_of_the_deep");

        // The Obliterator: end-stone waystone + 3x wom:antitheus
        bossLines("legendary_monsters:the_obliterator", 0x5A5754, List.of(
                new Drop("waystones:end_stone_waystone", 1),
                new Drop("wom:antitheus", 3)));

        boss("minecraft:ender_dragon", 0xB44AE8,
                "waystones:purpur_waystone", "minecraft:dragon_head");

        // Kraken and Leviathan drop the same thing.
        List<Drop> ocean = List.of(
                new Drop("waystones:prismarine_waystone", 1),
                new Drop("phospheneitems:poseidite_key", 1));
        bossLines("block_factorys_bosses:kraken", 0x80D8D3, ocean);
        bossLines("cataclysm:the_leviathan", 0x80D8D3, ocean);
    }

    private static void boss(String id, int tint, String... unique) {
        List<Drop> lines = new ArrayList<>();
        for (String u : unique) {
            lines.add(new Drop(u, 1));
        }
        bossLines(id, tint, lines);
    }

    private static void bossLines(String id, int tint, List<Drop> unique) {
        List<Drop> lines = new ArrayList<>(unique);
        lines.add(new Drop("phospheneitems:skill_orb", 3));
        ResourceLocation rl = ResourceLocation.parse(id);
        TABLE.put(rl, List.copyOf(lines));
        ALTAR_TINT.put(rl, tint);
    }

    private BossDrops() {
    }
}
