package psn.zirconium.features

import com.github.stivais.commodore.Commodore
import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.MapSetting
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.RenderEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.drawCustomBeacon
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import psn.zirconium.AsyncSave
import psn.zirconium.HasCommands
import psn.zirconium.ZirconiumEntry
import java.util.Locale.getDefault

object StaticWaypoints: AsyncSave, HasCommands, Module(
    name = "Static Waypoints",
    description = "static island based waypoints",
    category=ZirconiumEntry.ZCON
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
        if(confirm){
            modMessage("Added Waypoint $name at x:$x y:$y z:$z")
            config.save()
        }
        else{ modMessage("Failed To Add Waypoint") }
    }
    fun removeWaypoint(name: String){ removeWaypoint(name,LocationUtils.currentArea) }
    fun removeWaypoint(name: String, island: Island){
        storage[island]?.removeIf { it.name==name }
        if(storage[island]?.isEmpty() ?: false) storage.remove(island)
        modMessage("Removed Waypoint $name")
        config.save()
    }
    fun clearIsland(){ clearIsland(LocationUtils.currentArea) }
    fun clearIsland(island: Island){
        storage.remove(island)
        modMessage("Cleared Waypoints on Island $island")
        config.save()
    }
    fun clearAll(){
        storage.clear()
        modMessage("Cleared All Waypoints")
        config.save()
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
    
    override fun buildCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        Commodore("staticwaypoints","sw"){
            runs { modMessage("Static Waypoints") }
            literal("add").executable{
                param("name")
                runs{
                    name: String -> addWaypoint(name)
                }
            }
            literal("add").executable{
                param("name")
                param("x")
                param("y")
                param("z")
                runs{
                        name: String, x: Int,y: Int, z: Int -> addWaypoint(name,x,y,z)
                }
            }
            literal("add").executable{
                param("name")
                param("color"){
                    parser{
                        string: String ->
                        colorStringMap[string.lowercase(getDefault())]
                    }
                    suggests { colorStringMap.keys }
                }
                runs{
                    name: String -> addWaypoint(name)
                }
            }
            literal("add").executable{
                param("name")
                param("x")
                param("y")
                param("z")
                param("color"){
                    parser{
                            string: String ->
                        colorStringMap[string.lowercase(getDefault())]
                    }
                    suggests { colorStringMap.keys }
                }
                runs{
                        name: String, x: Int,y: Int, z: Int, color: Color ->
                    addWaypoint(name,x,y,z,color)
                }
            }
            literal("remove").executable{
                param("name")
                runs{
                        name: String ->
                    removeWaypoint(name)
                }
            }
            literal("clear"){
                literal("island").executable {
                    runs{
                        clearIsland()
                    }
                }
                literal("all").executable {
                    runs{
                        clearAll()
                    }
                }
            }
            literal("list").executable {
                runs{
                    list()
                }
            }
            literal("listAll").executable {
                runs{
                    listAll()
                }
            }
        }.register(dispatcher)
        //TODO redo static waypoints better
    }
    private val config=ModuleConfig("StaticWaypoints.json")
    override fun getConfig(): ModuleConfig {
        return config
    }
    private val colorStringMap=mapOf(
        "white" to Colors.WHITE,
        "black" to Colors.BLACK,
        "red" to Colors.MINECRAFT_RED,
        "darkred" to Colors.MINECRAFT_DARK_RED,
        "gold" to Colors.MINECRAFT_GOLD,
        "yellow" to Colors.MINECRAFT_YELLOW,
        "lime" to Colors.MINECRAFT_GREEN,
        "green" to Colors.MINECRAFT_GREEN,
        "darkgreen" to Colors.MINECRAFT_DARK_GREEN,
        "aqua" to Colors.MINECRAFT_AQUA,
        "lightblue" to Colors.MINECRAFT_AQUA,
        "cyan" to Colors.MINECRAFT_DARK_AQUA,
        "blue" to Colors.MINECRAFT_BLUE,
        "darkblue" to Colors.MINECRAFT_DARK_BLUE,
        "purple" to Colors.MINECRAFT_LIGHT_PURPLE,
        "darkpurple" to Colors.MINECRAFT_DARK_PURPLE,
        "grey" to Colors.gray26,
        "darkgrey" to Colors.gray38
    )
}