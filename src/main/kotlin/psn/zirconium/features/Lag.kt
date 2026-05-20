package psn.zirconium.features

import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import psn.zirconium.ZconCategory
import java.lang.System.gc

object Lag : Module(
    name = "Lag",
    description = "increase lag, but decrease memory use",
    category = ZconCategory.ZCON
){
    init{
        on<TickEvent.End> {
           gc()
        }
        //EventBus.subscribe({on<TickEvent.End>{gc()}})
    }
}