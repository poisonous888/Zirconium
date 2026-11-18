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

object ZirconiumEntry: ClientModInitializer {
    override fun onInitializeClient() {
        val modules=listOf(
            CustomCommands,
            Garden,
            MiscFeatures,
            GuiHighlight,
            //StaticWaypoints,
            HeldItemRender,
            //MouseLock,
            Lag,
            HideArmor,
            DropUtils,
            ChatRules,
            ChatUtils,
            PacketChecker,
            Zoom,
            FastFuse,
            CryptAlert,
            //Rift,
            //Timers,
            BersCone,
        )
        println("Zirconium has entered the chat")
        ModuleManager.registerModules(ModuleConfig("Zirconium.json"),*modules.filter{it !is AsyncSave}.toTypedArray())
        modules.filter{it is AsyncSave}.forEach{ModuleManager.registerModules((it as AsyncSave).config,it)}
        EventBus.subscribe(UpdateCheck())
        EventBus.subscribe(LocationCheck)
    }
    val zconCat=Category.custom("Zirconium",860,10)
    @JvmStatic var connection: Connection?=null
}
fun modMessageJava(msg:String){
    modMessage(msg,zcon)
}
interface AsyncSave {
    val config:ModuleConfig
}
const val zcon="§4Zcon §8»§r "
