package psn.zirconium

import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.features.Category
import com.odtheking.odin.features.ModuleManager
import psn.zirconium.features.Visuals
import psn.zirconium.features.GuiHighlight
import psn.zirconium.features.Garden
import psn.zirconium.features.MiscFeatures
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import psn.zirconium.features.CustomCommands
import psn.zirconium.features.DVD
import psn.zirconium.features.DropUtils
import psn.zirconium.features.HideArmor
import psn.zirconium.features.ItemPos
import psn.zirconium.features.Lag
import psn.zirconium.features.MouseLock
import psn.zirconium.features.StaticWaypoints

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
//        for(module in modules.filter{module->module is AsyncSave}){
//            val config=ModuleConfig("${module.name}.json")
//            ModuleManager.registerModules(config,module)
//            (module as AsyncSave).getConfig(config)
//        }
    }
    @JvmStatic val ZCON = Category.custom("Zirconium")
}
interface HasCommands{fun buildCommands(dispatcher:CommandDispatcher<FabricClientCommandSource>)}
interface AsyncSave{fun getConfig():ModuleConfig}
const val zcon="§4Zcon §8»§r "
