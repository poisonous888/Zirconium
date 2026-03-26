package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.MapSetting
import com.odtheking.odin.events.RenderEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.drawCustomBeacon
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import psn.zirconium.ZconCategory

object StaticWaypoints : Module(
    name = "Static Waypoints",
    description = "static island based waypoints",
    category = ZconCategory.ZCON
) {
    val defaultColor by ColorSetting("Default Color",Colors.MINECRAFT_AQUA,true,"default color for waypoints without a specified color")
    val add3x3 by ActionSetting("Add 3x3","readds the 3x3 waypoint for f7/m7 in case you clear it"){
        addWaypoint("3x3",54, 64, 114,Colors.MINECRAFT_RED,Island.Dungeon)
    }
    val storage by MapSetting(
        "Waypoint Storage",
        mutableMapOf(
            Island.Dungeon to MutableList<Waypoint>(1){
                Waypoint("3x3", BlockPos(54, 64, 114), Colors.MINECRAFT_RED)
            }
        )
    )
    init {
        on<RenderEvent.Extract> {
            storage[LocationUtils.currentArea]?.removeAll {
                drawCustomBeacon(it.name, it.blockPos, it.color)
                false
            }
        }
    }
    data class Waypoint(
        val name: String,
        val blockPos: BlockPos,
        val color: Color
    )
    fun addWaypoint(name: String){
        val pos = Minecraft.getInstance().player?.position() ?: return
        addWaypoint(name,Mth.floor(pos.x),Mth.floor(pos.y), Mth.floor(pos.z))
    }
    fun addWaypoint(name: String, x: Int, y: Int, z: Int){ addWaypoint(name,x,y,z,defaultColor) }
    fun addWaypoint(name: String, x: Int, y: Int, z: Int, color: Color){ addWaypoint(name,x,y,z,color,LocationUtils.currentArea) }
    fun addWaypoint(name: String, x: Int, y: Int, z: Int, color: Color, island: Island){
        storage[island] ?: storage.set(island, mutableListOf())
        val confirm = storage[island]?.add(Waypoint(name, BlockPos(x,y,z),color)) ?: false
        if(confirm){ modMessage("Added Waypoint $name at x:$x y:$y z:$z") }
        else{ modMessage("Failed To Add Waypoint") }
    }
    fun removeWaypoint(name: String){ removeWaypoint(name,LocationUtils.currentArea) }
    fun removeWaypoint(name: String, island: Island){
        storage[island]?.removeIf { it.name==name }
        if(storage[island]?.isEmpty() ?: false) storage.remove(island)
        modMessage("Removed Waypoint $name")
    }
    fun clearIsland(){ clearIsland(LocationUtils.currentArea) }
    fun clearIsland(island: Island){
        storage.remove(island)
        modMessage("Cleared Waypoints on Island $island")
    }
    fun clearAll(){
        storage.clear()
        modMessage("Cleared All Waypoints")
    }
    fun list(){ list(LocationUtils.currentArea) }
    fun list(island: Island){
        if(storage[island]?.isEmpty() ?: true){
            modMessage("No Waypoints on Island $island")
        }
        var out = "$island:\n"
        storage[island]?.forEach{ out+=" | ${it.name} at x: ${it.blockPos.x}, y: ${it.blockPos.y}, z: ${it.blockPos.z}" }
        modMessage(out)
    }
    fun listAll(){
        storage.keys.forEach{ list(it) }
    }
}