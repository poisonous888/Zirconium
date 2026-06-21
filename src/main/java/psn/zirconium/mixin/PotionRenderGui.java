package psn.zirconium.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(EffectsInInventory.class)
public abstract class PotionRenderGui{
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void removeRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (MiscFeatures.getNoPotionEffects()){
            ci.cancel();
        }
    }
}