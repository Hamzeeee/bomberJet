package io.hamza.github.utilities;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class WorldUtility {
    public double blocksPerSecond(Player player, Location location, long timePeriod, HashMap<Player, Location> playerLocationHashMap,
                                  HashMap<Player, Long> playerLastTimeHashMap, HashMap<Player, Integer> flyingSpeedHashMap) {

        if (!(playerLocationHashMap.containsKey(player) && playerLastTimeHashMap.containsKey(player))) {
            playerLastTimeHashMap.put(player, timePeriod);
            playerLocationHashMap.put(player, location);
        }

        if (System.currentTimeMillis() >= playerLastTimeHashMap.get(player)) {
            double distance = location.distance(playerLocationHashMap.get(player));

            playerLastTimeHashMap.put(player, timePeriod);
            playerLocationHashMap.put(player, location);

            flyingSpeedHashMap.put(player, (int) distance);

            return distance;
        }

        return -1;
    }
}
