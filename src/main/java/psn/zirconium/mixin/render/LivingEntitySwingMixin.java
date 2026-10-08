package psn.zirconium.mixin.render;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import psn.zirconium.features.HeldItemRender;

@Mixin(LivingEntity.SwingState.class)
public class LivingEntitySwingMixin{
    @ModifyArg(method = "start",at = @At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity$SwingDescription;<init>(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;I)V"),index = 2)
    private static int modifySwingTime(int durationTicks){
        if(HeldItemRender.doSwingDur()){
            if(HeldItemRender.doHaste()){
                durationTicks=HeldItemRender.getSwingDuration() + durationTicks - 7;
            }
            else{
                durationTicks=HeldItemRender.getSwingDuration();
            }
        }
        return durationTicks;
    }
}
