package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.MessageEvent
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
import psn.zirconium.utils.LocationCheck.dynamicLoad
import psn.zirconium.zcon

object Rift : Module(
    name = "Rift",
    description = "puffin' and spinnin'",
    category=ZirconiumEntry.zconCat
) {
    private val announce by BooleanSetting("Kill Timeout",true,"")
    private val puffHud by HUD("HUD","") {
        example->
        if(example)textDim("Spawns: 12 - Gravity Time: 120",0,0)
        else if(LocationUtils.isCurrentArea(Island.Rift)){ textDim(curString,0,0) }
        else 0 to 0
    }
    var curString=""
    var headCount=0
    var killCount=0
    fun announce(){
        alert("POTION")
        modMessage("Get Potion",zcon)
    }
    init {
        dynamicLoad(Island.Rift)
        on<MessageEvent>{
            modMessage(message)
            if(message.contains("vending machine")){
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
                "Kills: $killCount - Gravity Time: ???"
            }
            else{
                "Spawns: $headCount - Gravity Time: ???"
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
}