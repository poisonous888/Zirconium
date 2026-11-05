package psn.zirconium.utils

import com.odtheking.odin.events.LocationChangeEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import psn.zirconium.features.MiscFeatures
import psn.zirconium.zcon

object LocationCheck {
    fun Any.dynamicLoad(vararg island:Island) {
        locationList.add(DynamicModule(this as Module,island.toList()))
    }
    private class DynamicModule(
        val module:Module,
        val location:List<Island>
    )
    private val locationList=mutableListOf<DynamicModule>()
    
    init {
        on<LocationChangeEvent> {
            if(!MiscFeatures.dynamicLoading){
                locationList.forEach{EventBus.subscribe(it)}
                return@on
            }
            locationList.forEach{
                if(it.module.enabled&&it.location.any{loc->loc==LocationUtils.currentArea}) {
                    EventBus.subscribe(it.module)
                    modMessage("Loaded ${it.module.name} module",zcon)
                }
                else{
                    EventBus.unsubscribe(it)
                }
            }
        }
    }
}
