package psn.zirconium.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(Gui.class)
public abstract class PotionRenderHud{
    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void onRenderEffectOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (MiscFeatures.getNoPotionEffects()) {
            ci.cancel();
        }
    }
}
