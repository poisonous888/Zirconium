package psn.zirconium.utils

import com.github.stivais.commodore.Commodore
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import psn.zirconium.commands.locationCommand
import psn.zirconium.commands.modelCommand
import psn.zirconium.commands.rctaCommand

class CommandCheck {
    init {
        onCommand { rctaCommand }
        onCommand { modelCommand }
        onCommand { locationCommand }
    }
}
fun Any.onCommand(command: () -> Commodore){
    ClientCommandRegistrationCallback.EVENT.register{dispatcher,_->command.invoke().register(dispatcher)}
}
fun Any.buildCommands(command: DispatchContainer.() -> Unit){
    ClientCommandRegistrationCallback.EVENT.register{dispatcher,_->command.invoke(DispatchContainer(dispatcher))}
}
class DispatchContainer(
    val dispatcher:CommandDispatcher<FabricClientCommandSource>
)