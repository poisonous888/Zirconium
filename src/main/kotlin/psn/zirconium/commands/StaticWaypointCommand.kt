package psn.zirconium.commands

import com.github.stivais.commodore.Commodore
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.modMessage
import psn.zirconium.features.StaticWaypoints
import java.util.Locale.getDefault

val staticWaypointCmd = Commodore("staticwaypoints","sw"){
    runs { modMessage("Static Waypoints") }
    literal("add").executable{
        param("name")
        runs{
            name: String -> StaticWaypoints.addWaypoint(name)
        }
    }
    literal("add").executable{
        param("name")
        param("x")
        param("y")
        param("z")
        runs{
            name: String, x: Int,y: Int, z: Int -> StaticWaypoints.addWaypoint(name,x,y,z)
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
                name: String -> StaticWaypoints.addWaypoint(name)
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
            StaticWaypoints.addWaypoint(name,x,y,z,color)
        }
    }
    literal("remove").executable{
        param("name")
        runs{
            name: String ->
            StaticWaypoints.removeWaypoint(name)
        }
    }
    literal("clear"){
        literal("island").executable {
            runs{
                StaticWaypoints.clearIsland()
            }
        }
        literal("all").executable {
            runs{
                StaticWaypoints.clearAll()
            }
        }
    }
    literal("list").executable {
        runs{
            StaticWaypoints.list()
        }
    }
    literal("listAll").executable {
        runs{
            StaticWaypoints.listAll()
        }
    }
}
val colorStringMap=mapOf(
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