package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import psn.zirconium.ZconCategory

object OneEightNine : Module(
    name = "Misc 1.8.9 Stuff",
    description = "random small stuff from 1.8.9. Props to animatium for some of the code",
    category = ZconCategory.ZCON
) {
    @JvmStatic val diagonalWalk by BooleanSetting("Diagonal Backwards Walk",false,"1.8.9 backwards walking")
    @JvmStatic val CustomCapePhysics by BooleanSetting("Custom Cape Physics",false,"1.8.9 cape physics")
    @JvmStatic val sideLeanClamp by NumberSetting("Horizontal Cape Lean",150,0,180,1,"set to 0 for 1.21 physics, or a high number for 1.8.9 physics").withDependency { CustomCapePhysics }
    @JvmStatic val verticalLeanPosClamp by NumberSetting("Forwards Cape Lean",180,0,180,1,"set to 0 to prevent it from leaving your back when walking forwards").withDependency { CustomCapePhysics }
    @JvmStatic val verticalLeanNegClamp by NumberSetting("Backwards Cape Lean",0,-180,0,1,"set to 0 to prevent it from going through you when walking backwards").withDependency { CustomCapePhysics }
    @JvmStatic val flapMult by NumberSetting("Cape Flap Multiplier",1,0,5,1,"when you walk the cape flaps, this multiplies that effect").withDependency { CustomCapePhysics }
}