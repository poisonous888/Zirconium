package psn.zirconium.mixin;

import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.MiscFeatures;

@Mixin(Pack.class)
public class ResourcePackMixin {
    
    //from server-pack-unlocker
    //https://github.com/TheMysterys/Server-Pack-Unlocker
    
    @Inject(method = "isFixedPosition", at = @At("RETURN"), cancellable=true)
    private void noFixed(CallbackInfoReturnable<Boolean> cir){
        if(MiscFeatures.getNoRequiredPacks())cir.setReturnValue(false);
    }
    @Inject(method = "isRequired", at = @At("RETURN"), cancellable=true)
    private void noRequired(CallbackInfoReturnable<Boolean> cir){
        if(MiscFeatures.getNoRequiredPacks())cir.setReturnValue(false);
    }
}
