package psn.zirconium.mixin;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.HideArmor;

@Mixin(LivingEntityRenderer.class)
public class ArmorSkullMixin<T extends LivingEntity, S extends LivingEntityRenderState>{
    @Inject(method="extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at=@At("TAIL"))
    private void noSkull(T entity, S state, float partialTicks, CallbackInfo ci){
        if(HideArmor.checkSkull()){
            if(entity instanceof ArmorStand){
                return;
            }
            state.headItem.clear();
            state.wornHeadType=null;
        }
    }
}
