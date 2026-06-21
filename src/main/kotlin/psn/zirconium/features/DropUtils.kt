package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.InputEvent
import com.odtheking.odin.events.ScreenEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.*
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW
import psn.zirconium.AsyncSave
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon

object DropUtils: AsyncSave, Module(
    name = "Drop Utils",
    description = "protect items and drop stack modifier",
    category=ZirconiumEntry.ZCON
) {
    private val protections by DropdownSetting("Protections")
    private val doSBID by BooleanSetting("Protect Skyblock ID",true,"").withDependency { protections }
    private val doUUID by BooleanSetting("Protect UUID",true,"").withDependency { protections }
    private val doRecombed by BooleanSetting("Protect Recombed",true,"").withDependency { protections }
    private val doStarred by BooleanSetting("Protect Starred",true,"").withDependency { protections }
    private val doMuseum by BooleanSetting("Protect Museum Donated",true,"").withDependency { protections }
    private val protectAll by BooleanSetting("Protect All",false,"").withDependency { protections }

    private val doSound by BooleanSetting("Sound On Drop",true,"")
    private val permHotbar by BooleanSetting("Always Prevent Hotbar",true,"when enabled you have to be in your inventory to drop items")
    private val disableDungeons by BooleanSetting("Disable in Dungeons",true,"dungeons use drop as use ultimate so its not needed")
    private val highlightProtected by BooleanSetting("Highlight Protected",false,"tooltips are too hard rn")

    private val dropStackKey by KeybindSetting("Drop Stack Key", GLFW.GLFW_KEY_UNKNOWN, desc = "Set to unknown to disable,\nCtrl+drop still works,\nCurrently only works outside container/inventory")
    private val sbidKey by KeybindSetting("Skyblock ID Key", GLFW.GLFW_KEY_UNKNOWN).withDependency { doSBID }
    private val uuidKey by KeybindSetting("UUID Key",GLFW.GLFW_KEY_UNKNOWN).withDependency { doUUID }

    private val uidList by ListSetting("uuidList",mutableListOf(""))
    private val sbidList by ListSetting("sbidList",mutableListOf(""))

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private var noClickScreen=false
    fun isProtected(item: ItemStack): String?{
        if(item.item==Items.AIR)return null
        if(protectAll)return "All"
        val customData = item.customData
        if(doUUID &&
            customData.contains("uuid") &&
            uidList.contains(customData.get("uuid").toString())
            ) return "UUID"
        if(doSBID &&
            customData.contains("id") &&
            sbidList.contains(customData.get("id").toString())
            ) return "Skyblock ID"
        if(doStarred && customData.contains("upgrade_level")) return "Starred"
        if(doRecombed && customData.contains("rarity_upgrades")) return "Recombed"
        if(doMuseum && customData.contains("donated_museum")) return "Museum"
        return null
    }
    @JvmStatic fun doDropHotbar(item: ItemStack): Boolean{
        if(disableDungeons && DungeonUtils.inDungeons){
            val room=DungeonUtils.currentRoomName
            if(room=="Entrance"||room=="Unknown"&&DungeonUtils.inClear){
                modMessage("Cannot Drop Items Until The Dungeon Has Started!", zcon)
                return true
            }
            return false
        }
        if(permHotbar)return true
        return dropWithMsg(item)
    }
    @JvmStatic fun doDropContainer(slot: Slot?, slotId: Int, clickType: ContainerInput): Boolean{
        if(slot==null){
            if(clickType==ContainerInput.QUICK_CRAFT){
                return false
            }
            return dropWithMsg((mc.screen as? AbstractContainerScreen<*>)?.menu?.carried?:return false)
        }
        if(noClickScreen||clickType==ContainerInput.THROW||slotId<0)return dropWithMsg(slot.item)
        return false
    }
    fun dropWithMsg(item: ItemStack): Boolean{
        val prot=isProtected(item)?:return false
        modMessage("Prevented Your ${item.hoverName.string} From Being Dropped ($prot)", zcon)
        if(doSound)alert("")
        return true
    }
    fun uuidNew(item: ItemStack){
        if(!item.customData.contains("uuid")){
            modMessage("Item ${item.hoverName.string} Does Not Have A UUID, Use Skyblock ID Instead", zcon)
            return
        }
        val id = item.customData.get("uuid").toString()
        if(uidList.contains(id)){
            uidList.remove(id)
            modMessage("§4Removed ${item.hoverName.string} From UUID Protection", zcon)
            alert("")
            config.save()
            return
        }
        uidList.add(id)
        modMessage("§2Added ${item.hoverName.string} To UUID Protection", zcon)
        alert("")
        config.save()
    }
    fun sbidNew(item: ItemStack){
        if(!item.customData.contains("id")){
            modMessage("Item ${item.hoverName.string} Does Not Have A Skyblock ID, Use UUID Instead", zcon)
            return
        }
        val id = item.customData.get("id").toString()
        if(sbidList.contains(id)){
            sbidList.remove(id)
            modMessage("§4Removed ${item.hoverName.string} From Skyblock ID Protection", zcon)
            alert("")
            config.save()
            return
        }
        sbidList.add(id)
        modMessage("§2Added ${item.hoverName.string} To Skyblock ID Protection", zcon)
        alert("")
        config.save()
    }
    fun getHoveredInv():ItemStack?{
        val item=(mc.screen as? AbstractContainerScreen<*>)?.hoveredSlot?.item?:return null
        if(item.item==Items.AIR)return null
        return item
    }
    fun getHeldHotbar(): ItemStack?{
        return mc.player?.inventory?.selectedItem
    }
    init {
        on<GuiEvent.RenderSlot>{
            if(!highlightProtected)return@on
            isProtected(slot.item)?:return@on
            guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Colors.MINECRAFT_DARK_RED.rgba)
        }
        on<ScreenEvent.KeyPress>{
            when(input.key) {
                sbidKey.value -> sbidNew(getHoveredInv() ?: return@on)
                uuidKey.value -> uuidNew(getHoveredInv() ?: return@on)
                dropStackKey.value -> {
                    val screenAccess = mc.screen as? AbstractContainerScreen<*> ?: return@on.also{modMessage("not abstractcontainerscreen")}
                    val slot = screenAccess.hoveredSlot ?: return@on.also{modMessage("no hoveredslot")}
                    if (dropWithMsg(slot.item)) return@on
                    screenAccess.slotClicked(slot, slot.index, 1, ContainerInput.THROW)
                }
            }
        }
        on<InputEvent>{
            if(key!=dropStackKey)return@on
            if(doDropHotbar(getHeldHotbar()?:return@on))return@on
            mc.player?.drop(true)
        }
        on<ScreenEvent.Open>{
            noClickScreen=screen !is InventoryScreen
            if(noClickScreen) schedule(1,true) { doOpen() }
        }
        on<ScreenEvent.Close>{
            noClickScreen=false
        }
    }
    fun doOpen(){
        val sc=mc.screen as? AbstractContainerScreen<*> ?:return
        if(sc.title.string=="Salvage Items"){
            noClickScreen=true
            return
        }
        if(sc.menu.isValidSlotIndex(49)){
            val sellSlot=sc.menu.getSlot(49)
            if(sellSlot.item.hoverName.string=="Sell Item"){
                noClickScreen=true
                return
            }
            sellSlot.item.lore.forEach{
                t->if(t.string=="Click to buyback!"){
                    noClickScreen=true
                    return
                }
            }
        }
        noClickScreen=false
    }
    override fun onDisable() {
        noClickScreen=false
        //modMessage("Disabling Drop Utils Prevents Using Keybinds To Modify Protected List, But Due To It Relying On A Mixin, It Will Still Function")
        super.onDisable()
    }
    private val config=ModuleConfig("DropUtils.json")
    override fun getConfig(): ModuleConfig {
        return config
    }
}