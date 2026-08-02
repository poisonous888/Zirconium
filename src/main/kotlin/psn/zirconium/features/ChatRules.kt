package psn.zirconium.features

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.*
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.ChatPacketEvent
import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.ChatManager.hideMessage
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.modMessage
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.protocol.configuration.ClientboundResetChatPacket
import psn.zirconium.AsyncSave
import psn.zirconium.HasCommands
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon

object ChatRules: AsyncSave, HasCommands, Module(
    name = "Chat Rules",
    description = "Chat Alerts and Hider",
    category=ZirconiumEntry.ZCON
) {
    private val addName by StringSetting("Name", "", desc="")
    private val addTrigger by StringSetting("Regex","",desc="")
    private val addType by SelectorSetting("Type", "",listOf("Contains","Matches","Regex"),"")
    private val addHide by BooleanSetting("Hide",false,"")
    private val addMessage by StringSetting("Alert (blank for none)","",desc="")
    private val ruleAdd by ActionSetting("Add Rule", "") {
        addRule(addName,addTrigger,"$addType",addMessage,addHide)
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val presetChatRules by DropdownSetting("Preset Rules")
    private val pickAlert by BooleanSetting("Pickaxe ability alert",false,"").withDependency { presetChatRules }
    private val pickAlertEnd by BooleanSetting("Pickaxe ability done alert",false,"").withDependency { presetChatRules && pickAlert }
    private val pickReg=Regex("(Mining Speed Boost|Pickobulus|Anomalous Desire|Maniac Miner|Gemstone Infusion|Sheer Force) is now available!")
    private val pickEndReg=Regex("Your (Mining Speed Boost|Anomalous Desire|Maniac Miner|Gemstone Infusion|Sheer Force) has expired!")
    private val abilityHider by BooleanSetting("Ability damage hider",false,"").withDependency { presetChatRules }
    private val abilityReg=Regex("^Your [A-Za-z ]+ hit [0-9]+ (enemies|enemy) for [0-9,.]+ damage\\.$")
    private val blocksInWayHider by BooleanSetting("Blocks in the way hider",false,"").withDependency { presetChatRules }
    private val blocksInWayReg=Regex("^There are blocks in the way!$")
    private val grandmaHider by BooleanSetting("Kill combo hider",false,"").withDependency { presetChatRules }
    private val grandmaReg=Regex("(^\\+[0-9]+ Kill Combo)|(^Your Kill Combo has expired! You reached a [0-9]+ Kill Combo!$)")
    private val cooldownHider by BooleanSetting("Ability cooldown hider",false,"").withDependency { presetChatRules }
    private val cooldownReg=Regex("^This ability is on cooldown for [0-9]+s\\.$")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val savedRules by ListSetting("Saved Rules",mutableListOf<Rule>())
    data class Rule(
        var name:String,
        var trigger:String,
        var type:String,
        var message:String,
        var hide:Boolean,
        var enabled:Boolean=true,
    )
    fun addRule(name:String, trigger:String, type: String, message:String, hide: Boolean){
        if(name==""){
            modMessage("§cName cannot be blank",zcon)
            return
        }
        for((curName, curTrig) in savedRules){
            if(curName==name){
                modMessage("§cChat rule $name is already taken. Use a different name or rename/remove the existing one",zcon)
                return
            }
            if(curTrig==trigger){
                modMessage("§cA rule for $trigger already exists ($curName)",zcon)
                return
            }
        }
        savedRules.add(Rule(name,trigger,type,message,hide))
        modMessage("Added rule $name for trigger $trigger${if(message=="")"" else " with message $message"}${if(hide)" that hides messages" else ""}",zcon)
        modMessage("Use /rule message to add/chance an alert message or /rule hide to hide/unhide the message","")
        saveLoad()
        unabled()
    }
    fun removeRule(name:String){
        if(savedRules.removeIf{alrt -> alrt.name==name}){
            modMessage("Removed rule $name",zcon)
            saveLoad()
            unabled()
            return
        }
        modMessage("§cNo rule found For $name",zcon)
    }
    fun renameRule(name:String, newName:String){
        if(newName==""){
            modMessage("§cName cannot be blank",zcon)
            return
        }
        var found:Rule?=null
        for(alrt in savedRules){
            if(alrt.name==name){
                found=alrt
            }
            if(alrt.name==newName){
                modMessage("§cChat rule $name is already taken. Use a different name or rename/remove the existing one",zcon)
                return
            }
        }
        if(found==null){
            modMessage("§cNo rule found For $name",zcon)
            return
        }
        found.name=newName
        modMessage("Renamed rule $name to $newName",zcon)
        unabled()
    }
    fun remessageRule(name:String, message:String){
        var found:Rule?=null
        for(rule in savedRules){
            if(rule.name==name){
                found=rule
            }
        }
        if(found==null){
            modMessage("§cNo rule found For $name",zcon)
            return
        }
        found.message=message
        modMessage("Changed message of rule $name to $message",zcon)
        saveLoad()
        unabled()
    }
    fun rehideRule(name:String, hide:Boolean){
        var found:Rule?=null
        for(rule in savedRules){
            if(rule.name==name){
                found=rule
            }
        }
        if(found==null){
            modMessage("§cNo rule found For $name",zcon)
            return
        }
        if(found.hide==hide){
            modMessage("Rule $name already ${if(hide)"hides" else "dosent hide"} messages",zcon)
            return
        }
        found.hide=hide
        modMessage("Rule $name ${if(hide)"now" else "no longer"} hides messages",zcon)
        saveLoad()
        unabled()
    }
    fun rebindRule(name:String, newType: String, newTrigger:String){
        var found:Rule?=null
        for(alrt in savedRules){
            if(alrt.name==name){
                found=alrt
            }
            if(alrt.trigger==newTrigger){
                modMessage("§cAn alert for $newTrigger already exists (${alrt.name})",zcon)
                return
            }
        }
        if(found==null){
            modMessage("§cNo alert found For $name",zcon)
            return
        }
        found.type=newType
        found.trigger=newTrigger
        modMessage("Rebound $name to $newTrigger",zcon)
        saveLoad()
        unabled()
    }
    fun toggleRule(name: String, state: Boolean?){
        var found:Rule?=null
        for(alrt in savedRules){
            if(alrt.name==name){
                found=alrt
            }
        }
        if(found==null){
            modMessage("§cNo alert found For $name",zcon)
            return
        }
        found.enabled=state?:!found.enabled
        modMessage("${if(found.enabled)"Enabled" else "Disabled"} rule $name",zcon)
        saveLoad()
        unabled()
    }
    fun printRules(){
        if(savedRules.isEmpty()){
            modMessage("Current Rules: None",zcon)
            return
        }
        modMessage("Current Rules:",zcon)
        for(rule in savedRules){
            modMessage(" | '${rule.name}' : ${rule.trigger} -> ${if(rule.message=="") "no alert" else "alert \"${rule.message}\""}${if(rule.hide) ", hide message" else ""}","")
        }
        CustomCommands.unabled()
    }
    fun clearRules(){
        savedRules.clear()
        saveLoad()
        unabled()
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    data class Manip(
        val reg:Regex,
        val hide:Boolean,
        val msg:String?=null,
    )
    private val loadedManips = mutableListOf<Manip>()
    fun saveLoad(){
        config.save()
        load()
    }
    fun load(){
        loadedManips.clear()
        for(rule in savedRules){
            if(!rule.enabled)continue
            val reg = when(rule.type){
                "regex" -> rule.trigger
                "contains" -> stripReg(rule.trigger)
                "matches" -> "^${stripReg(rule.trigger)}$"
                else -> {modMessage("Invalid regex type ${rule.type} with string ${rule.trigger}");""}
            }
            loadedManips.add(Manip(Regex(reg),rule.hide,rule.message.ifBlank{null}))
        }
        if(abilityHider)loadedManips.add(Manip(abilityReg,true))
        if(blocksInWayHider)loadedManips.add(Manip(blocksInWayReg,true))
        if(grandmaHider)loadedManips.add(Manip(grandmaReg,true))
        if(cooldownHider)loadedManips.add(Manip(cooldownReg,true))
        if(pickAlert)loadedManips.add(Manip(pickReg,false,"§4Ability"))
        if(pickAlertEnd)loadedManips.add(Manip(pickEndReg,false,"§aAbility Over"))
    }
    fun stripReg(s:String):String{return s.replace("/[#-.]|[[-^]|[?|{}]/g", "\\$&")}
    init{
        on<ChatPacketEvent>{
            for((reg,hide,msg) in loadedManips){
                if(reg.containsMatchIn(value)){
                    if(hide)hideMessage()
                    else{alert(msg?:"")}
                }
            }
        }
        on<LevelEvent.Load>{
            load()
        }
        onReceive<ClientboundResetChatPacket>{  }
    }
    
    private val config=ModuleConfig("ChatRules.json")
    override fun getConfig():ModuleConfig{ return config }
    override fun buildCommands(dispatcher:CommandDispatcher<FabricClientCommandSource>){
        Commodore("chatrule","rule"){
            runs{
                modMessage("Chat Utils: Chat Rule",zcon)
                modMessage(" | /rule add <name> <alert|hider|blank> <contains|matches|regex> <trigger> : Creates a new chat rule for the specified regex","")
                modMessage(" | /rule message <name> <new message> : Changes the alert message for the specified chat rule, leave blank to remove message","")
                modMessage(" | /rule hide <name> <true|false> : Changes weather the message is hidden","")
                modMessage(" | /rule regex <name> <contains|matches|regex> <new trigger> : Changes the trigger regex of the specified chat rule","")
                modMessage(" | /rule rename <name> <new name> : Changes the trigger regex of the specified chat rule","")
                modMessage(" | /rule toggle <name> <true|false (optional)> : Enables or disables a chat rule","")
                modMessage(" | /rule list : Lists all chat rules","")
                modMessage(" | /rule remove <name> : Removes the specified chat rule","")
                modMessage(" | /rule clear : Removes all chat rules","")
            }
            literal("add").executable{
                param("name")
                param("type").suggests{
                    listOf("alert","hider","blank")
                }
                param("regexType").suggests{
                    listOf("contains","matches","regex")
                }
                param("trigger")
                runs{name:String,type:String,regexType: String,trigger:GreedyString ->
                    if(!regexType.lowercase().equalsOneOf("contains","matches","regex")){
                        modMessage("§cInvalid regex type $regexType",zcon)
                        return@runs
                    }
                    when(type.lowercase()){
                        "alert" -> addRule(name,trigger.string,regexType.lowercase(),name,false)
                        "hider" -> addRule(name,trigger.string,regexType.lowercase(),"",true)
                        "blank" -> addRule(name,trigger.string,regexType.lowercase(),"",false)
                        else -> {
                            modMessage("§cInvalid type $type",zcon)
                            modMessage("","")
                            return@runs
                        }
                    }
                }
            }
            literal("regex").executable{
                param("name").suggests {
                    savedRules.map { i -> i.name }
                }
                param("regexType").suggests{
                    listOf("contains","matches","regex")
                }
                param("trigger")
                runs{name:String,regexType:String,trigger:GreedyString ->
                    if(!regexType.lowercase().equalsOneOf("contains","matches","regex")){
                        modMessage("§cInvalid regex type $regexType",zcon)
                        return@runs
                    }
                    rebindRule(name,regexType,trigger.string)
                }
            }
            literal("message").executable{
                param("name").suggests {
                    savedRules.map { i -> i.name }
                }
                param("newMessage")
                runs{name:String,newMessage:GreedyString ->
                    remessageRule(name,newMessage.string)
                }
            }
            literal("rename").executable{
                param("name").suggests {
                    savedRules.map { i -> i.name }
                }
                param("newName")
                runs{name:String,newName:String ->
                    renameRule(name,newName)
                }
            }
            literal("hide").executable{
                param("name").suggests {
                    savedRules.map { i -> i.name }
                }
                param("hide").suggests{
                    listOf("true","false")
                }
                runs{name:String,hide:Boolean ->
                    rehideRule(name,hide)
                }
            }
            literal("toggle").executable{
                param("name").suggests {
                    savedRules.map { i -> i.name }
                }
                param("state").suggests{
                    listOf("true","false")
                }
                runs{name:String,state:Boolean? ->
                    toggleRule(name,state)
                }
            }
            literal("list").executable{
                runs{
                    printRules()
                }
            }
            literal("remove").executable{
                param("name").suggests {
                    savedRules.map { i -> i.name }
                }
                runs{name:String ->
                    removeRule(name)
                }
            }
            literal("clear").executable{
                runs{
                    clearRules()
                }
            }
        }.register(dispatcher)
    }
    fun unabled(){
        if(!enabled)modMessage("§cChat Utils Module is disabled, custom chat rules will not function but can be modified","")
    }
}