package me.joja.potionSMP;

import me.joja.potionSMP.commands.EffectAdminCommand;
import me.joja.potionSMP.commands.WithdrawCommand;
import me.joja.potionSMP.listeners.*;
import me.joja.potionSMP.managers.RecipeManager;
import me.joja.potionSMP.utils.YamlStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class PotionSMP extends JavaPlugin {

    private static PotionSMP instance;

    @Override
    public void onEnable() {
        instance = this;

        // Load storage
        YamlStorage.load();

        // Register commands
        this.getCommand("withdraw").setExecutor(new WithdrawCommand());
        EffectAdminCommand adminCommand = new EffectAdminCommand();
        this.getCommand("effectadmin").setExecutor(adminCommand);
        this.getCommand("effectadmin").setTabCompleter(adminCommand);

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new EffectItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new EffectTokenListener(), this);


        // Register recipes
        RecipeManager.registerRecipes();
    }

    @Override
    public void onDisable() {
        YamlStorage.saveAll();
    }

    public static PotionSMP getInstance() {
        return instance;
    }
}
