package psn.zirconium.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.DropUtils;

@Mixin(AbstractContainerScreen.class)
public abstract class DropInvMixin{
    @Inject(method = "slotClicked",at = @At("HEAD"), cancellable = true)
    private void cancelDropInv(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci){
        if(DropUtils.doDropContainer(slot,slotId,containerInput)){
            ci.cancel();
        }
    }
}