//package psn.zirconium.mixin;
//
//import com.llamalad7.mixinextras.sugar.Local;
//import net.minecraft.client.color.block.BlockColors;
//import net.minecraft.client.color.block.BlockTintSource;
//import net.minecraft.client.color.block.BlockTintSources;
//import net.minecraft.client.renderer.BiomeColors;
//import net.minecraft.client.renderer.block.BlockAndTintGetter;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.state.BlockState;
//import org.jspecify.annotations.NonNull;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import psn.zirconium.features.MiscFeatures;
//
//import java.util.List;
//
//import static psn.zirconium.ZirconiumEntryKt.modMessageJava;
//
//@Mixin(BiomeColors.class)
//public abstract class BiomeColorsMixin{
//    @Inject(method="getAverageGrassColor",at=@At("HEAD"), cancellable=true)
//    private static void grass(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir){
//        if(MiscFeatures.getCustomBiomeColor())cir.setReturnValue(MiscFeatures.getBiomeColor().getRgba());
//    }
//    @Inject(method="getAverageFoliageColor",at=@At("HEAD"), cancellable=true)
//    private static void leaves(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir){
//        if(MiscFeatures.getCustomBiomeColor())cir.setReturnValue(MiscFeatures.getBiomeColor().getRgba());
//    }
//    @Inject(method="getAverageDryFoliageColor",at=@At("HEAD"), cancellable=true)
//    private static void dryLeaves(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir){
//        if(MiscFeatures.getCustomBiomeColor())cir.setReturnValue(MiscFeatures.getBiomeColor().getRgba());
//    }
//
////    @Inject(method="createDefault",at=@At(value="RETURN"))
////    private static void append(CallbackInfoReturnable<BlockColors> cir, @Local(name="colors") BlockColors colors){
////        modMessageJava("append");
////        if(!MiscFeatures.getCustomBiomeColor())return;
////        colors.register(
////            List.of(customTint),
////            Blocks.LARGE_FERN,
////            Blocks.TALL_GRASS,
////            Blocks.FERN,
////            Blocks.SHORT_GRASS,
////            Blocks.POTTED_FERN,
////            Blocks.BUSH,
////            Blocks.GRASS_BLOCK,
//////            Blocks.SPRUCE_LEAVES,
//////            Blocks.BIRCH_LEAVES,
////            Blocks.OAK_LEAVES,
////            Blocks.JUNGLE_LEAVES,
////            Blocks.ACACIA_LEAVES,
////            Blocks.DARK_OAK_LEAVES,
////            Blocks.VINE,
////            Blocks.MANGROVE_LEAVES,
////            Blocks.LEAF_LITTER,
////            Blocks.SUGAR_CANE,
////            Blocks.ATTACHED_MELON_STEM,
////            Blocks.ATTACHED_PUMPKIN_STEM,
////            Blocks.MELON_STEM,
////            Blocks.PUMPKIN_STEM,
////            Blocks.LILY_PAD
////        );
////        colors.register(
////            List.of(BlockTintSources.constant(-1), BlockTintSources.grass()),
////            Blocks.PINK_PETALS,
////            Blocks.WILDFLOWERS
////        );
////    }
//}
