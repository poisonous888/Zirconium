package psn.zirconium

import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
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
import psn.zirconium.features.ItemPos
import psn.zirconium.features.Lag
import psn.zirconium.features.MouseLock
import psn.zirconium.features.StaticWaypoints

object ZirconiumEntry : ClientModInitializer {
    override fun onInitializeClient() {
        val modules=arrayOf(
            CustomCommands,
            Garden,
            MiscFeatures,
            GuiHighlight,
            Visuals,
            StaticWaypoints,
            //AutoComplete,
            DropUtils,
            ItemPos,
            //TeleportLine,
            MouseLock,
            DVD,
            Lag,
        )
        println("Zirconium has entered the chat")
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            modules.forEach{ module ->
                if(module is HasCommands) module.buildCommands(dispatcher)
            }
        }
        listOf(this).forEach{EventBus.subscribe(it)}
        ModuleManager.registerModules(zcConfig,*modules)
    }
}
interface HasCommands{fun buildCommands(dispatcher:CommandDispatcher<FabricClientCommandSource>)}
val zcConfig = ModuleConfig("Zirconium.json")
const val zcon="§4Zcon §8»§r "
