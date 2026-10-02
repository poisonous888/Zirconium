package psn.zirconium.commands

import com.github.stivais.commodore.Commodore
import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.CustomData
import psn.zirconium.zcon

val modelCommand=Commodore("getmodel"){
    runs{
        modMessage("Pack Model:",zcon)
        modMessage(mc.player?.mainHandItem?.getOrDefault(DataComponents.ITEM_MODEL, CustomData.EMPTY),"")
        modMessage("Base Item Model:",zcon)
        modMessage(mc.player?.mainHandItem?.item,"")
    }
}
val locationCommand=Commodore("getlocation"){
    runs{
        modMessage("Cur Location:",zcon)
        modMessage(LocationUtils.currentArea,"")
    }
}

