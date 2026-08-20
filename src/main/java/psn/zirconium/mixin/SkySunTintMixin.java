package psn.zirconium.mixin;

import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import psn.zirconium.features.MiscFeatures;

@Mixin(AtmosphericFogEnvironment.class)
public abstract class SkySunTintMixin{
    @ModifyConstant(method = "getBaseColor", constant = @Constant(intValue = 4))
    private int injected(int value) {
        if(MiscFeatures.getNoSky())return 2048;
        return value;
    }
}
