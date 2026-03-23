package psn.zirconium

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import psn.zirconium.commands.maxwell
import psn.zirconium.commands.stashItem
import psn.zirconium.commands.stashMaterial
import psn.zirconium.features.OneEightNine
import psn.zirconium.features.GuiHighlight
import psn.zirconium.features.PestCooldown
import psn.zirconium.features.MiscFeatures
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object ZirconiumEntry : ClientModInitializer {

    override fun onInitializeClient() {
        println("Odin Addon initialized!")

        // Register commands by adding to the array
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            arrayOf(maxwell, stashItem, stashMaterial).forEach { commodore -> commodore.register(dispatcher) }
        }

        // Register objects to event bus by adding to the list
        listOf(this).forEach { EventBus.subscribe(it) }

        // Register modules by adding to the list
        ModuleManager.registerModules(ModuleConfig("Zirconium.json"),
            PestCooldown,
            MiscFeatures,
            GuiHighlight,
            OneEightNine
        )
    }
}
