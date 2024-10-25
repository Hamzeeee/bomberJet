package io.hamza.github.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.hamza.github.main.Main;
import io.hamza.github.utilities.WorldUtilitie;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BomberListener implements org.bukkit.event.Listener {

    private final Main plugin;
    private final HashMap<Player, Location> playerLocationHashMap;
    private final HashMap<Player, Boolean> isWearingElytra;
    private final HashMap<Player, Long> playerLastTimeHashMap;
    private final HashMap<Player, Integer> flyingSpeedHashMap;


    public BomberListener(Main plugin) {
        this.plugin = plugin;
        this.playerLocationHashMap = new HashMap<>();
        this.playerLastTimeHashMap = new HashMap<>();
        this.flyingSpeedHashMap = new HashMap<>();
        this.isWearingElytra = new HashMap<>();
    }

    @EventHandler
    public void playerQuitEven(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerLastTimeHashMap.remove(player);
        flyingSpeedHashMap.remove(player);
        playerLocationHashMap.remove(player);
        isWearingElytra.remove(player);
    }

    @EventHandler
    public void PlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        if (!isWearingElytra.containsKey(event.getPlayer())) {
            isWearingElytra.put(event.getPlayer(), false);
        }

        if (event.getSlotType() == PlayerArmorChangeEvent.SlotType.CHEST && event.getNewItem().equals(ItemStack.of(Material.ELYTRA))) {
            isWearingElytra.put(event.getPlayer(), true);
        } else {
            player.sendMessage("FALSE");
            isWearingElytra.put(event.getPlayer(), false);
        }
    }


    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int debug = 0;

        if (!isWearingElytra.containsKey(player)) {
            isWearingElytra.put(event.getPlayer(), false);
        }

        if (isWearingElytra.get(player) && player.isGliding() && flyingSpeedHashMap.get(player) >= 22) {
            player.getWorld().spawnEntity(player.getLocation(), EntityType.ARROW);
            debug++;
            player.sendMessage(debug + "");
        }
    }

    @EventHandler
    public void checkBlocksPerSecond(PlayerMoveEvent event) {
        WorldUtilitie worldUtilitie = new WorldUtilitie();
        Player player = event.getPlayer();
        Location location = player.getLocation();

        long currentTime = System.currentTimeMillis() + 1000;

        double bps = worldUtilitie.blocksPerSecond(player, location, currentTime,
                playerLocationHashMap, playerLastTimeHashMap, flyingSpeedHashMap);

        if (bps != -1) {
            player.sendMessage(bps + "B/s");
        }
    }

    @EventHandler
    public void ProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.ARROW) {
            Entity entity = event.getEntity();
            entity.getWorld().createExplosion(entity.getLocation(), 10, true, true);
            event.getEntity().remove();
        }
    }


}
