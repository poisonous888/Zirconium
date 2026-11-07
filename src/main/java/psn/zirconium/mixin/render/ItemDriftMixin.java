package psn.zirconium.mixin.render;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.HeldItemRender;

@Mixin(LocalPlayer.class)
public abstract class ItemDriftMixin extends Entity{
    public ItemDriftMixin(EntityType<?> type, Level level){
        super(type, level);
    }
    
    @Shadow public float xBob;
    @Shadow public float yBob;
    @Shadow public float xBobO;
    @Shadow public float yBobO;
    
    @Inject(method = "applyInput",at=@At("TAIL"))
    private void safe(CallbackInfo ci){
        if(!HeldItemRender.INSTANCE.getEnabled())return;
        final var amt = HeldItemRender.getDriftLag();
        
        xBob = xBobO + (getXRot() - xBobO) * amt;
        yBob = yBobO + (getYRot() - yBobO) * amt;
    }
}