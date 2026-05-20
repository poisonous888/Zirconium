package psn.zirconium.mixin;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import psn.zirconium.features.HideArmor;

@Mixin(LivingEntityRenderer.class)
public class ArmorSkullMixin{
    @Redirect(
        method="extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
        at=@At(value="INVOKE", target="Lnet/minecraft/world/item/BlockItem;getBlock()Lnet/minecraft/world/level/block/Block;")
    )
    private Block hideSkull(BlockItem instance){
        if(HideArmor.checkSkull()) return Blocks.AIR;
        return instance.getBlock();
    }
}
