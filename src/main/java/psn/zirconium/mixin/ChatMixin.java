package psn.zirconium.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.ChatUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Mixin(value=ChatComponent.class,priority=69420)
public class ChatMixin{
    @Shadow @Final public List<GuiMessage> allMessages;
    @Shadow private void refreshTrimmedMessages(){}
    
    @Unique private final DateTimeFormatter format=DateTimeFormatter.ofPattern("HH:mm:ss");
    @Unique @Final private HashMap<String,Info> lookup=new HashMap<>();
    private static class Info{
        Info(GuiMessage l){latest=l;}
        GuiMessage latest;
        int count=1;
    }
    
    @Inject(method="addMessage",at=@At("HEAD"), cancellable=true)
    private void checkInstantCancel(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci){
        final var str=contents.getString();
        final var blank=str.isBlank();
        if(ChatUtils.getHideBlank()&&blank)ci.cancel();
        if(blank)return;
        separator=ChatUtils.checkSeparator(str);
        if(ChatUtils.getSeparators()==0&&separator) ci.cancel();
    }
    @Unique boolean separator=false;
    @Redirect(method="addMessage",at=@At(value="NEW", target="Lnet/minecraft/client/multiplayer/chat/GuiMessage;"))
    private GuiMessage compact(int addedTime, Component content, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag){
        final var message=Component.empty();
        final var out=new GuiMessage(addedTime,message,signature,source,tag);
        if(ChatUtils.getTimestamp())message.append("§8["+LocalDateTime.now().format(format)+"]§r ");
        message.append(content);
        if(ChatUtils.getCompact()&&!(ChatUtils.getSeparators()==2&&separator)){
            final var str=content.getString();
            final var info=lookup.get(str);
            if(info!=null){
                info.count++;
                message.append(" §8("+info.count+")");
                allMessages.remove(info.latest);
                refreshTrimmedMessages();
                info.latest=out;
                return out;
            }
            lookup.put(str,new Info(out));
            return out;
        }
        return out;
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    @Inject(method="clearMessages",at=@At("HEAD"), cancellable=true)
    private void noClear(boolean history, CallbackInfo ci){
        if(history&&ChatUtils.getNoClear())ci.cancel();
        lookup.clear();
    }
    
    //props to https://modrinth.com/mod/morechathistory
    
    @ModifyExpressionValue(method={"addMessageToDisplayQueue", "addMessageToQueue", "addRecentChat"}, at=@At(value="CONSTANT", args="intValue=100"))
    public int noMax(int original){
        if(ChatUtils.getInfLines()) return 16000;
        return original;
    }
}