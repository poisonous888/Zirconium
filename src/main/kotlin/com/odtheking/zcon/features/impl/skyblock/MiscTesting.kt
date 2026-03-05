package com.odtheking.zcon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket

object MiscTesting : Module(
    name = "Misc Testing",
    description = ""
) {
    private val sendTabChanges by BooleanSetting("Send Tab Changes", true, desc = "")
    init {
        onReceive<ClientboundPlayerInfoUpdatePacket> {
            val tabListEntries=entries()?.mapNotNull { it.displayName?.string }?.ifEmpty { return@onReceive } ?: return@onReceive
            for (entry in tabListEntries){
                if(sendTabChanges){modMessage(entry)}
            }
        }
    }
}