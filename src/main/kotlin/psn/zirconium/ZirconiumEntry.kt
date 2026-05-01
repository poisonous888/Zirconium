package psn.zirconium

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import psn.zirconium.commands.maxwellCmd
import psn.zirconium.commands.stashItemCmd
import psn.zirconium.commands.stashMaterialCmd
import psn.zirconium.features.Visuals
import psn.zirconium.features.GuiHighlight
import psn.zirconium.features.Garden
import psn.zirconium.features.MiscFeatures
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import psn.zirconium.commands.staticWaypointCmd
import psn.zirconium.features.DropUtils
import psn.zirconium.features.ItemPos
import psn.zirconium.features.MouseLock
import psn.zirconium.features.StaticWaypoints

object ZirconiumEntry : ClientModInitializer {
    override fun onInitializeClient() {
        println("Zirconium has entered the chat")
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val cmd=mutableListOf(maxwellCmd, stashItemCmd, stashMaterialCmd, staticWaypointCmd)
            cmd.forEach { commodore -> commodore.register(dispatcher) }
        }
        listOf(this).forEach { EventBus.subscribe(it) }
        ModuleManager.registerModules(ModuleConfig("Zirconium.json"),
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
        )
    }
}
