package com.github.imhammer.ihnukkitutils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.utils.ClientChainData;

/**
 * @author  ImHammer DEV &lt;undefined@undefined.com&gt;
 * @version 1.0.0
 * @since   1.0.0
 */
public class EventsListener implements Listener
{
    private final IHNukkitUtils plugin;
    private final BiConsumer<Player, String> xuidCallback;
    
    /**
     * 
     * @param plugin the IHNukkitUtils plugin
     * @param xuidCallback function executed to set the Player's xuid 
     */
    public EventsListener(
        IHNukkitUtils plugin,
        BiConsumer<Player, String> xuidCallback
    )
    {
        this.plugin = plugin;
        this.xuidCallback = xuidCallback;
    }

    /**
     * Handling the {@link DataPacketReceiveEvent} event for XUID serving and receiving
     * @param event called event
     */
    @EventHandler
    public void onDataPacketReceiveEvent(DataPacketReceiveEvent event)
    {
        final Gson GSON = new Gson();

        Player player = event.getPlayer();
        DataPacket packet = event.getPacket();

        if (packet instanceof LoginPacket) {
            // Reiniciando a leitura de bits
            packet.setOffset(0);

            //
            int size = packet.getLInt();
            if (size > 52428800) {
                throw new IllegalArgumentException(player.getName() + ": Chain data too big: " + size);
            }

            String xuid = null;

            String data = new String(packet.get(size), StandardCharsets.UTF_8);
            Map<String, List<String>> map = GSON.fromJson(data, new TypeToken<Map<String, List<String>>>() {}.getType());
            if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
            List<String> chains = map.get("chain");
            for (String c : chains) {
                JsonObject chainMap = ClientChainData.decodeToken(c);
                if (chainMap == null) continue;
                if (chainMap.has("extraData")) {
                    JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                    if (extra.has("XUID")) xuid = extra.get("XUID").getAsString();
                    // if (extra.has("displayName")) packet.username = extra.get("displayName").getAsString();
                    // if (extra.has("identity")) packet.clientUUID = UUID.fromString(extra.get("identity").getAsString());
                }
            }

            if (xuid == null || xuid.isBlank() || xuid.isEmpty()) {
                player.kick("XBOX Live auth is required!");
                return;
            }

            this.xuidCallback.accept(player, xuid);
        }
    }
}
