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
    
    //from animatium
    //https://modrinth.com/mod/animatium
    
    @Redirect(method = "extractCapeState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float removeOldClamp(float value, float min, float max){
        if(Visuals.doCustomCape()) return value;
        return Mth.clamp(value, min, max);
    }
    @Inject(method = "extractCapeState",at = @At("TAIL"))
    private void newClamp(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci){
        if(!Visuals.doCustomCape()) return;
        state.capeFlap=state.capeFlap * Visuals.getFlapMult();
        state.capeLean=Mth.clamp(state.capeLean, Visuals.getVerticalLeanNegClamp(), Visuals.getVerticalLeanPosClamp());
        state.capeLean2=Mth.clamp(state.capeLean2,-Visuals.getSideLeanClamp(), Visuals.getSideLeanClamp());
    }
}
