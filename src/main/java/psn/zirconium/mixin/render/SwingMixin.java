package psn.zirconium.mixin.render;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import psn.zirconium.features.HeldItemRender;

@Mixin(Minecraft.class)
public abstract class SwingMixin{
    @Redirect(
        method = "handleKeybinds",
        at = @At(value="INVOKE", target="Lnet/minecraft/client/KeyMapping;consumeClick()Z",ordinal = 0),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z",ordinal = 0),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startAttack()Z")
        )
    )
    private boolean handle(KeyMapping instance){
        while(options.keyAttack.consumeClick()) fakeHit();
        return false;
    }
    @Shadow @Final public Options options;
    @Shadow @Nullable public LocalPlayer player;
    @Unique private void fakeHit(){
        if(!HeldItemRender.doUsing()||player==null)return;
        var swing=player.swingState;
        var animation=player.getMainHandItem().getAttackAnimation();
        swing.startIfAble(InteractionHand.MAIN_HAND,animation,player.getModifiedSwingDuration(animation));
    }
}