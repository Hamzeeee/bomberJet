package io.hamza.github.main;

import io.hamza.github.listener.BomberListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BomberListener(this), this);
    }
}
