package org.virgil698.netherportalfix;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.virgil698.netherportalfix.mixin.BridgeManager;

public final class NetherPortalFixPlugin extends JavaPlugin implements Listener {

    private static NetherPortalFixPlugin instance;
    private PortalDataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;
        dataManager = new PortalDataManager(this);
        BridgeManager.INSTANCE.setBridge(new NetherPortalFixBridge(this, dataManager));

        // 注册事件监听
        getServer().getPluginManager().registerEvents(this, this);

        // 为已在线的玩家加载数据
        getServer().getOnlinePlayers().forEach(dataManager::loadPlayerData);

        getLogger().info("NetherPortalFix has been enabled!");
    }

    @Override
    public void onDisable() {
        // 保存所有在线玩家的数据
        getServer().getOnlinePlayers().forEach(dataManager::savePlayerData);
        getLogger().info("NetherPortalFix has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        dataManager.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dataManager.savePlayerData(event.getPlayer());
        dataManager.clearPlayerCache(event.getPlayer().getUniqueId());
    }

    public static NetherPortalFixPlugin getInstance() {
        return instance;
    }
}
