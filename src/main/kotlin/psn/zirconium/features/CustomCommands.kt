package psn.zirconium.features

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.clickgui.settings.impl.MapSetting
import com.odtheking.odin.clickgui.settings.impl.StringSetting
import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.InputEvent
import com.odtheking.odin.events.LevelEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.sendCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.lwjgl.glfw.GLFW
import psn.zirconium.AsyncSave
import psn.zirconium.HasCommands
import psn.zirconium.ZirconiumEntry
import psn.zirconium.zcon

object CustomCommands: AsyncSave, HasCommands, Module(
    name = "Custom Commands",
    description = "Command Aliases and Command Keybinds",
    category=ZirconiumEntry.ZCON
) {
    private val aliasCmd by StringSetting("Alias","",desc="")
    private val keybindKey by KeybindSetting("Key", GLFW.GLFW_KEY_UNKNOWN)
    private val exec by StringSetting("Runs","",desc="")
    private val aliasAdd by ActionSetting("Add Alias",""){
        addAlias(aliasCmd,exec)
    }
    private val keybindAdd by ActionSetting("Add Keybind",""){
        addKeybind(keybindKey,exec)
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val savedAliases by MapSetting("Saved Aliases",mutableMapOf<String,String>())
    fun addAlias(cmdNew:String, execNew:String){
        if(cmdNew==execNew){
            modMessage("§cCannot bind a command to itself",zcon)
            return
        }
        if(savedAliases[cmdNew]!=null){
            modMessage("§cAlias '$cmdNew' is already bound to /${savedAliases[cmdNew]}!",zcon)
            return
        }
        savedAliases[cmdNew]=execNew
        modMessage("Added alias /$cmdNew for command /${savedAliases[cmdNew]}",zcon)
        saveAliasChanges()
    }
    fun removeAlias(keyRem:String){
        val execRem=savedAliases.remove(keyRem)
        if(execRem==null){
            modMessage("§cNo Alias Found For '$keyRem'",zcon)
            return
        }
        modMessage("Removed alias /$keyRem for command /$execRem",zcon)
        saveAliasChanges()
    }
    fun renameAlias(alias:String,newName:String){
        val execRem=savedAliases.remove(alias)
        if(newName==execRem){
            modMessage("§cCannot bind a command to itself",zcon)
            return
        }
        if(execRem==null){
            modMessage("§cNo Alias Found For '$alias'",zcon)
            return
        }
        savedAliases[newName]=execRem
        modMessage("Renamed alias /$alias to /$newName",zcon)
        saveAliasChanges()
    }
    fun rebindAlias(alias:String,newCmd:String){
        if(alias==newCmd){
            modMessage("§cCannot bind a command to itself",zcon)
            return
        }
        if(savedAliases[alias]==null){ modMessage("Added alias /$alias for command /$newCmd",zcon) }
        else{ modMessage("Rebound alias /$alias to /$newCmd",zcon) }
        savedAliases[alias]=newCmd
        saveAliasChanges()
    }
    fun printAliases(){
        modMessage("Current Aliases:",zcon)
        for(alias in savedAliases){
            modMessage(" | ${alias.key} : /${alias.value}","")
        }
    }
    fun clearAliases(){
        savedAliases.clear()
        saveAliasChanges()
    }
    fun saveAliasChanges(){
        modMessage("Commands will be reloaded when you swap lobbies",zcon)
        config.save()
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val savedKeybinds by MapSetting("Saved Keybinds",mutableMapOf<String,String>())
    private val loadedKeybinds=mutableMapOf<InputConstants.Key,String>()
    fun parseKeybind(str:String): InputConstants.Key?{
        try { return InputConstants.getKey("key.keyboard.$str") } catch(_: Exception){}
        try { return InputConstants.getKey("key.keyboard.left.$str") } catch(_: Exception){}
        try { return InputConstants.getKey(str) } catch(_: Exception){}
        modMessage("Failed to parse key $str",zcon)
        return null
    }
    fun saveLoadKeybinds(){
        config.save()
        loadKeybinds()
    }
    fun loadKeybinds(){
        loadedKeybinds.clear()
        for(bind in savedKeybinds){
            loadedKeybinds[InputConstants.getKey(bind.key)]=bind.value
        }
    }
    fun addKeybind(keyNew:InputConstants.Key, execNew:String){
        if(savedKeybinds[keyNew.name]!=null){
            modMessage("§cKeybind ${keyNew.name} is already bound to /${savedKeybinds[keyNew.name]}!",zcon)
            return
        }
        savedKeybinds[keyNew.name]=execNew
        modMessage("Added keybind ${keyNew.name} for command /$execNew",zcon)
        saveLoadKeybinds()
        unabled()
    }
    fun removeKeybind(key:InputConstants.Key){
        val execRem=savedKeybinds.remove(key.name)
        if(execRem==null){
            modMessage("§cNo keybind found For '${key.name}'",zcon)
            return
        }
        modMessage("Removed keybind '${key.name}' for command /$execRem",zcon)
        saveLoadKeybinds()
        unabled()
    }
    fun rekeyKeybind(key:InputConstants.Key,newName:InputConstants.Key){
        val execRem=savedKeybinds.remove(key.name)
        if(execRem==null){
            modMessage("§cNo keybind Found For '$key'",zcon)
            return
        }
        savedKeybinds[newName.name]=execRem
        modMessage("Changed key '$key' to '$newName' for command /${savedKeybinds[newName.name]}",zcon)
        saveLoadKeybinds()
        unabled()
    }
    fun rebindKeybind(keyNew:InputConstants.Key, execNew:String){
        if(savedKeybinds[keyNew.name]!=null){modMessage("Rebound keybind ${keyNew.name} to /$execNew",zcon)}
        else{modMessage("Added keybind ${keyNew.name} for command /$execNew",zcon)}
        savedKeybinds[keyNew.name]=execNew
        saveLoadKeybinds()
        unabled()
    }
    fun printKeybinds(){
        modMessage("Current Keybinds:",zcon)
        for(bind in loadedKeybinds){
            modMessage(" | '${bind.key.name}' : /${bind.value}","")
        }
        unabled()
    }
    fun clearKeybinds(){
        savedKeybinds.clear()
        saveLoadKeybinds()
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private var timeout=false
    init{
        on<InputEvent>{
            if(timeout)return@on
            for(bind in loadedKeybinds){
                if(bind.key.value==key.value){
                    timeout=true
                    sendCommand(bind.value)
                    schedule(5,true){timeout=false}
                    return@on
                }
            }
        }
        on<LevelEvent.Load>{
            timeout=false
            loadKeybinds()
        }
    }
    
    private val config=ModuleConfig("CustomCommands.json")
    override fun getConfig(): ModuleConfig {
        return config
    }
    override fun buildCommands(dispatcher:CommandDispatcher<FabricClientCommandSource>){
        for(pair in savedAliases){
            Commodore(pair.key){
                runs{
                    sendCommand(pair.value)
                }
            }.register(dispatcher)
        }
        Commodore("alias"){
            runs{
                modMessage("Custom Commands: Aliases",zcon)
                modMessage(" | /alias add <alias> <command> : Adds a new alias for the specified command","")
                modMessage(" | /alias rename <alias> <new name> : Changes the alias of the specified alias","")
                modMessage(" | /alias command <alias> <new command> : Changes the command for the specified alias","")
                modMessage(" | /alias list : Lists current aliases","")
                modMessage(" | /alias remove <alias> : Removes the specified alias","")
                modMessage(" | /alias clear : Removes all aliases","")
            }
            literal("add").executable{
                param("alias").suggests {
                    savedAliases.map { i -> i.key }
                }
                param("command")
                runs{alias: String, command: GreedyString ->
                    addAlias(alias,command.string)
                }
            }
            literal("remove").executable{
                param("alias").suggests {
                    savedAliases.map { i -> i.key }
                }
                runs{alias: String ->
                    removeAlias(alias)
                }
            }
            literal("clear").executable{runs{clearAliases()}}
            literal("list").executable{runs{printAliases()}}
            literal("rename").executable{
                param("alias").suggests {
                    savedAliases.map { i -> i.key }
                }
                param("newName")
                runs{alias: String, newName: String ->
                    renameAlias(alias,newName)
                }
            }
            literal("command").executable{
                param("alias").suggests {
                    savedAliases.map { i -> i.key }
                }
                param("newCommand")
                runs{alias: String, newCommand: GreedyString ->
                    rebindAlias(alias,newCommand.string)
                }
            }
        }.register(dispatcher)
        Commodore("keybind"){
            runs{
                modMessage("Custom Commands: Keybinds",zcon)
                unabled()
                modMessage(" | /keybind add <key> <command> : Adds a new keybind for the specified command","")
                modMessage(" | /keybind bind <key> <new key> : Changes the key of the specified keybind","")
                modMessage(" | /keybind command <key> <new command> : Changes the command for the specified keybind","")
                modMessage(" | /keybind list : Lists current keybinds","")
                modMessage(" | /keybind remove <key> : Removes the specified keybind","")
                modMessage(" | /keybind clear : Removes all keybinds","")
            }
            literal("add").executable{
                param("key").suggests {
                    savedKeybinds.map { i -> i.key }
                }
                param("command")
                runs{key: String, command: GreedyString ->
                    addKeybind(parseKeybind(key)?:return@runs,command.string)
                }
            }
            literal("remove").executable{
                param("key").suggests {
                    savedKeybinds.map { i -> i.key }
                }
                runs{key: String ->
                    removeKeybind(parseKeybind(key)?:return@runs)
                }
            }
            literal("clear").executable{runs{clearKeybinds()}}
            literal("list").executable{runs{printKeybinds()}}
            literal("bind").executable{
                param("key").suggests {
                    savedKeybinds.map { i -> i.key }
                }
                param("newKey")
                runs{key: String, newKey: String ->
                    rekeyKeybind(parseKeybind(key)?:return@runs,parseKeybind(newKey)?:return@runs)
                }
            }
            literal("rebind").executable{
                param("key").suggests {
                    savedKeybinds.map { i -> i.key }
                }
                param("newCommand")
                runs{key: String, newCommand: GreedyString ->
                    rebindKeybind(parseKeybind(key)?:return@runs,newCommand.string)
                }
            }
        }.register(dispatcher)
    }
    fun unabled() {
        if(!enabled) modMessage("§cCustom Commands Module is disabled, custom keybinds will not function but can be modified","")
    }
}