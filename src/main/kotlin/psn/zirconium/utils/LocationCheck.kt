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
    fun Any.dynamicLoad(island:Island) {
        locationList.add(DynamicModule(this as Module,island))
    }
    private class DynamicModule(
        val module:Module,
        val location:Island
    )
    private val locationList=mutableListOf<DynamicModule>()
    private val loaded=mutableListOf<DynamicModule>()
    
    init {
        on<LocationChangeEvent> {
            for(mod in loaded){
                EventBus.unsubscribe(mod)
            }
            loaded.clear()
            if(!MiscFeatures.dynamicLoading){
                for(mod in locationList){
                    EventBus.subscribe(mod)
                }
                return@on
            }
            for(mod in locationList){
                if(mod.module.enabled&&LocationUtils.isCurrentArea(mod.location)) {
                    EventBus.subscribe(mod.module)
                    modMessage("Loaded ${mod.module.name} module",zcon)
                }
            }
        }
    }
}
