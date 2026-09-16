package psn.zirconium.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.HeldItemRender;
import psn.zirconium.features.MiscFeatures;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin{
    @Shadow public abstract void setScreen(@Nullable Screen screen);
    @Inject(method="setScreen",at=@At("HEAD"), cancellable=true)
    private void noLoadingScreen(Screen screen, CallbackInfo ci){
        if(MiscFeatures.getNoLoadingScreen()&&screen instanceof LevelLoadingScreen){
            setScreen(null);
            ci.cancel();
        }
    }
    
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
        if (!player.swinging || player.swingTime >= player.getCurrentSwingDuration() / 2 || player.swingTime < 0){
            player.swingTime = -1;
            player.swinging = true;
            player.swingingArm = InteractionHand.MAIN_HAND;
        }
    }
}