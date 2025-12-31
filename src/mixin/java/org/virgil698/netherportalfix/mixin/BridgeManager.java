package org.virgil698.netherportalfix.mixin;

import java.util.UUID;

public class BridgeManager {
    public static final BridgeManager INSTANCE = new BridgeManager();
    private volatile Bridge bridge;

    private BridgeManager() {}

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void logDebug(String message) {
        Bridge b = bridge;
        if (b != null) {
            b.logDebug(message);
        }
    }

    public void logInfo(String message) {
        Bridge b = bridge;
        if (b != null) {
            b.logInfo(message);
        }
    }

    public void storeReturnPortal(UUID playerUuid, String fromDim, long fromPosLong, long toPosLong) {
        Bridge b = bridge;
        if (b != null) {
            b.storeReturnPortal(playerUuid, fromDim, fromPosLong, toPosLong);
        }
    }

    public Long findReturnPortal(UUID playerUuid, String fromDim, long fromPosLong) {
        Bridge b = bridge;
        if (b != null) {
            return b.findReturnPortal(playerUuid, fromDim, fromPosLong);
        }
        return null;
    }

    public void removeReturnPortal(UUID playerUuid, String portalUid) {
        Bridge b = bridge;
        if (b != null) {
            b.removeReturnPortal(playerUuid, portalUid);
        }
    }
}
