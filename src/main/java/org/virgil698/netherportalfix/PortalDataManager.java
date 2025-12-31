package org.virgil698.netherportalfix;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理传送门数据的存储
 * 使用内存缓存 + Bukkit PDC 持久化
 * 线程安全设计，适用于 Folia 环境
 */
public class PortalDataManager {

    private static final int MAX_PORTAL_DISTANCE_SQ = 16;

    // 内存缓存：playerUUID -> (fromDim:fromPosLong -> toPosLong)
    private final Map<UUID, Map<String, Long>> portalCache = new ConcurrentHashMap<>();

    private final NetherPortalFixPlugin plugin;
    private final NamespacedKey portalDataKey;

    public PortalDataManager(NetherPortalFixPlugin plugin) {
        this.plugin = plugin;
        this.portalDataKey = new NamespacedKey(plugin, "portal_data");
    }

    /**
     * 存储返回传送门
     */
    public void storeReturnPortal(UUID playerUuid, String fromDim, long fromPosLong, long toPosLong) {
        Map<String, Long> playerPortals = portalCache.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>());

        // 移除距离过近的旧记录
        String keyPrefix = fromDim + ":";
        playerPortals.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(keyPrefix)) {
                long existingFromPos = Long.parseLong(entry.getKey().substring(keyPrefix.length()));
                return isNearby(existingFromPos, fromPosLong);
            }
            return false;
        });

        // 存储新记录
        String key = fromDim + ":" + fromPosLong;
        playerPortals.put(key, toPosLong);

        // 异步保存到 PDC
        saveToPlayerAsync(playerUuid);
    }

    /**
     * 查找返回传送门
     */
    public Long findReturnPortal(UUID playerUuid, String fromDim, long fromPosLong) {
        Map<String, Long> playerPortals = portalCache.get(playerUuid);
        if (playerPortals == null) {
            return null;
        }

        String keyPrefix = fromDim + ":";
        for (Map.Entry<String, Long> entry : playerPortals.entrySet()) {
            if (entry.getKey().startsWith(keyPrefix)) {
                long storedFromPos = Long.parseLong(entry.getKey().substring(keyPrefix.length()));
                if (isNearby(storedFromPos, fromPosLong)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 移除返回传送门记录
     */
    public void removeReturnPortal(UUID playerUuid, String portalKey) {
        Map<String, Long> playerPortals = portalCache.get(playerUuid);
        if (playerPortals != null) {
            playerPortals.remove(portalKey);
            saveToPlayerAsync(playerUuid);
        }
    }

    /**
     * 玩家加入时加载数据
     */
    public void loadPlayerData(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        String data = pdc.get(portalDataKey, PersistentDataType.STRING);
        if (data != null && !data.isEmpty()) {
            Map<String, Long> playerPortals = new ConcurrentHashMap<>();
            for (String entry : data.split(";")) {
                if (entry.isEmpty()) continue;
                String[] parts = entry.split("=");
                if (parts.length == 2) {
                    try {
                        playerPortals.put(parts[0], Long.parseLong(parts[1]));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            portalCache.put(player.getUniqueId(), playerPortals);
        }
    }

    /**
     * 玩家退出时保存数据
     */
    public void savePlayerData(Player player) {
        Map<String, Long> playerPortals = portalCache.get(player.getUniqueId());
        if (playerPortals != null && !playerPortals.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Long> entry : playerPortals.entrySet()) {
                if (sb.length() > 0) sb.append(";");
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
            player.getPersistentDataContainer().set(portalDataKey, PersistentDataType.STRING, sb.toString());
        }
    }

    /**
     * 清理玩家缓存
     */
    public void clearPlayerCache(UUID playerUuid) {
        portalCache.remove(playerUuid);
    }

    private void saveToPlayerAsync(UUID playerUuid) {
        Player player = plugin.getServer().getPlayer(playerUuid);
        if (player != null && player.isOnline()) {
            // 在 Folia 环境下，需要在玩家所在的区域线程执行
            try {
                // 尝试使用 Folia API
                player.getScheduler().run(plugin, task -> savePlayerData(player), null);
            } catch (NoSuchMethodError e) {
                // 非 Folia 环境，直接执行
                plugin.getServer().getScheduler().runTask(plugin, () -> savePlayerData(player));
            }
        }
    }

    private boolean isNearby(long pos1, long pos2) {
        int x1 = (int) (pos1 >> 38);
        int y1 = (int) ((pos1 << 52) >> 52);
        int z1 = (int) ((pos1 << 26) >> 38);

        int x2 = (int) (pos2 >> 38);
        int y2 = (int) ((pos2 << 52) >> 52);
        int z2 = (int) ((pos2 << 26) >> 38);

        int dx = x1 - x2;
        int dy = y1 - y2;
        int dz = z1 - z2;

        return (dx * dx + dy * dy + dz * dz) <= MAX_PORTAL_DISTANCE_SQ;
    }
}
