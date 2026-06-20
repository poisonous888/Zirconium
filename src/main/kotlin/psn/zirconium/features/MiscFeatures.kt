package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket
import psn.zirconium.ZirconiumEntry

object MiscFeatures : Module(
    name = "Misc Features",
    description = "Random small stuff that dosent need a dedicated module",
    category=ZirconiumEntry.ZCON
) {
    @JvmStatic val noLoadingScreen by BooleanSetting("No Loading Screen",false,"")
    //@JvmStatic val noResourceLoad by BooleanSetting("No Reloading Screen",false,"cancels the resource reloading screen. props to rrls")
    @JvmStatic val closeSign by BooleanSetting("Close Sign On Enter", false,"Closes Sign GUI's When The Enter Key Is Pressed")
    @JvmStatic val noRecipeBook by BooleanSetting("No Recipe Book",false,"removes recipe book from inv")
    @JvmStatic val noPotionEffects by BooleanSetting("No Potion Effects",false,"removes the potion effect display from the inentory and the main hud")
    private val noPackUpdate by BooleanSetting("No Server Packs",false,"disables servers updating your resource pack")
    @JvmStatic val noRequiredPacks by BooleanSetting("No Required Packs",false,"you can move or remove any pack you want, just dont remove the minecraft pack ;}")
//    private val trimCommandOnFail by BooleanSetting("Trim commands on fail",false,"when a command fails, removes the last character and tries again until it succeeds or the command is empty")
//    private val maxTrim by NumberSetting("Max Trim",1,1,10,1,"").withDependency { trimCommandOnFail }
//    private val trimCmdDelay by NumberSetting("Trim Delay",5,0,20,1,"").withDependency { trimCommandOnFail }
//    private val comFailReg=Regex("^Unknown command\\. Type \"/help\" for help\\. \\('([a-zA-Z0-9 ]+)'\\)$")
    init {
        onReceive<ClientboundResourcePackPushPacket>{
            if(!noPackUpdate)return@onReceive
            it.cancel()
            mc.execute{
                mc.player?.connection?.send(
                    ServerboundResourcePackPacket(
                        id,
                        ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED
                    )
                )
            }
        }
//        onSend<ServerboundChatCommandPacket>{
//            if(!trimCommandOnFail)return@onSend
//            CmdFailCheck.timeout=0
//            EventBus.subscribe(CmdFailCheck)
//        }
    }
//    private object CmdFailCheck{
//        var timeout=0
//        init {
//            on<ChatPacketEvent>{
//                val cmd=comFailReg.find(value)?.groups[1]?.value?.dropLast(1)?:return@on
//                schedule(trimCmdDelay,true){sendCommand(cmd)}
//                timeout=trimCmdDelay
//            }
//            on<TickEvent.Server>{
//                if(timeout==-1)EventBus.unsubscribe(this)
//                timeout--
//            }
//        }
//    }
}