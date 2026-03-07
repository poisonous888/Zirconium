package com.odtheking.zcon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.features.Module
import org.lwjgl.glfw.GLFW

object MiscFeatures : Module(
    name = "Misc Features",
    description = "Random small stuff that dosent need a dedicated module"
) {
    private val dropStackKey by KeybindSetting("Drop Stack Key", GLFW.GLFW_KEY_UNKNOWN, desc = "Currently only works outside inventory. ill fix it after 0.1").onPress {
        if (!enabled) return@onPress
        mc.player?.drop(true)
    }
    @JvmStatic val closeSign by BooleanSetting(
        "Close Sign On Enter",
        false,
        desc = "Closes Sign GUI's When The Enter Key Is Pressed"
    )
//        .onPress {
//        if (!enabled) return@onPress
//        modMessage(mc.screen?.javaClass.toString())
//        //mc.player?.closeContainer()
//        if(mc.screen is AbstractSignEditScreen){
//            modMessage("test")
//            mc.player?.closeContainer()
//        }
//    }
//    init{
//        on<GuiEvent.KeyPress>{
//            modMessage("got key event")
//            //if(!closeSign){return@on}
//            if(input.key==GLFW.GLFW_KEY_ENTER){
//                modMessage("key is enter")
//                if(mc.screen is SignEditScreen){
//                    screen.onClose()
//                    modMessage("closed")
//                }
//            }
//        }
//    }
}