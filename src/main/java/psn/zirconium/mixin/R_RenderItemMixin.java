package psn.zirconium.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.ItemPos;

@Mixin(ItemInHandRenderer.class)
public abstract class R_RenderItemMixin {
    
    //from nofrills
    //https://modrinth.com/mod/nofrills
    
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private float oOffHandHeight;
    @Shadow private float oMainHandHeight;

    @Shadow
    protected abstract void swingArm(float attack, PoseStack poseStack, int invert, HumanoidArm arm);

    @Inject(method = "applyItemArmTransform", at = @At("TAIL"))
    private void applyTransform(PoseStack poseStack, HumanoidArm arm, float inverseArmHeight, CallbackInfo ci){
        ItemPos.executeTranslate(poseStack, arm);
    }
    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void customSwing(float attack, PoseStack poseStack, int invert, HumanoidArm arm, CallbackInfo ci){
        if(ItemPos.doSwing()){
            ItemPos.executeSwing(poseStack, arm ==HumanoidArm.RIGHT?1:-1, attack);
            ci.cancel();
        }
    }
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private void alwaysApplySwingAnim(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci){
        var arm = hand ==InteractionHand.MAIN_HAND;
        swingArm(attack, poseStack, arm?1:-1, arm? player.getMainArm(): player.getMainArm().getOpposite());
    }




    @Inject(method = "shouldInstantlyReplaceVisibleItem",at = @At("HEAD"), cancellable = true)
    private void disableHand(ItemStack currentlyVisibleItem, ItemStack expectedItem, CallbackInfoReturnable<Boolean> ci){
        if(ItemPos.doNoSwap()){ci.cancel();ci.setReturnValue(true);}
    }
    @Inject(method = "tick",at = @At("TAIL"))
    private void disableReequip(CallbackInfo ci){
        if(ItemPos.doNoSwap()){
            this.mainHandHeight=1f;
            this.offHandHeight=1f;
            this.oMainHandHeight=1f;
            this.oOffHandHeight=1f;
        }
    }
    @Inject(method = "renderPlayerArm",at = @At("HEAD"), cancellable = true)
    private void disableHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float inverseArmHeight, float attackValue, HumanoidArm arm, CallbackInfo ci){
        if(ItemPos.doDisableHand()){ci.cancel();}
    }
    @Inject(method = "renderPlayerArm",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;getPlayerRenderer(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;"))
    private void handTransform(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float inverseArmHeight, float attackValue, HumanoidArm arm, CallbackInfo ci){
        if(ItemPos.doSyncHand()){
            ItemPos.executeTranslate(poseStack, arm);
        }
    }
}