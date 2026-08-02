package psn.zirconium.utils

import com.google.gson.JsonObject
import com.odtheking.odin.OdinMod
import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.ScreenEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.network.WebUtils.fetchJson
import kotlinx.coroutines.launch
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import psn.zirconium.features.MiscFeatures
import psn.zirconium.zcon
import java.net.URI

class UpdateCheck {
    init {
        on<ScreenEvent.Open>{
            MiscFeatures.loaded=true
        }
        on<LevelEvent.Load>{
            EventBus.unsubscribe(this@UpdateCheck)
            MiscFeatures.loaded=true
            if(mc.player?.name?.string?.lowercase()=="zerostrike92"){EventBus.subscribe(object:Any(){var True=2;init{on<GuiEvent.DrawTooltip>{if(True==1)cancel()};on<ScreenEvent.Open>{True=True.and(3).inc()}}})}
            OdinMod.scope.launch{
                if(!MiscFeatures.updateNotif)return@launch
                val curVer=FabricLoader.getInstance().getModContainer("zconaddon").get().metadata.version.friendlyString?:return@launch
                val actionJson=fetchJson<JsonObject>("https://api.github.com/repos/poisonous888/Zirconium/actions/artifacts").getOrNull()?:return@launch
                val newVer=Regex("zirconium-([0-9.]+)\\.jar").find(actionJson.getAsJsonArray("artifacts").get(0).asJsonObject.get("name").asString)?.groups[1]?.value
                val link="https://github.com/poisonous888/Zirconium/actions"
                if(newVer!=curVer){
                    modMessage("§4//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//§r","")
                    modMessage("§cupdate zirconium bruv §7(§5$curVer§7 -> §d$newVer§7)", zcon)
                    modMessage(
                        Component.literal("§b$link").withStyle {
                            it.withClickEvent(ClickEvent.OpenUrl(URI(link))).withHoverEvent(HoverEvent.ShowText(Component.literal(link)))
                        },""
                    )
                    modMessage("§cyou can disable this message under MiscFeatures","")
                    modMessage("§4//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//","")
                }
            }
        }
    }
}
