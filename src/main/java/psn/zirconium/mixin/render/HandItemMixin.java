package psn.zirconium.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import psn.zirconium.features.HeldItemRender;

@Mixin(FirstPersonHandsAndItems.class)
public abstract class HandItemMixin{
    @WrapMethod(method = "shouldInstantlyReplaceVisibleItem")
    private boolean customRenderer(ItemStack currentlyVisibleItem, ItemStack expectedItem, LocalPlayer player, Operation<Boolean> original){
        if(HeldItemRender.INSTANCE.getEnabled())return true;
        return original.call(currentlyVisibleItem,expectedItem,player);
    }
}