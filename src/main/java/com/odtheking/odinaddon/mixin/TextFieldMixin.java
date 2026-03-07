package com.odtheking.odinaddon.mixin;

import com.odtheking.odin.utils.ChatUtilsKt;
import com.odtheking.zcon.features.impl.skyblock.MiscFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// example mixin
@Mixin(AbstractSignEditScreen.class)
public abstract class TextFieldMixin{
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void enterKeyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if(keyEvent.key() == GLFW.GLFW_KEY_ENTER && MiscFeatures.getCloseSign()){
            Screen mcScreen=Minecraft.getInstance().screen;
            if(mcScreen instanceof SignEditScreen){
                mcScreen.onClose();
            }
        }
    }
}