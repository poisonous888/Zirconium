package psn.zirconium.features

import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.textDim
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon

object CryptAlert : Module(
    name = "Crypt Solver",
    description = "hehe boi",
    category=ZirconiumEntry.ZCON
) {
    private val hud by HUD("Hud",""){
        textDim("Crypts: $total",0,0)
    }
    var total=0
    init {
        on<LevelEvent.Load>{
            total=0
        }
    }
    fun add(i:Int){
        total+=i
        if(total>=75&&enabled){
            alert("75 CRYPTS!!!")
            modMessage("75 Crypts In This Dungeon",zcon)
        }
    }
}