package net.loot.bosstweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Frees Ars Nouveau rituals from needing a Source supply.
 *
 * <p>{@code RitualBrazierTile#tick} pays a ritual's Source cost every cycle by calling
 * {@code SourceUtil.takeSourceMultipleWithParticles(pos, level, 6, cost)}; when that returns {@code null}
 * (no Source Jar / Sourcelink within range) it {@code return}s early and the ritual stalls forever.
 * Phosphene's ItemObliterator config removes every Source Jar, Creative Source Jar and Sourcelink, which
 * would permanently freeze ritual_gravity, ritual_restoration, ritual_sanctuary, both conjure-island
 * rituals and the automation rituals.
 *
 * <p>Forcing {@code consumesSource()} (which Ars derives as {@code getSourceCost() > 0}) to {@code false}
 * makes the brazier skip the whole pay-or-stall block, so every ritual runs on its normal timer with no
 * Source. Targeted by string + {@code require = 0} so the mod loads cleanly with or without Ars Nouveau.
 */
@Mixin(targets = "com.hollingsworth.arsnouveau.api.ritual.AbstractRitual", remap = false)
public class ArsRitualNoSourceMixin {

    @Inject(method = "consumesSource", at = @At("HEAD"), cancellable = true, require = 0)
    private void phospheneBossTweaks$ritualsNeedNoSource(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
