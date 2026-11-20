package psn.zirconium.features

import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.Island
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import psn.zirconium.ZirconiumEntry
import psn.zirconium.utils.DynamicModule
import psn.zirconium.zcon

object Scatha : DynamicModule(
    name = "Scatha",
    description = "literally just a scatha timer",
    category = ZirconiumEntry.zconCat,
    islands = listOf(Island.CrystalHollows)
) {
    val hud by HUD("Hud",""){example->
        if(example) return@HUD textDim("§aCooldown 600",0,0)
        if(!active)return@HUD 0 to 0
        textDim(if(time==-1)"§cREADY" else "§aCooldown $time",0,0)
    }
    var time=-1
    
    init {
        onReceive<ClientboundSoundPacket> {
            if (sound.value() != SoundEvents.SPIDER_STEP) return@onReceive
            alert("§aSpawned!")
            time=600//20*30
        }
        on<TickEvent.Server>{
            if(time>=0){
                time--
                if(time==0){
                    alert("§cReady!")
                    modMessage("§cReady to spawn another",zcon)
                }
            }
        }
    }
}

