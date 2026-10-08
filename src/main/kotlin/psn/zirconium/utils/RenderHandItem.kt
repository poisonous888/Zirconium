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
import psn.zirconium.features.HeldItemRender.driftMult
import psn.zirconium.features.HeldItemRender.itemHeight
import psn.zirconium.features.HeldItemRender.itemLength
import psn.zirconium.features.HeldItemRender.itemWidth
import psn.zirconium.features.HeldItemRender.itemX
import psn.zirconium.features.HeldItemRender.itemXrot
import psn.zirconium.features.HeldItemRender.itemY
import psn.zirconium.features.HeldItemRender.itemYrot
import psn.zirconium.features.HeldItemRender.itemZ
import psn.zirconium.features.HeldItemRender.itemZrot
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
        ps.rotateDegrees(Axis.XP,(state.viewXRot-state.xBob)*driftMult)
        ps.rotateDegrees(Axis.YP,(state.viewYRot-state.yBob)*driftMult)
        
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
                fallback.renderPlayerArm(ps, snc, 0, 0f, attack, arm,prs)
            }
            //map if map
            else if(mainHandItem.has(DataComponents.MAP_ID)){
                fallback.renderTwoHandedMap(ps,snc,0,ars.xRot,HEIGHT,attack,prs,state)
            }
            //normal item
            else{
                //all the translation stuff
                translate(ps,mainHandItem,state)
                //render the item
                state.mainHandRenderState.submit(ps,snc,ars.lightCoords,OverlayTexture.NO_OVERLAY,0)
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
    
    private fun translate(ps:PoseStack,stack:ItemStack,state:FirstPersonHandsAndItemsRenderState){
        //applyItemArmTransform
        ps.translate(invert*0.56f,-0.52f+HEIGHT*-0.6f,-0.72f)
        
        //use transform
        if(mc.player?.isUsingItem==true){
            useTranslate(ps,stack,state)
        }
        
        //apply custom position
        customTranslate(ps)
        
        //swing transform
        swingTranslate(ps)
    }
    private fun customTranslate(ps:PoseStack){
        ps.scale(itemWidth, itemHeight, itemLength)
        ps.translate(invert * itemX, itemY, itemZ)
        ps.rotate(Axis.XP,(itemXrot))
        ps.rotate(Axis.YP,(invert * itemYrot))
        ps.rotate(Axis.ZP,(invert * itemZrot))
    }
    private fun swingTranslate(ps:PoseStack) {
        if(translateSwing){
            val tx=-0.4f*Mth.sin((Mth.sqrt(attack)*Math.PI.toFloat()).toDouble())
            val ty=0.2f*Mth.sin((Mth.sqrt(attack)*(Math.PI.toFloat()*2f)).toDouble())
            val tz=-0.2f*Mth.sin((attack*Math.PI))
            ps.translate(invert*tx,ty,tz)
        }
        
        val yRot=Mth.sin((attack*attack*Math.PI.toFloat()).toDouble())
        val xzRot=Mth.sin((Mth.sqrt(attack)*Math.PI))
        
        ps.rotateDegrees(Axis.YP,invert*(45.0f+yRot*-20.0f))
        ps.rotateDegrees(Axis.ZP,invert*xzRot*-20.0f)
        ps.rotateDegrees(Axis.XP,xzRot*-80.0f)
        ps.rotateDegrees(Axis.YP,invert*-45.0f)
    }
    
    private fun block(ps:PoseStack){
        ps.translate(invert*-0.14142136f,0.08f,0.14142136f)
        ps.rotate(Axis.XP.rotationDegrees(-102.25f))
        ps.rotate(Axis.YP.rotationDegrees(invert*13.365f))
        ps.rotate(Axis.ZP.rotationDegrees(invert*78.05f))
    }
    private fun eatOfficial(ps:PoseStack,ticks:Int){
        val currUsageTime:Float=ticks-frameInterp+1f
        val scaledUsageTime=currUsageTime/useDur
        val eatJiggle=1.0f-scaledUsageTime.toDouble().pow(27.0).toFloat()
        
        if(scaledUsageTime<0.8f) {
            val extraHeightOffset=Mth.abs(Mth.cos((currUsageTime/4f*Math.PI))*0.1f)
            ps.translate(0.0f,extraHeightOffset,0.0f)
        }
        
        ps.translate(eatJiggle*0.6f*invert.toFloat(),eatJiggle*-0.5f,0f)
        ps.rotateDegrees(Axis.YP,invert.toFloat()*eatJiggle*90f)
        ps.rotateDegrees(Axis.XP,eatJiggle*10f)
        ps.rotateDegrees(Axis.ZP,invert.toFloat()*eatJiggle*30f)
    }
    private fun eatBetter(ps:PoseStack,ticks:Int){
        val currUsageTime=ticks-frameInterp+1
        val scaledUsageTime=currUsageTime/useDur
        
        if(scaledUsageTime<0.8f) {
        
        }
        ps.translate(-scaledUsageTime,scaledUsageTime/2,0f)
    }
    private fun useTranslate(ps:PoseStack,item:ItemStack,state:FirstPersonHandsAndItemsRenderState){
        val arm=if(invert==1)HumanoidArm.LEFT else HumanoidArm.RIGHT
        val timeHeld=useDur-(state.useItemRemainingTicks-frameInterp+1.0f)
        when(item.useAnimation) {
            ItemUseAnimation.NONE,ItemUseAnimation.BUNDLE -> {}
            ItemUseAnimation.EAT,ItemUseAnimation.DRINK -> {
                eatBetter(ps,state.useItemRemainingTicks)
            }
            
            ItemUseAnimation.BLOCK -> if(item.item !is ShieldItem) {
                block(ps)
            }
            
            ItemUseAnimation.BOW,ItemUseAnimation.CROSSBOW -> {
                ps.translate(invert.toFloat()*-0.2785682f,0.18344387f,0.15731531f)
                ps.rotateDegrees(Axis.XP,-13.935f)
                ps.rotateDegrees(Axis.YP,invert.toFloat()*35.3f)
                ps.rotateDegrees(Axis.ZP,invert.toFloat()*-9.785f)
                val timeHeld:Float=useDur-(state.useItemRemainingTicks-frameInterp+1f)
                val p=timeHeld/20f
                val power=((p*p+p*2f)/3f).coerceAtMost(1f)
                
                if(power>0.1f) {
                    val shakeOffset=Mth.sin(((timeHeld-0.1f)*1.3f).toDouble())
                    val shakeIntensity=power-0.1f
                    val shake=shakeOffset*shakeIntensity
                    ps.translate(shake*0f,shake*0.004f,shake*0f)
                }
                
                ps.translate(0f,0f,power*0.04f)
                ps.scale(1f,1f,1f+power*0.2f)
                ps.rotateDegrees(Axis.YN,invert*45f)
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
                //ps.translate(invert*-0.56f,0.52f+HEIGHT*0.6f,0.72f)
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