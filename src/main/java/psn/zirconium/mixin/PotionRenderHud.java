package psn.zirconium.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(Gui.class)
public abstract class PotionRenderHud{
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void onRenderEffectOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (MiscFeatures.getNoPotionEffects()) {
            ci.cancel();
        }
    }

//    @WrapWithCondition(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
//    private boolean shouldClearChat(ChatHud instance, boolean clearHistory) {
//        return !(ChatTweaks.instance.isActive() && ChatTweaks.keepHistory.value());
//    }
}
