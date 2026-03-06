package com.odtheking.zcon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.features.Module
import org.lwjgl.glfw.GLFW

object DropStack : Module(
    name = "Drop Stack",
    description = "Keybind for dropping a whole stack"
) {
    private val dropStackKey by KeybindSetting("Key", GLFW.GLFW_KEY_UNKNOWN, desc = "Description.").onPress {
        if (!enabled) return@onPress
        mc.player?.drop(true)
    }
}