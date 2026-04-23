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
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private float oOffHandHeight;
    @Shadow private float oMainHandHeight;

    @Shadow
    protected abstract void swingArm(float f, PoseStack poseStack, int i, HumanoidArm humanoidArm);

    @Inject(method = "applyItemArmTransform", at = @At("TAIL"))
    private void applyTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f, CallbackInfo ci){
        ItemPos.executeTranslate(poseStack,humanoidArm);
    }
    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void customSwing(float f, PoseStack poseStack, int i, HumanoidArm humanoidArm, CallbackInfo ci){
        if(ItemPos.doSwing()){
            ItemPos.executeSwing(poseStack,humanoidArm==HumanoidArm.RIGHT?1:-1,f);
            ci.cancel();
        }
    }
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private void alwaysApplySwingAnim(AbstractClientPlayer acp, float f, float g, InteractionHand ihand, float h, ItemStack itemStack, float i, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int j, CallbackInfo ci){
        var arm = ihand==InteractionHand.MAIN_HAND;
        swingArm(h, poseStack, arm?1:-1, arm?acp.getMainArm():acp.getMainArm().getOpposite());
    }




    @Inject(method = "shouldInstantlyReplaceVisibleItem",at = @At("HEAD"), cancellable = true)
    private void disableHand(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<Boolean> ci){
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
    private void disableHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f, float g, HumanoidArm humanoidArm, CallbackInfo ci){
        if(ItemPos.doDisableHand()){ci.cancel();}
    }
    @Inject(method = "renderPlayerArm",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;getPlayerRenderer(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;"))
    private void handTransform(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f, float g, HumanoidArm humanoidArm, CallbackInfo ci){
        if(ItemPos.doSyncHand()){
            ItemPos.executeTranslate(poseStack,humanoidArm);
        }
    }
}