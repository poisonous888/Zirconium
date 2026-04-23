package psn.zirconium.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.ItemPos;

@Mixin(Minecraft.class)
public abstract class R_SwingUsingMixin {
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Final public Options options;

    @Unique Boolean hasSwungAir=false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void swingWhileUsing(CallbackInfo ci){
        if(!ItemPos.doSwingWhileUsing())return;
        if(player==null)return;
        if(!options.keyAttack.isDown()) {
            hasSwungAir=false;
            return;
        }
        if(hasSwungAir)return;
        if(!player.isUsingItem())return;
        if(player.swinging && player.swingTime < ItemPos.getSwingDuration() / 2)return;
        player.swingTime = -1;
        player.swinging = true;
        player.swingingArm = player.getUsedItemHand();
        hasSwungAir = true;
    }
}