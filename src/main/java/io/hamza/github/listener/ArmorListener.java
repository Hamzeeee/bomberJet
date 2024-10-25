package io.hamza.github.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.hamza.github.main.Main;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private final HashMap<Player, Short> playerTaskId;
    private final HashMap<Player, Location> playerLocationHashMap;
    private final HashMap<Player, Boolean> isWearingElytra;
    private final HashMap<Player, Long> playerLastTimeHashMap;
    private final HashMap<Player, Short> flyingSpeedHashMap;


    public ArmorListener(Main plugin) {
        this.plugin = plugin;
        this.playerTaskId = new HashMap<>();
        this.playerLocationHashMap = new HashMap<>();
        this.playerLastTimeHashMap = new HashMap<>();
        this.flyingSpeedHashMap = new HashMap<>();
        this.isWearingElytra = new HashMap<>();
    }

    @EventHandler
    public void PlayerArmorChangeEvent(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("TEST");

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
        int taskId = 0;

        if (isWearingElytra.get(player) && flyingSpeedHashMap.get(player) <= 10 && player.isGliding()) {
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                player.getWorld().spawnEntity(player.getLocation(), EntityType.ARROW);
            }, 0, 5);

            if (playerTaskId.containsKey(player)) {
                playerTaskId.put(player, (short) taskId);
            }
        } else {
            playerTaskId.remove(player);
        }
    }

    @EventHandler
    public void checkBlocksPerSecond(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location location = player.getLocation();

        long currentTime = System.currentTimeMillis() + 200;

        if (playerLocationHashMap.containsKey(player) && playerLastTimeHashMap.containsKey(player)) {

            if (System.currentTimeMillis() > playerLastTimeHashMap.get(player)) {

                short distance = (short) (location.distance(playerLocationHashMap.get(player)) * 5);

                player.sendMessage(distance + "B/s");
                playerLastTimeHashMap.put(player, currentTime);
                playerLocationHashMap.put(player, location);
                flyingSpeedHashMap.put(player, distance);
            }

        } else {
            playerLocationHashMap.put(player, location);
            playerLastTimeHashMap.put(player, currentTime);
        }
    }

    @EventHandler
    public void ProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.ARROW) {
            event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 50);
            event.getEntity().remove();
        }
    }


}
