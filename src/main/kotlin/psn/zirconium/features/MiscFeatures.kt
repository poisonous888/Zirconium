package psn.zirconium.features

import com.odtheking.odin.OdinMod
import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket
import psn.zirconium.ZirconiumEntry

object MiscFeatures : Module(
    name = "Misc Features",
    description = "Random small stuff that dosent need a dedicated module",
    category=ZirconiumEntry.ZCON
) {
    val updateNotif by BooleanSetting("Update Notification",true,"")
    
    val loadCat by DropdownSetting("Load")
    @JvmStatic val noLoadingScreen by BooleanSetting("No Loading Screen",false,"disables the loading screen ").withDependency { loadCat }
    private val noPackPush by BooleanSetting("No Server Packs",false,"disables servers downloading and forcing a resource pack").withDependency { loadCat }
    private val noRequiredPacks by BooleanSetting("No Required Packs",false,"you can remove any resource pack you want, just dont remove the minecraft pack ;}").withDependency { loadCat }
    var loaded=false
    @JvmStatic fun canRemovePacks():Boolean{return noRequiredPacks&&loaded}
    
    val renderCat by DropdownSetting("Render")
    @JvmStatic val noRecipeBook by BooleanSetting("No Recipe Book",false,"removes recipe book from inv").withDependency { renderCat }
    @JvmStatic val noPotionEffects by BooleanSetting("No Potion Effects",false,"removes the potion effect display from the inventory and the main hud").withDependency { renderCat }
    @JvmStatic val noLiquidFog by BooleanSetting("No Liquid Fog",false,"removes the fog from being underwater or under lava").withDependency { renderCat }
    
    val gameCat by DropdownSetting("Gameplay")
    @JvmStatic val closeSign by BooleanSetting("Close Sign On Enter", false,"Closes Sign GUI's When The Enter Key Is Pressed").withDependency { gameCat }
//    private val trimCommandOnFail by BooleanSetting("Trim commands on fail",false,"when a command fails, removes the last character and tries again until it succeeds or the command is empty")
//    private val maxTrim by NumberSetting("Max Trim",1,1,10,1,"").withDependency { trimCommandOnFail }
//    private val trimCmdDelay by NumberSetting("Trim Delay",5,0,20,1,"").withDependency { trimCommandOnFail }
//    private val comFailReg=Regex("^Unknown command\\. Type \"/help\" for help\\. \\('([a-zA-Z0-9 ]+)'\\)$")
    
    val visualCat by DropdownSetting("Visual")
    @JvmStatic val diagonalWalk by BooleanSetting("Diagonal Backwards Walk",false,"1.8.9 backwards walking").withDependency { visualCat }
    @JvmStatic val customCapePhysics by BooleanSetting("Cape Physics",false,"1.8.9 adjacent cape physics").withDependency { visualCat }
    @JvmStatic val noSky by BooleanSetting("No Sky Effects",false,"").withDependency { visualCat }
    @JvmStatic val customSkyColor by BooleanSetting("Custom Sky/Fog Color",false,"").withDependency { visualCat }
    @JvmStatic val skyColor by ColorSetting("Sky Color",Colors.MINECRAFT_AQUA,false,"").withDependency { visualCat&&customSkyColor }
    @JvmStatic val fullbright by BooleanSetting("Fullbright",false,"").withDependency { visualCat }
    @JvmStatic val lightColor by ColorSetting("Light Color",Colors.WHITE,false,"").withDependency { visualCat&&fullbright }
//    @JvmStatic val weather by SelectorSetting("Weather Type","Vanilla",listOf("Vanilla","Clear","Rain","Snow"),"").withDependency { visualCat }
//    @JvmStatic val setWeatherStats by BooleanSetting("Set Weather Stats",false,"").withDependency { visualCat }
//    @JvmStatic val weatherIntensity by NumberSetting("Weather Intensity",1f,0,1,0.01,"").withDependency { visualCat, setWeatherStats }
//    @JvmStatic val weatherRadius by NumberSetting("Weather Radius",1,0,10,1,"").withDependency { visualCat, setWeatherStats }
//    @JvmStatic val customBiomeColor by BooleanSetting("Custom Biome Color",false,"").withDependency { visualCat }
//    @JvmStatic val biomeColor by ColorSetting("Biome Color",Colors.WHITE,false,"").withDependency { visualCat&&customBiomeColor }
//    val updateBiomeColors by ActionSetting("Update Biome Colors",""){
//        mc.levelRenderer.allChanged()
//    }.withDependency { visualCat }
    
    init {
        onReceive<ClientboundResourcePackPushPacket>{
            if(!noPackPush)return@onReceive
            val connection=mc.player?.connection?.connection?:ZirconiumEntry.connection
            if(connection==null){
                OdinMod.logger.error("Zcon: Unable To Reciprocate Resource Pack Packet: Connection Is Null")
                return@onReceive
            }
            connection.send(
                    ServerboundResourcePackPacket(id,ServerboundResourcePackPacket.Action.ACCEPTED)
            )
            connection.send(
                ServerboundResourcePackPacket(id,ServerboundResourcePackPacket.Action.DOWNLOADED)
            )
            connection.send(
                ServerboundResourcePackPacket(id,ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED)
            )
            it.cancel()
        }
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