package org.virgil698.netherportalfix.mixin;

import java.util.UUID;

public interface Bridge {
    void logDebug(String message);
    void logInfo(String message);
    
    /**
     * 存储返回传送门数据到玩家持久化存储
     * @param playerUuid 玩家UUID
     * @param fromDim 来源维度
     * @param fromPosLong 来源位置（long编码）
     * @param toPosLong 目标位置（long编码）
     */
    void storeReturnPortal(UUID playerUuid, String fromDim, long fromPosLong, long toPosLong);
    
    /**
     * 查找返回传送门
     * @param playerUuid 玩家UUID
     * @param fromDim 来源维度
     * @param fromPosLong 来源位置（long编码）
     * @return 返回传送门位置（long编码），如果没有找到返回null
     */
    Long findReturnPortal(UUID playerUuid, String fromDim, long fromPosLong);
    
    /**
     * 移除返回传送门记录
     * @param playerUuid 玩家UUID
     * @param portalUid 传送门记录的UUID
     */
    void removeReturnPortal(UUID playerUuid, String portalUid);
}
