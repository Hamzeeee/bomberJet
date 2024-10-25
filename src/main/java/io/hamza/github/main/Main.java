package io.hamza.github.main;

import io.hamza.github.listener.ArmorListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ArmorListener(this), this);
    }
}
