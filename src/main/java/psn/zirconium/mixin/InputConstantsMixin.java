package psn.zirconium.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.sdl.SDLHints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

import static org.lwjgl.sdl.SDLHints.SDL_HINT_MOUSE_RELATIVE_SYSTEM_SCALE;

@Mixin(InputConstants.class)
public class InputConstantsMixin{
    @Inject(method = "grabMouse",at = @At("HEAD"))
    private static void relativeSystemScale(Window window, double xpos, double ypos, CallbackInfo ci){
        if(MiscFeatures.getNoRawInput()){
            SDLHints.SDL_SetHint(SDL_HINT_MOUSE_RELATIVE_SYSTEM_SCALE,"1");
        }
    }
}
