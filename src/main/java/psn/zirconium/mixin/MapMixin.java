package psn.zirconium.mixin;

import com.odtheking.odin.features.impl.dungeon.map.tile.DungeonRoom;
import com.odtheking.odin.features.impl.dungeon.map.tile.RoomData;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import psn.zirconium.features.CryptAlert;

@Mixin(DungeonRoom.class)
public class MapMixin{
    @Shadow public RoomData getData(){return null;}
    @Inject(method="<init>*", at=@At("HEAD"))
    private void appendCrypt(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir){
        CryptAlert.addCrypts(getData().getCrypts());
    }
}
