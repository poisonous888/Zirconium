package psn.zirconium.utils

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.effects.SpearAnimations
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState
import net.minecraft.client.renderer.state.level.PlayerRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.component.DataComponents
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.ShieldItem
import psn.zirconium.features.HeldItemRender.itemHeight
import psn.zirconium.features.HeldItemRender.itemLength
import psn.zirconium.features.HeldItemRender.itemWidth
import psn.zirconium.features.HeldItemRender.itemX
import psn.zirconium.features.HeldItemRender.itemXrot
import psn.zirconium.features.HeldItemRender.itemY
import psn.zirconium.features.HeldItemRender.itemYrot
import psn.zirconium.features.HeldItemRender.itemZ
import psn.zirconium.features.HeldItemRender.itemZrot
import psn.zirconium.features.HeldItemRender.swingOrot
import psn.zirconium.features.HeldItemRender.swingXrot
import psn.zirconium.features.HeldItemRender.swingYrot
import psn.zirconium.features.HeldItemRender.swingZrot
import psn.zirconium.features.HeldItemRender.swingx
import psn.zirconium.features.HeldItemRender.swingy
import psn.zirconium.features.HeldItemRender.swingz
import psn.zirconium.features.HeldItemRender.translateSwing
import kotlin.math.pow

object RenderHandItem{
    val mc=Minecraft.getInstance()
    val fallback:FirstPersonHandsAndItemsRenderer = mc.gameRenderer.firstPersonHandsAndItemsRenderer
    
    //inverseArmHeight | 0f = up, 1f = down
    const val HEIGHT=0f
    
    var attack=0f
    var useDur=0
    var frameInterp=0f
    var invert=1
    
    fun render(
        partialTicks:Float,
        ps:PoseStack,
        snc:SubmitNodeCollector,
        prs:PlayerRenderState,
        state:FirstPersonHandsAndItemsRenderState
    ){
        frameInterp=partialTicks
        val ars=prs.avatarRenderState?:return
        
        //swing animation stuff
        val attackValue=ars.swingAnimation
        val attackHand=ars.currentSwing?.hand?:InteractionHand.MAIN_HAND
        
        //the slowly catching up effect when you turn the camera
        ps.rotateDegrees(Axis.XP,(state.viewXRot-state.xBob)*0.1f)
        ps.rotateDegrees(Axis.YP,(state.viewYRot-state.yBob)*0.1f)
        
        //items
        val mainHandItem=ars.mainHandItemStack
        val offhandItem=ars.rightHandItemStack
        
        //compacted hand rendering logic
        val usingOff=ars.isUsingItem&&attackHand==InteractionHand.OFF_HAND
        val renderMainHand=!usingOff
        val renderOffHand=!offhandItem.isEmpty
        
        //hand position multiplier | 1 = right, -1 = left
        invert=1
        
        if(renderMainHand) {
            //create new positioning state
            ps.pushPose()
            //current swing/use animation
            attack=if(attackHand==InteractionHand.MAIN_HAND)attackValue else 0f
            useDur=state.mainHandUseDuration
            //arm if empty
            if(mainHandItem.isEmpty){
                if(ars.isInvisible)return
                val arm=HumanoidArm.RIGHT //TODO left hand support
                fallback.renderPlayerArm(ps, snc, ars.lightCoords, 0f, attack, arm,prs)
            }
            //map if map
            else if(mainHandItem.has(DataComponents.MAP_ID)){
                fallback.renderTwoHandedMap(ps,snc,ars.lightCoords,ars.xRot,HEIGHT,attack,prs,state)
            }
            //normal item
            else{
                //all the translation stuff
                translate(ps,mainHandItem,state)
                //render the item
                state.offHandRenderState.submit(ps,snc,ars.lightCoords,OverlayTexture.NO_OVERLAY,0)
            }
            //saves positioning
            ps.popPose()
        }
        //flip hand
        invert*=-1
        if(renderOffHand) {
            //current swing/use animation
            attack=if(attackHand==InteractionHand.OFF_HAND) attackValue else 0.0f
            useDur=state.offHandUseDuration
            //create new positioning state
            ps.pushPose()
            //all the translation stuff
            translate(ps,offhandItem,state)
            //render the item
            state.offHandRenderState.submit(ps,snc,ars.lightCoords,OverlayTexture.NO_OVERLAY,0)
            //saves positioning
            ps.popPose()
        }
    }
    
    private fun translate(poseStack:PoseStack,itemStack:ItemStack,state:FirstPersonHandsAndItemsRenderState){
        //applyItemArmTransform
        poseStack.translate(invert*0.56f,-0.52f+HEIGHT*-0.6f,-0.72f)
        
        //use transform
        if(mc.player?.isUsingItem==true){
            useTranslate(poseStack,itemStack,state)
        }
        
        //apply custom position
        customTranslate(poseStack)
        
        //swing transform
        swingTranslate(poseStack)
    }
    private fun customTranslate(poseStack:PoseStack){
        poseStack.scale(itemWidth, itemHeight, itemLength)
        poseStack.translate(invert * itemX, itemY, itemZ)
        poseStack.rotate(Axis.XP,(itemXrot.toFloat()))
        poseStack.rotate(Axis.YP,(invert * itemYrot).toFloat())
        poseStack.rotate(Axis.ZP,(invert * itemZrot).toFloat())
    }
    private fun swingTranslate(poseStack:PoseStack) {
        val sqf = Mth.sqrt(attack) * Math.PI
        
        if(translateSwing){
            val tx = swingx * -0.4f * Mth.sin(sqf) * invert
            val ty = swingy * 0.2f * Mth.sin(sqf * 2)
            val tz = swingz * -0.2f * Mth.sin(attack * Math.PI)
            poseStack.translate(tx, ty, tz)
        }
        
        val xz = Mth.sin(sqf)
        val r = invert * swingOrot
        
        val y = swingYrot * Mth.sin(attack * attack * Math.PI) * invert - r
        val x = swingXrot * xz
        val z = swingZrot * xz * invert
        poseStack.rotate(Axis.YP,y)
        poseStack.rotate(Axis.ZP,z)
        poseStack.rotate(Axis.XP,x)
        poseStack.rotate(Axis.YP,r)
    }
    
    private fun block(poseStack:PoseStack){
        poseStack.translate(invert*-0.14142136f,0.08f,0.14142136f)
        poseStack.rotate(Axis.XP.rotationDegrees(-102.25f))
        poseStack.rotate(Axis.YP.rotationDegrees(invert*13.365f))
        poseStack.rotate(Axis.ZP.rotationDegrees(invert*78.05f))
    }
    private fun useTranslate(ps:PoseStack,item:ItemStack,state:FirstPersonHandsAndItemsRenderState){
        val arm=if(invert==1)HumanoidArm.LEFT else HumanoidArm.RIGHT
        val timeHeld=useDur-(state.useItemRemainingTicks-frameInterp+1.0f)
        when(item.useAnimation) {
            ItemUseAnimation.NONE,ItemUseAnimation.BUNDLE -> {}
            ItemUseAnimation.EAT,ItemUseAnimation.DRINK -> {
                ps.translate(invert*-0.56f,0.52f+HEIGHT*0.6f,0.72f)
                val currUsageTime=state.useItemRemainingTicks-frameInterp+1.0f
                val scaledUsageTime=currUsageTime/useDur
                if(scaledUsageTime<0.8f) {
                    val extraHeightOffset=Mth.abs(Mth.cos((currUsageTime/4.0f*Math.PI))*0.1f)
                    ps.translate(0.0f,extraHeightOffset,0.0f)
                }
                
                val eatJiggle=1.0f-scaledUsageTime.toDouble().pow(27.0).toFloat()
                ps.translate(eatJiggle*0.6f*invert,eatJiggle*-0.5f,eatJiggle*0.0f)
                ps.rotate(Axis.YP,invert*eatJiggle*90.0f)
                ps.rotate(Axis.XP,eatJiggle*10.0f)
                ps.rotate(Axis.ZP,invert*eatJiggle*30.0f)
                ps.translate(invert*0.56f,-0.52f+HEIGHT*-0.6f,-0.72f)
            }
            
            ItemUseAnimation.BLOCK -> if(item.item !is ShieldItem) {
                block(ps)
            }
            
            ItemUseAnimation.BOW,ItemUseAnimation.CROSSBOW -> {
                ps.translate(invert*-0.2785682f,0.18344387f,0.15731531f)
                ps.rotate(Axis.XP,-13.935f)
                ps.rotate(Axis.YP,invert*35.3f)
                ps.rotate(Axis.ZP,invert*-9.785f)
                var power=timeHeld/20.0f
                power=(power*power+power*2.0f)/3.0f
                if(power>1.0f) {
                    power=1.0f
                }
                
                if(power>0.1f) {
                    val shakeOffset=Mth.sin(((timeHeld-0.1f)*1.3f).toDouble())
                    val shakeIntensity=power-0.1f
                    val shake=shakeOffset*shakeIntensity
                    ps.translate(shake*0.0f,shake*0.004f,shake*0.0f)
                }
                
                ps.translate(power*0.0f,power*0.0f,power*0.04f)
                ps.scale(1.0f,1.0f,1.0f+power*0.2f)
                ps.rotate(Axis.YN,invert*45.0f)
            }
            
            ItemUseAnimation.TRIDENT -> {
                ps.translate(invert*-0.5f,0.7f,0.1f)
                ps.rotate(Axis.XP,-55.0f)
                ps.rotate(Axis.YP,invert*35.3f)
                ps.rotate(Axis.ZP,invert*-9.785f)
                var power=timeHeld/10.0f
                if(power>1.0f) {
                    power=1.0f
                }
                
                if(power>0.1f) {
                    val shakeOffset=Mth.sin(((timeHeld-0.1f)*1.3f).toDouble())
                    val shakeIntensity=power-0.1f
                    val shake=shakeOffset*shakeIntensity
                    ps.translate(shake*0.0f,shake*0.004f,shake*0.0f)
                }
                
                ps.translate(0.0f,0.0f,power*0.2f)
                ps.scale(1.0f,1.0f,1.0f+power*0.2f)
                ps.rotate(Axis.YN,invert*45.0f)
            }
            
            ItemUseAnimation.BRUSH -> {
                ps.translate(invert*-0.56f,0.52f+HEIGHT*0.6f,0.72f)
                fallback.applyBrushTransform(ps,frameInterp,arm,state.useItemRemainingTicks.toFloat())
            }
            
            ItemUseAnimation.SPEAR -> {
                ps.translate(invert*0.56f,-0.52f,-0.72f)
                SpearAnimations.firstPersonUse(1f,ps,timeHeld,arm,item)
            }
            
            else-> {}
        }
    }
}