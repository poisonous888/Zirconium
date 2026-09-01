package psn.zirconium.features

import com.odtheking.odin.features.Module
import psn.zirconium.ZirconiumEntry

object MouseLock : Module(
    name = "Mouse Lock",
    description = "Prevents turning with the mouse, use the keybind setting inside to enable/disable",
    category=ZirconiumEntry.ZCON
) {
    @JvmStatic fun enabled():Boolean{return enabled}
}