package io.hamza.github.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.hamza.github.main.Main;
import io.hamza.github.utilities.CreateItemMeta;
import io.hamza.github.utilities.WorldUtilitie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class BomberListener implements Listener {

    private final Main plugin;
    private final HashMap<Player, Location> playerLocationHashMap;
    private final HashMap<Player, Long> playerLastTimeHashMap;
    private final HashMap<Player, Integer> flyingSpeedHashMap;

    private final String nukeItemName = "§cNuke";
    private ItemStack nuke;
    private ItemStack elytra;
    private BossBar bar;
    private int speed = 20;
    private int maxSpeed = 40;

    private Player player;

    public BomberListener(Main plugin) {
        this.plugin = plugin;
        this.playerLocationHashMap = new HashMap<>();
        this.playerLastTimeHashMap = new HashMap<>();
        this.flyingSpeedHashMap = new HashMap<>();
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        CreateItemMeta createItemMeta = new CreateItemMeta();
        player = event.getPlayer();
        bar = Bukkit.createBossBar(
                 "0B/s",
                BarColor.WHITE,
                BarStyle.SOLID
        );

        bar.addPlayer(player);

        nuke = createItemMeta.createItem(Material.FIREWORK_ROCKET, nukeItemName, List.of("§4Do a 20er Bomb"), true);
        elytra = createItemMeta.createItem(Material.ELYTRA, "§l§eBomber", List.of(""), true);


        player.getInventory().setItem(0, nuke);
        player.getInventory().setChestplate(elytra);
        player.sendMessage("meow!");
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();

        if (item != null && item.isSimilar(elytra) || item.isSimilar(nuke)) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }
    }

    public void setBossBar(int blockSpeed) {
        bar.setTitle(blockSpeed + "b/s");
        bar.setColor(BarColor.GREEN);

        if (blockSpeed >= 18) {
            bar.setColor(BarColor.YELLOW);
        }

        if (blockSpeed >= 33) {
            bar.setColor(BarColor.RED);
        }
        try {
            bar.setProgress((double) blockSpeed / maxSpeed);
        } catch (IllegalArgumentException e) {

        }
    }

    @EventHandler
    public void playerQuitEven(PlayerQuitEvent event) {
        player = event.getPlayer();
        playerLastTimeHashMap.remove(player);
        flyingSpeedHashMap.remove(player);
        playerLocationHashMap.remove(player);
    }

    public boolean isWearingElytra(Player player) {
        if (player.getInventory().getChestplate().isSimilar(elytra)) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void openBombBay(PlayerInteractEvent event) {
        player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.isSimilar(nuke)) {
                player.sendMessage("Meow!");
                throwBomb(player);
            }

        }

    }

    public void throwBomb(Player player) {
        int debug = 0;

        if (isWearingElytra(player) && player.isGliding() && flyingSpeedHashMap.get(player) >= speed) {
            player.getWorld().spawnEntity(player.getLocation(), EntityType.ARROW);
            debug++;
            player.sendMessage(debug + "");
        }
    }


    @EventHandler
    public void checkBlocksPerSecond(PlayerMoveEvent event) {
        if (!flyingSpeedHashMap.containsKey(player)) {
            flyingSpeedHashMap.put(player, 0);
        }

        WorldUtilitie worldUtilitie = new WorldUtilitie();
        player = event.getPlayer();
        Location location = player.getLocation();

        long currentTime = System.currentTimeMillis() + 1000;

        double bps = worldUtilitie.blocksPerSecond(player, location, currentTime,
                playerLocationHashMap, playerLastTimeHashMap, flyingSpeedHashMap);

        if (bps != -1) {
            setBossBar((int) bps);
        }
    }

    @EventHandler
    public void ProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.ARROW) {
            Entity entity = event.getEntity();
            entity.getWorld().createExplosion(entity.getLocation(), 20, true, true);
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void throwSnowBall(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.SNOWBALL) {
            Entity entity = event.getEntity();
            entity.getWorld().createExplosion(entity.getLocation(), 8, true, true);
            event.getEntity().remove();
        }
    }


}
