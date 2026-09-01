package psn.zirconium.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MouseLock;
import psn.zirconium.features.Zoom;

@Mixin(MouseHandler.class)
public class MouseMixin{
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void noRotate(double mousea, CallbackInfo ci) {
        if (MouseLock.enabled()) ci.cancel();
    }
    @Inject(method="onScroll",at=@At("HEAD"), cancellable=true)
    private void zoomScroll(long handle, double xoffset, double yoffset, CallbackInfo ci){
        if(Zoom.getZooming()){
            Zoom.scroll(yoffset);
            ci.cancel();
        }
    }
}