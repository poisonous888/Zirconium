package psn.zirconium

import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.features.Category
import com.odtheking.odin.features.ModuleManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import psn.zirconium.features.*

object ZirconiumEntry : ClientModInitializer {
    override fun onInitializeClient() {
        val modules=listOf(
            CustomCommands,
            Garden,
            MiscFeatures,
            GuiHighlight,
            Visuals,
            StaticWaypoints,
            //AutoComplete,
            ItemPos,
            //TeleportLine,
            MouseLock,
            DVD,
            Lag,
            HideArmor,
            DropUtils,
            ChatUtils,
            CPSDisplay,
        )
        println("Zirconium has entered the chat")
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            modules.forEach{ module ->
                if(module is HasCommands) module.buildCommands(dispatcher)
            }
        }
        ModuleManager.registerModules(ModuleConfig("Zirconium.json"),*modules.filter{module->module !is AsyncSave}.toTypedArray())
        for(module in modules.filter{module->module is AsyncSave}){
            println(module.name)
            ModuleManager.registerModules((module as AsyncSave).getConfig(),module)
        }
    }
    @JvmStatic val ZCON = Category.custom("Zirconium")
}
interface HasCommands{fun buildCommands(dispatcher:CommandDispatcher<FabricClientCommandSource>)}
interface AsyncSave{fun getConfig():ModuleConfig}
const val zcon="§4Zcon §8»§r "
