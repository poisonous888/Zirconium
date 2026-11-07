package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.RenderableSetting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import psn.zirconium.ZirconiumEntry
import kotlin.math.abs

object HeldItemRender : Module(
    name = "Held Item Render",
    description = "changes the held item position",
    category = ZirconiumEntry.zconCat
){
    private val positioncat by DropdownSetting("Item Position",desc="")
    var itemX by NumberSetting("x",0.0,-0.5..0.5,0.05,"").withDependency { positioncat }
    var itemY by NumberSetting("y",0.0,-0.5..0.5,0.05,"").withDependency { positioncat }
    var itemZ by NumberSetting("z",0.0,-0.5..0.5,0.05,"").withDependency { positioncat }
    private val rsTrans by ActionSetting("Reset Translation",""){setPos()}.withDependency { positioncat }
    fun setPos(x:Double=0.0,y:Double=0.0,z:Double=0.0){
        itemX=x
        itemY=y
        itemZ=z
    }

    var itemXrot by NumberSetting("x rot",0f,-3.14..3.14,.01,"").withDependency { positioncat }
    var itemYrot by NumberSetting("y rot",0f,-3.14..3.14,.01,"").withDependency { positioncat }
    var itemZrot by NumberSetting("z rot",0f,-3.14..3.14,.01,"").withDependency { positioncat }
    private val rsRot by ActionSetting("Reset Rotation",""){setRot()}.withDependency { positioncat }
    fun setRot(x:Float=0f,y:Float=0f,z:Float=0f){
        itemXrot=x
        itemYrot=y
        itemZrot=z
    }
    
    var sameScale by BooleanSetting("Same Scale",true,"").withDependency { positioncat }
    var itemWidth by NumberSetting("width",1f,-1..5,0.05,"").withDependency { positioncat && !sameScale }
    var itemHeight by NumberSetting("height",1f,-1..5,0.05,"").withDependency { positioncat && !sameScale }
    var itemLength by NumberSetting("length",1f,-1..5,0.05,"").withDependency { positioncat && !sameScale }
    var itemScale by NumberSetting("scale",1f,-1..5,0.05,"").withDependency { positioncat && sameScale}
    private val rsScale by ActionSetting("Reset Scale",""){setScale(sameScale)}.withDependency { positioncat }
    fun setScale(same:Boolean,w:Float=1f,h:Float=1f,l:Float=1f){
        sameScale=same
        if(same)itemScale=w
        else{
            itemWidth=w
            itemHeight=h
            itemLength=l
        }
    }
    
    private val rsPos by ActionSetting("Reset Position",""){
        rsTrans.invoke()
        rsRot.invoke()
        rsScale.invoke()
    }.withDependency { positioncat }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val swingcat by DropdownSetting("Swing",desc="")
    var swingXrot by NumberSetting("Swing X Rot",-80f,-160..0,8,"").withDependency { swingcat }
    var swingYrot by NumberSetting("Swing Y Rot",-20f,-40..0,4,"").withDependency { swingcat }
    var swingZrot by NumberSetting("Swing Z Rot",-20f,-40..0,4,"").withDependency { swingcat }
    var swingOrot by NumberSetting("Swing Offset Rot",-45f,-90..0,5,"").withDependency { swingcat }
    
    var translateSwing by BooleanSetting("Translate Swing",true,"").withDependency { swingcat }
    var swingX by NumberSetting("Swing X",1f,0..4,0.25,"").withDependency { swingcat && translateSwing }
    var swingY by NumberSetting("Swing Y",1f,0..4,0.25,"").withDependency { swingcat && translateSwing }
    var swingZ by NumberSetting("Swing Z",1f,0..4,0.25,"").withDependency { swingcat && translateSwing }
    
    private val rsSwing by ActionSetting("Reset Swing Transform",""){setSwing(translateSwing)}.withDependency { swingcat }
    fun setSwing(
        translate:Boolean,
        xrot:Float=-80f,
        yrot:Float=-20f,
        zrot:Float=-20f,
        orot:Float=-45f,
        x:Float=1f,
        y:Float=1f,
        z:Float=1f
    ){
        translateSwing=translate
        swingXrot=xrot
        swingYrot=yrot
        swingZrot=zrot
        swingOrot=orot
        if(translate){
            swingX=x
            swingY=y
            swingZ=z
        }
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val speedDriftCat by DropdownSetting("Speed And Drift",desc="")
    
    private var customSwingDuration by BooleanSetting("Custom Swing Duration",false,"").withDependency { speedDriftCat }
    private var ignoreHaste by BooleanSetting("Ignore Haste",false,"haste and mining speed effects dont affect swing speed").withDependency { speedDriftCat && customSwingDuration }
    private var swingDurationFast:Int by NumberSetting("Swing Duration",7, 2..20,1,"exactly what it sounds like").withDependency {speedDriftCat&&customSwingDuration&&swingDurationSlow==20}
    private var swingDurationSlow:Int by NumberSetting("Swing Duration (Slow)",20, 20..100,5,"i see you ppl with your slow asf swing speeds").withDependency {speedDriftCat&&customSwingDuration&&swingDurationFast==20}
    
    @JvmStatic val swingDuration get()=if(swingDurationFast==20)swingDurationSlow else swingDurationFast
    
    var driftMult by NumberSetting("Drift Multiplier",.1f,0.0..0.4,0.02,"set to 0 to disable drift").withDependency {speedDriftCat}
    @JvmStatic var driftLag by NumberSetting("Drift Lag",.5f,0.0..1.0,0.02,"").withDependency {speedDriftCat&&driftMult>0}
    
    @JvmStatic var reswingPercent by NumberSetting("Reswing Percentage",0.5, 0..1,0.1,"% that the current swing has to be through before you can start another swing").withDependency {speedDriftCat}
    
    private val rsSpeedDrift by ActionSetting("Reset Speed And Drift",""){
        setSpeedDrift(customSwingDuration)
    }.withDependency { speedDriftCat }
    fun setSpeedDrift(custom:Boolean,dur:Int=7,mult:Float=.1f,lag:Float=.5f,reswing:Double=0.5,haste:Boolean?=null){
        customSwingDuration=custom
        if(dur>20){
            swingDurationFast=20
            swingDurationSlow=dur
        }
        else{
            swingDurationFast=dur
            swingDurationSlow=20
        }
        ignoreHaste=haste?:custom
        driftMult=mult
        driftLag=lag
        reswingPercent=reswing
    }
    
    @JvmStatic val doSwingDur get()=enabled&&customSwingDuration
    @JvmStatic val doHaste get()=enabled&&!ignoreHaste&&customSwingDuration
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val misccat by DropdownSetting("Misc",desc="")
    private var swingWhileUsing by BooleanSetting("Swing While Using",false,"left click while drawing a bow, drinking a potion, etc").withDependency { misccat }
    private var alt3rdEatDrink by BooleanSetting("Eat And Drink 3rd Person",false,"eat and drink now use goat horn 3rd person anim").withDependency { misccat }
    
    @JvmStatic val doUsing get()=enabled&&swingWhileUsing
    @JvmStatic val doDrink3rd get()=enabled&&alt3rdEatDrink

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private val presets by DropdownSetting("Presets",desc="")
    private val vanilla by ActionSetting("Vanilla",""){
        setPos()
        setRot()
        setScale(true)
        setSwing(true)
        setSpeedDrift(false)
        
        swingWhileUsing=false
        alt3rdEatDrink=false
    }.withDependency { presets }
    private val smallPres by ActionSetting("Small",""){
        setPos(0.15,0.1,0.0)
        setRot()
        setScale(true,0.35f)
        setSwing(false)
        setSpeedDrift(true)
        swingWhileUsing=true
        alt3rdEatDrink=false
    }.withDependency { presets }
    private val fastPres by ActionSetting("Fast",""){
        setSwing(false)
        setSpeedDrift(true,2,0f)
    }.withDependency { presets }
    private val slowPres by ActionSetting("Slow",""){
        setSwing(false)
        setSpeedDrift(true,60,.12f,.3f)
    }.withDependency { presets }
    private val illusionPres by ActionSetting("Illusion",""){
        if(sameScale)setScale(false,itemScale,itemScale,itemScale)
        setScale(false,-abs(itemWidth),itemHeight,itemLength)
    }.withDependency { presets }
    private val poisonPres by ActionSetting("Poison",""){
        setPos(0.1,0.1,0.0)
        setRot()
        setScale(true,0.7f)
        setSwing(false,yrot=0f,xrot=-88f)
        setSpeedDrift(true,6)
        swingWhileUsing=true
        alt3rdEatDrink=true
    }.withDependency { presets }
}