//package psn.zirconium.mixin;
//import com.odtheking.odin.features.Module;
//import com.odtheking.odin.features.ModuleManager;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import psn.zirconium.features.MiscFeatures;
//
//@Mixin(value = ModuleManager.class, remap = false)
//public class OdinModuleManagerMixin{
//    @Redirect(
//        method = {"registerModules"},
//        at = @At(
//            value = "INVOKE",
//            target = "Lcom/odtheking/odin/features/Module;isDevModule()Z"
//        )
//    )
//    private boolean noDevModules(Module no) {
//        return MiscFeatures.getOdinDevModules();
//    }
//}