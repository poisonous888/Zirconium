package psn.zirconium.features

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
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
        var type:TimerType,
        var time:Int,
        var regex:String,
    )
    enum class TimerType{
        Session, //resets on game load
        Cooldown, //resets after time
        Irl, //resets at midnight
    }
    val loadedTimers = mutableListOf<Timer>()
    abstract class Timer(val procTime:Int){
        abstract fun check():Boolean
        class Session(procTime:Int):Timer(procTime){
            var procced=false
            override fun check():Boolean {
                if(procced) return false
                procced=true
                return true
            }
        }
        class Cooldown(procTime:Int):Timer(procTime){
            var lastChecked=0
            override fun check():Boolean {
                val hours=calendar.get(Calendar.HOUR_OF_DAY)
                val mins=calendar.get(Calendar.MINUTE)
                val curTime=hours*60+mins
                
                val normal=curTime>lastChecked
                val check=curTime>procTime
                val procced=lastChecked>curTime
                lastChecked=curTime
                
                if(procced) return false
                if(check&&normal) return true
                return false
            }
        }
        class Irl(procTime:Int):Timer(procTime) {
            var lastChecked=0
            override fun check():Boolean {
                val hours=calendar.get(Calendar.HOUR_OF_DAY)
                val mins=calendar.get(Calendar.MINUTE)
                val curTime=hours*60+mins
                if(curTime>lastChecked) {
                    lastChecked=curTime
                    return curTime>procTime
                }
                else {
                    return true
                }
            }
        }
        companion object{
            val calendar:Calendar=Calendar.getInstance()
        }
    }
    
    fun checkTimers(){
    
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    init{
        onCommand{daily}
        on<LevelEvent.Load>{
            checkTimers()
        }
    }
    
    private val config=ModuleConfig("Dailies.json")
    override fun getConfig(): ModuleConfig { return config }
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
//        literal("add").executable{
//            param("alias").suggests {
//                savedTimers.map {i -> i.key }
//            }
//            param("command")
//            runs{alias: String, command: GreedyString ->
//                addAlias(alias,command.string)
//            }
//        }
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