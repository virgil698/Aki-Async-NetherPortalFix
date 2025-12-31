package org.virgil698.netherportalfix;

import org.virgil698.netherportalfix.mixin.Bridge;

import java.util.UUID;

public class NetherPortalFixBridge implements Bridge {

    private final NetherPortalFixPlugin plugin;
    private final PortalDataManager dataManager;

    public NetherPortalFixBridge(NetherPortalFixPlugin plugin, PortalDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    @Override
    public void logDebug(String message) {
        plugin.getLogger().fine("[Debug] " + message);
    }

    @Override
    public void logInfo(String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void storeReturnPortal(UUID playerUuid, String fromDim, long fromPosLong, long toPosLong) {
        dataManager.storeReturnPortal(playerUuid, fromDim, fromPosLong, toPosLong);
    }

    @Override
    public Long findReturnPortal(UUID playerUuid, String fromDim, long fromPosLong) {
        return dataManager.findReturnPortal(playerUuid, fromDim, fromPosLong);
    }

    @Override
    public void removeReturnPortal(UUID playerUuid, String portalUid) {
        dataManager.removeReturnPortal(playerUuid, portalUid);
    }
}
