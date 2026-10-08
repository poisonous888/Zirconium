package psn.zirconium.mixin.render;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import psn.zirconium.features.HeldItemRender;
import psn.zirconium.features.MiscFeatures;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity>{
    @Redirect(
        method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
        at = @At(value="INVOKE", target="Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/ItemUseAnimation;")
    )
    private static ItemUseAnimation blockFood(ItemStack instance){
        var anim=instance.getUseAnimation();
        if(HeldItemRender.doDrink3rd()&&anim==ItemUseAnimation.DRINK||anim==ItemUseAnimation.EAT){
            return ItemUseAnimation.TOOT_HORN;
        }
        return anim;
    }
    
    //from animatium
    //https://modrinth.com/mod/animatium
    
    @Redirect(method = "extractCapeState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float removeOldClamp(float value, float min, float max){
        if(MiscFeatures.getCustomCapePhysics()) return value;
        return Mth.clamp(value, min, max);
    }
    @Inject(method = "extractCapeState",at = @At("TAIL"))
    private void newClamp(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci){
        if(!MiscFeatures.getCustomCapePhysics()) return;
        state.capeLean=Mth.clamp(state.capeLean, 0, 180);
        state.capeLean2=Mth.clamp(state.capeLean2,-150, 150);
    }
}
