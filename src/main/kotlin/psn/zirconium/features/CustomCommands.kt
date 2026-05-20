package psn.zirconium.features

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.CommandDispatcher
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.clickgui.settings.impl.MapSetting
import com.odtheking.odin.clickgui.settings.impl.StringSetting
import com.odtheking.odin.events.InputEvent
import com.odtheking.odin.events.WorldEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.sendCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.lwjgl.glfw.GLFW
import psn.zirconium.HasCommands
import psn.zirconium.ZconCategory
import psn.zirconium.zcConfig
import psn.zirconium.zcon

object CustomCommands : HasCommands,Module(
    name = "Custom Commands",
    description = "Command Alias's and Command Keybinds",
    category = ZconCategory.ZCON
) {
    private val aliasCmd by StringSetting("Command","",desc="")
    private val aliasExec by StringSetting("Executes","",desc="")
    private val aliasAdd by ActionSetting("Add Alias",""){
        addAlias(aliasCmd,aliasExec)
    }
    private val keybindKey by KeybindSetting("Key", GLFW.GLFW_KEY_UNKNOWN)
    private val keybindExec by StringSetting("Executes","",desc="")
    private val keybindAdd by ActionSetting("Add Keybind",""){
        addKeybind(keybindKey,keybindExec)
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val savedAliases by MapSetting("Saved Aliases",mutableMapOf<String,String>())
    fun addAlias(cmdNew:String, execNew:String){
        if(savedAliases[cmdNew]!=null){
            modMessage("§cAlias $cmdNew is already bound to ${savedAliases[cmdNew]}!",zcon)
            return
        }
        savedAliases[cmdNew]=execNew
        modMessage("Added alias $cmdNew for command ${savedAliases[cmdNew]}",zcon)
        saveAliasChanges()
    }
    fun removeAlias(keyRem:String){
        val execRem=savedAliases.remove(keyRem)
        if(execRem==null){
            modMessage("§cNo Alias Found For $keyRem",zcon)
            return
        }
        modMessage("Removed alias $keyRem for command $execRem",zcon)
        saveAliasChanges()
    }
    fun renameAlias(alias:String,newName:String){
        val execRem=savedAliases.remove(alias)
        if(execRem==null){
            modMessage("§cNo Alias Found For $alias",zcon)
            return
        }
        savedAliases[newName]=execRem
        modMessage("Renamed alias $alias to $newName",zcon)
        saveAliasChanges()
    }
    fun rebindAlias(alias:String,newCmd:String){
        if(savedAliases[alias]==null){ modMessage("Added alias $alias for command $newCmd",zcon) }
        else{ modMessage("Rebound alias $alias to /$newCmd",zcon) }
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
        zcConfig.save()
        //TODO reregister commands
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val savedKeybinds by MapSetting("Saved Keybinds",mutableMapOf<InputConstants.Key,String>())
    fun parseKeybind(str:String): InputConstants.Key?{
        try {
            return InputConstants.getKey("key.keyboard.$str")
        }
        catch(_: Exception){
            modMessage("Failed to parse key $str",zcon)
            return null
        }
    }
    fun addKeybind(keyNew:InputConstants.Key, execNew:String){
        if(savedKeybinds[keyNew]!=null){
            modMessage("§cKeybind ${keyNew.name} is already bound to ${savedKeybinds[keyNew]}!",zcon)
            return
        }
        savedKeybinds[keyNew]=execNew
        modMessage("Added keybind ${keyNew.name} for command $execNew",zcon)
        zcConfig.save()
    }
    fun removeKeybind(keyRem:InputConstants.Key){
        val execRem=savedKeybinds.remove(keyRem)
        if(execRem==null){
            modMessage("§cNo keybind Found For $key",zcon)
            return
        }
        modMessage("Removed keybind '$key' for command $execRem",zcon)
        zcConfig.save()
    }
    fun rekeyKeybind(key:InputConstants.Key,newName:InputConstants.Key){
        val execRem=savedKeybinds.remove(key)
        if(execRem==null){
            modMessage("§cNo keybind Found For $key",zcon)
            return
        }
        savedKeybinds[newName]=execRem
        modMessage("Changed key '$key' to '$newName' for command ${savedKeybinds[newName]}",zcon)
        zcConfig.save()
    }
    fun rebindKeybind(keyNew:InputConstants.Key, execNew:String){
        if(savedKeybinds[keyNew]!=null){modMessage("Rebound keybind ${keyNew.name} to /$execNew",zcon)}
        else{modMessage("Added keybind ${keyNew.name} for command $execNew",zcon)}
        savedKeybinds[keyNew]=execNew
        zcConfig.save()
    }
    fun printKeybinds(){
        modMessage("Current Keybinds:",zcon)
        for(bind in savedKeybinds){
            modMessage(" | '${bind.key.name}' : /${bind.value}","")
        }
    }
    fun clearKeybinds(){
        savedKeybinds.clear()
        zcConfig.save()
    }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    var timeout=false
    init{
        on<InputEvent>{
            if(timeout)return@on
            for(bind in savedKeybinds){
                if(bind.key.value==key.value){
                    timeout=true
                    sendCommand(bind.value)
                    schedule(5,true){timeout=false}
                    return@on
                }
            }
        }
        on<WorldEvent.Load>{
            timeout=false
        }
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
                modMessage("Custom Commands Module: Aliases","")
                modMessage(" | /alias add <alias> <command> : Adds a new alias for the specified command","")
                modMessage(" | /alias remove <alias> : Removes the specified alias","")
                modMessage(" | /alias clear : Removes all aliases","")
                modMessage(" | /alias list : Lists current aliases","")
                modMessage(" | /alias rename <alias> <new name> : Changes the alias of the specified alias","")
                modMessage(" | /alias rebind <alias> <new command> : Changes the command for the specified alias","")
            }
            literal("add").executable{
                param("alias")
                param("command")
                runs{alias: String, command: GreedyString ->
                    addAlias(alias,command.string)
                }
            }
            literal("remove").executable{
                param("alias")
                runs{alias: String ->
                    removeAlias(alias)
                }
            }
            literal("clear").executable{runs{clearAliases()}}
            literal("list").executable{runs{printAliases()}}
            literal("rename").executable{
                param("alias")
                param("newName")
                runs{alias: String, newName: String ->
                    renameAlias(alias,newName)
                }
            }
            literal("rebind").executable{
                param("alias")
                param("newCommand")
                runs{alias: String, newCommand: GreedyString ->
                    rebindAlias(alias,newCommand.string)
                }
            }
        }.register(dispatcher)
        Commodore("keybind"){
            runs{
                modMessage("Custom Commands Module: Keybinds","")
                if(!enabled)modMessage("§cCustom Commands Module is disabled, custom keybinds will not function but can be modified","")
                modMessage(" | /keybind add <key> <command> : Adds a new keybind for the specified command","")
                modMessage(" | /keybind remove <key> : Removes the specified keybind","")
                modMessage(" | /keybind clear : Removes all keybinds","")
                modMessage(" | /keybind list : Lists current keybinds","")
                modMessage(" | /keybind rekey <key> <new key> : Changes the key of the specified keybind","")
                modMessage(" | /keybind rebind <key> <new command> : Changes the command for the specified keybind","")
            }
            literal("add").executable{
                param("key")
                runs{key: String, command: GreedyString ->
                    addKeybind(parseKeybind(key)?:return@runs,command.string)
                }
            }
            literal("remove").executable{
                param("key")
                runs{key: String ->
                    removeKeybind(parseKeybind(key)?:return@runs)
                }
            }
            literal("clear").executable{runs{clearKeybinds()}}
            literal("list").executable{runs{printKeybinds()}}
            literal("rekey").executable{
                param("key")
                param("newKey")
                runs{key: String, newKey: String ->
                    rekeyKeybind(parseKeybind(key)?:return@runs,parseKeybind(newKey)?:return@runs)
                }
            }
            literal("rebind").executable{
                param("key")
                param("newCommand")
                runs{key: String, newCommand: GreedyString ->
                    rebindKeybind(parseKeybind(key)?:return@runs,newCommand.string)
                }
            }
        }.register(dispatcher)
    }
}