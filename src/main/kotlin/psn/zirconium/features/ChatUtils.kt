package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.*
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon

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
    
    @JvmStatic val timestamp by BooleanSetting("Include Time",false,"")
    @JvmStatic val noClear by BooleanSetting("Dont Clear Chat",false,"")
    @JvmStatic val infLines by BooleanSetting("No Chat Limit",false,"")
    //TODO @JvmStatic val noResetScroll by BooleanSetting("Dont Reset Scroll",false,"")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    @JvmStatic val compact by BooleanSetting("Compact Chat",false,"")
    @JvmStatic val compactNumbers by BooleanSetting("Compact Numbers",false,"")
    @JvmStatic val numberRegex=Regex("[0-9.,]+")
    @JvmStatic val hideBlank by BooleanSetting("Hide Blank",false,"")
    @JvmStatic val separators by SelectorSetting("Separator Mode", "All",
        listOf("Hide","Compact","All"),""
    )
    private val advancedCat by DropdownSetting("Advanced Compact Settings")
    @JvmStatic val debug by BooleanSetting("Debug Messages",false,"").withDependency { advancedCat }
    @JvmStatic val cleanCutoff by NumberSetting("Clean Up Buffer Trigger",200,10,1000,10,"").withDependency { advancedCat }
    @JvmStatic val purgeCutoff by NumberSetting("Purge Buffer Trigger",20,1,100,1,"").withDependency { advancedCat }
    private val testAmt by NumberSetting("Test Exponent",0,0,10,1,"")
    private val test by ActionSetting("Perform Test",""){
        for(i in 1..Math.powExact(10,testAmt)){
            modMessage(i,zcon)
        }
    }
    @JvmStatic fun logDebug(str:String,size:Int,newSize:Int){
        if(debug)modMessage("$str | $size -> $newSize",zcon)
    }
}