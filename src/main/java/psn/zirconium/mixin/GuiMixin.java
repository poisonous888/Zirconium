package psn.zirconium.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(Gui.class)
public abstract class GuiMixin{
    @Shadow public abstract void setScreen(@Nullable Screen screen);
    @Inject(method="setScreen",at=@At("HEAD"), cancellable=true)
    private void noLoadingScreen(Screen screen, CallbackInfo ci){
        if(MiscFeatures.getNoLoadingScreen()&&screen instanceof LevelLoadingScreen){
            setScreen(null);
            ci.cancel();
        }
    }
}