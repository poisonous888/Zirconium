package psn.zirconium

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import psn.zirconium.commands.maxwellCmd
import psn.zirconium.commands.stashItemCmd
import psn.zirconium.commands.stashMaterialCmd
import psn.zirconium.features.OneEightNine
import psn.zirconium.features.GuiHighlight
import psn.zirconium.features.PestCooldown
import psn.zirconium.features.MiscFeatures
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import psn.zirconium.commands.staticWaypointCmd
import psn.zirconium.features.DropUtils
import psn.zirconium.features.StaticWaypoints

object ZirconiumEntry : ClientModInitializer {

    override fun onInitializeClient() {
        println("Zirconium has entered the chat")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val cmd=mutableListOf(maxwellCmd, stashItemCmd, stashMaterialCmd, staticWaypointCmd)
//            if(AutoComplete.enabled){
//                if(AutoComplete.autocompleteWarps){
//                    cmd.add(warpsComplete)
//                }
//            }
            cmd.forEach { commodore -> commodore.register(dispatcher) }
        }

        // Register objects to the event bus by adding to the list
        listOf(this).forEach { EventBus.subscribe(it) }

        // Register modules by adding to the list
        ModuleManager.registerModules(ModuleConfig("Zirconium.json"),
            PestCooldown,
            MiscFeatures,
            GuiHighlight,
            OneEightNine,
            StaticWaypoints,
            //AutoComplete,
            DropUtils,
        )
    }
}
