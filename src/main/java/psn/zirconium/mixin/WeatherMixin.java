//package psn.zirconium.mixin;
//
//import net.minecraft.client.renderer.WeatherEffectRenderer;
//import net.minecraft.client.renderer.state.level.WeatherRenderState;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.biome.Biome;
//import net.minecraft.world.phys.Vec3;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import psn.zirconium.features.MiscFeatures;
//
//@Mixin(WeatherEffectRenderer.class)
//public abstract class WeatherMixin{
//    @Inject(method="getPrecipitationAt",at=@At("HEAD"), cancellable=true)
//    private static void setType(Level level, BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir){
//        final var type=MiscFeatures.getWeather();
//        switch(type){
//            case 1:
//                cir.setReturnValue(Biome.Precipitation.NONE);
//                break;
//            case 2:
//                cir.setReturnValue(Biome.Precipitation.RAIN);
//                break;
//            case 3:
//                cir.setReturnValue(Biome.Precipitation.SNOW);
//                break;
//        }
//    }
//    @Inject(method="extractRenderState",at=@At("TAIL"))
//    private void intensity(Level level, int ticks, float partialTicks, Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci){
//        if(!MiscFeatures.getSetWeatherStats()) return;
//        renderState.intensity=MiscFeatures.getWeatherIntensity();
//        renderState.radius=MiscFeatures.getWeatherRadius();
//    }
//}
