package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import psn.zirconium.ZconCategory

object MiscFeatures : Module(
    name = "Misc Features",
    description = "Random small stuff that dosent need a dedicated module",
    category = ZconCategory.ZCON
) {

    @JvmStatic val closeSign by BooleanSetting("Close Sign On Enter", false,"Closes Sign GUI's When The Enter Key Is Pressed")
    @JvmStatic val noRecipeBook by BooleanSetting("No Recipe Book",false,"removes recipe book from inv")
    @JvmStatic val noPotionEffectsHud by BooleanSetting("No Potion Effects Hud",false,"removes the potion effect display from the inentory and the main hud")
    @JvmStatic val noPotionEffects by BooleanSetting("No Potion Effects",false,"removes the potion effect display from the inentory and the main hud")
    //@JvmStatic val noWorldLoad by BooleanSetting("No World Loading Screen",false,"cancels the world loading screen. props to nofrills")
    //@JvmStatic val noResourceLoad by BooleanSetting("No Reloading Screen",false,"cancels the resource reloading screen. props to rrls")
}