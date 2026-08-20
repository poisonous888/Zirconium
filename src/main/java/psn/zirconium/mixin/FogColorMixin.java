package psn.zirconium.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(FogRenderer.class)
public abstract class FogColorMixin{
    @Inject(method="computeFogColor",at=@At(value="HEAD"), cancellable=true)
    private static void fogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f dest, CallbackInfo ci){
        if(!MiscFeatures.getCustomSkyColor())return;
        final var col=MiscFeatures.getSkyColor();
        dest.set(col.getRedFloat(),col.getGreenFloat(),col.getBlueFloat(), 1f);
        ci.cancel();
    }
    
    
    
    
    
    //still removes all fog
//        @Shadow @Final private static List<FogEnvironment> FOG_ENVIRONMENTS;
//        @Final @Unique private static List<FogEnvironment> DarkBlindEnv=Lists.newArrayList(new FogEnvironment[]{new BlindnessFogEnvironment(), new DarknessFogEnvironment()});
//        @Redirect(method="setupFog",at=@At(value="FIELD", target="Lnet/minecraft/client/renderer/fog/FogRenderer;FOG_ENVIRONMENTS:Ljava/util/List;"))
//        private static List<FogEnvironment> onlyDarkAndBlind(){
//            if(MiscFeatures.getNoFog())return DarkBlindEnv;
//            return FOG_ENVIRONMENTS;
//        }
    

    
    //dosent work
//    @Inject(method="getFogType",at=@At(value="RETURN"), cancellable=true)
//    private static void noLavaWater(Camera camera, CallbackInfoReturnable<FogType> cir){
//        final var type=cir.getReturnValue();
//        if(type==FogType.LAVA||type==FogType.WATER)cir.setReturnValue(FogType.NONE);
//    }
//    @Inject(method="computeFogColor", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;entity()Lnet/minecraft/world/entity/Entity;"), cancellable=true)
//    private static void noFogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f dest, CallbackInfo ci, @Local(ordinal=0) FogType fogType){
//        if(fogType==FogType.LAVA||fogType==FogType.WATER) ci.cancel();
//    }
//

    
    //dosent work
//    @Inject(method="setupFog", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;entity()Lnet/minecraft/world/entity/Entity;"), cancellable=true)
//    private static void noFog(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir, @Local(ordinal=0) FogType fogType){
//        if(fogType==FogType.LAVA||fogType==FogType.WATER){
//            cir.setReturnValue(new FogData());
//        }
//    }
//    @Inject(method="computeFogColor", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;entity()Lnet/minecraft/world/entity/Entity;"), cancellable=true)
//    private static void noFogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f dest, CallbackInfo ci, @Local(ordinal=0) FogType fogType){
//        if(fogType==FogType.LAVA||fogType==FogType.WATER) ci.cancel();
//    }
//    @Inject(method="setupFog", at=@At(value="INVOKE", target="Lnet/minecraft/client/Camera;entity()Lnet/minecraft/world/entity/Entity;"), cancellable=true)
//    private static void noFog(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir, @Local(ordinal=0) FogType fogType){
//        if(fogType==FogType.LAVA||fogType==FogType.WATER) cir.cancel();
//    }
    
    
    
    
    //cool effect but not what im lf
//    @Inject(method="getFogType",at=@At(value="RETURN"), cancellable=true)
//    private static void noLavaWater(Camera camera, CallbackInfoReturnable<FogType> cir){
//        final var type=cir.getReturnValue();
//        if(type==FogType.LAVA||type==FogType.WATER)cir.setReturnValue(FogType.NONE);
//    }
//    @Inject(method="computeFogColor",at=@At(value="HEAD"), cancellable=true)
//    private static void blackFog(Camera camera, float partialTicks, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f dest, CallbackInfo ci){
//        ci.cancel();
//    }
    
    
    //works but removes ALL fog, which is a cheat
//    @Inject(method="updateBuffer(Lnet/minecraft/client/renderer/fog/FogData;)V",at=@At("HEAD"), cancellable=true)
//    private static void noFog(FogData fog, CallbackInfo ci){
//        if(MiscFeatures.getNoFog()) ci.cancel();
//    }
}
