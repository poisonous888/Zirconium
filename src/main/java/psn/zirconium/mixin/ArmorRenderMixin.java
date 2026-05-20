package psn.zirconium.mixin;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.HideArmor;

@Mixin(HumanoidMobRenderer.class)
public abstract class ArmorRenderMixin{
    @Inject(method="getEquipmentIfRenderable",at=@At("HEAD"), cancellable=true)
    private static void disableLayers(LivingEntity livingEntity, EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> cir){
        if(HideArmor.checkIfRenderable(equipmentSlot)){
            cir.setReturnValue(ItemStack.EMPTY);
            cir.cancel();
        }
    }
}
