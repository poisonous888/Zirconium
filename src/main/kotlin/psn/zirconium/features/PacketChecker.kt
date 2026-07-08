package psn.zirconium.features

import com.odtheking.odin.OdinMod
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.RenderEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.events.core.onSend
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.render.text
import net.minecraft.network.protocol.Packet
import psn.zirconium.ZirconiumEntry
import java.util.concurrent.CopyOnWriteArrayList

object PacketChecker : Module(
    name = "Packet Checker",
    description = "Lists Packets",
    category=ZirconiumEntry.ZCON
) {
    data class PacketStore(
        val name: String,
        var count: Int,
        var timestamp: Long,
        val incoming: Boolean,
    )
    val packetBuffer = CopyOnWriteArrayList<PacketStore>()
    //val packetBuffer = mutableMapOf<String, PacketStore>()
    init{
        onReceive<Packet<*>>{
            var str=type().toString()
            var cur=packetBuffer.firstOrNull{it.name==str&&it.incoming}?:PacketStore(str,0,0,true).also{packetBuffer.add(it)}
            cur.timestamp=System.currentTimeMillis()+serverLen
            cur.count++
            if(!logPackets)return@onReceive
            //if(onlyBetweenLevels&&!logging)return@onReceive
            OdinMod.logger.warn(toString())
        }
        onSend<Packet<*>>{
            var str=type().toString()
            var cur=packetBuffer.firstOrNull{it.name==str&&it.incoming.not()}?:PacketStore(str,0,0,false).also{packetBuffer.add(it)}
            cur.timestamp=System.currentTimeMillis()+clientLen
            cur.count++
            if(!logPackets)return@onSend
            //if(onlyBetweenLevels&&!logging)return@onSend
            OdinMod.logger.warn(toString())
        }
        on<RenderEvent.Extract>{
            val curTime=System.currentTimeMillis()
            packetBuffer.removeIf {
                (if(it.incoming)serverClear else clientClear)&&it.timestamp<curTime
            }
        }
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    val clientCol by ColorSetting("Clientbound Color",Colors.MINECRAFT_DARK_RED,true,"")
    val serverCol by ColorSetting("Serverbound Color",Colors.MINECRAFT_DARK_GREEN,true,"")
    
    val clientClear by BooleanSetting("Clientbound Clear packets",true,"")
    val clientLen by NumberSetting("Clientbound Keep duration",1000,100,5000,100,"")
    
    val serverClear by BooleanSetting("Serverbound Clear packets",true,"")
    val serverLen by NumberSetting("Serverbound Keep duration",1000,100,5000,100,"")
    
//    val doDelay by BooleanSetting("Delay Adding Packets",false,"")
//    val addDelay by NumberSetting("Add Delay",0,0,20,1,"")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    val clientHud by HUD("Clientbound Hud",""){_->
        var curLine=0
        packetBuffer.forEach {
            if(it.incoming.not())return@forEach
            text("${it.count} : ${it.name}", 0, curLine,clientCol)
            curLine+=8
        }
        text("Recent Clientbound", 0, curLine,clientCol)
        80 to curLine
    }
    val serverHud by HUD("Serverbound Hud",""){_->
        var curLine=0
        packetBuffer.forEach {
            if(it.incoming)return@forEach
            text("${it.count} : ${it.name}", 0, curLine,serverCol)
            curLine+=8
        }
        text("Recent Serverbound", 0, curLine,serverCol)
        80 to curLine
    }
    val combinedHud by HUD("Combined Hud","MAKE SURE YOU CAN SEE THE \"Max Packets Shown\" WHEN MOVING OR IT WILL CRASH YOUR GAME"){_->
        var curLine=0
        (if(packetBuffer.size>maxOnScreen)packetBuffer.takeLast(maxOnScreen) else packetBuffer).forEach{
            text("${it.count} : ${it.name}", 0, curLine,if(it.incoming)clientCol else serverCol)
            curLine+=8
        }
        text("Recent Packets", 0, curLine,Colors.MINECRAFT_DARK_PURPLE)
        80 to curLine
    }
    val maxOnScreen by NumberSetting("Max Packets Shown",80,1,100,1,"")
    val logPackets by BooleanSetting("Log Packets",false,"")
    val reset by ActionSetting("Reset",""){
        packetBuffer.clear()
    }
}