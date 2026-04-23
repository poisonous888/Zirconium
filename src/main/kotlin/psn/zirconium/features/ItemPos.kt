package psn.zirconium.features

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.*
import com.odtheking.odin.features.Module
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm
import psn.zirconium.ZconCategory

object ItemPos : Module(
    name = "Held Item Render",
    description = "changes the held item position",
    category = ZconCategory.ZCON
){
    private val translation by DropdownSetting("Translation")
    private val itemX by NumberSetting("x",0.0,-0.5,0.5,0.05,"").withDependency { translation }
    private val itemY by NumberSetting("y",0.0,-0.5,0.5,0.05,"").withDependency { translation }
    private val itemZ by NumberSetting("z",0.0,-0.5,0.5,0.05,"").withDependency { translation }
    private val rsTrans by ActionSetting("Reset Translation",""){
        settings["x"]?.reset()
        settings["y"]?.reset()
        settings["z"]?.reset()
    }.withDependency { translation }
    private val rotation by DropdownSetting("Rotation")
    private val itemXrot by NumberSetting("x rot",0,-180,180,1,"").withDependency { rotation }
    private val itemYrot by NumberSetting("y rot",0,-180,180,1,"").withDependency { rotation }
    private val itemZrot by NumberSetting("z rot",0,-180,180,1,"").withDependency { rotation }
    private val rsRot by ActionSetting("Reset Rotation",""){
        settings["x rot"]?.reset()
        settings["y rot"]?.reset()
        settings["z rot"]?.reset()
    }.withDependency { rotation }
    private val scale by DropdownSetting("Scale")
    private val itemWidth by NumberSetting("width",1f,-1,5,0.05,"").withDependency { scale }
    private val itemHeight by NumberSetting("height",1f,-1,5,0.05,"").withDependency { scale }
    private val itemLength by NumberSetting("length",1f,-1,5,0.05,"").withDependency { scale }
    private val rsScale by ActionSetting("Reset Scale",""){
        settings["width"]?.reset()
        settings["height"]?.reset()
        settings["length"]?.reset()
    }.withDependency { scale }

    @JvmStatic fun executeTranslate(poseStack: PoseStack, humanoidArm: HumanoidArm) {
        if (!enabled)return
        val i = if(humanoidArm == HumanoidArm.RIGHT) 1 else -1
        if(itemWidth!=0f||itemHeight!=0f||itemLength!=0f) poseStack.scale(itemWidth, itemHeight, itemLength)
        if(itemX!=0.0||itemY!=0.0||itemZ!=0.0) poseStack.translate(i * itemX, itemY, itemZ)
        if(itemXrot!=0)poseStack.mulPose(Axis.XP.rotationDegrees(itemXrot.toFloat()))
        if(itemYrot!=0)poseStack.mulPose(Axis.YP.rotationDegrees((i * itemYrot).toFloat()))
        if(itemZrot!=0)poseStack.mulPose(Axis.ZP.rotationDegrees((i * itemZrot).toFloat()))
    }

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private val swingTransform by DropdownSetting("Swing Transform")
    private val customSwing by BooleanSetting("Custom Swing",false,"").withDependency { swingTransform }
    private val swingWhileUsing by BooleanSetting("Swing While Using",false,"left click while drawing a bow").withDependency { swingTransform }

    private val swingXrot by NumberSetting("Swing X Rot",-80f,-160,0,16,"").withDependency { swingTransform }
    private val swingYrot by NumberSetting("Swing Y Rot",-20f,-40,0,4,"").withDependency { swingTransform }
    private val swingZrot by NumberSetting("Swing Z Rot",-20f,-40,0,4,"").withDependency { swingTransform }
    private val swingOrot by NumberSetting("Swing Offset Rot",-45f,-90,0,5,"").withDependency { swingTransform }

    private val translateSwing by BooleanSetting("Translate Swing",true,"").withDependency { swingTransform }
    private val swingx by NumberSetting("Swing X",1f,0,4,0.25,"").withDependency { swingTransform && translateSwing }
    private val swingy by NumberSetting("Swing Y",1f,0,4,0.25,"").withDependency { swingTransform && translateSwing }
    private val swingz by NumberSetting("Swing Z",1f,0,4,0.25,"").withDependency { swingTransform && translateSwing }

    private val rsSwing by ActionSetting("Reset Swing Transform",""){
        settings["Swing X Rot"]?.reset()
        settings["Swing Y Rot"]?.reset()
        settings["Swing Z Rot"]?.reset()
        settings["Swing Offset Rot"]?.reset()
        settings["Translate Swing"]?.reset()
        settings["Swing X"]?.reset()
        settings["Swing Y"]?.reset()
        settings["Swing Z"]?.reset()
    }.withDependency { swingTransform }

    @JvmStatic fun doSwing(): Boolean {return enabled&&customSwing}
    @JvmStatic fun doSwingWhileUsing():Boolean{return enabled&&customSwing&&swingWhileUsing}
    @JvmStatic fun executeSwing(poseStack: PoseStack,arm: Int,f: Float){
        val pi = 3.0
        val sqf = Mth.sqrt(f) * pi

        if(translateSwing){
            val tx = swingx * -0.4f * Mth.sin(sqf) * arm
            val ty = swingy * 0.2f * Mth.sin(sqf * 2)
            val tz = swingz * -0.2f * Mth.sin(f * pi)
            poseStack.translate(tx, ty, tz)
        }

        val xz = Mth.sin(sqf)
        val r = arm * swingOrot

        val y = swingYrot * Mth.sin(f * f * pi) * arm - r
        val x = swingXrot * xz
        val z = swingZrot * xz * arm
        poseStack.mulPose(Axis.YP.rotationDegrees(y))
        poseStack.mulPose(Axis.ZP.rotationDegrees(z))
        poseStack.mulPose(Axis.XP.rotationDegrees(x))
        poseStack.mulPose(Axis.YP.rotationDegrees(r))
    }

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private val swingLen by DropdownSetting("Swing Settings")
    private val customSwingDuration by BooleanSetting("Custom Swing Duration",false,"").withDependency { swingLen }
    @JvmStatic val swingDuration by NumberSetting("Swing Duration",7,2,14,1,"").withDependency { swingLen && customSwingDuration }
    private val ignoreHaste by BooleanSetting("Ignore Haste",false,"").withDependency { swingLen && customSwingDuration }

    @JvmStatic fun doSwingDur(): Boolean{return enabled&&customSwingDuration}
    @JvmStatic fun doHaste():Boolean{return enabled&&ignoreHaste&&customSwingDuration}

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private val handNswap by DropdownSetting("Hand and Swap")
    private val noSwapAnim by BooleanSetting("No Swap",false,"").withDependency { handNswap }
    private val disableHand by BooleanSetting("Disable Hand",false,"").withDependency { handNswap }
    private val syncHand by BooleanSetting("Sync Hand With Item",false,"").withDependency { handNswap }

    @JvmStatic fun doNoSwap():Boolean{return enabled&&noSwapAnim}
    @JvmStatic fun doDisableHand():Boolean{return enabled&&disableHand}
    @JvmStatic fun doSyncHand():Boolean{return enabled&&syncHand}

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private val presets by DropdownSetting("Presets")
    private val vanilla by ActionSetting("Vanilla",""){
        rsTrans.invoke()
        rsRot.invoke()
        rsScale.invoke()
        rsSwing.invoke()
        settings["Custom Swing Duration"]?.reset()
        settings["Swing Duration"]?.reset()
        settings["Swing While Using"]?.reset()
        settings["No Swap"]?.reset()
        settings["Disable Hand"]?.reset()
        settings["Sync Hand With Item"]?.reset()
    }.withDependency { presets }
    private val small by ActionSetting("Small",""){
        setSettings(Preset(
            trans = listOf(0.1, 0.2, 0.0),
            rot = listOf(0, 0, 0),
            scale = listOf(0.35f, 0.35f, 0.35f),
            swing = listOf(-80f,-20f,-20f,-45f,0f,0f,0f),
            doSwingDur = true,
            swingDur = 4,
            haste = true,
            swap = true,
            nohand = true,
            sync = false,
            using = true,
        ))
    }.withDependency { presets }
    private val horizontal by ActionSetting("Horizontal",""){
        setSettings(Preset(
            trans = listOf(-0.3, 0.25, -0.05),
            rot = listOf(11, -22, 92),
            scale = listOf(1f, 1f, 1f),
            swing = listOf(0f,0f,0f,0f,0.75f,0f,0f),
            doSwingDur = true,
            swingDur = 12,
            haste = true,
            swap = true,
            nohand = true,
            sync = false,
            using = true,
        ))
    }.withDependency { presets }
    private val poison by ActionSetting("Poison",""){
        setSettings(Preset(
            trans = listOf(0.1, 0.1, 0.0),
            rot = listOf(0, 0, 0),
            scale = listOf(0.75f, 0.75f, 0.75f),
            swing = listOf(-80f,-20f,-20f,-45f,0f,0f,0f),
            doSwingDur = true,
            swingDur = 6,
            haste = true,
            swap = true,
            nohand = false,
            sync = false,
            using = true,
        ))
    }.withDependency { presets }
    data class Preset(
        val trans:List<Double>,
        val rot:List<Int>,
        val scale:List<Float>,
        val swing:List<Float>,
        val doSwingDur:Boolean,
        val swingDur:Int,
        val haste:Boolean?=false,
        val swap:Boolean,
        val nohand:Boolean,
        val sync:Boolean,
        val using:Boolean,
    )
    @Suppress("UNCHECKED_CAST")
    private fun setSettings(p: Preset){
        (settings["x"] as NumberSetting<Double>).value = p.trans[0]
        (settings["y"] as NumberSetting<Double>).value = p.trans[1]
        (settings["z"] as NumberSetting<Double>).value = p.trans[2]
        (settings["x rot"] as NumberSetting<Int>).value = p.rot[0]
        (settings["y rot"] as NumberSetting<Int>).value = p.rot[1]
        (settings["z rot"] as NumberSetting<Int>).value = p.rot[2]
        (settings["width"] as NumberSetting<Float>).value = p.scale[0]
        (settings["height"] as NumberSetting<Float>).value = p.scale[1]
        (settings["length"] as NumberSetting<Float>).value = p.scale[2]
        (settings["Swing X Rot"] as NumberSetting<Float>).value = p.swing[0]
        (settings["Swing Y Rot"] as NumberSetting<Float>).value = p.swing[1]
        (settings["Swing Z Rot"] as NumberSetting<Float>).value = p.swing[2]
        (settings["Swing Offset Rot"] as NumberSetting<Float>).value = p.swing[3]
        (settings["Swing X"] as NumberSetting<Float>).value = p.swing[4]
        (settings["Swing Y"] as NumberSetting<Float>).value = p.swing[5]
        (settings["Swing Z"] as NumberSetting<Float>).value = p.swing[6]
        (settings["Translate Swing"] as BooleanSetting).value = p.swing[4] != 0f || p.swing[5] != 0f || p.swing[6] != 0f
        (settings["Custom Swing Duration"] as BooleanSetting).value = p.doSwingDur
        (settings["Swing Duration"] as NumberSetting<Int>).value = p.swingDur
        (settings["No Swap"] as BooleanSetting).value = p.swap
        (settings["Disable Hand"] as BooleanSetting).value = p.nohand
        (settings["Sync Hand With Item"] as BooleanSetting).value = p.sync
        (settings["Swing While Using"] as BooleanSetting).value = p.using
        (settings["Custom Swing"] as BooleanSetting).value = true
    }
}