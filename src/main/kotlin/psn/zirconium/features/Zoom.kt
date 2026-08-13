package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting.Companion.isDown
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import org.lwjgl.glfw.GLFW
import psn.zirconium.ZirconiumEntry
import kotlin.math.pow

object Zoom: Module(
    name = "Zoom",
    description = "Copy Chat, Compact Chat, Etc",
    category=ZirconiumEntry.ZCON
) {
    private val zoomKey by KeybindSetting("Zoom Key",GLFW.GLFW_KEY_UNKNOWN)
    private val reset by BooleanSetting("Reset To Default After Use",true,"")
    private val defaultZoom by NumberSetting("Default Zoom",2.0,-5,5,1,"").withDependency { reset }
    private val zoomMult by NumberSetting("Zoom Multiplier",1.0,0,3,0.1,"")
    private val invertScroll by BooleanSetting("Invert Scroll",false,"")
    private val hideHud by BooleanSetting("Hide Hud",false,"")
    private val cinCam by BooleanSetting("Cinematic Camera",false,"")
    private val always by BooleanSetting("Always On",false,"")
    
    @JvmStatic var zooming=false
    @JvmStatic fun pollZoomKey(){
        val test=enabled&&zoomKey.isDown()||always
        if(test&&!zooming){
            if(cinCam)mc.options.smoothCamera=true
            if(hideHud)mc.options.hideGui=true
            zooming=true
        }
        if(zooming&&!test){
            if(cinCam)mc.options.smoothCamera=false
            if(hideHud)mc.options.hideGui=false
            if(reset)zoomLevel=defaultZoom
            zooming=false
        }
    }
    
    var zoomLevel=1.0
    @JvmStatic fun scroll(yoffset: Double){
        zoomLevel+=yoffset*if(invertScroll) -1 else 1
    }
    @JvmStatic fun getZoom():Float{
        return 2.0.pow(zoomLevel*zoomMult).toFloat()
    }
}