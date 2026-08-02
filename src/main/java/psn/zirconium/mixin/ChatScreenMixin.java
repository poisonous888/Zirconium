package psn.zirconium.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.ChatUtils;

import static com.odtheking.odin.utils.ChatUtilsKt.modMessage;
import static psn.zirconium.ZirconiumEntryKt.zcon;

@Mixin(ChatScreen.class)
public class ChatScreenMixin{
    @Inject(method="mouseClicked",at=@At("HEAD"))
    private void copy(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir){
        if(event.button()==1&&ChatUtils.checkCopyKey()){
            modMessage("Not Currently Implimented",zcon,null);
            Minecraft.getInstance().keyboardHandler.setClipboard("");
        }
    }
}
