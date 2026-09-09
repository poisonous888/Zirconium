package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.ChatMessageEvent
import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.playSoundAtPlayer
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.setTitle
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.sounds.SoundEvents
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon

object Garden : Module(
    name = "Garden",
    description = "various garden stuff",
    category=ZirconiumEntry.ZCON
) {
    private val minTimeChange by NumberSetting("Min Time Change",30,0,120,1,"")
    private val timerRegex = Regex("(Cooldown: )([0-9]+m)? ?([0-9]+s)?")
    private val pestRegex = Regex("^YUCK! [0-9] \uE07F Pest have spawned in Plot - [0-9]+!$")
    private val pestHud by HUD("Pest HUD","") {
        editing -> when{
        LocationUtils.isCurrentArea(Island.Garden)||editing->textDim(curString,0,0)
            else -> 0 to 0
        }
    }
    private val test by ActionSetting("Test Alert",""){
        announce()
    }
    fun announce(){
        setTitle("SWAP")
        modMessage("Swap Equipment",zcon)
        for(a in 1..5) {
            schedule(a*4){
                playSoundAtPlayer(SoundEvents.ANVIL_LAND,2f,1.5f-0.2f*a)
            }
        }
    }
    var curString=""
    var curTime=0
    var timeDiff=0
    var spawned=false
    var announced=true
    fun processTabList(tabListEntries:List<String>){
        for(entry in tabListEntries) {
            timerRegex.find(entry)?.destructured?.let {(_,mins,secs) ->
                val newMins=mins.substringBefore("m").toIntOrNull()?:0
                val newSecs=secs.substringBefore("s").toIntOrNull()?:0
                val newTime=newMins*60+newSecs
                
                if(spawned){
                    if(newTime>0){
                        curTime=newTime
                        spawned=false
                    }
                }
                else{
                    if(newTime>curTime+minTimeChange){
                        timeDiff=newTime-curTime
                        curTime=newTime
                    }
                    else{
                        curTime=newTime
                    }
                }
                val adjustedTime=curTime-timeDiff
                
                if(adjustedTime>0){
                    val curSec=adjustedTime%60
                    val curMin=adjustedTime/60
                    curString="${curMin}m ${curSec}s Until Spawn"
                    if(announced)announced=false
                }
                else{
                    if(!announced) {
                        announced=true
                        announce()
                    }
                    if(curTime>0){
                        val curSec=curTime%60
                        val curMin=curTime/60
                        curString="READY (${curMin}m ${curSec}s)"
                    }
                    else{
                        curString="READY"
                    }
                }
                
            }
        }
    }
    private object GardenHelper {
        init {
            onReceive<ClientboundPlayerInfoUpdatePacket> {
                val tabListEntries=entries().mapNotNull{it.displayName?.string}.ifEmpty {return@onReceive}
                processTabList(tabListEntries)
            }
            on<ChatMessageEvent>{
                if(pestRegex.containsMatchIn(value)){
                    spawned=true
                    modMessage("spawned!")
                }
            }
//            onReceive<ClientboundAddEntityPacket> {
//                modMessage("x:$x y:$y z:$z - $type")
//            }
        }
    }
    init {
        on<LevelEvent.Load> {
            EventBus.unsubscribe(GardenHelper)
            schedule(100,true){
                if(LocationUtils.isCurrentArea(Island.Garden)) {
                    EventBus.subscribe(GardenHelper)
                    modMessage("loaded garden module",zcon)
                    return@schedule
                }
            }
        }
    }
}