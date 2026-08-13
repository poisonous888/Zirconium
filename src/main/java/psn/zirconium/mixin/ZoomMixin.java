package psn.zirconium.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import psn.zirconium.features.Zoom;

@Mixin(GameRenderer.class)
public class ZoomMixin{
    @ModifyVariable(method="renderLevel", at=@At(value="STORE", ordinal=0), name="projectionMatrix")
    private Matrix4f zoom(Matrix4f projectionMatrix){
        Zoom.pollZoomKey();
        if(Zoom.getZooming()){
            final var z=Zoom.getZoom();
            projectionMatrix.scale(z,z,1f);
        }
        return projectionMatrix;
    }
//    @Inject(method="getNightVisionScale",at=@At("HEAD"))
//    private static void fullbrightMaybeMaybe(LivingEntity camera, float a, CallbackInfoReturnable<Float> cir){
//
//    }
}