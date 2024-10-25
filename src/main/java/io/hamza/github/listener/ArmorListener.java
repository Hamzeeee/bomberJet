package io.hamza.github.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.hamza.github.main.Main;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArmorListener implements org.bukkit.event.Listener {

    private final Main plugin;
    private final HashMap<Player, Location> playerLocationHashMap;
    private final HashMap<Player, Boolean> isWearingElytra;
    private final HashMap<Player, Long> playerLastTimeHashMap;
    private final HashMap<Player, Short> flyingSpeedHashMap;


    public ArmorListener(Main plugin) {
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

        if (isWearingElytra.get(player) && player.isGliding() && flyingSpeedHashMap.get(player) >= 25) {
            player.getWorld().spawnEntity(player.getLocation(), EntityType.ARROW);
            debug++;
            player.sendMessage(debug + "");
        }
    }

    @EventHandler
    public void checkBlocksPerSecond(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location location = player.getLocation();

        long currentTime = System.currentTimeMillis() + 1000;

        if (playerLocationHashMap.containsKey(player) && playerLastTimeHashMap.containsKey(player)) {

            if (System.currentTimeMillis() >= playerLastTimeHashMap.get(player)) {
                double distance = (location.distance(playerLocationHashMap.get(player)));

                player.sendMessage(distance + "B/s");
                playerLastTimeHashMap.put(player, currentTime);
                playerLocationHashMap.put(player, location);

                flyingSpeedHashMap.put(player, (short) distance);
            }

        } else {
            playerLocationHashMap.put(player, location);
            playerLastTimeHashMap.put(player, currentTime);
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
