package psn.zirconium.features

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.events.InputEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.render.text
import psn.zirconium.ZirconiumEntry

object CPSDisplay : Module(
    name = "CPS Display",
    description = "This is a test module's description.",
    category=ZirconiumEntry.ZCON
) {
    private val button by SelectorSetting("Button",buttonEnum.Both, desc="The button to display the CPS of.")
    enum class buttonEnum{
        Both,
        LEFT,
        RIGHT
    }
    private val mouseText by BooleanSetting("Show Button Name", true, desc="Shows the button name.")
    private val textColor by ColorSetting("Color", Color(239, 239, 239, 1f), allowAlpha=true, desc="The color of the text.")
    private val hud by HUD("HUD", "Displays your clicks per second in the HUD.",false) {
        leftClicks.removeAll { System.currentTimeMillis() > it }
        rightClicks.removeAll { System.currentTimeMillis() > it }
        
        val value = if (button == buttonEnum.Both) "${leftClicks.size}" else "${rightClicks.size}"
        
        if (mouseText) {
            if (button == buttonEnum.RIGHT) {
                text("LMB", 1, 1, textColor)
                text(leftClicks.size.toString(), 7, 15, textColor)
                text("RMB", 35, 1, textColor)
                text(rightClicks.size.toString(), 42, 15, textColor)
            } else {
                val text = if (button == buttonEnum.Both) "LMB" else "RMB"
                text(text, 1, 1, textColor)
                text(value, 7, 15, textColor)
            }
        } else {
            if (button == buttonEnum.RIGHT) {
                text(leftClicks.size.toString(), 1, 10, textColor)
                text(rightClicks.size.toString(), 35, 10, textColor)
            } else text(value, 5, 10, textColor)
        }
        if (button == buttonEnum.RIGHT) 54 to 24 else 20 to 24
    }
    
    private val leftClicks = mutableListOf<Long>()
    private val rightClicks = mutableListOf<Long>()
    
    init {
        on<InputEvent>{
            if(key.value==InputConstants.MOUSE_BUTTON_LEFT){
                leftClicks.add(System.currentTimeMillis()+1000)
            }
            if(key.value==InputConstants.MOUSE_BUTTON_RIGHT){
                rightClicks.add(System.currentTimeMillis()+1000)
            }
        }
    }
}