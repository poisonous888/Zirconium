package psn.zirconium.features

import com.odtheking.odin.events.LocationChangeEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.features.impl.dungeon.map.DungeonScan
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import psn.zirconium.ZirconiumEntry

object CryptAlert : Module(
    name = "Crypt Alert",
    description = "warns you when there are 75+ crypts in the current dungeon",
    category=ZirconiumEntry.ZCON
) {
    private val hud by HUD("Hud",""){
        example->if(!example||!DungeonUtils.inDungeons)return@HUD 0 to 0
        textDim("Crypts: ${DungeonUtils.cryptCount}/$total",0,0)
    }
    var total=0
    init {
        on<LocationChangeEvent>{
            total=0
            for(room in DungeonScan.rooms){
                total+=room.data?.crypts?:0
            }
        }
    }
}