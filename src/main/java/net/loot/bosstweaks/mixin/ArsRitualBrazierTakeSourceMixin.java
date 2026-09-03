package net.loot.bosstweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Companion to {@link ArsRitualNoSourceMixin}. A few rituals (e.g. {@code DenySpawnRitual} =
 * ritual_sanctuary) call {@code AbstractRitual#takeSourceNow} themselves, which delegates to
 * {@code RitualBrazierTile#takeSource}. With {@code consumesSource()} forced to {@code false} that method
 * would return {@code false} and leave the ritual flagged as "needs source". Forcing it to {@code true}
 * clears that flag so those rituals also run without a Source supply.
 */
@Mixin(targets = "com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile", remap = false)
public class ArsRitualBrazierTakeSourceMixin {

    @Inject(method = "takeSource", at = @At("HEAD"), cancellable = true, require = 0)
    private void phospheneBossTweaks$alwaysPaid(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
