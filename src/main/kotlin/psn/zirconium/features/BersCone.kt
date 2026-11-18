package psn.zirconium.features

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.clickgui.settings.RenderableSetting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.*
import com.odtheking.odin.events.LocationChangeEvent
import com.odtheking.odin.events.RenderExtractEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.*
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.render.PrimitiveRenderer
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import com.odtheking.odin.utils.skyblock.dungeon.DungeonClass
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon
import kotlin.math.cos
import kotlin.math.sin

object BersCone:Module(
    name="Bers Cone",
    description="Attribute Fusion Keybinds",
    category=ZirconiumEntry.zconCat
) {
    private val color by ColorSetting("Color",Colors.MINECRAFT_DARK_PURPLE,false,"")
    private val range by NumberSetting("Swing Range",0f,0..15,0.1,"")
    private val segments by NumberSetting("Segments",32,3..100,1,"")
    private val rings by NumberSetting("Rings",1,1..6,1,"")
    private val lines by BooleanSetting("Lines",true,"")
    private val lawnmower by BooleanSetting("Lawnmower Helper",true,"")
    private val lawnColor by ColorSetting("Lawnmower Color",Colors.MINECRAFT_AQUA,false,"").withDependency { lawnmower }
    private val lawnTrim by NumberSetting("Lawnmower Trim",4.5,3..5,0.1,"").withDependency { lawnmower }
    private val allowedItems by ListSetting("Allowed Items",mutableListOf<String>())
    private val addHeld by ActionSetting("Toggle Held Item","adds or removes your held item from the whitelist"){
        val held=mc.player?.mainHandItem?.itemId?:return@ActionSetting
        if(allowedItems.remove(held)){
            modMessage("§4Removed $held to allowed items",zcon)
            return@ActionSetting
        }
        allowedItems.addLast(held)
        modMessage("§2Added $held to allowed items",zcon)
    }
    private val removeHeld by ActionSetting("Clear Allowed Items",""){
        allowedItems.clear()
        modMessage("§4Cleared allowed items",zcon)
    }
    private var inactive=false
    private var lastClassBers=false
    
    init {
        on<LocationChangeEvent>{
            inactive=!LocationUtils.isCurrentArea(Island.Dungeon)
            checkBers(0)
        }
        
        on<RenderExtractEvent> {
            if(inactive||!lastClassBers)return@on
            if(!allowedItems.contains(mc.player?.mainHandItem?.itemId))return@on
            
            val player=mc.player?:return@on
            val render=player.renderPos.add(0.0,1.62,0.0)
            val x=render.x.toFloat()
            val y=render.y.toFloat()
            val z=render.z.toFloat()
            
            val poseStack=cameraRelativePose()
            poseStack.rotateAround(Axis.YP.rotationDegrees(-player.yRot),x,y,z)
            poseStack.rotateAround(Axis.XP.rotationDegrees(player.xRot+90),x,y,z)
            poseStack.rotateAround(Axis.YP.rotationDegrees(90f),x,y,z)
            
            poseStack.pushPose()
            poseStack.translate(0.0,range.toDouble(),0.0)
            val ringPercentage=1.0/rings
            for(i in 0..<rings){
                
                drawTiltedCircle(
                    render,
                    range*ringPercentage*(rings-i),
                    if(lines)range*ringPercentage*(rings-1-i) else range*ringPercentage*(rings-i),
                    color,
                    segments,poseStack = poseStack
                )
            }
            poseStack.popPose()
            
            
            poseStack.translate(0.0,lawnTrim,0.0)
            if(lawnmower){
                context.submitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.LINES) { pose, buffer ->
                    val rgba=lawnColor.rgba
                    PrimitiveRenderer.renderVector(
                        pose,
                        buffer,
                        Vector3f(x,y,z),
                        Vec3(-1.0,0.0,0.0),
                        rgba,
                        rgba,
                        25f
                    )
                }
            }
            
            
            poseStack.popPose()
        }
    }
    
    fun checkBers(i:Int){
        when(DungeonUtils.currentDungeonPlayer.clazz){
            DungeonClass.BERSERK -> lastClassBers=true
            DungeonClass.EMPTY -> {
                if(i>30)return
                schedule(20,true){checkBers(1)}
            }
            else -> lastClassBers=false
        }
    }
}

fun RenderExtractEvent.drawTiltedCircle(
    center:Vec3,
    radius:Double,
    lineRadius:Double,
    color:Color,
    segments:Int=32,
    thickness:Float=5f,
    poseStack:PoseStack
) {
    val rgba = color.rgba
    val angleStep = 2.0 * Math.PI / segments
    
    context.submitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.LINES) { pose, buffer ->
        fun segment(from: Vec3, to: Vec3) {
            PrimitiveRenderer.renderVector(
                pose,
                buffer,
                Vector3f(from.x.toFloat(), from.y.toFloat(), from.z.toFloat()),
                to.subtract(from),
                rgba,
                rgba,
                thickness
            )
        }
        for (i in 0 until segments) {
            val angle1 = i * angleStep
            val angle2 = (i + 1) * angleStep
            
            val x1 = (radius * cos(angle1))
            val z1 = (radius * sin(angle1))
            val x2 = (radius * cos(angle2))
            val z2 = (radius * sin(angle2))
            val x3 = (lineRadius * cos(angle1))
            val z3 = (lineRadius * sin(angle1))
            
            val p1 = center.add(x1, 0.0,z1)
            val p2 = center.add(x2, 0.0,z2)
            val p3 = center.add(x3, 0.0,z3)
            
            segment(p1, p2)
            segment(p1, p3)
        }
    }
}


//TODO remove bc itll be in odin eventually

fun RenderExtractEvent.cameraRelativePose(offset: Vec3 = Vec3.ZERO): PoseStack {
    val camera = mc.gameRenderer.mainCamera().position()
    val poseStack = context.poseStack()
    poseStack.pushPose()
    poseStack.translate(offset.x - camera.x, offset.y - camera.y, offset.z - camera.z)
    return poseStack
}