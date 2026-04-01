package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.customData
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import net.minecraft.world.item.ItemStack
import org.lwjgl.glfw.GLFW
import psn.zirconium.ZconCategory

object DropUtils : Module(
    name = "Drop Utils",
    description = "protect items and drop stack modifier",
    category = ZconCategory.ZCON
) {
    val doSBID by BooleanSetting("Protect Skyblock ID",true,"")
    @JvmStatic val sbidKey by KeybindSetting("Skyblock ID Key", GLFW.GLFW_KEY_UNKNOWN).withDependency { doSBID }
    val doUUID by BooleanSetting("Protect UUID",true,"")
    @JvmStatic val uuidKey by KeybindSetting("UUID Key",GLFW.GLFW_KEY_UNKNOWN).withDependency { doUUID }
    val doRecombed by BooleanSetting("Protect Recombed",true,"")
    val doStarred by BooleanSetting("Protect Starred",true,"")
    val doMuseum by BooleanSetting("Protect Museum Donated",true,"")
    val permHotbar by BooleanSetting("Always Prevent Hotbar",true,"when enabled you have to be in your inventory to drop items")
    val disableDungeons by BooleanSetting("Disable in Dungeons",true,"dungeons use drop as use ultimate so its not needed")
    val highlightProtected by BooleanSetting("Highlight Protected",false,"tooltips are too hard rn")

    val uidList by ListSetting("uuidList",mutableListOf(""))
    val sbidList by ListSetting("sbidList",mutableListOf(""))

    //private val dropStackKey by KeybindSetting("Custom Drop Stack Modifier", GLFW.GLFW_KEY_UNKNOWN, desc = "set to unknown to disable")

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    fun isProtected(item: ItemStack,message: Boolean): Boolean{
        val customData = item.customData
        if(doUUID && customData.contains("uuid")){
            if(uidList.contains(customData.get("uuid").toString())){
                if(message)modMessage("Prevented Your ${item.hoverName.string} From Being Dropped (Skyblock ID)")
                return true
            }
        }
        if(doSBID && customData.contains("id")){
            if(sbidList.contains(customData.get("id").toString())){
                if(message)modMessage("Prevented Your ${item.hoverName.string} From Being Dropped (UUID)")
                return true
            }
        }
        if(doStarred && customData.contains("upgrade_level")){
            if(message)modMessage("Prevented Your ${item.hoverName.string} From Being Dropped (starred)")
            return true
        }
        if(doRecombed && customData.contains("rarity_upgrades")){
            if(message)modMessage("Prevented Your ${item.hoverName.string} From Being Dropped (recombed)")
            return true
        }
        if(doMuseum && customData.contains("donated_museum")){
            if(message)modMessage("Prevented Your ${item.hoverName.string} From Being Dropped (museum)")
            return true
        }
        return false
    }
    @JvmStatic fun doDropHotbar(item: ItemStack): Boolean{
        if(disableDungeons && DungeonUtils.inDungeons){
            val room = DungeonUtils.currentRoomName
            if(!room.equalsOneOf("Entrance","Unknown")){
                return false
            }
        }
        return isProtected(item,true) || permHotbar
    }
    @JvmStatic fun doDropContainer(item: ItemStack): Boolean{
        return isProtected(item,true)
    }
    @JvmStatic fun uuidNew(item: ItemStack){
        if(!item.customData.contains("uuid")){
            modMessage("Item ${item.hoverName.string} Does Not Have A UUID, Use Skyblock ID Instead")
            return
        }
        val id = item.customData.get("uuid").toString()
        if(uidList.contains(id)){
            uidList.remove(id)
            modMessage("§4Removed ${item.hoverName.string} From UUID Protection")
            return
        }
        uidList.add(id)
        modMessage("§2Added ${item.hoverName.string} To UUID Protection")
    }
    @JvmStatic fun sbidNew(item: ItemStack){
        if(!item.customData.contains("id")){
            modMessage("Item ${item.hoverName.string} Does Not Have A Skublock ID, Use UUID Instead")
            return
        }
        val id = item.customData.get("id").toString()
        if(sbidList.contains(id)){
            sbidList.remove(id)
            modMessage("§4Removed ${item.hoverName.string} From Skyblock ID Protection")
            return
        }
        sbidList.add(id)
        modMessage("§2Added ${item.hoverName.string} To Skyblock ID Protection")
    }
    init {
        on<GuiEvent.RenderSlot>{
            if(!highlightProtected)return@on
            if(isProtected(slot.item,false)){
                guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Colors.MINECRAFT_DARK_RED.rgba)
                guiGraphics.renderFakeItem(slot.item,slot.x, slot.y)
            }
        }
    }
//    @JvmStatic fun getTooltip(item: ItemStack): Component {
//        modMessage(item.getTooltipLines())
//    }
}