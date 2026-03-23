package psn.zirconium.mixin;
import com.odtheking.odin.config.ModuleConfig;
import com.odtheking.odin.features.Module;
import com.odtheking.odin.features.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.stream.Stream;

/*
@Mixin({ModuleManager.class})
public abstract class OdinModuleManagerMixin {
    @Redirect(
            method = {"registerModules"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/odtheking/odin/features/Module;isDevModule()Z"
            )
    )
    private boolean noDevModules(Module no) {
        return false;
    }
}
*/

@Mixin(value = ModuleManager.class, remap = false)
public class OdinModuleManagerMixin{
    @ModifyVariable(method = "registerModules", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static Module[] separateDevModules(Module[] modules, ModuleConfig config) {
        if(modules.length<2){return modules;}
        Stream<Module> dev=Arrays.stream(modules).filter(Module::isDevModule);
        Stream<Module> out=Arrays.stream(modules).filter(module -> !module.isDevModule());
        ModuleManager modman=ModuleManager.INSTANCE;
        for(Module reregester:dev.toArray(Module[]::new)){
            modman.registerModules(new ModuleConfig(reregester.getName()+".json"),reregester);
        }
        return out.toArray(Module[]::new);
    }
    @Redirect(
        method = {"registerModules"},
        at = @At(
            value = "INVOKE",
            target = "Lcom/odtheking/odin/features/Module;isDevModule()Z"
        )
    )
    private boolean noDevModules(Module no) {
        return false;
    }
}