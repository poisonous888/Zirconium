package psn.zirconium.mixin;

import net.minecraft.client.Minecraft;
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
import static com.odtheking.odin.utils.PlayerUtilsKt.playSoundAtPlayer;
import static psn.zirconium.ZirconiumEntryKt.modMessageJava;

@Mixin(ChatScreen.class)
public class ChatScreenMixin{
    @Inject(method="mouseClicked", at=@At("HEAD"), cancellable=true)
    private void copy(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir){
        if(!ChatUtils.getCopyChat()) return;
        final var modifier = switch(ChatUtils.getCopyModifier()){
            case ChatUtils.Modifier.CONTROL -> event.hasControlDown();
            case ChatUtils.Modifier.SHIFT -> event.hasShiftDown();
            case ChatUtils.Modifier.ALT -> event.hasAltDown();
            case ChatUtils.Modifier.NONE -> true;
        };
        if(!modifier) return;
        final var msg = getHoveredChatMessage(event.x(), event.y());
        if(msg == null) return;
        final var str = ChatUtils.getFormatRegex().replace(msg.content().getString(), "").trim();
        if(ChatUtils.getMsgOnCopy()) modMessageJava("copied chat - " + str);
        playSoundAtPlayer(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);
        Minecraft.getInstance().keyboardHandler.setClipboard(str);
        cir.cancel();
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    // it works? maybe? mc chat is way too complicated man
    
    @Unique GuiMessage getHoveredChatMessage(double mx,double my){
        final var mc=Minecraft.getInstance();
        final var cc=mc.gui.hud.getChat();
        if(!cc.isChatFocused())return null;
        final var s=(float)cc.getScale();
        final var w=Mth.ceil((float)cc.getWidth()/s);
        final var x=(mx-4.0)/s;
        final var y=my/s;
        if(x<-4||x>w+4)return null;
        final var h=mc.getWindow().getGuiScaledHeight();
        final var anchor=Mth.floor((float)(h-40)/s);
        final var lh=(int)(9.0*(mc.options.chatLineSpacing().get()+1.0));
        final var offset=(anchor+lh)-y;
        if(offset<0)return null;
        final var slot=Mth.floor(offset/lh);
        if(slot<0||slot>=cc.getLinesPerPage())return null;
        final var index=slot+cc.chatScrollbarPos-1;
        if(index<0||index>=cc.trimmedMessages.size())return null;
        return cc.trimmedMessages.get(index).parent();
    }
}
