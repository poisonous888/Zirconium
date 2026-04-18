package psn.zirconium.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MouseLock;

@Mixin(MouseHandler.class)
public class MouseLockMixin{
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void noRotate(double d, CallbackInfo ci) {
        if (MouseLock.enabled()) {
            ci.cancel();
        }
    }
}