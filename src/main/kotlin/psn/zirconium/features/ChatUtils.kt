package psn.zirconium.features

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.clickgui.settings.impl.StringSetting
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.ChatPacketEvent
import com.odtheking.odin.events.WorldEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.ChatManager.hideMessage
import com.odtheking.odin.utils.alert
import com.odtheking.odin.utils.modMessage
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import psn.zirconium.AsyncSave
import psn.zirconium.HasCommands
import psn.zirconium.ZirconiumEntry
import psn.zirconium.utils.RegexMutable
import psn.zirconium.utils.RegexType
import psn.zirconium.zcon

object ChatUtils: AsyncSave, HasCommands, Module(
    name = "Chat Utils",
    description = "Chat Alerts, Chat Hider, Copy Chat, etc",
    category=ZirconiumEntry.ZCON
) {
    private val addName by StringSetting("Name", "", desc="")
    private val addTrigger by StringSetting("Regex","",desc="")
    private val addMessage by StringSetting("Alert (blank for none)","",desc="")
    private val addHide by BooleanSetting("Hide",false,"")
    private val addType by SelectorSetting("Type", "",listOf("Contains","Matches","Regex"),"")
    private val ruleAdd by ActionSetting("Add Rule", "") {
        val type=when(addType) {
            0 -> RegexType.CONTAINS
            1 -> RegexType.MATCHES
            2 -> RegexType.REGEX
            else -> {
                modMessage("§cSomething VERY bad has gone wrong! Tell poison about this!", zcon)
                return@ActionSetting
            }
        }
        addRule(addName,addTrigger,type,addMessage,addHide)
    }
    private val hiders by DropdownSetting("Preset Hiders")
    private val abilityHider by BooleanSetting("Ability damage",false,"").withDependency { hiders }
    private val abilityReg=Regex("^Your [A-Za-z ]+ hit [0-9]+ (enemies|enemy) for [0-9,.]+ damage\\.$")
    private val blocksInWayHider by BooleanSetting("Blocks in the way",false,"").withDependency { hiders }
    private val blocksInWayReg=Regex("^There are blocks in the way!$")
    private val grandmaHider by BooleanSetting("Kill combo",false,"").withDependency { hiders }
    private val grandmaReg=Regex("^\\+[0-9]+ Kill Combo")
    private val grandmaReg2=Regex("^Your Kill Combo has expired! You reached a [0-9]+ Kill Combo!$")
    private val cooldownHider by BooleanSetting("Ability cooldown",false,"").withDependency { hiders }
    private val cooldownReg=Regex("^This ability is on cooldown for [0-9]+s\\.$")
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val savedRules by ListSetting("Saved Rules",mutableListOf<Rule>())
    data class Rule(
        var name:String,
        var trigger:RegexMutable,
        var message:String,
        var hide:Boolean,
        var enabled:Boolean=true,
    )
    fun addRule(name:String, trigger:String, type: RegexType, message:String, hide: Boolean){
        if(name==""){
            modMessage("§cName cannot be blank",zcon)
            return
        }
        for(rule in savedRules){
            if(rule.name==name){
                modMessage("§cChat rule $name is already taken. Use a different name or rename/remove the existing one",zcon)
                return
            }
            if(rule.trigger.regexString==trigger){
                modMessage("§cA rule for $trigger already exists (${rule.name})",zcon)
                return
            }
        }
        savedRules.add(Rule(name,RegexMutable(trigger,type),message,hide))
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
    fun rebindRule(name:String, newType: RegexType, newTrigger:String){
        var found:Rule?=null
        for(alrt in savedRules){
            if(alrt.name==name){
                found=alrt
            }
            if(alrt.trigger.regexString==newTrigger){
                modMessage("§cAn alert for $newTrigger already exists (${alrt.name})",zcon)
                return
            }
        }
        if(found==null){
            modMessage("§cNo alert found For $name",zcon)
            return
        }
        found.trigger.regexType=newType
        found.trigger.regexString=newTrigger
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
    
    private val loadedManips = mutableListOf<Rule>()
    fun saveLoad(){
        config.save()
        load()
    }
    fun load(){
        loadedManips.clear()
        for(alrt in savedRules){
            if(!alrt.enabled)continue
            alrt.trigger.load()
            loadedManips.add(alrt)
        }
    }
    init{
        on<ChatPacketEvent>{
            if(abilityHider&&abilityReg.containsMatchIn(value))hideMessage()
            if(blocksInWayHider&&blocksInWayReg.containsMatchIn(value))hideMessage()
            if(grandmaHider&&(grandmaReg.containsMatchIn(value)||grandmaReg2.containsMatchIn(value)))hideMessage()
            if(cooldownHider&&cooldownReg.containsMatchIn(value))hideMessage()
            for(manip in loadedManips){
                if(manip.trigger.regex.containsMatchIn(value)){
                    if(manip.message!="")alert(manip.message)
                    if(manip.hide)hideMessage()
                }
            }
        }
        on<WorldEvent.Load>{
            load()
        }
    }
    
    private val config=ModuleConfig("ChatUtils.json")
    override fun getConfig():ModuleConfig{ return config }
    override fun buildCommands(dispatcher:CommandDispatcher<FabricClientCommandSource>){
        Commodore("chatrule","rule"){
            runs{
                modMessage("Chat Utils: Chat Rule",zcon)
                modMessage(" | /rule add <name> <contains|matches|regex> <trigger> : Creates a new chat rule for the specified regex","")
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
                    val trigReg=when(regexType.lowercase()){
                        "contains" -> RegexType.CONTAINS
                        "matches" -> RegexType.MATCHES
                        "regex" -> RegexType.REGEX
                        else -> {
                            modMessage("§cInvalid regex type $regexType",zcon)
                            return@runs
                        }
                    }
                    when(type.lowercase()){
                        "alert" -> addRule(name,trigger.string,trigReg,name,false)
                        "hider" -> addRule(name,trigger.string,trigReg,"",true)
                        "blank" -> addRule(name,trigger.string,trigReg,"",false)
                        else -> {
                            modMessage("§cInvalid type $type",zcon)
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
                    val trigReg=when(regexType.lowercase()){
                        "contains" -> RegexType.CONTAINS
                        "matches" -> RegexType.MATCHES
                        "regex" -> RegexType.REGEX
                        else -> {
                            modMessage("§cInvalid regex type $regexType",zcon)
                            return@runs
                        }
                    }
                    rebindRule(name,trigReg,trigger.string)
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