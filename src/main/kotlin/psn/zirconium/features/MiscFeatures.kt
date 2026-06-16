package psn.zirconium.features

import com.odtheking.odin.OdinMod
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.ChatPacketEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.events.core.onSend
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.sendCommand
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket
import psn.zirconium.ZirconiumEntry

object MiscFeatures : Module(
    name = "Misc Features",
    description = "Random small stuff that dosent need a dedicated module",
    category=ZirconiumEntry.ZCON
) {
    @JvmStatic val noLoadingScreen by BooleanSetting("No Loading Screen",false,"")
    @JvmStatic val closeSign by BooleanSetting("Close Sign On Enter", false,"Closes Sign GUI's When The Enter Key Is Pressed")
    @JvmStatic val noRecipeBook by BooleanSetting("No Recipe Book",false,"removes recipe book from inv")
    @JvmStatic val noPotionEffects by BooleanSetting("No Potion Effects",false,"removes the potion effect display from the inentory and the main hud")
    //@JvmStatic val noWorldLoad by BooleanSetting("No World Loading Screen",false,"cancels the world loading screen. props to nofrills")
    //@JvmStatic val noResourceLoad by BooleanSetting("No Reloading Screen",false,"cancels the resource reloading screen. props to rrls")
    private val noPackUpdate by BooleanSetting("No Server Packs",false,"disables servers updating your resource pack")
    @JvmStatic val noRequiredPacks by BooleanSetting("No Required Packs",false,"you can move or remove any pack you want, just dont remove the minecraft pack ;}")
    private val trimCommandOnFail by BooleanSetting("Trim commands on fail",false,"when a command fails, removes the last character and tries again until it succeeds or the command is empty")
    private val trimCmdDelay by NumberSetting("Trim Delay",5,0,20,1,"")
    private val comFailReg=Regex("^Unknown command\\. Type \"/help\" for help\\. \\('([a-zA-Z0-9 ]+)'\\)$")
    init {
        onReceive<ClientboundResourcePackPushPacket>{
            if(!noPackUpdate)return@onReceive
            it.cancel()
            OdinMod.mc.execute{
                OdinMod.mc.player?.connection?.send(ServerboundResourcePackPacket(
                    id,ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED
                ))
            }
        }
        onSend<ServerboundChatCommandPacket>{
            if(!trimCommandOnFail)return@onSend
            EventBus.subscribe(CmdFailCheck)
            schedule(2,true){EventBus.unsubscribe(CmdFailCheck)}
        }
    }
    private object CmdFailCheck{
        init {
            on<ChatPacketEvent>{
                val cmd=comFailReg.find(value)?.groups[1]?.value?.dropLast(1)?:return@on
                schedule(trimCmdDelay,true){sendCommand(cmd)}
            }
        }
    }
}