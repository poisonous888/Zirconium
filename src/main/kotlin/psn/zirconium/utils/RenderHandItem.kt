package psn.zirconium.utils

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.effects.SpearAnimations
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.component.DataComponents
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
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
    val fallback:ItemInHandRenderer = mc.gameRenderer.itemInHandRenderer
    
    //inverseArmHeight | 0f = up, 1f = down
    const val HEIGHT=0f
    
    var attack=0f
    var frameInterp=0f
    var invert=1
    
    fun mainRender(fi:Float,poseStack:PoseStack,snc:SubmitNodeCollector,player:LocalPlayer,lightCoords:Int) {
        frameInterp=fi
        
        //swing animation stuff
        val attackValue=player.getAttackAnim(frameInterp)
        val attackHand=player.swingingArm?:InteractionHand.MAIN_HAND
        
        //the slowly catching up effect when you turn the camera
        val xBob=Mth.lerp(frameInterp,player.xBobO,player.xBob)
        val yBob=Mth.lerp(frameInterp,player.yBobO,player.yBob)
        poseStack.mulPose(Axis.XP.rotationDegrees((player.getViewXRot(frameInterp)-xBob)*0.1f))
        poseStack.mulPose(Axis.YP.rotationDegrees((player.getViewYRot(frameInterp)-yBob)*0.1f))
        
        //items
        val mainHandItem=player.mainHandItem
        val offhandItem=player.offhandItem
        
        //compacted hand rendering logic
        val usingOff=player.isUsingItem&&player.usedItemHand==InteractionHand.OFF_HAND
        val renderMainHand=!usingOff
        val renderOffHand=!offhandItem.isEmpty
        
        //hand position multiplier | 1 = right, -1 = left
        invert=1
        
        if(renderMainHand) {
            //create new positioning state
            poseStack.pushPose()
            //current swing animation
            attack=if(attackHand==InteractionHand.MAIN_HAND) attackValue else 0.0f
            //arm if empty
            if(mainHandItem.isEmpty){
                if(player.isInvisible)return
                val arm=HumanoidArm.RIGHT //TODO left hand support
                fallback.renderPlayerArm(poseStack, snc, lightCoords, 0f, attack, arm)
            }
            //map if map
            else if(mainHandItem.has(DataComponents.MAP_ID)){
                val xRot=player.getXRot(frameInterp)
                fallback.renderTwoHandedMap(poseStack,snc,lightCoords,xRot,HEIGHT,attack)
            }
            //normal item
            else{
                //all the translation stuff
                translate(poseStack,mainHandItem)
                //render the item
                renderItem(player,mainHandItem,ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,poseStack,snc,lightCoords)
            }
            //saves positioning
            poseStack.popPose()
        }
        //flip hand
        invert*=-1
        if(renderOffHand) {
            //current swing animation
            attack=if(attackHand==InteractionHand.OFF_HAND) attackValue else 0.0f
            //create new positioning state
            poseStack.pushPose()
            //all the translation stuff
            translate(poseStack,offhandItem)
            //render the item
            renderItem(player,offhandItem,ItemDisplayContext.FIRST_PERSON_LEFT_HAND,poseStack,snc,lightCoords)
            //saves positioning
            poseStack.popPose()
        }
        
        //render all?
        mc.gameRenderer.featureRenderDispatcher.renderAllFeatures()
        mc.renderBuffers().bufferSource().endBatch()
    }
    
    private fun translate(poseStack:PoseStack,itemStack:ItemStack){
        //applyItemArmTransform
        poseStack.translate(invert*0.56f,-0.52f+HEIGHT*-0.6f,-0.72f)
        
        //use transform
        if(mc.player?.isUsingItem==true){
            useTranslate(poseStack,itemStack)
        }
        
        //apply custom position
        customTranslate(poseStack)
        
        //swing transform
        swingTranslate(poseStack)
    }
    private fun customTranslate(poseStack:PoseStack){
        poseStack.scale(itemWidth, itemHeight, itemLength)
        poseStack.translate(invert * itemX, itemY, itemZ)
        poseStack.mulPose(Axis.XP.rotationDegrees(itemXrot.toFloat()))
        poseStack.mulPose(Axis.YP.rotationDegrees(((invert * itemYrot).toFloat())))
        poseStack.mulPose(Axis.ZP.rotationDegrees(((invert * itemZrot).toFloat())))
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
        poseStack.mulPose(Axis.YP.rotationDegrees(y))
        poseStack.mulPose(Axis.ZP.rotationDegrees(z))
        poseStack.mulPose(Axis.XP.rotationDegrees(x))
        poseStack.mulPose(Axis.YP.rotationDegrees(r))
    }
    
    private fun block(poseStack:PoseStack){
        poseStack.translate(invert*-0.14142136f,0.08f,0.14142136f)
        poseStack.mulPose(Axis.XP.rotationDegrees(-102.25f))
        poseStack.mulPose(Axis.YP.rotationDegrees(invert*13.365f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(invert*78.05f))
    }
    private fun useTranslate(poseStack:PoseStack,itemStack:ItemStack){
        val player=mc.player!!
        val arm=if(invert==1)HumanoidArm.LEFT else HumanoidArm.RIGHT
        when(itemStack.useAnimation) {
            ItemUseAnimation.NONE,ItemUseAnimation.BUNDLE -> {}
            ItemUseAnimation.EAT,ItemUseAnimation.DRINK -> {
                poseStack.translate(invert*-0.56f,0.52f+HEIGHT*0.6f,0.72f)
                val currUsageTime=player.useItemRemainingTicks-frameInterp+1.0f
                val scaledUsageTime=currUsageTime/itemStack.getUseDuration(player)
                if(scaledUsageTime<0.8f) {
                    val extraHeightOffset=Mth.abs(Mth.cos((currUsageTime/4.0f*Math.PI))*0.1f)
                    poseStack.translate(0.0f,extraHeightOffset,0.0f)
                }
                
                val eatJiggle=1.0f-scaledUsageTime.toDouble().pow(27.0).toFloat()
                poseStack.translate(eatJiggle*0.6f*invert,eatJiggle*-0.5f,eatJiggle*0.0f)
                poseStack.mulPose(Axis.YP.rotationDegrees(invert*eatJiggle*90.0f))
                poseStack.mulPose(Axis.XP.rotationDegrees(eatJiggle*10.0f))
                poseStack.mulPose(Axis.ZP.rotationDegrees(invert*eatJiggle*30.0f))
                poseStack.translate(invert*0.56f,-0.52f+HEIGHT*-0.6f,-0.72f)
            }
            
            ItemUseAnimation.BLOCK -> if(itemStack.item !is ShieldItem) {
                block(poseStack)
            }
            
            ItemUseAnimation.BOW,ItemUseAnimation.CROSSBOW -> {
                poseStack.translate(invert*-0.2785682f,0.18344387f,0.15731531f)
                poseStack.mulPose(Axis.XP.rotationDegrees(-13.935f))
                poseStack.mulPose(Axis.YP.rotationDegrees(invert*35.3f))
                poseStack.mulPose(Axis.ZP.rotationDegrees(invert*-9.785f))
                val timeHeld=itemStack.getUseDuration(player)-(player.useItemRemainingTicks-frameInterp+1.0f)
                var power=timeHeld/20.0f
                power=(power*power+power*2.0f)/3.0f
                if(power>1.0f) {
                    power=1.0f
                }
                
                if(power>0.1f) {
                    val shakeOffset=Mth.sin(((timeHeld-0.1f)*1.3f).toDouble())
                    val shakeIntensity=power-0.1f
                    val shake=shakeOffset*shakeIntensity
                    poseStack.translate(shake*0.0f,shake*0.004f,shake*0.0f)
                }
                
                poseStack.translate(power*0.0f,power*0.0f,power*0.04f)
                poseStack.scale(1.0f,1.0f,1.0f+power*0.2f)
                poseStack.mulPose(Axis.YN.rotationDegrees(invert*45.0f))
            }
            
            ItemUseAnimation.TRIDENT -> {
                poseStack.translate(invert*-0.5f,0.7f,0.1f)
                poseStack.mulPose(Axis.XP.rotationDegrees(-55.0f))
                poseStack.mulPose(Axis.YP.rotationDegrees(invert*35.3f))
                poseStack.mulPose(Axis.ZP.rotationDegrees(invert*-9.785f))
                val timeHeld=itemStack.getUseDuration(player)-(player.useItemRemainingTicks-frameInterp+1.0f)
                var power=timeHeld/10.0f
                if(power>1.0f) {
                    power=1.0f
                }
                
                if(power>0.1f) {
                    val shakeOffset=Mth.sin(((timeHeld-0.1f)*1.3f).toDouble())
                    val shakeIntensity=power-0.1f
                    val shake=shakeOffset*shakeIntensity
                    poseStack.translate(shake*0.0f,shake*0.004f,shake*0.0f)
                }
                
                poseStack.translate(0.0f,0.0f,power*0.2f)
                poseStack.scale(1.0f,1.0f,1.0f+power*0.2f)
                poseStack.mulPose(Axis.YN.rotationDegrees(invert*45.0f))
            }
            
            ItemUseAnimation.BRUSH -> {
                poseStack.translate(invert*-0.56f,0.52f+HEIGHT*0.6f,0.72f)
                fallback.applyBrushTransform(poseStack,frameInterp,arm,player)
            }
            
            ItemUseAnimation.SPEAR -> {
                poseStack.translate(invert*0.56f,-0.52f,-0.72f)
                val timeHeld=itemStack.getUseDuration(player)-(player.useItemRemainingTicks-frameInterp+1.0f)
                SpearAnimations.firstPersonUse(player.getTicksSinceLastKineticHitFeedback(frameInterp),poseStack,timeHeld,arm,itemStack)
            }
            
            else-> {}
        }
    }
    private fun renderItem(mob:LivingEntity,itemStack:ItemStack,type:ItemDisplayContext,poseStack:PoseStack,submitNodeCollector:SubmitNodeCollector,lightCoords:Int) {
        val renderState=ItemStackRenderState()
        fallback.itemModelResolver.updateForTopItem(renderState,itemStack,type,mob.level(),mob,mob.id+type.ordinal)
        renderState.submit(poseStack,submitNodeCollector,lightCoords,OverlayTexture.NO_OVERLAY,0)
    }
}