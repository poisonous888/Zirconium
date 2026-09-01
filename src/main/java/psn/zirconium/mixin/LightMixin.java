package psn.zirconium.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(LightmapRenderStateExtractor.class)
public class LightMixin{
    @Shadow private boolean needsUpdate;
    @Shadow @Final private GameRenderer renderer;
    @Shadow @Final private Minecraft minecraft;
    @Unique private Vector3fc lightColor=new Vector3f(1,1,1);
    
    @Inject(method="extract",at=@At(value="HEAD"), cancellable=true)
    public void extract(LightmapRenderState renderState, float partialTicks, CallbackInfo ci) {
        if(!MiscFeatures.getFullbright())return;
        ci.cancel();
        renderState.needsUpdate = this.needsUpdate;
        if (this.needsUpdate) {
            ClientLevel level = this.minecraft.level;
            LocalPlayer player = this.minecraft.player;
            if (level != null && player != null) {
                ProfilerFiller profiler = Profiler.get();
                profiler.push("lightmap");
                
                renderState.blockFactor = 0;
                renderState.skyFactor = 0;
                renderState.brightness = 0;
                renderState.darknessEffectScale = 0;
                renderState.nightVisionEffectIntensity = 0;
                renderState.bossOverlayWorldDarkening = renderer.getBossOverlayWorldDarkening(partialTicks);
                
                renderState.blockLightTint = lightColor;
                renderState.skyLightColor = lightColor;
                renderState.nightVisionColor = lightColor;
                final var rgb=MiscFeatures.getLightColor();
                renderState.ambientColor=new Vector3f(rgb.getRedFloat(),rgb.getGreenFloat(),rgb.getBlueFloat());
                //renderState.ambientColor = lightColor;
                
                profiler.pop();
                this.needsUpdate = false;
            }
        }
    }
}