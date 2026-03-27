package psn.zirconium.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(value = AbstractRecipeBookScreen.class)
public class RecipeBookMixin {
    @Inject(method = "initButton", at = @At("HEAD"), cancellable = true)
    public void noRecipeBook(CallbackInfo ci) {
        if (MiscFeatures.getNoRecipeBook()) ci.cancel();
    }
}