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
            PauseScreen.RETURN_TO_GAME, button -> {
                minecraft.gui.setScreen(null);
                minecraft.mouseHandler.grabMouse();
            }
        ).width(204).build();
        
        var resourcepacks=Button.builder(
            OptionsScreen.RESOURCEPACK, (button) ->
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
            PauseScreen.OPTIONS, button -> minecraft.gui.setScreen(new OptionsScreen(this,minecraft.options))
        ).width(98).build();
        
        var sound=Button.builder(
            OptionsScreen.SOUNDS, button -> minecraft.gui.setScreen(new SoundOptionsScreen(this,minecraft.options))
        ).width(98).build();
        
        var mod_options=Button.builder(
            Component.literal("Odin Config..."), button -> minecraft.gui.setScreen(ClickGUI.INSTANCE)
        ).width(98).build();
        
        var disconnect=Button.builder(
            CommonComponents.GUI_DISCONNECT, button -> {
                minecraft
                    .getReportingContext()
                    .draftReportHandled(
                        minecraft,
                        this,
                        () -> minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE),
                        true
                    );
            }
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
                (button) -> {
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
                (button) ->
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
    
//    private void createPauseMenu() {
//        GridLayout gridLayout = new GridLayout();
//        gridLayout.defaultCellSetting().padding(4, 4, 4, 0);
//        GridLayout.RowHelper helper = gridLayout.createRowHelper(2);
//        helper.addChild(Button.builder(RETURN_TO_GAME, (button) -> {
//            this.minecraft.gui.setScreen(null);
//            this.minecraft.mouseHandler.grabMouse();
//        }).width(204).build(), 2, gridLayout.newCellSettings().paddingTop(50));
//        helper.addChild(this.openScreenButton(ADVANCEMENTS, () -> new AdvancementsScreen(this.minecraft.player.connection.getAdvancements(), this)));
//        helper.addChild(this.openScreenButton(STATS, () -> new StatsScreen(this, this.minecraft.player.getStats())));
//        LinearLayout iconButtonRow = LinearLayout.horizontal().spacing(4);
//        SpriteIconButton reportBugsButton = SpriteIconButton.builder(REPORT_BUGS, ConfirmLinkScreen.confirmLink(this, CommonLinks.SNAPSHOT_BUGS_FEEDBACK), true).width(20).sprite(Identifier.withDefaultNamespace("pause_menu/bug"), 15, 15).withTootip().build();
//        reportBugsButton.active = !SharedConstants.getCurrentVersion().dataVersion().isSideSeries();
//        iconButtonRow.addChild(reportBugsButton);
//        SpriteIconButton feedbackButton = SpriteIconButton.builder(SEND_FEEDBACK, ConfirmLinkScreen.confirmLink(this, SharedConstants.getCurrentVersion().stable() ? CommonLinks.RELEASE_FEEDBACK : CommonLinks.SNAPSHOT_FEEDBACK), true).width(20).sprite(Identifier.withDefaultNamespace("pause_menu/social_interactions"), 15, 15).withTootip().build();
//        iconButtonRow.addChild(feedbackButton);
//        this.friends = CommonButtons.friends(20, (var1) -> OnlineOptionsScreen.confirmFriendsListEnabled(this.minecraft, () -> this.minecraft.gui.setScreen(new FriendsOverlayScreen(this)), this), !this.minecraft.isDemo() && !this.minecraft.isOfflineDeveloperMode());
//        iconButtonRow.addChild(this.friends);
//        SpriteIconButton playerReportingButton = SpriteIconButton.builder(PLAYER_REPORTING, (var1) -> this.minecraft.gui.setScreen(new SocialInteractionsScreen(this)), true).width(20).sprite(Identifier.withDefaultNamespace("pause_menu/player_reporting"), 15, 15).withTootip().build();
//        iconButtonRow.addChild(playerReportingButton);
//        IntegratedServer integratedServer = this.minecraft.getSingleplayerServer();
//        if (integratedServer != null && this.minecraft.player != null) {
//            List<Map.Entry<UUID, PlayerInfo>> list = this.minecraft.player.connection.getSeenPlayers().entrySet().stream().filter((entry) -> entry.getKey() != this.minecraft.player.getUUID()).toList();
//            if (list.isEmpty()) {
//                playerReportingButton.active = false;
//                playerReportingButton.setTooltip(NO_PLAYERS_TO_REPORT_TOOLTIP);
//            }
//        }
//
//        helper.addChild(iconButtonRow, 2, gridLayout.newCellSettings().alignHorizontallyCenter());
//        Optional<? extends Holder<Dialog>> additions = this.getCustomAdditions();
//        additions.ifPresent((dialogHolder) -> this.addCustomDialogButtons(this.minecraft, dialogHolder, helper));
//        if (this.minecraft.level != null) {
//            helper.addChild(this.openScreenButton(pOPTIONS, () -> new OptionsScreen(this, this.minecraft.options)));
//            helper.addChild(this.openScreenButton(WORLD_OPTIONS, () -> new WorldOptionsScreen(this, this.minecraft.level)));
//        }
//
//        this.disconnectButton = (Button)helper.addChild(Button.builder(CommonComponents.disconnectButtonLabel(this.minecraft.isLocalServer()), (button) -> {
//            button.active = false;
//            this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, () -> this.minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true);
//        }).width(204).build(), 2);
//        gridLayout.arrangeElements();
//        FrameLayout.alignInRectangle(gridLayout, 0, 0, this.width, this.height, 0.5F, 0.25F);
//        gridLayout.visitWidgets(this::addRenderableWidget);
//    }
}
