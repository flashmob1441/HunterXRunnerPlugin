package org.flashmob.hunterXHunterPlugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.flashmob.hunterXHunterPlugin.commands.*;
import org.flashmob.hunterXHunterPlugin.events.*;
import org.flashmob.hunterXHunterPlugin.managers.RoleManager;

import java.util.Objects;

public final class HunterXRunnerPlugin extends JavaPlugin {

    private final RoleManager roleManager = new RoleManager();

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        SetRole setRole = new SetRole(roleManager);
        ListRole listRole = new ListRole(roleManager);

        Objects.requireNonNull(getCommand("setrole")).setExecutor(setRole);
        Objects.requireNonNull(getCommand("setrole")).setTabCompleter(setRole);

        Objects.requireNonNull(getCommand("listrole")).setExecutor(listRole);
        Objects.requireNonNull(getCommand("listrole")).setTabCompleter(listRole);

        Objects.requireNonNull(getCommand("devstart")).setExecutor(new DevStartGame());
        Objects.requireNonNull(getCommand("suicide")).setExecutor(new Suicide(roleManager));
        Objects.requireNonNull(getCommand("moveto")).setExecutor(new MoveTo(roleManager, this));
        Objects.requireNonNull(getCommand("getcompass")).setExecutor(new GetCompassCommand(this));
        Objects.requireNonNull(getCommand("startgame")).setExecutor(new StartGame(roleManager, this));
        Objects.requireNonNull(getCommand("hunterchat")).setExecutor(new HunterChat(roleManager));

        getServer().getPluginManager().registerEvents(new PlayerExitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(roleManager, this), this);
        getServer().getPluginManager().registerEvents(new CompassInteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new MoveToCooldownResetListener(), this);
        getServer().getPluginManager().registerEvents(new FreezeMoveListener(roleManager), this);
        getServer().getPluginManager().registerEvents(new RunnerPortalListener(roleManager), this);
        getServer().getPluginManager().registerEvents(new RoleAssignmentListener(roleManager), this);
        getServer().getPluginManager().registerEvents(new RunnerWinListener(roleManager), this);
        getServer().getPluginManager().registerEvents(new GameLogicListener(roleManager, this), this);

        getLogger().info("HunterXRunner started");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
