package psn.zirconium.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.HeldItemRender;
import psn.zirconium.utils.RenderHandItem;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class RenderItemMixin{
    @Inject(method = "submitHandsWithItems",at = @At("HEAD"), cancellable=true)
    private void customRenderer(float partialTicks, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci){
        if(HeldItemRender.INSTANCE.getEnabled()){
            RenderHandItem.INSTANCE.render(partialTicks, poseStack, submitNodeCollector, playerState, state);
            ci.cancel();
        }
    }
}