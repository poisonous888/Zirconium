package psn.zirconium.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.HeldItemRender;
import psn.zirconium.utils.RenderHandItem;

@Mixin(ItemInHandRenderer.class)
public abstract class R_RenderItemMixin {
    @Inject(method = "renderHandsWithItems",at = @At("HEAD"), cancellable=true)
    private void customRenderer(float frameInterp, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LocalPlayer player, int lightCoords, CallbackInfo ci){
        if(HeldItemRender.INSTANCE.getEnabled()){
            RenderHandItem.INSTANCE.mainRender(frameInterp,poseStack,submitNodeCollector,player,lightCoords);
            ci.cancel();
        }
    }
}