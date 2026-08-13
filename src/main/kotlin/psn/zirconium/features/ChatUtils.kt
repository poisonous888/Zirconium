package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.features.Module
import psn.zirconium.ZirconiumEntry

object ChatUtils: Module(
    name = "Chat Utils",
    description = "Copy Chat, Compact Chat, Etc",
    category=ZirconiumEntry.ZCON
) {
    @JvmStatic val copyChat by BooleanSetting("Copy Chat",false,"Copy hovered chat message to clipboard when you click it")
    @JvmStatic val msgOnCopy by BooleanSetting("Message On Copy",false,"").withDependency { copyChat }
    @JvmStatic val copyModifier by SelectorSetting("Copy Chat Modifier", "Control",
        listOf("Control","Shift","Alt","None"),""
    ).withDependency { copyChat }
    @JvmStatic val formatRegex=Regex("§.")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    @JvmStatic val compact by BooleanSetting("Compact Chat",false,"")
    @JvmStatic val timestamp by BooleanSetting("Include Time",false,"")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    @JvmStatic val hideBlank by BooleanSetting("Hide Blank",false,"")
    @JvmStatic val separators by SelectorSetting("Separator Mode", "All",
        listOf("Hide","Compact","All"),""
    )
    @JvmStatic fun checkSeparator(s:String):Boolean{
        val first=s[0]
        for(c:Char in s){
            if(c!=first)return false
        }
        return true
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    @JvmStatic val noClear by BooleanSetting("Dont Clear Chat",false,"")
    @JvmStatic val infLines by BooleanSetting("No Chat Limit",false,"")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    
    
}