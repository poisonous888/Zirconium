package psn.zirconium.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.ItemPos;
import psn.zirconium.features.Visuals;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class R_LivingEntityMixin extends Entity {
    @Shadow public float yBodyRot;
    public R_LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "getCurrentSwingDuration",at = @At("HEAD"), cancellable = true)
    private void noHaste(CallbackInfoReturnable<Integer> ci){
        if(ItemPos.doHaste()){
            ci.setReturnValue(ItemPos.getSwingDuration());
            ci.cancel();
        }
    }
    @Inject(method = "getCurrentSwingDuration",at = @At("RETURN"), cancellable = true)
    private void withHaste(CallbackInfoReturnable<Integer> ci){
        if(ItemPos.doSwingDur()) {
            ci.setReturnValue(ItemPos.getSwingDuration() + ci.getReturnValue() - 7);
            // swing value is 7 without haste/fatigue
        }
    }
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;abs(F)F"))
    private float rotateBackwardsWalking(float value, Operation<Float> original) {
        if (Visuals.doDiagWalk()) {
            return 0F;
        }
        return original.call(value);
    }
    @WrapOperation(method = "tickHeadTurn", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(F)F"))
    private float backwardsWalkingHeadRotation(float value, Operation<Float> original) {
        if (Visuals.doDiagWalk()) {
            value = Mth.clamp(value, -75.0F, 75.0F);
            this.yBodyRot = this.getYRot() - value;
            if (Math.abs(value) > 50.0F) {
                this.yBodyRot += value * 0.2F;
            }
            return Float.MIN_VALUE;
        }
        return original.call(value);
    }
}
