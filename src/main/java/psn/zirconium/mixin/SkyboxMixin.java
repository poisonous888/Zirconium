package psn.zirconium.mixin;

import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(SkyRenderer.class)
public abstract class SkyboxMixin{
//    @Inject(method="extractRenderState",at=@At("HEAD"))
//    private static void noSky(ClientLevel level, float partialTicks, Camera camera, SkyRenderState state, CallbackInfo ci){
//        if(MiscFeatures.getNoSky()) state.skybox=DimensionType.Skybox.NONE;
//    }
    @Inject(method="renderSkyDisc",at=@At("HEAD"), cancellable=true)
    private static void noDisc(CallbackInfo ci){
        if(MiscFeatures.getNoSky()) ci.cancel();
    }
    @Inject(method="renderDarkDisc",at=@At("HEAD"), cancellable=true)
    private static void noDarkDisc(CallbackInfo ci){
        if(MiscFeatures.getNoSky()) ci.cancel();
    }
    @Inject(method="renderSunriseAndSunset",at=@At("HEAD"), cancellable=true)
    private static void noSunriseColors(CallbackInfo ci){
        if(MiscFeatures.getNoSky()) ci.cancel();
    }
}
