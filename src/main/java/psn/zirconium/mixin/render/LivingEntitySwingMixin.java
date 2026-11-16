package psn.zirconium.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import psn.zirconium.features.HeldItemRender;

@Mixin(LivingEntity.SwingState.class)
public class LivingEntitySwingMixin{
    @ModifyArg(method = "start",at = @At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity$SwingDescription;<init>(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;I)V"),index = 2)
    private static int modifySwingTime(int durationTicks){
        if(HeldItemRender.getDoSwingDur()){
            if(HeldItemRender.getDoHaste()){
                durationTicks=HeldItemRender.getSwingDuration()+durationTicks-7;
            }
            else{
                durationTicks=HeldItemRender.getSwingDuration();
            }
        }
        return durationTicks;
    }
    
    @Shadow private LivingEntity.@Nullable SwingDescription currentSwing;
    @Shadow private int ticks;
    @Shadow private void start(final InteractionHand hand, final SwingAnimation animation, final int durationTicks){}
    @WrapMethod(method = "startIfAble")
    private boolean startIfAbleCustom(InteractionHand hand, SwingAnimation animation, int durationTicks, Operation<Boolean> original){
        if(!HeldItemRender.INSTANCE.getEnabled())return original.call(hand,animation,durationTicks);
        if(
            currentSwing!=null &&
            (
                ticks < 2 ||
                ticks/(double)currentSwing.durationTicks() <= HeldItemRender.getReswingPercent()
            )
        ) return false;
        start(hand, animation, durationTicks);
        return true;
    }
}
