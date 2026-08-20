package psn.zirconium.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.MiscFeatures;

@Mixin(Camera.class)
public abstract class FogCameraMixin{
    @Inject(method="getFluidInCamera",at=@At("HEAD"), cancellable=true)
    private static void noFog(CallbackInfoReturnable<FogType> cir){
        if(!MiscFeatures.getNoLiquidFog())return;
        cir.setReturnValue(FogType.NONE);
        cir.cancel();
    }
}
