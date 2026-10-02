//package psn.zirconium.mixin;
//
//import com.mojang.blaze3d.platform.InputConstants;
//import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.decoration.ArmorStand;
//import org.lwjgl.sdl.SDLMouse;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//import psn.zirconium.features.HideArmor;
//import psn.zirconium.features.MiscFeatures;
//
//@Mixin(InputConstants.class)
//public class InputConstantsMixin<T extends LivingEntity, S extends LivingEntityRenderState>{
//    @ModifyArg(method="grabMouse", at=@At(value="INVOKE", target="Lorg/lwjgl/sdl/SDLMouse;SDL_SetWindowRelativeMouseMode(JZ)Z"),index = 1)
//    private static boolean grab(boolean enabled){
//        if(MiscFeatures.getNoRawInput()){
//            SDLMouse.SDL_HideCursor();
//            return false;
//        }
//        return true;
//    }
//    @ModifyArg(method="releaseMouse", at=@At(value="INVOKE", target="Lorg/lwjgl/sdl/SDLMouse;SDL_SetWindowRelativeMouseMode(JZ)Z"),index = 1)
//    private static boolean release(boolean enabled){
//        if(MiscFeatures.getNoRawInput()){
//            SDLMouse.SDL_HideCursor();
//            return false;
//        }
//        return true;
//    }
//}
