package psn.zirconium.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
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
    @Shadow private @Nullable Slot clickedSlot;

//    @Inject(method = "mouseClicked",at = @At("HEAD"), cancellable = true)
//    private void cancelDrop(MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> ci){
//        var slot=clickedSlot;
//
//        if(slot==null) return;
//        if(DropUtils.doDropContainer(slot.getItem(),clickType)){
//            ci.setReturnValue(false);
//            ci.cancel();
//        }
//    }

    @Inject(method = "slotClicked",at = @At("HEAD"), cancellable = true)
    private void cancelDropInv(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci){
//        DropUtils.msg(clickType.name());
//        if(clickType==ClickType.THROW)ci.cancel();

        if(slot==null) return;
        if(DropUtils.doDropContainer(slot.getItem(),clickType)){
            ci.cancel();
        }
    }

//    @Inject(method = "getTooltipFromContainerItem",at = @At("RETURN"))
//    private void protectedTooltip(ItemStack itemStack, CallbackInfoReturnable<List<Component>> ci){
//        DropUtils.getTooltip(itemStack);
//        //ci.getReturnValue().add(DropUtils.getTooltip(itemStack));
//    }
}