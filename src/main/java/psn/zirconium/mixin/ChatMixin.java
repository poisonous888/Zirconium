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
        checkSeparator(str);
        if(ChatUtils.getSeparators()==0&&separator) ci.cancel();
    }
    @Unique private void checkSeparator(String str){
        final var first=str.charAt(0);
        for(char c:str.toCharArray()){
            if(c != first){
                separator=false;
                return;
            }
        }
        separator=true;
    }
    @Unique boolean separator=false;
    @Redirect(method="addMessage",at=@At(value="NEW", target="Lnet/minecraft/client/multiplayer/chat/GuiMessage;"))
    private GuiMessage compact(int addedTime, Component content, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag){
        //rebuilding the component to allow modification
        final var message=Component.empty();
        final var out=new GuiMessage(addedTime,message,signature,source,tag);
        if(ChatUtils.getTimestamp())message.append("§8["+LocalDateTime.now().format(format)+"]§r ");
        message.append(content);
        //separator mode "all" does not get compacted
        if(ChatUtils.getCompact()&&!(ChatUtils.getSeparators()==2&&separator)){
            if(ChatUtils.getDebug())IO.println(lookup.size());
            //get the lookup key for the message
            //with compact numbers on, additionally replace every number with a tab character
            var str=content.getString();
            if(ChatUtils.getCompactNumbers())str=ChatUtils.getNumberRegex().replace(str,"\t");
            final var info=lookup.get(str);
            //message has been seen before and must be compacted
            if(info!=null){
                info.count++;
                message.append(" §8("+info.count+")");
                //reverse order to prevent lag with high list size
                for(int i=allMessages.size()-1;i>=0;i--){
                    final var cur=allMessages.get(i);
                    if(cur.equals(info.latest)){
                        allMessages.remove(i);
                        break;
                    }
                }
                allMessages.remove(info.latest);
                refreshTrimmedMessages();
                info.latest=out;
            }
            //message has not been seen before
            else{
                //check if buffer needs to be culled
                final var preCull=lookup.size();
                if(preCull>=ChatUtils.getCleanCutoff()){
                    //in the first stage it only culls messages that have only been seen once
                    //because the buffer holds onto every message sent, most messages are culled here
                    lookup.entrySet().removeIf(cur -> cur.getValue().count < 2);
                    final var postCull=lookup.size();
                    ChatUtils.logDebug("Cleaning Up Buffer",preCull,postCull);
                    //if there are still a lot of leftover compact terms, fully clears the buffer
                    //the main source of lag is when it has to search really far back in allMessages
                    //by clearing the buffer, it tells it to stop compacting anything before the clear,
                    //which SHOULD make far back searches a lot less common
                    if(postCull>=ChatUtils.getPurgeCutoff()){
                        lookup.clear();
                        ChatUtils.logDebug("Purged Buffer",postCull,0);
                    }
                }
                //add to compact list
                lookup.put(str,new Info(out));
            }
        }
        return out;
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    @Inject(method="clearMessages",at=@At("HEAD"), cancellable=true)
    private void noClear(boolean history, CallbackInfo ci){
        if(history&&ChatUtils.getNoClear())ci.cancel();
        else lookup.clear();
    }
    
//    @Inject(method="resetChatScroll",at=@At("HEAD"), cancellable=true)
//    private void noScroll(CallbackInfo ci){
//        if(ChatUtils.getNoResetScroll())ci.cancel();
//        modMessageJava("scrolled");
//    }
    
    //props to https://modrinth.com/mod/morechathistory
    
    @ModifyExpressionValue(method={"addMessageToDisplayQueue", "addMessageToQueue", "addRecentChat"}, at=@At(value="CONSTANT", args="intValue=100"))
    public int noMax(int original){
        if(ChatUtils.getInfLines()) return 16000;
        return original;
    }
}