package psn.zirconium.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.DropUtils;

@Mixin(LocalPlayer.class)
public abstract class DropHotbarMixin extends AbstractClientPlayer{
    public DropHotbarMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void cancelDropHotbar(boolean entireStack, CallbackInfoReturnable<Boolean> ci) {
        //DropUtils.isProtected()
        if(DropUtils.doDropHotbar(getInventory().getSelectedItem())){
            ci.cancel();
        }
    }

//    @ModifyVariable(method = "drop", at = @At("HEAD"), ordinal = 0, argsOnly = true)
//    private boolean dropStack(boolean bl){
//        return bl || DropUtils.isDropHeld();
//    }
}
