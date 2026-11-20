package psn.zirconium.utils

import com.odtheking.odin.events.LocationChangeEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Category
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import psn.zirconium.features.MiscFeatures
import psn.zirconium.zcon

object LocationCheck {
    private val locationList=mutableListOf<DynamicModule>()
    fun registerDynamic(module:DynamicModule){
        locationList.add(module)
    }
    init {
        on<LocationChangeEvent> {
            if(!MiscFeatures.dynamicLoading){
                locationList.forEach{
                    EventBus.subscribe(it)
                    it.active=true
                }
                return@on
            }
            locationList.forEach{
                if(it.enabled&&it.islands.any{loc->loc==LocationUtils.currentArea}) {
                    EventBus.subscribe(it)
                    it.active=true
                    modMessage("Loaded ${it.name} module",zcon)
                }
                else{
                    EventBus.unsubscribe(it)
                    it.active=false
                }
            }
        }
    }
}
abstract class DynamicModule(
    name:String,
    description:String,
    category:Category,
    val islands:List<Island>
):Module(
    name = name,
    description = description,
    category = category
){
    var active=false
    init {
        LocationCheck.registerDynamic(this)
    }
}
