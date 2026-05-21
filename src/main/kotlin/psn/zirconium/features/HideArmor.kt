package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import net.minecraft.world.entity.EquipmentSlot
import psn.zirconium.ZirconiumEntry

object HideArmor : Module(
    name = "Hide Armor",
    description = "Hides certain armor pieces",
    category=ZirconiumEntry.ZCON
) {
    private val skull by BooleanSetting("Skull",false,"")
    private val helm by BooleanSetting("Helmet",false,"")
    private val chest by BooleanSetting("Chestplate",false,"")
    private val legs by BooleanSetting("Leggings",false,"")
    private val boots by BooleanSetting("Boots",false,"")
    @JvmStatic fun checkIfRenderable(slot: EquipmentSlot):Boolean{
        if(!enabled)return false
        when(slot){
            EquipmentSlot.HEAD -> {if(helm)return true}
            EquipmentSlot.CHEST -> {if(chest)return true}
            EquipmentSlot.LEGS -> {if(legs)return true}
            EquipmentSlot.FEET -> {if(boots)return true}
            else -> {}
        }
        return false
    }
    @JvmStatic fun checkSkull():Boolean{
        return enabled&&skull
    }
}