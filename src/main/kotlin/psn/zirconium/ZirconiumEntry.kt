package psn.zirconium

import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.Category
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.utils.modMessage
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.Connection
import psn.zirconium.commands.modelCommand
import psn.zirconium.commands.rctaCommand
import psn.zirconium.features.*
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
        )
        val commands=listOf(
            rctaCommand,
            modelCommand,
        )
        println("Zirconium has entered the chat")
        ClientCommandRegistrationCallback.EVENT.register{dispatcher, _ ->
            modules.forEach{ module ->
                if(module is HasCommands)module.buildCommands(dispatcher)
            }
            commands.forEach{ command ->
                command.register(dispatcher)
            }
        }
        ModuleManager.registerModules(ModuleConfig("Zirconium.json"),*modules.filter{module->module !is AsyncSave}.toTypedArray())
        for(module in modules.filter{module->module is AsyncSave}){
            ModuleManager.registerModules((module as AsyncSave).getConfig(),module)
        }
        EventBus.subscribe(UpdateCheck())
    }
    @JvmStatic val ZCON = Category.custom("Zirconium")
    @JvmStatic var connection: Connection?=null
}
fun modMessageJava(msg:String){
    modMessage(msg,zcon)
}
interface HasCommands{fun buildCommands(dispatcher:CommandDispatcher<FabricClientCommandSource>)}
interface AsyncSave{fun getConfig():ModuleConfig}
const val zcon="§4Zcon §8»§r "
