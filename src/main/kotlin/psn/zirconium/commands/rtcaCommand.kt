package psn.zirconium.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.google.gson.JsonObject
import com.odtheking.odin.OdinMod
import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.network.WebUtils.fetchJson
import com.odtheking.odin.utils.network.hypixelapi.RequestUtils
import kotlinx.coroutines.launch

val rctaCommand=Commodore("rtca","classaverage"){
    runs{
        try {
            getClassAverage(mc.user.name)
        }
        catch(e: Exception){
            modMessage("Caught exception: ${e.message}")
        }
    }
    runs{ name: GreedyString ->
        try {
            getClassAverage(name.string)
        }
        catch(e: Exception){
            modMessage("Caught exception: ${e.message}")
        }
    }
    
}
fun getClassAverage(name:String){
    OdinMod.scope.launch {
        val uuid=RequestUtils.getUuid(name).getOrNull()?.id
        modMessage(uuid)
        val data=fetchJson<JsonObject>("https://api.odtheking.com/hypixel/get/$uuid").getOrNull()
        val profiles=data?.asJsonObject?.get("profiles")
        val curIsland=let {
            profiles?.asJsonArray?.forEach {
                if(it.asJsonObject.get("selected").asBoolean) return@let it.asJsonObject
            }
            return@let null
        }
        modMessage(curIsland?.get("cute_name"))
        val members=curIsland?.get("members")?.asJsonObject
        val profile=members?.get(uuid)?.asJsonObject
        
        val attributes=profile?.get("attributes")?.asJsonObject?.get("stacks")?.asJsonObject
        val scarfShardRaw=attributes?.get("catacombs_graduate")?.asInt
        val scarfShard=getAttLvl(scarfShardRaw!!, 5)*2
        
        //calculateDungeonLevel()
        //https://github.com/adjectiven0un/adjectils/blob/main/dungeon.html#L641
        //https://api.odtheking.com/hypixel/get/c84c5cfba2bc409f8759ca6b7084138e
        modMessage("this command is not working just yet, hold tight!")
    }
    
    
}
val attributeLookup=listOf(
    listOf(1,4,9,15,22,30,40,54,72,96),
    listOf(1,3,6,10,15,21,28,36,48,64),
    listOf(1,3,6,9,13,17,22,28,36,48),
    listOf(1,2,4,6,9,12,16,20,25,32),
    listOf(1,2,3,5,7,9,12,15,19,24),
)
fun getAttLvl(stacks:Int,type:Int):Int{
    var i=0
    val curLine=attributeLookup[type]
    while(i<10){
        if(curLine[i]>stacks) return i+1
        i++
    }
    return 0
}