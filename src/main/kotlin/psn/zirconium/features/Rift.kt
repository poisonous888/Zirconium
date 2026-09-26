package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.MessageEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.sendCommand
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon


object Rift : Module(
    name = "Rift",
    description = "puffin' and spinnin'",
    category=ZirconiumEntry.zconCat
) {
    private val announce by BooleanSetting("Kill Timeout",true,"")
    private val puffHud by HUD("HUD","") {
        editing -> when{
            LocationUtils.isCurrentArea(Island.Garden)||editing->textDim(curString,0,0)
            else -> 0 to 0
        }
    }
    var curString=""
    var headCount=0
    var killCount=0
    fun announce(){
        alert("POTION")
        modMessage("Get Potion",zcon)
    }
    private object RiftHelper {
        init {
            on<MessageEvent>{
                if(message.contains("BUFF! A vending machine splashed you with Gravity I!")){
                    modMessage("Captured Gravity Buff",zcon)
                    schedule(12000,true){announce()}
                }
            }
            onReceive<ClientboundSoundPacket>{
                if(type()==SoundEvents.HORSE_BREATHE){
                    headCount++
                }
                if(type()==SoundEvents.CHICKEN_EGG){
                    startTimeout(-1)
                }
                curString=if(killCount>0){
                    "Kills: $killCount\nGravity Time: ???"
                }
                else{
                    "Spawns: $headCount\nGravity Time: ???"
                }
                
            }
        }
    }
    fun startTimeout(prevCount:Int){
        if(prevCount==-1)killCount++
        if(killCount!=1) {
            modMessage("Started combo with $headCount spawns",zcon)
            if(announce)sendCommand("pc Started combo with $headCount spawns")
            return
        }
        if(prevCount==killCount){
            modMessage("Puff Combo: $killCount ($headCount spawned)",zcon)
            if(announce)sendCommand("pc Puff Combo: $killCount ($headCount spawned)")
            killCount=0
            headCount=0
            return
        }
        schedule(20,true){startTimeout(killCount)}
    }
    init {
        on<LevelEvent.Load> {
            EventBus.unsubscribe(RiftHelper)
            schedule(100,true){
                if(LocationUtils.isCurrentArea(Island.Rift)) {
                    EventBus.subscribe(RiftHelper)
                    modMessage("loaded rift module",zcon)
                    return@schedule
                }
            }
        }
    }
}