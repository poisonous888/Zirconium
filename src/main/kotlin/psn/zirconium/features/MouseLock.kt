package psn.zirconium.features

import com.odtheking.odin.features.Module
import psn.zirconium.ZconCategory

object MouseLock : Module(
    name = "Mouse Lock",
    description = "Prevents turning with the mouse, use the keybind setting inside to enable/disable",
    category = ZconCategory.ZCON
) {
    @JvmStatic fun enabled():Boolean{return enabled}
}