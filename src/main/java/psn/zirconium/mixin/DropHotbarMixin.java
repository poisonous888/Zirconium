package psn.zirconium.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.DropUtils;

@Mixin(MultiPlayerGameMode.class)
public abstract class DropHotbarMixin{
    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void cancelDropHotbar(LocalPlayer player, boolean all, CallbackInfo ci) {
        if(DropUtils.doDropHotbar(player.getInventory().getSelectedItem())){
            ci.cancel();
        }
    }
}
