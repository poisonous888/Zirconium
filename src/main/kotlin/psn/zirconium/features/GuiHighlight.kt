package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.customData
import com.odtheking.odin.utils.loreString
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import psn.zirconium.ZconCategory

object GuiHighlight : Module(
    name = "GUI Highlight",
    description = "Highlights Certain GUI Slots",
    category = ZconCategory.ZCON
) {
    private val highlightPet by BooleanSetting("Highlight Active Pet", false, desc = "displays a colored background behind your active pet")
    private val petColor by ColorSetting("Active Pet Color",Colors.MINECRAFT_RED,false,"the highlight color").withDependency { highlightPet }
    private val highlightCommission by BooleanSetting("Highlight Completed Commissions", false, desc = "displays a colored background behind completed commissions")
    private val commissionColor by ColorSetting("Commission Color",Colors.MINECRAFT_GREEN,false,"the highlight color").withDependency { highlightCommission }
    private val highlightAnvil by BooleanSetting("Highlight Like Books In Anvil", false, desc = "skytils anvil helper")
    //private val blockWrongAnvil by BooleanSetting("Block Incompatible Books", false, desc = "prevents you from combining books with different enchantment levels").withDependency { highlightAnvil }
    private val anvilColor by ColorSetting("Anvil Highlight Color",Colors.MINECRAFT_AQUA,false,"the highlight color").withDependency { highlightAnvil }
    private val petRegex = Regex("Pets(?: \\((\\d)/(\\d)\\))?")
    private var switch = 0
    private var curBook=""
    private var curLvl=0
    init {
        on<GuiEvent.Open>{
            if(screen !is AbstractContainerScreen<*>) {
                switch=0
                return@on
            }
            val title=screen.title.string
            if(petRegex.containsMatchIn(title)){
                //modMessage("correct title")
                switch=1
                return@on
            }
            if(title.equals("Commissions")){
                switch=2
                return@on
            }
            if(title.equals("Anvil")){
                switch=3
                return@on
            }
            switch=0
        }
        on<GuiEvent.DrawSlot>{
            if(switch==0)return@on
            val lore=slot.item.loreString
            if(switch==1){
                if(highlightPet&&lore.contains("Click to despawn!")){
                    guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, petColor.rgba)
                    guiGraphics.renderFakeItem(slot.item,slot.x, slot.y)
                }
                return@on
            }
            if(switch==2){
                if(highlightCommission&&lore.contains("Click to claim rewards!")){
                    guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, commissionColor.rgba)
                    guiGraphics.renderFakeItem(slot.item,slot.x, slot.y)
                }
                return@on
            }
            val customData=slot.item.customData
            if(!highlightAnvil||!customData.contains("enchantments")) return@on
            val enchantmentTag=customData.get("enchantments")?.asCompound()?.get()
            val enchantmentFirst=enchantmentTag?.entrySet()?.first()
            val enchantmentName= enchantmentFirst?.key.toString()
            val enchantmentLvl=enchantmentFirst?.value.toString().toInt()
            if(curBook==enchantmentName&&curLvl==enchantmentLvl){
                guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, anvilColor.rgba)
                guiGraphics.renderFakeItem(slot.item,slot.x, slot.y)
            }
            if(slot.index==29){
                if(!slot.item.item.equals(Items.ENCHANTED_BOOK)){
                    curLvl=-1
                    return@on
                }
                curBook=enchantmentName
                curLvl=enchantmentLvl
            }
            if(slot.index==33&&!(curBook==enchantmentName&&curLvl==enchantmentLvl)&&curLvl!=-1){
                guiGraphics.renderFakeItem(ItemStack(Items.BARRIER),80, 54)
                alert("INVALID COMBINE")
            }
        }
    }
}