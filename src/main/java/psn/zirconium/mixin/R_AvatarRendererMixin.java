package psn.zirconium.mixin;

import psn.zirconium.features.Visuals;
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
public abstract class R_AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity>{
    @Redirect(method = "extractCapeState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float removeOldClamp(float f, float g, float h){
        if(Visuals.doCustomCape()) return f;
        return Mth.clamp(f, g, h);
    }
    @Inject(method = "extractCapeState",at = @At("TAIL"))
    private void newClamp(AvatarlikeEntity avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo ci){
        if(!Visuals.doCustomCape()) return;
        avatarRenderState.capeFlap=avatarRenderState.capeFlap * Visuals.getFlapMult();
        avatarRenderState.capeLean=Mth.clamp(avatarRenderState.capeLean, Visuals.getVerticalLeanNegClamp(), Visuals.getVerticalLeanPosClamp());
        avatarRenderState.capeLean2=Mth.clamp(avatarRenderState.capeLean2,-Visuals.getSideLeanClamp(), Visuals.getSideLeanClamp());
    }
}
