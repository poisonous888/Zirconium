package psn.zirconium.features

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.LocationChangeEvent
import com.odtheking.odin.events.MessageEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import psn.zirconium.AsyncSave
import psn.zirconium.ZirconiumEntry
import psn.zirconium.utils.onCommand
import psn.zirconium.zcon
import java.util.*

object Timers: AsyncSave, Module(
    name = "Timers",
    description = "Reminds you to do @#$%",
    category=ZirconiumEntry.zconCat
) {
    private val savedTimers by ListSetting("Saved Timers",mutableListOf<SavedTimer>())
    data class SavedTimer(
        var name:String,
        var type:TimerType,
        var time:Int,
        var regex:String,
    )
    enum class TimerType{
        Session, //resets on game load
        Cooldown, //resets after time
        Irl, //resets at midnight
    }
    fun verifyName(name:String):Boolean{
        return savedTimers.map{it.name}.contains(name)
    }
    fun addTimer(name:String,type:TimerType,time:Int,regex:String){
        if(verifyName(name)){
            modMessage("Name Already Exists",zcon)
            return
        }
        savedTimers.add(SavedTimer(name, type, time, regex))
        modMessage("Added Timer ",zcon)
    }
    val loadedTimers = mutableListOf<Timer>()
    abstract class Timer(val procTime:Int,val regex:Regex){
        abstract fun check():Boolean
        abstract fun proc()
        fun curTime():Int{
            val hours=calendar.get(Calendar.HOUR_OF_DAY)
            val mins=calendar.get(Calendar.MINUTE)
            return hours*60+mins
        }
        class Session(procTime:Int,regex:Regex):Timer(procTime,regex){
            var procced=false
            override fun check():Boolean {
                if(procced) return false
                procced=true
                return true
            }
            override fun proc(){}
        }
        class Cooldown(procTime:Int,regex:Regex):Timer(procTime,regex){
            var nextProc=0
            var lastChecked=0
            override fun check():Boolean {
                val curTime=curTime()
                val procced=if(curTime>=lastChecked) lastChecked<=nextProc else curTime>nextProc
                if(!procced) lastChecked=curTime
                return procced
            }
            
            override fun proc(){
                nextProc=(curTime()+procTime).mod(3600)
            }
        }
        class Irl(procTime:Int,regex:Regex):Timer(procTime,regex) {
            var lastChecked=0
            override fun check():Boolean {
                val curTime=curTime()
                val procced=if(curTime>=lastChecked) lastChecked<=procTime else curTime>procTime
                if(!procced) lastChecked=curTime
                return procced
            }
            
            override fun proc() {
                lastChecked=curTime()
            }
        }
        companion object{
            val calendar:Calendar=Calendar.getInstance()
        }
    }
    fun saveLoad(){
        config.save()
        load()
    }
    fun load(){
        for((_,type,time,regex) in savedTimers){
            val reg=Regex(regex)
            val toAdd=when(type){
                TimerType.Session->Timer.Session(time,reg)
                TimerType.Cooldown->Timer.Cooldown(time,reg)
                TimerType.Irl->Timer.Irl(time,reg)
            }
            loadedTimers.add(toAdd)
        }
    }
    fun checkTimers(){
        for(timer in loadedTimers) timer.check()
    }
    fun checkProced(str:String){
        loadedTimers.forEach {
            if(it.regex.containsMatchIn(str)){
                it.proc()
            }
        }
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    init{
        onCommand{daily}
        on<LevelEvent.Load>{
            checkTimers()
        }
        on<LocationChangeEvent>{
            if(LocationUtils.isCurrentArea(Island.Dungeon,Island.Kuudra,Island.Mineshaft)){
                EventBus.unsubscribe(ConstCheck)
            }
            else{
                EventBus.subscribe(ConstCheck)
            }
        }
    }
    object ConstCheck{
        init {
            onReceive<ClientboundPlayerInfoUpdatePacket>{
                checkTimers()
            }
            on<MessageEvent>{
                checkProced(message)
            }
        }
    }
    
    override val config=ModuleConfig("Dailies.json")
    fun printCommands(){
        modMessage("Timers",zcon)
        modMessage(" | /daily add 24h <name> <regex...> : creates a new daily that resets at midnight","")
        modMessage(" | /daily add time <time> <name> <regex...> : creates a new daily that resets after the specified time has passed","")
        modMessage(" | /daily add irl <time> <name> <regex...> : creates a new daily that resets at that time each day based on your local clock","")

        modMessage(" | /daily list : lists all current dailies","")
        modMessage(" | /daily remove <name> : remove specified daily","")
        modMessage(" | /daily clear : remove all dailies","")
        
        modMessage(" | /reminder add <time> <message> : adds a one-time reminder","")
        modMessage(" | /reminder list : lists current reminders","")
        modMessage(" | /reminder clear : clears all reminders","")
    }
    val daily=Commodore("daily"){
        runs{
            printCommands()
        }
        literal("add").executable{
            param("alias").suggests {
                savedTimers.map {i -> i.name }
            }
            param("command")
            runs{alias: String, command: GreedyString ->
            
            }
        }
//        literal("remove").executable{
//            param("alias").suggests {
//                savedTimers.map {i -> i.key }
//            }
//            runs{alias: String ->
//                removeTimer(alias)
//            }
//        }
//        literal("clear").executable{runs{clearAliases()}}
//        literal("list").executable{runs{printAliases()}}
//        literal("rename").executable{
//            param("alias").suggests {
//                savedTimers.map {i -> i.key }
//            }
//            param("newName")
//            runs{alias: String, newName: String ->
//                renameAlias(alias,newName)
//            }
//        }
//        literal("command").executable{
//            param("alias").suggests {
//                savedTimers.map {i -> i.key }
//            }
//            param("newCommand")
//            runs{alias: String, newCommand: GreedyString ->
//                rebindAlias(alias,newCommand.string)
//            }
//        }
    }
    fun unabled() {
        if(!enabled) modMessage("§cTimers Module is disabled, timers and reminders will not function but can be modified","")
    }
}