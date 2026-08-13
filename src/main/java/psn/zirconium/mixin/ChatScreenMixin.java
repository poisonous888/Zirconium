package psn.zirconium.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.ChatUtils;

import static com.odtheking.odin.utils.ChatUtilsKt.modMessage;
import static com.odtheking.odin.utils.PlayerUtilsKt.playSoundAtPlayer;
import static psn.zirconium.ZirconiumEntryKt.zcon;

@Mixin(ChatScreen.class)
public class ChatScreenMixin{
    @Inject(method="mouseClicked", at=@At("HEAD"), cancellable=true)
    private void copy(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir){
        if(!ChatUtils.getCopyChat()) return;
        final var modifier=switch(ChatUtils.getCopyModifier()){
            case 0 -> event.hasControlDown();
            case 1 -> event.hasShiftDown();
            case 2 -> event.hasAltDown();
            case 3 -> true;
            default -> false;
        };
        if(event.button() != 0 || !modifier) return;
        final var msg=getHoveredChatMessage();
        if(msg == null) return;
        var str=ChatUtils.getFormatRegex().replace(msg.content().getString(), "").trim();
        if(ChatUtils.getMsgOnCopy()) modMessage("copied chat - " + str, zcon, null);
        playSoundAtPlayer(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);
        Minecraft.getInstance().keyboardHandler.setClipboard(str);
        cir.cancel();
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    //AI bc for some reason mc decided to make getting the hovered chat message for the style only
    //dw i tested it
    @Unique
    private GuiMessage getHoveredChatMessage(){
        var mc=Minecraft.getInstance();
        var chatComponent=mc.gui.getChat();
        
        double mouseY=mc.mouseHandler.ypos() * (double)mc.getWindow().getGuiScaledHeight() / (double)mc.getWindow().getScreenHeight();
        double distanceFromBottom=(double)mc.getWindow().getGuiScaledHeight() - 40.0 - mouseY;
        
        if(distanceFromBottom < 0) return null;
        
        double scale=chatComponent.getScale();
        double lineHeight=chatComponent.getLineHeight();
        
        int lineIndex=Mth.floor(distanceFromBottom / (scale * lineHeight) + (double)chatComponent.chatScrollbarPos);
        
        if(lineIndex < 0 || lineIndex >= chatComponent.trimmedMessages.size()) return null;
        int finalIndex=getFinalIndex(lineIndex, chatComponent);
        if(finalIndex < 0 || finalIndex >= chatComponent.allMessages.size()) return null;
        
        return chatComponent.allMessages.get(finalIndex);
        
    }
    
    @Unique
    private int getFinalIndex(int lineIndex, ChatComponent chatComponent){
        while(lineIndex < chatComponent.trimmedMessages.size()){
            var line=chatComponent.trimmedMessages.get(lineIndex);
            if(line.endOfEntry()) break;
            lineIndex++;
        }
        int targetMessageIdx=0;
        for(int i=chatComponent.trimmedMessages.size() - 1; i >= lineIndex; i--){
            if(i < chatComponent.trimmedMessages.size() && chatComponent.trimmedMessages.get(i).endOfEntry()){
                targetMessageIdx++;
            }
        }
        
        return chatComponent.allMessages.size() - targetMessageIdx;
    }
    
}
