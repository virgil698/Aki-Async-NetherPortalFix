package org.virgil698.netherportalfix.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.virgil698.netherportalfix.mixin.BridgeManager;
import org.virgil698.netherportalfix.mixin.ReturnPortal;
import org.virgil698.netherportalfix.mixin.ReturnPortalManager;

import java.util.Optional;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    @ModifyExpressionValue(
            method = "getExitPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/portal/PortalForcer;findClosestPortalPosition(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/border/WorldBorder;I)Ljava/util/Optional;"
            )
    )
    private Optional<BlockPos> modifyExitPortal(Optional<BlockPos> original, ServerLevel level, Entity entity, BlockPos pos, BlockPos exitPos, boolean isNether, WorldBorder worldBorder, int searchRadius, boolean canCreatePortal, int createRadius) {
        if (!(entity instanceof ServerPlayer player)) {
            return original;
        }

        BlockPos fromPos = entity.blockPosition();
        ResourceKey<Level> fromDim = entity.level().dimension();
        ReturnPortal returnPortal = ReturnPortalManager.findReturnPortal(player, fromDim, fromPos);

        if (returnPortal == null) {
            BridgeManager.INSTANCE.logDebug("No return portal found for player " + player.getScoreboardName());
            return original;
        }

        if (!level.getBlockState(returnPortal.getPos()).is(Blocks.NETHER_PORTAL)) {
            BridgeManager.INSTANCE.logInfo("Return portal at " + returnPortal.getPos() + " is no longer valid");
            return original;
        }

        BridgeManager.INSTANCE.logDebug("Redirecting player " + player.getScoreboardName() + " to return portal at " + returnPortal.getPos());
        return Optional.of(returnPortal.getPos());
    }
}
