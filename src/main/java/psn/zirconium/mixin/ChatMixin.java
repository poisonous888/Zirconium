package psn.zirconium.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.ChatUtils;
import java.util.ArrayDeque;
import java.util.List;

@Mixin(value=ChatComponent.class,priority=69420)
public class ChatMixin{
    private static class CompactEntry{
        CompactEntry(GuiMessage r,int c,String s){raw=r;count=c;msg=s;}
        GuiMessage raw;
        Integer count;
        String msg;
    }
    @Unique @Final private ArrayDeque<CompactEntry> compactCheck=new ArrayDeque<>(ChatUtils.getCompactLines());
    @Shadow @Final private List<GuiMessage> allMessages;
    @Shadow private void refreshTrimmedMessages(){}
    
    @Inject(method="addMessage",at=@At("HEAD"), cancellable=true)
    private void compact(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci){
        if(ChatUtils.checkBlankAndSeparator(contents)){
            ci.cancel();
            return;
        }
        final var checkStr=contents.getString();
        final var compIter=compactCheck.iterator();
        while(compIter.hasNext()){
            final var cur=compIter.next();
            if(cur.msg.equals(checkStr)){
                compIter.remove();
                allMessages.remove(cur.raw);
                refreshTrimmedMessages();
                cur.count++;
                compactCheck.addFirst(cur);
                return;
            }
        }
        while(compactCheck.size()>=ChatUtils.getCompactLines()){compactCheck.removeLast();}
        compactCheck.addFirst(new CompactEntry(null, 1, contents.getString()));
    }
    @ModifyArg(method="addMessage",at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/chat/GuiMessage;<init>(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V"),index=1)
    private Component addCount(Component content){
        final var count=compactCheck.getFirst().count;
        if(count<2)return content;
        return content.copy().append(" §8("+count+")");
    }
    @Inject(method="addMessage",at=@At(value="TAIL"))
    private void captureGuiMessage(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci, @Local(name="message") GuiMessage message){
        if(!ChatUtils.getCompact())return;
        compactCheck.getFirst().raw=message;
    }
    
    
    @Inject(method="clearMessages",at=@At("HEAD"), cancellable=true)
    private void noClear(boolean history, CallbackInfo ci){
        if(ChatUtils.getNoClear())ci.cancel();
    }
    
    //props to https://modrinth.com/mod/morechathistory
    
    @ModifyExpressionValue(method={"addMessageToDisplayQueue", "addMessageToQueue", "addRecentChat"}, at=@At(value="CONSTANT", args="intValue=100"))
    public int noMax(int original){
        if(ChatUtils.getInfLines()) return 16000;
        return original;
    }
}
