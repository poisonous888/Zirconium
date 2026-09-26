package psn.zirconium.features

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.events.ScreenEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.clickSlot
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.playSoundAtPlayer
import com.odtheking.odin.utils.skyblock.Island
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.sounds.SoundEvents
import psn.zirconium.ZirconiumEntry
import psn.zirconium.utils.LocationCheck.dynamicLoad
import psn.zirconium.zcon

object FastFuse : Module(
    name = "Fast Fuse",
    description = "Attribute Fusion Keybinds",
    category=ZirconiumEntry.zconCat
) {
    val confirm by KeybindSetting("Confirm",InputConstants.UNKNOWN.value)
    val repeat by KeybindSetting("Repeat",InputConstants.UNKNOWN.value)
    val fusionRegex = Regex("(\\([0-9]+/[0-9]+\\) )?Fusion Box")
    var timeout=false
    
    init {
        dynamicLoad(Island.MoongladeMarsh)
        on<ScreenEvent.KeyPress>{
            var type=0
            val title=screen.title.string
            if(fusionRegex.containsMatchIn(title))type=1
            if(title=="Confirm Fusion")type=2
            if(type==0){return@on}
            
            if(confirm.value==repeat.value){
                modMessage("Cant Bind Confirm And Repeat To The Same Key",zcon)
                playSoundAtPlayer(SoundEvents.WITHER_DEATH)
                return@on
            }
            
            if(timeout){
                alert("Wait")
                return@on
            }
            
            if(screen !is AbstractContainerScreen<*>)return@on
            val screenId=(screen as AbstractContainerScreen<*>).menu.containerId
            
            var index=0
            if(type==2&&input.key==confirm.value)index=33
            if(type==1&&input.key==repeat.value)index=47
            if(index==0)return@on
            
            timeout=true
            schedule(5,true){timeout=false}
            mc.player?.clickSlot(screenId, index)
        }
    }
}

