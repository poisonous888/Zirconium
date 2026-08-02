package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import net.minecraft.network.chat.Component
import psn.zirconium.ZirconiumEntry

object ChatUtils: Module(
    name = "Chat Utils",
    description = "Copy Chat, Compact Chat, Etc",
    category=ZirconiumEntry.ZCON
) {
    @JvmStatic val compact by BooleanSetting("Compact Chat",false,"")
    @JvmStatic val compactLines by NumberSetting("Compact Lines",20,1,100,1,"")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val hideBlank by BooleanSetting("Hide Blank",false,"")
    private val hideSeperators by BooleanSetting("Hide Separators",false,"")
    val seperatorRegex=Regex("^[▬-]+$")
    @JvmStatic fun checkBlankAndSeparator(components: Component):Boolean{
        if(!enabled)return false
        val str = components.string
        return (hideBlank&&str.isBlank())||(hideSeperators&&seperatorRegex.containsMatchIn(str))
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    @JvmStatic val noClear by BooleanSetting("Dont Clear Chat",false,"")
    @JvmStatic val infLines by BooleanSetting("No Chat Limit",false,"")
    //private val copyKey by KeybindSetting("Copy Chat Modifier",GLFW.GLFW_KEY_UNKNOWN,"blank to disable")
    
    
    @JvmStatic fun checkCopyKey():Boolean{
        return false//GLFW.glfwGetKey(mc.window.handle(),copyKey.value)==1
        //TODO do this
    }
    
}