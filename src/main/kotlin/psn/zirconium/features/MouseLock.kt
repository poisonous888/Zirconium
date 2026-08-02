package psn.zirconium.features

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.features.Module
import net.minecraft.client.MouseHandler
import psn.zirconium.ZirconiumEntry

class MouseLock : Module(
    name = "Mouse Lock",
    description = "Prevents turning with the mouse, use the keybind setting inside to enable/disable",
    category=ZirconiumEntry.ZCON
) {
    //@JvmStatic fun enabled():Boolean{return enabled}
//    override fun onEnable() {
//        super.onEnable()
//        mc.mouseHandler.mouseGrabbed=false
//        InputConstants.grabOrReleaseMouse(mc.window, 212995, mc.mouseHandler.xpos(), mc.mouseHandler.ypos())
//    }
//    override fun onDisable() {
//        super.onDisable()
//        InputConstants.grabOrReleaseMouse(mc.window, 212995, mc.mouseHandler.xpos(), mc.mouseHandler.ypos());
//    }
}