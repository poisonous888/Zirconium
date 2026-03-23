package com.odtheking.odinaddon.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.odtheking.zcon.features.impl.skyblock.OneEightNine;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class BackwardsWalkMixin extends Entity {

    @Shadow
    public float yBodyRot;

    public BackwardsWalkMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;abs(F)F"))
    private float rotateBackwardsWalking(float value, Operation<Float> original) {
        if (OneEightNine.getDiagonalWalk()) {
            return 0F;
        } else {
            return original.call(value);
        }
    }

    @WrapOperation(method = "tickHeadTurn", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(F)F"))
    private float backwardsWalkingHeadRotation(float value, Operation<Float> original) {
        if (OneEightNine.getDiagonalWalk()) {
            value = Mth.clamp(value, -75.0F, 75.0F);
            this.yBodyRot = this.getYRot() - value;
            if (Math.abs(value) > 50.0F) {
                this.yBodyRot += value * 0.2F;
            }

            return Float.MIN_VALUE;
        } else {
            return original.call(value);
        }
    }
}
