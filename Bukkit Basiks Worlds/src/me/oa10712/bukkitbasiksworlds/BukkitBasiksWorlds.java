package me.oa10712.bukkitbasiksworlds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.oa10712.bukkitbasikscore.functions.setConfig;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Oa10712
 */
public class BukkitBasiksWorlds extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = Bukkit.getServer();
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    File worldsConfigFile = new File(dataFolder.getPath() + File.separator + "bukkitbasiksconfig.yml");
    File worldsDataFile = new File(dataFolder.getPath() + File.separator + "worlddata.yml");
    File gameDataFile;
    private YamlConfiguration worldsConfig;
    private YamlConfiguration worldsData;
    public transient setConfig configset;
    public transient gamefunctions game;
    public transient worldfunctions worldfunct;

    @Override
    public void onDisable() {
        try {
            worldsConfig.load(worldsConfigFile);
            worldsConfig.set("Worlds.Enabled", false);
            worldsConfig.save(worldsConfigFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEnable() {
        this.configset = new setConfig();
        this.game = new gamefunctions();
        this.worldfunct = new worldfunctions();
        worldsConfig = new YamlConfiguration();
        worldsData = new YamlConfiguration();
        try {
            worldsConfig.load(worldsConfigFile);
            if ((worldsConfig.contains("Worlds.Defaults.PVP") == false) || (worldsConfig.getBoolean("Worlds.Default") == true)) {
                worldfunct.setupWorldsConfig();
            }
            if (worldsDataFile.exists() == false) {
                worldfunct.setupWorldsData();
            }
            worldsData.load(worldsDataFile);
            worldsConfig.load(worldsConfigFile);
            worldsConfig.set("Worlds.Enabled", true);
            worldsConfig.save(worldsConfigFile);
            List worlds = worldsConfig.getList("Worlds.Worlds");
            for (int i = 0; i < worlds.size(); i++) {
                Boolean load = worldsData.getBoolean(worlds.get(i) + ".Loaded");
                log.info(worlds.get(i) + " is " + load);
                if (load) {
                    Player player = null;
                    worldfunct.loadWorld((String) worlds.get(i), player);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (cmd.getName().equalsIgnoreCase("createworld")) {
            worldfunct.createWorld(args, player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("loadworld")) {
            worldfunct.loadWorld(args[0], player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("evacuateworld")) {
            worldfunct.evacuateWorld(args, player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("unloadworld")) {
            worldfunct.unloadWorld(args, player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("deleteworld")) {
            worldfunct.deleteWorld(args, player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("copyworld")) {
            worldfunct.copyWorld(args, player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("world")) {
            String argout = null;
            for (int i = 1; i < args.length; i++) {
                if (i == 1) {
                    argout = args[1];
                } else {
                    argout = argout + " " + args[i];
                }
            }
            String[] argouts = argout.split(" ");
            if (args[0].equalsIgnoreCase("create")) {
                worldfunct.createWorld(argouts, player);
            }
            if (args[0].equalsIgnoreCase("load")) {
                worldfunct.loadWorld(argouts[0], player);
            }
            if (args[0].equalsIgnoreCase("unload")) {
                worldfunct.unloadWorld(argouts, player);
            }
            if (args[0].equalsIgnoreCase("evacuate")) {
                worldfunct.evacuateWorld(argouts, player);
            }
            if (args[0].equalsIgnoreCase("delete")) {
                worldfunct.deleteWorld(argouts, player);
            }
            if (args[0].equalsIgnoreCase("copy")) {
                worldfunct.copyWorld(argouts, player);
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("savegame")) {
            game.saveGame(args, player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("loadgame")) {
            game.loadGame(args, player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("team")) {
            game.team(args, player);
            return true;
        }
        return false;
    }

    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        try {
            worldsData.load(worldsDataFile);
            World world = event.getLocation().getWorld();
            Object[] blocked = worldsData.getStringList(world + ".BlockSpawn").toArray();
            for (int i = 0; i < blocked.length; i++) {
                if (event.getCreatureType().getName().equals((String) blocked[i])) {
                    event.getEntity().remove();
                }
                log.info((String) blocked[i]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Functions
}
