package psn.zirconium.features

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.events.ScreenEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.clickSlot
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.playSoundAtPlayer
import com.odtheking.odin.utils.skyblock.Island
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.sounds.SoundEvents
import psn.zirconium.ZirconiumEntry
import psn.zirconium.utils.DynamicModule
import psn.zirconium.zcon

object FastFuse : DynamicModule(
    name = "Fast Fuse",
    description = "Attribute Fusion Keybinds",
    category = ZirconiumEntry.zconCat,
    islands = listOf(Island.MoongladeMarsh)
) {
    val confirm by KeybindSetting("Confirm",InputConstants.UNKNOWN.value)
    val repeat by KeybindSetting("Repeat",InputConstants.UNKNOWN.value)
    val fusionRegex = Regex("(\\([0-9]+/[0-9]+\\) )?Fusion Box")
    var timeout=false
    
    init {
        on<ScreenEvent.KeyPress>{
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
            val title=screen.title.string
            val type=when(input.key){
                confirm.value if title=="Confirm Fusion" -> 33
                repeat.value if fusionRegex.containsMatchIn(title) -> 47
                else -> return@on
            }
            
            timeout=true
            schedule(5,true){timeout=false}
            
            mc.player?.clickSlot(type)
        }
    }
}

