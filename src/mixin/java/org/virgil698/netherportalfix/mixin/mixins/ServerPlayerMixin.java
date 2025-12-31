package org.virgil698.netherportalfix.mixin.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.virgil698.netherportalfix.mixin.BridgeManager;
import org.virgil698.netherportalfix.mixin.ReturnPortalManager;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(
            method = "triggerDimensionChangeTriggers",
            at = @At("HEAD")
    )
    private void onDimensionChange(ServerLevel fromLevel, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ResourceKey<Level> fromDim = fromLevel.dimension();
        ResourceKey<Level> toDim = player.level().dimension();

        // 只处理主世界和地狱之间的传送
        if (!isOverworldNetherTravel(fromDim, toDim)) {
            BridgeManager.INSTANCE.logDebug("Not storing return portal: not overworld-nether travel (" + fromDim.location() + " -> " + toDim.location() + ")");
            return;
        }

        // 获取玩家传送前的位置
        BlockPos lastPos = ((LivingEntityAccessor) player).getLastPos();
        if (lastPos == null) {
            BridgeManager.INSTANCE.logDebug("Not storing return portal: lastPos is null");
            return;
        }

        // 查找传送前位置附近的传送门
        BlockPos fromPortal = ReturnPortalManager.findPortalAt(player, fromDim, lastPos);
        if (fromPortal == null) {
            BridgeManager.INSTANCE.logDebug("Not storing return portal: no portal found at last position " + lastPos);
            return;
        }

        // 存储返回传送门
        BlockPos toPos = player.blockPosition();
        ReturnPortalManager.storeReturnPortal(player, toDim, toPos, fromPortal);
        BridgeManager.INSTANCE.logInfo("Stored return portal for " + player.getScoreboardName() + ": " + toDim.location() + " " + toPos + " -> " + fromPortal);
    }

    private boolean isOverworldNetherTravel(ResourceKey<Level> fromDim, ResourceKey<Level> toDim) {
        return (fromDim == Level.OVERWORLD && toDim == Level.NETHER) ||
               (fromDim == Level.NETHER && toDim == Level.OVERWORLD);
    }
}
