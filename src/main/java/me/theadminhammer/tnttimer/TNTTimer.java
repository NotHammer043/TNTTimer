package me.theadminhammer.tnttimer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.Map;

public class TNTTimer extends JavaPlugin implements Listener {

    private final Map<TNTPrimed, Long> tntTimers = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("TNTTimer has been enabled!");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        FileConfiguration config = getConfig();

        config.options().copyDefaults(true);
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TNTPrimed tnt : tntTimers.keySet()) {
                    long timeRemaining = tntTimers.get(tnt) - 50;

                    if (timeRemaining > 0) {
                        tntTimers.put(tnt, timeRemaining);
                        updateTNTName(tnt, timeRemaining, config);
                    } else {
                    }
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    @Override
    public void onDisable() {
        getLogger().info("TNTTimer has been disabled!");
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getEntity();
            long timerValue = (tnt.getFuseTicks() * 50);
            tntTimers.put(tnt, timerValue);
            updateTNTName(tnt, timerValue, getConfig());
        }
    }
    private void updateTNTName(TNTPrimed tnt, long timeRemaining, FileConfiguration config) {
        Location tntLocation = tnt.getLocation();

        int totalMilliseconds = (int) timeRemaining - 1800;
        int seconds = totalMilliseconds / 1000;
        int milliseconds = (totalMilliseconds / 10) % 100;

        String customText = config.getString("text", "{time}.{ms}s");

        ChatColor textColor;
        if (seconds >= 2) {
            textColor = ChatColor.GREEN;
        } else if (seconds >= 1) {
            textColor = ChatColor.YELLOW;
        } else {
            textColor = ChatColor.RED;
        }

        customText = textColor.toString() + ChatColor.BOLD.toString() +
                customText.replace("{time}", String.valueOf(seconds))
                        .replace("{ms}", String.format("%02d", milliseconds));

        tnt.setCustomName(customText);
        tnt.setCustomNameVisible(true);
    }
}
