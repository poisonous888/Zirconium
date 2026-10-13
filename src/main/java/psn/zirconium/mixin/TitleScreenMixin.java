package psn.zirconium.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen{
    protected TitleScreenMixin(Component title){super(title);}
    
    @Inject(method = "init",at=@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;isDemo()Z"), cancellable=true)
    private void noMicroSlop(CallbackInfo ci){
        if(!MiscFeatures.getNoMicroSlop())return;
        
        int spacing = 24;
        int topPos = this.height / 4 + 48;
        int sidePos = width / 2 - 100;
        
        addRenderableWidget(
            Button.builder(
                Component.translatable("menu.singleplayer"),
                (singlePlayer) ->
                    minecraft.gui.setScreen(new SelectWorldScreen(this))
            )
            .bounds(sidePos, topPos, 200, 20)
            .build()
        );
        
        addRenderableWidget(
            Button.builder(
                Component.translatable("menu.multiplayer"),
                (multiplayer) -> minecraft.gui.setScreen(new JoinMultiplayerScreen(this))
            )
            .bounds(sidePos, topPos + spacing, 200, 20)
            .build()
        );
        
        addRenderableWidget(
            Button.builder(
                Component.translatable("menu.options"),
                (options) ->
                    minecraft.gui.setScreen(new OptionsScreen(this, this.minecraft.options))
            )
            .bounds(sidePos, topPos + 2 * spacing + 12, 98, 20)
            .build()
        );
        
        addRenderableWidget(
            Button.builder(
                Component.translatable("menu.quit"),
                (quit) ->
                    minecraft.stop()
            )
            .bounds(width / 2 + 2, topPos + 2 * spacing + 12, 98, 20)
            .build()
        );
        
        var text=Component.literal(MiscFeatures.getTitleText());
        int copyrightWidth = font.width(text);
        int copyrightX = width - copyrightWidth - 2;
        addRenderableWidget(
            new PlainTextButton(
                copyrightX,
                this.height - 10,
                copyrightWidth,
                10,
                text,
                (nothing) -> {},
                this.font
            )
        );
        
        
        ci.cancel();
    }
}
