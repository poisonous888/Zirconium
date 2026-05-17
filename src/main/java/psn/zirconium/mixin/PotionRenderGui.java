package psn.zirconium.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

import java.util.Collection;

@Mixin(EffectsInInventory.class)
public abstract class PotionRenderGui{
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void removeRender(GuiGraphics guiGraphics, Collection<MobEffectInstance> collection, int i, int j, int k, int l, int m, CallbackInfo ci) {
        if (MiscFeatures.getNoPotionEffects()){
            ci.cancel();
        }
    }
}