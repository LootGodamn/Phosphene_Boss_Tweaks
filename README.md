# Phosphene Boss Tweaks

NeoForge 1.21.1. One jar = a small mod **plus** a bundled datapack.

## 1. Boss drops (Java — `net.loot.bosstweaks.drops`)

8 bosses drop an **exact** list and nothing else — signature weapons and any forced/`dropCustomDeathLoot`
drops are wiped (`LivingDropsEvent.getDrops().clear()`). The Ender Dragon is handled from
`LivingDeathEvent` because it bypasses the drop event. Every boss also drops 3× `phospheneitems:skill_orb`.

| Boss | Drops |
|---|---|
| `pale_monarch:pale_monarch` | `waystones:waystone`, `phospheneitems:diamond_key` |
| `legendary_monsters:posessed_paladin` | `waystones:mossy_waystone`, `phospheneitems:netherite_key` |
| `cataclysm:maledictus` | `waystones:red_nether_bricks_waystone`, `phospheneitems:totemic_key` |
| `block_factorys_bosses:underworld_knight` | `waystones:blackstone_waystone`, `phospheneitems:eternal_key` |
| `legendary_monsters:cloud_golem` | `waystones:deepslate_waystone`, `deeperdarker:heart_of_the_deep` |
| `legendary_monsters:the_obliterator` | `waystones:end_stone_waystone`, 3x `wom:antitheus` |
| `minecraft:ender_dragon` | `waystones:purpur_waystone`, `minecraft:dragon_head` |
| `block_factorys_bosses:kraken` **and** `cataclysm:the_leviathan` | `waystones:prismarine_waystone`, `phospheneitems:poseidite_key` |

Config (`config/phosphene_boss_tweaks-common.toml`): `enableBossDrops`, `zeroBossXp` (default true —
the Dragon's XP is awarded by its death animation, not the event, so it is unaffected).

Every lookup is by id — a missing mod / entity / item just makes that rule inert.

Pale Monarch's `pale_axe` is dropped by its **cutscene corpse** (`pale_monarch:pale_monarch_death_entity`,
via `dropCustomDeathLoot`), not the boss itself, so that helper entity is also drop-wiped
(`BossDrops.SUPPRESS_DROPS`). The axe never hits the ground.

## 2. Boss Altar (`phosphene_boss_tweaks:boss_altar`)

Unbreakable block, no drops, no piston move. Block-entity NBT:

- `Boss` — entity id to summon, e.g. `"cataclysm:maledictus"`
- `Tint` — `0xRRGGBB` crystal-group colour (tintindex 0; base untinted)

Right-click **with an empty hand and not sneaking** on the server: spawns the entity one block up, plays a
sound, then **deletes itself** (no drop). One use. Place with
`/setblock ~ ~ ~ phosphene_boss_tweaks:boss_altar{Boss:"...",Tint:...}` or from the **Phosphene Bosses**
creative tab, which carries one preset per boss (tint matched to the drop key) plus a blank altar.

Caveats:
- A **sneaking** player never triggers it — vanilla routes a shift-click straight to block placement and
  skips `useWithoutItem`. Right-click standing normally.
- The `minecraft:ender_dragon` preset spawns a loose dragon with no fight arena, portal, or egg and
  degraded AI. The natural End fight is the intended source of the purpur waystone + dragon head; the
  preset is only there for completeness.

## 3. Bundled datapack (`data/...`, `AFTER` all six mods so overrides win)

- **Worldgen wiped** for cataclysm, legendary_monsters, block_factorys_bosses, ars_nouveau, waystones:
  every `structure_set` emptied, every `structure` given `biomes:[]`, every `tags/worldgen/biome` emptied,
  every `configured_feature` -> `minecraft:no_op`, every `neoforge:biome_modifier` -> `neoforge:none`.
- **Sealed Realm** (`pale_monarch:sealed_realm`) -> flat void (never visited once the idol is gone).
- **Waystones village injectors** (5 lithostitched modifiers) -> `neoforge:false`.
- **Waystone recipes** (all 12) -> disabled. Waystones are creative + boss-drop only.
- **Monarch Idol recipe** -> disabled.
- **Ars Nouveau archwood recipes** (~38: wand, scribes table, imbuement chamber, potion diffuser, mob jar,
  dowsing rod, all rituals, glyph alts) -> swap archwood for `#minecraft:planks` / `#minecraft:logs` /
  `#minecraft:wooden_slabs`. Pure-decorative archwood-block recipes and the essence->sapling recipes are
  left alone; the Novice Spellbook was never archwood-gated.

## 4. ItemObliterator (merged into `config/item_obliterator.json5`; backup `.phosphene-boss-bak`)

Regex entries that cut Waystones down to the 8 boss variants and remove every vanilla summon route:
all `*_spawn_egg`s of the four boss mods, cataclysm re-summon items, LM eyes/summoners/tesseract/warpers,
BFB arena keys, the Monarch Idol, and `pale_monarch:pale_axe`. **Keep `use_hashmap_optimizations = false`.**

## Test checklist

1. Game loads; `/reload` is clean (watch for worldgen errors from the overrides).
2. New chunks of cataclysm / LM / BFB biomes: no structures, no ores, no ambient boss mobs.
3. `/give @s phosphene_boss_tweaks:boss_altar` from the Phosphene Bosses tab -> place -> right-click ->
   the boss spawns and the altar vanishes. Kill it -> only the listed drops + 3 skill orbs, XP = 0.
4. Ender Dragon -> purpur waystone + dragon head + 3 skill orbs (at its death position).
5. JEI: waystones un-craftable; archwood-gated ars items now accept any planks.
6. No boss spawn egg / summon item is obtainable (creative search / JEI).
