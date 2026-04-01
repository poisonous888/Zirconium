package psn.zirconium.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.DropUtils;

@Mixin(AbstractContainerScreen.class)
public abstract class DropInvMixin{
    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Inject(method = "slotClicked",at = @At("HEAD"), cancellable = true)
    private void cancelDropInv(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci){
        if(clickType!=ClickType.THROW) return;
        if(DropUtils.doDropContainer(slot.getItem())){
            ci.cancel();
        }
    }
    @Inject(method = "keyPressed",at = @At("HEAD"))
    private void protectNewItem(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> ci){
        assert hoveredSlot != null;
        if(keyEvent.key()==DropUtils.getSbidKey().getValue()){
            DropUtils.sbidNew(hoveredSlot.getItem());
        }
        if(keyEvent.key()==DropUtils.getUuidKey().getValue()){
            DropUtils.uuidNew(hoveredSlot.getItem());
        }
    }
//    @Inject(method = "getTooltipFromContainerItem",at = @At("RETURN"))
//    private void protectedTooltip(ItemStack itemStack, CallbackInfoReturnable<List<Component>> ci){
//        DropUtils.getTooltip(itemStack);
//        //ci.getReturnValue().add(DropUtils.getTooltip(itemStack));
//    }
}