package psn.zirconium.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(ClientLevel.class)
public abstract class BlockParticleMixin{
    @Inject(method ="addDestroyBlockEffect",at = @At("HEAD"), cancellable = true)
    private void noBreakParticles(BlockPos pos,BlockState blockState,CallbackInfo ci){
        if(MiscFeatures.getNoBreakParticles()) ci.cancel();
    }
}