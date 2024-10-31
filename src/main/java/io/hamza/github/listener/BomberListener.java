package io.hamza.github.listener;

import io.hamza.github.main.Main;
import io.hamza.github.utilities.BomberJetItem;
import io.hamza.github.utilities.BomberJetRules;
import io.hamza.github.utilities.CreateItemMeta;
import io.hamza.github.utilities.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

import java.util.HashMap;

public class BomberListener implements Listener {

    private final Main plugin;
    private final HashMap<Player, Location> playerLocationHashMap;
    private final HashMap<Player, Long> playerLastTimeHashMap;
    private final HashMap<Player, Integer> flyingSpeedHashMap;

    private final String nukeItemName = "§cNuke";
    private ItemStack nuke;
    private ItemStack elytra;
    private ItemStack snowball;
    private BossBar bar;
    private int speed = 20;
    private int maxSpeed = 40;


    public BomberListener(Main plugin) {
        this.plugin = plugin;
        this.playerLocationHashMap = new HashMap<>();
        this.playerLastTimeHashMap = new HashMap<>();
        this.flyingSpeedHashMap = new HashMap<>();
    }

    // TODO: Add comments for more readability
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CreateItemMeta createItemMeta = new CreateItemMeta();
        bar = Bukkit.createBossBar(
                "0B/s",
                BarColor.WHITE,
                BarStyle.SOLID
        );

        bar.addPlayer(player);

        nuke = BomberJetItem.NUKE_ROCKET.getItemStack();
        elytra = BomberJetItem.ELYTRA.getItemStack();

        player.getInventory().setItem(0, nuke);
        player.getInventory().setChestplate(elytra);
        player.sendMessage("meow!");
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return;

        ItemStack item = event.getCurrentItem();

        if (item != null && item.isSimilar(elytra) || item.isSimilar(nuke)) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void playerQuitEven(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerLastTimeHashMap.remove(player);
        flyingSpeedHashMap.remove(player);
        playerLocationHashMap.remove(player);
    }

    @EventHandler
    public void openBombBay(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.isSimilar(nuke)) {
                player.sendMessage("Meow!");
                throwBomb(player);
            }

        }

    }

    @EventHandler
    public void checkBlocksPerSecond(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!flyingSpeedHashMap.containsKey(player)) {
            flyingSpeedHashMap.put(player, 0);
        }

        WorldUtility worldUtilitie = new WorldUtility();
        Location location = player.getLocation();

        long currentTime = System.currentTimeMillis() + 1000;

        double bps = worldUtilitie.blocksPerSecond(player, location, currentTime,
                playerLocationHashMap, playerLastTimeHashMap, flyingSpeedHashMap);

        if (bps != -1) {
            setBossBar((int) bps);
        }
    }

    // TODO: Check so only particular snowballs/arrows make an explosion
    @EventHandler
    public void throwSnowBall(ProjectileHitEvent event) {
        for (BomberJetItem jetItem : BomberJetItem.values()) {
            if () {

                Entity entity = event.getEntity();
                entity.getWorld().createExplosion(entity.getLocation(), 20, true, true);
                entity.remove();
            }
        }
    }

    public void setBossBar(int blockSpeed) {
        bar.setTitle(blockSpeed + "b/s");
        bar.setColor(BomberJetRules.setBossBarColorRules(blockSpeed));

        try {
            bar.setProgress((double) 1 / maxSpeed * blockSpeed);
        } catch (IllegalArgumentException ignored) {

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

    public boolean isWearingElytra(Player player) {
        return player.getInventory().getChestplate().isSimilar(elytra);
    }

}
