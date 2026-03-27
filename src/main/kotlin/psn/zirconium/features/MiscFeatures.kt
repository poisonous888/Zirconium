package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import psn.zirconium.ZconCategory

object MiscFeatures : Module(
    name = "Misc Features",
    description = "Random small stuff that dosent need a dedicated module",
    category = ZconCategory.ZCON
) {
//    private val dropStackKey by KeybindSetting("Drop Stack Key", GLFW.GLFW_KEY_UNKNOWN, desc = "Currently only works outside inventory. ill fix it after 0.1").onPress {
//        if (!enabled) return@onPress
//        mc.player?.drop(true)
//    }
//@JvmStatic fun message(s: String){
//    modMessage(s);
//}
    @JvmStatic val closeSign by BooleanSetting("Close Sign On Enter", false,"Closes Sign GUI's When The Enter Key Is Pressed")
    @JvmStatic val noRecipeBook by BooleanSetting("No Recipe Book",false,"removes recipe book from inv")
//@JvmStatic val noWorldLoad by BooleanSetting("No World Loading Screen",false,"cancels the world loading screen. props to nofrills")
    //@JvmStatic val noResourceLoad by BooleanSetting("No Reloading Screen",false,"cancels the resource reloading screen. props to rrls")
}