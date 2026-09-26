package psn.zirconium

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.Category
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.utils.modMessage
import net.fabricmc.api.ClientModInitializer
import net.minecraft.network.Connection
import psn.zirconium.features.*
import psn.zirconium.utils.LocationCheck
import psn.zirconium.utils.UpdateCheck

object ZirconiumEntry : ClientModInitializer {
    override fun onInitializeClient() {
        val modules=listOf(
            CustomCommands,
            Garden,
            MiscFeatures,
            GuiHighlight,
            //Visuals,
            StaticWaypoints,
            //AutoComplete,
            HeldItemRender,
            //TeleportLine,
            MouseLock,
            //DVD,
            Lag,
            HideArmor,
            DropUtils,
            ChatRules,
            ChatUtils,
            CPSDisplay,
            //Dailies,
            PacketChecker,
            //ExplosiveMute,
            Zoom,
            FastFuse,
            CryptAlert,
            Rift,
        )
        println("Zirconium has entered the chat")
        ModuleManager.registerModules(ModuleConfig("Zirconium.json"),*modules.filter{module->module !is AsyncSave}.toTypedArray())
        for(module in modules.filter{module->module is AsyncSave}){
            ModuleManager.registerModules((module as AsyncSave).getConfig(),module)
        }
        EventBus.subscribe(UpdateCheck())
        EventBus.subscribe(LocationCheck)
    }
    val zconCat = Category.custom("Zirconium",860,10)
    @JvmStatic var connection: Connection?=null
}
fun modMessageJava(msg:String){
    modMessage(msg,zcon)
}
interface AsyncSave{fun getConfig():ModuleConfig}
const val zcon="§4Zcon §8»§r "
