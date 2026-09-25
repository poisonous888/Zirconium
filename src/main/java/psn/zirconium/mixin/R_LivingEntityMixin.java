package psn.zirconium.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import psn.zirconium.features.MiscFeatures;
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
    
    //from animatium
    //https://modrinth.com/mod/animatium
    
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;abs(F)F"))
    private float rotateBackwardsWalking(float v, Operation<Float> original) {
        if (MiscFeatures.getDiagonalWalk()) {
            return 0F;
        }
        return original.call(v);
    }
    @WrapOperation(method = "tickHeadTurn", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(F)F"))
    private float backwardsWalkingHeadRotation(float a, Operation<Float> original) {
        if (MiscFeatures.getDiagonalWalk()) {
            a= Mth.clamp(a, -75.0F, 75.0F);
            this.yBodyRot = this.getYRot() - a;
            if (Math.abs(a) > 50.0F) {
                this.yBodyRot += a * 0.2F;
            }
            return Float.MIN_VALUE;
        }
        return original.call(a);
    }
}
