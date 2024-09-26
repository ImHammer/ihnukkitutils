package com.github.imhammer.ihnukkitutils;

import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;

/**
 * @author  ImHammer DEV &lt;undefined@undefined.com&gt;
 * @version 1.0.0
 * @since   1.0.0
 */
public class IHNukkitUtils extends PluginBase
{
    /** Mapping all player XUIDs */
    protected final Map<String, String> playersXuid = new HashMap<>();

    @Override
    public void onEnable()
    {
        getLogger().info("IHNukkitUtils has been enabled!");
        getServer().getPluginManager().registerEvents(new EventsListener(
            this,
            this::addPlayerXuid
        ), this);
    }

    /**
     * Saves the XUID of a given {@link Player}
     * 
     * @param player the player for save xuid
     * @param xuid   XUID referring to the player
     */
    protected void addPlayerXuid(Player player, String xuid)
    {
        this.playersXuid.put(player.getName(), xuid);
    }

    /**
     * Gets the xuid of a given Player
     * 
     * @param player the player
     * @return      xuid of player
     */
    public String getPlayerXuid(Player player)
    {
        return this.playersXuid.get(player.getName());
    }
}
