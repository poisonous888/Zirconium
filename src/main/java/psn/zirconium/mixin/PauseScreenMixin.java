package psn.zirconium.mixin;

import com.odtheking.odin.clickgui.ClickGUI;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import psn.zirconium.features.MiscFeatures;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen{
    protected PauseScreenMixin(Component title){super(title);}
    
    @Inject(method = "createPauseMenu",at=@At(value="HEAD"), cancellable=true)
    private void test(CallbackInfo ci){
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper helper = gridLayout.createRowHelper(2);
        
        var return_game=Button.builder(
            PauseScreen.RETURN_TO_GAME, _ -> {
                minecraft.gui.setScreen(null);
                minecraft.mouseHandler.grabMouse();
            }
        ).width(204).build();
        
        var resourcepacks=Button.builder(
            OptionsScreen.RESOURCEPACK, (_) ->
                minecraft.gui.setScreen(
                    new PackSelectionScreen(
                        minecraft.getResourcePackRepository(),
                        (packRepository)->{
                            minecraft.options.updateResourcePacks(packRepository);
                            minecraft.gui.setScreen(this);
                        },
                        minecraft.getResourcePackDirectory(),
                        Component.translatable("resourcePack.title")
                    )
                )
        ).width(98).build();
        
        var options=Button.builder(
            PauseScreen.OPTIONS, _ -> minecraft.gui.setScreen(new OptionsScreen(this,minecraft.options))
        ).width(98).build();
        
        var sound=Button.builder(
            OptionsScreen.SOUNDS, _ -> minecraft.gui.setScreen(new SoundOptionsScreen(this,minecraft.options))
        ).width(98).build();
        
        var mod_options=Button.builder(
            Component.literal("Odin Config..."), _ -> minecraft.gui.setScreen(ClickGUI.INSTANCE)
        ).width(98).build();
        
        var disconnect=Button.builder(
            CommonComponents.GUI_DISCONNECT, _ ->
                 minecraft
                .getReportingContext()
                .draftReportHandled(
                    minecraft,
                    this,
                    () -> minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE),
                    true
                )
        ).width(204).build();
        
        helper.addChild(return_game,2, gridLayout.newCellSettings().paddingTop(60));
        helper.addChild(resourcepacks);
        helper.addChild(sound);
        helper.addChild(options);
        helper.addChild(mod_options);
        helper.addChild(disconnect,2);
        
        gridLayout.arrangeElements();
        FrameLayout.alignInRectangle(gridLayout, 0, 0, this.width, this.height, 0.5F, 0.25F);
        gridLayout.visitWidgets(this::addRenderableWidget);
        ci.cancel();
    }
    
    @Inject(method = "createPauseMenu",at=@At(value="HEAD"), cancellable=true)
    private void noMicroSlop(CallbackInfo ci){
        if(!MiscFeatures.getNoMicroSlop())return;
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper helper = gridLayout.createRowHelper(2);
        
        helper.addChild(
            Button.builder(
                PauseScreen.RETURN_TO_GAME,
                (_) -> {
                    minecraft.gui.setScreen(null);
                    minecraft.mouseHandler.grabMouse();
                }
            ).width(204).build(),
            2,
            gridLayout.newCellSettings().paddingTop(50)
        );
        
        helper.addChild(
            Button.builder(
                PauseScreen.OPTIONS,
                (_) ->
                    minecraft.gui.setScreen(new OptionsScreen(this, minecraft.options))
            ).width(204).build(),
            2,
            gridLayout.newCellSettings().paddingTop(50)
        );
        
        
        helper.addChild(Button.builder(CommonComponents.GUI_DISCONNECT, (button) -> {
            button.active = false;
            minecraft
                .getReportingContext()
                .draftReportHandled(minecraft, this, () -> this.minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true);
        }).width(204).build(), 2);
        
        
        gridLayout.arrangeElements();
        FrameLayout.alignInRectangle(gridLayout, 0, 0, this.width, this.height, 0.5F, 0.25F);
        gridLayout.visitWidgets(this::addRenderableWidget);
        ci.cancel();
    }
}
