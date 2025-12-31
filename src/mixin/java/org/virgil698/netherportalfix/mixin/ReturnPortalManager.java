package org.virgil698.netherportalfix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalForcer;

public class ReturnPortalManager {

    // 搜索半径：地狱16，主世界128
    private static final int NETHER_PORTAL_SEARCH_RADIUS = 16;
    private static final int OVERWORLD_PORTAL_SEARCH_RADIUS = 128;

    public static BlockPos findPortalAt(Player player, ResourceKey<Level> dim, BlockPos pos) {
        MinecraftServer server = player.level().getServer();
        if (server != null) {
            ServerLevel fromWorld = server.getLevel(dim);
            if (fromWorld != null) {
                PortalForcer portalForcer = fromWorld.getPortalForcer();
                int searchRadius = dim == Level.NETHER ? NETHER_PORTAL_SEARCH_RADIUS : OVERWORLD_PORTAL_SEARCH_RADIUS;
                return portalForcer.findClosestPortalPosition(pos, fromWorld.getWorldBorder(), searchRadius).orElse(null);
            }
        }
        return null;
    }

    public static ReturnPortal findReturnPortal(ServerPlayer player, ResourceKey<Level> fromDim, BlockPos fromPos) {
        Long toPosLong = BridgeManager.INSTANCE.findReturnPortal(
                player.getUUID(),
                fromDim.location().toString(),
                fromPos.asLong()
        );
        if (toPosLong != null) {
            return new ReturnPortal(null, BlockPos.of(toPosLong));
        }
        return null;
    }

    public static void storeReturnPortal(ServerPlayer player, ResourceKey<Level> fromDim, BlockPos fromPos, BlockPos toPos) {
        BridgeManager.INSTANCE.storeReturnPortal(
                player.getUUID(),
                fromDim.location().toString(),
                fromPos.asLong(),
                toPos.asLong()
        );
        BridgeManager.INSTANCE.logDebug("Stored return portal: " + fromDim.location() + " " + fromPos + " -> " + toPos);
    }
}
