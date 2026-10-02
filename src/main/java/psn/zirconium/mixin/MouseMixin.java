package psn.zirconium.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;
import psn.zirconium.features.MouseLock;
import psn.zirconium.features.Zoom;

@Mixin(MouseHandler.class)
public class MouseMixin{
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void noRotate(double mousea, CallbackInfo ci) {
        if (MouseLock.enabled()) ci.cancel();
        if(MiscFeatures.getNoRawInput()){
            
        }
    }
    @Inject(method="onScroll",at=@At("HEAD"), cancellable=true)
    private void zoomScroll(long handle, double xoffset, double yoffset, CallbackInfo ci){
        if(Zoom.getZooming()){
            Zoom.scroll(yoffset);
            ci.cancel();
        }
    }
    
    
    
    @Unique private final double[] threshhold = { 0.0, 75.25, 320.0, 975.0, 4000.0 };
    @Unique private final double[] mult = { 0.0, 1.15,  4.25,  15.5,  40.0 };
    
    @Unique private double getMultiplier(double inputSpeed) {
        for (int i = 0; i < threshhold.length - 1; i++) {
            if (inputSpeed >= threshhold[i] && inputSpeed <= threshhold[i + 1]) {
                double x0 = threshhold[i];
                double x1 = threshhold[i + 1];
                double y0 = mult[i];
                double y1 = mult[i + 1];
                
                double slope = (y1 - y0) / (x1 - x0);
                return y0 + slope * (inputSpeed - x0);
            }
        }
        // Fallback for extreme flicks exceeding the max table point
        return mult[mult.length - 1] / threshhold[threshhold.length - 1];
    }
}