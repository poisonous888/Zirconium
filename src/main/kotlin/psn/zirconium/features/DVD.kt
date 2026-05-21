package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.clickgui.settings.impl.StringSetting
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.render.text
import psn.zirconium.ZirconiumEntry
import java.awt.Color.getHSBColor

object DVD : Module(
    name = "DVD",
    description = "No further explanation.",
    category=ZirconiumEntry.ZCON
) {
    private val boxWidth by NumberSetting("Box Width", 50, 0, 150, 1, desc = "Width of the DVD box.")
    private val boxHeight by NumberSetting("Box Height", 50, 0, 150, 1, desc = "Height of the DVD box.")

    private val speed by NumberSetting("Speed", 1f, 1, 3, .1, desc = "Speed of the DVD box.")
    private val text by StringSetting("Text", "ODVD", desc = "Text to display on the DVD box.")

    private var lastUpdateTime = System.nanoTime()
    private var color = Colors.WHITE.copy()
    private var x = 10
    private var y = 10
    private var dx = 1
    private var dy = 1

    private var hud by HUD("DVD","",false){
        updatePosition()
        fill(x, y, x + boxWidth, y + boxHeight, color.rgba)
        text(text, (x + boxWidth / 2f - mc.font.width(text) / 2f).toInt(), (y + boxHeight / 2f - 5).toInt(), color, true)
        return@HUD 0 to 0
    }

    override fun onEnable() {
        x = mc.window.width / 4
        y = mc.window.height / 4
        lastUpdateTime = System.nanoTime()
        super.onEnable()
    }

    private fun randomDVDColor(): Color {
        val javaColor = getHSBColor((Math.random() * 360).toFloat(), 1.0f, 0.5f)
        return Color(javaColor.red, javaColor.green, javaColor.blue)
    }

    private fun updatePosition() {
        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0
        lastUpdateTime = currentTime

        val movement = speed * deltaTime * 200
        x += (dx * movement.toFloat()).toInt()
        y += (dy * movement.toFloat()).toInt()


        val screenWidth = mc.window.width / 2
        val screenHeight = mc.window.height / 2

        if (x <= 0) {
            x = 0
            dx = -dx
            color = randomDVDColor()
        } else if (x + boxWidth >= screenWidth) {
            x = (screenWidth - boxWidth)
            dx = -dx
            color = randomDVDColor()
        }

        if (y <= 0) {
            y = 0
            dy = -dy
            color = randomDVDColor()
        } else if (y + boxHeight >= screenHeight) {
            y = (screenHeight - boxHeight)
            dy = -dy
            color = randomDVDColor()
        }

        if ((x <= 0 || x + boxWidth >= screenWidth) && (y <= 0 || y + boxHeight >= screenHeight)) alert("$text has hit a corner!")
    }
}