package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.ScreenEvent
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.customData
import com.odtheking.odin.utils.loreString
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import psn.zirconium.ZirconiumEntry

object GuiHighlight : Module(
    name = "GUI Highlight",
    description = "Highlights Certain GUI Slots",
    category=ZirconiumEntry.ZCON
) {
    private val highlightPet by BooleanSetting("Highlight Active Pet", false, desc = "displays a colored background behind your active pet")
    private val petColor by ColorSetting("Active Pet Color",Colors.MINECRAFT_RED,false,"the highlight color").withDependency { highlightPet }
    private val highlightCommission by BooleanSetting("Highlight Completed Comms", false, desc = "displays a colored background behind completed commissions")
    private val commissionColor by ColorSetting("Commission Color",Colors.MINECRAFT_GREEN,false,"the highlight color").withDependency { highlightCommission }
    private val highlightAnvil by BooleanSetting("Anvil Helper", false, desc = "skytils anvil helper")
    private val blockWrongAnvil by BooleanSetting("Block Incompatible Books", false, desc = "prevents you from combining books with different enchantment levels").withDependency { highlightAnvil }
    private val anvilColor by ColorSetting("Anvil Highlight Color",Colors.MINECRAFT_AQUA,false,"the highlight color").withDependency { highlightAnvil }
    private val petRegex = Regex("Pets(?: \\((\\d)/(\\d)\\))?")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private var curHelper:Any?=null
    init{
        on<ScreenEvent.Open>{
            if(highlightCommission&&screen.title.string=="Commissions"){
                EventBus.subscribe(CommHelper)
                curHelper=CommHelper
            }
            if(highlightAnvil&&screen.title.string=="Anvil"){
                EventBus.subscribe(BookHelper)
                curHelper=BookHelper
            }
            if(highlightPet&&petRegex.containsMatchIn(screen.title.string)){
                EventBus.subscribe(PetHelper)
                curHelper=PetHelper
            }
        }
        on<ScreenEvent.Close>{
            EventBus.unsubscribe(curHelper?:return@on)
            curHelper=null
        }
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private object BookHelper{
        private var blocked = false
        private var book = ""
        private var lvl = -1
        init {
            on<GuiEvent.RenderSlot>{
                if(!slot.item.customData.contains("enchantments")) return@on
                val enchant=slot.item.customData.get("enchantments")?.asCompound()?.get()?.entrySet()?.first()
                val bookCur=enchant?.key.toString()
                val lvlCur=enchant?.value.toString().toInt()
                if(lvlCur==lvl&&bookCur==book){
                    guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, anvilColor.rgba)
                }
                if(slot.index==29){
                    if(slot.item.item==Items.ENCHANTED_BOOK){
                        book=bookCur
                        lvl=lvlCur
                    }
                    else{
                        book=""
                    }
                }
                if(slot.index==33){
                    if(book.isEmpty()){
                        blocked=false
                        guiGraphics.item(ItemStack(Items.ENCHANTED_BOOK),80, 54)
                        return@on
                    }
                    if(slot.item.item!=Items.ENCHANTED_BOOK){
                        blocked=false
                        guiGraphics.item(ItemStack(Items.WRITABLE_BOOK),80, 54)
                        return@on
                    }
                    if(lvl==lvlCur&&book==bookCur){
                        blocked=false
                        guiGraphics.item(ItemStack(Items.END_PORTAL_FRAME),80, 54)
                        return@on
                    }
                    guiGraphics.item(ItemStack(Items.BARRIER),80, 54)
                    //alert("INVALID COMBINE")
                    blocked=true
                }
            }
            on<GuiEvent.SlotClick>{
                if(blocked&&blockWrongAnvil&&slotId==31)cancel()
            }
        }
    }
    
    private object CommHelper{
        init {
            on<GuiEvent.RenderSlot>{
                if(slot.item.loreString.contains("Click to claim rewards!")){
                    guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, commissionColor.rgba)
                }
            }
        }
    }
    
    private object PetHelper{
        init {
            on<GuiEvent.RenderSlot>{
                if(slot.item.loreString.contains("Click to despawn!")){
                    guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, petColor.rgba)
                }
            }
        }
    }
}