package com.odtheking.zcon

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import com.odtheking.zcon.commands.maxwell
import com.odtheking.zcon.commands.stashItem
import com.odtheking.zcon.commands.stashMaterial
import com.odtheking.zcon.features.impl.skyblock.OneEightNine
import com.odtheking.zcon.features.impl.skyblock.GuiHighlight
import com.odtheking.zcon.features.impl.skyblock.PestCooldown
import com.odtheking.zcon.features.impl.skyblock.MiscFeatures
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
