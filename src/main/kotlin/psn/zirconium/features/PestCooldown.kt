package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.clickgui.settings.impl.StringSetting
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import psn.zirconium.ZconCategory

object PestCooldown : Module(
    name = "Pest Cooldown",
    description = "displays pest cooldown while on the garden",
    category = ZconCategory.ZCON
) {
    private val notifyTime by NumberSetting("Notify On Time", 120, 0, 500, desc = "")
    private val notifyMessage by StringSetting("Message", "Pests Off Cooldown!", desc = "Message to display")
    private val timerRegex = Regex("(Cooldown: )([0-9]+m)? ?([0-9]+s)?")
    private val pestHud by HUD("Example HUD", desc = "This is a hud.") {
        example -> when{
            LocationUtils.isCurrentArea(Island.Garden) -> textDim(curString, 0, 0)
            else -> 0 to 0
        }
    }
    var curString=""
    var primed=false
    init {
        onReceive<ClientboundPlayerInfoUpdatePacket> {
            if(!LocationUtils.isCurrentArea(Island.Garden)){return@onReceive}
            val tabListEntries=entries()?.mapNotNull { it.displayName?.string }?.ifEmpty { return@onReceive } ?: return@onReceive
            for (entry in tabListEntries){
                timerRegex.find(entry)?.destructured?.let { (_,mins,secs) ->
                    val curMins = mins.substringBefore("m").toIntOrNull() ?: 0
                    val curSecs = secs.substringBefore("s").toIntOrNull() ?: 0
                    val curTimer = curMins*60+curSecs
                    //modMessage("$curTimer - $primed")
                    curString=entry
                    if(curTimer<notifyTime&&primed){
                        modMessage(notifyMessage)
                        alert(notifyMessage)
                        primed=false
                    }
                    if(curTimer>notifyTime&&!primed){
                        primed=true
                    }
                }
            }
        }
    }
}