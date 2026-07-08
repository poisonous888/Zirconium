package psn.zirconium.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.*;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.utils.StaticConnection;

import java.time.Duration;
import java.util.function.Consumer;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class ConnectionSetupMixin{
    @Inject(method="<init>",at=@At("TAIL"))
    private void captureConnection(Connection connection, Minecraft minecraft, ServerData serverData, Screen parent, boolean newWorld, Duration worldLoadDuration, Consumer<Component> updateStatus, LevelLoadTracker levelLoadTracker, TransferState transferState, CallbackInfo ci){
        StaticConnection.setConnection(connection);
    }
}
