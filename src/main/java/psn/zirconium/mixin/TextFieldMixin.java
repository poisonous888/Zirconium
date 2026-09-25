package psn.zirconium.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import psn.zirconium.features.MiscFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class TextFieldMixin{
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void enterKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if(event.key() == InputConstants.KEY_RETURN && MiscFeatures.getCloseSign()){
            Screen mcScreen=Minecraft.getInstance().gui.screen();
            if(mcScreen instanceof SignEditScreen){
                mcScreen.onClose();
            }
        }
    }
}