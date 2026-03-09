package com.odtheking.zcon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.loreString
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

object GuiHighlight : Module(
    name = "GUI Highlight",
    description = "Highlights Certain GUI Slots"
) {
    private val highlightPet by BooleanSetting("Highlight Active Pet", false, desc = "")
    private val petColor by ColorSetting("Active Pet Color",Colors.MINECRAFT_RED,false,"")
    private val highlightCommission by BooleanSetting("Highlight Completed Commissions", false, desc = "")
    private val commissionColor by ColorSetting("Commission Color",Colors.MINECRAFT_GREEN,false,"")
    private val screenRegex = Regex("Pets(?: \\((\\d)/(\\d)\\))?|Commissions")
    private var incorrectScreen = true
    init {
        on<GuiEvent.Open>{
            if(screen !is AbstractContainerScreen<*>) {
                incorrectScreen=true
                return@on
            }
            val title=screen.title.string
            //modMessage(title)
            if(screenRegex.containsMatchIn(title)){
                //modMessage("correct title")
                incorrectScreen=false
            }
        }
        on<GuiEvent.DrawSlot>{
            if(incorrectScreen)return@on
            val lore=slot.item.loreString
            //slot.item.customData.get("enchantments").toString()
            if(lore.contains("Click to despawn!")&&highlightPet){
                guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, petColor.rgba)
                guiGraphics.renderFakeItem(slot.item,slot.x, slot.y)
            }
            if(lore.contains("Click to claim rewards!")&&highlightCommission){
                guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, commissionColor.rgba)
                guiGraphics.renderFakeItem(slot.item,slot.x, slot.y)
            }
        }
    }
}