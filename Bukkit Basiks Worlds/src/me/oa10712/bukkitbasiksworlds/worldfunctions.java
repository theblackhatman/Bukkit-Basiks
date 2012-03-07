package me.oa10712.bukkitbasiksworlds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.oa10712.bukkitbasikscore.functions.setConfig;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class worldfunctions {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = Bukkit.getServer();
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    public transient setConfig configset;
    private YamlConfiguration worldsConfig;
    private YamlConfiguration worldsData;
    File worldsConfigFile = new File(dataFolder.getPath() + File.separator + "bukkitbasiksconfig.yml");
    File worldsDataFile = new File(dataFolder.getPath() + File.separator + "worlddata.yml");

    worldfunctions() {
        this.configset = new setConfig();
        worldsConfig = new YamlConfiguration();
        worldsData = new YamlConfiguration();
    }

    public void createWorld(String[] args, Player player) {
        if (args.length == 0) {
            log.info("Incorrect usage");
            if (player != null) {
                player.sendMessage("Incorrect Usage");
            }
        } else {
            WorldCreator wc = new WorldCreator(args[0]);
            if (args.length == 1) {
                wc.environment(World.Environment.NORMAL);
            }
            if (args.length == 2) {
                wc.environment(World.Environment.valueOf(args[1].toUpperCase()));
            }
            if (args.length == 3) {
                wc.environment(World.Environment.valueOf(args[1].toUpperCase()));
                wc.generator(server.getPluginManager().getPlugin(args[2]).getDescription().getFullName());
            }
            if (wc != null) {
                server.createWorld(wc);

            }
            String back = newWorldData(args);
            if (player != null) {
                player.sendMessage(back);
            }
            log.info(back);
        }
    }

    public void loadWorld(String args, Player player) {
        try {
            worldsData = new YamlConfiguration();
            worldsData.load(worldsDataFile);
            List<World> worlds = server.getWorlds();
            Boolean loaded = false;
            for (int i = 0; i < worlds.size(); i++) {
                if (worlds.get(i).getName().equals(args)) {
                    loaded = true;
                }
            }
            if (loaded) {
                if (player != null) {
                    player.sendMessage("World " + args + " already loaded");
                }
                log.info("World " + args + " already loaded");
            } else {
                World w = null;
                WorldCreator c = new WorldCreator(args);
                Object[] worlddata = loadWorldData(args);
                c.environment(World.Environment.valueOf((String) worlddata[0]));
                if (worlddata[1].equals("")) {
                } else {
                    c.generator((String) worlddata[1]);
                }
                w = c.createWorld();
                w.setDifficulty(Difficulty.valueOf((String) worlddata[3]));
                w.setPVP((Boolean) worlddata[4]);
                if (player != null) {
                    player.sendMessage("World " + args + " loaded");
                }
                log.info("World " + args + " loaded");
                worldsData.set(args + ".Loaded", true);
                worldsData.save(worldsDataFile);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setupWorldsData() {
        List worlds = server.getWorlds();
        int i;
        for (i = 0; i < worlds.size(); i++) {
            World world = server.getWorlds().get(i);
            String[] BlockSpawn = {"None"};
            worldsData.set(world.getName() + ".Enviroment", world.getEnvironment().name());
            worldsData.set(world.getName() + ".Mode", world.getWorldType().getName());
            worldsData.set(world.getName() + ".Difficulty", world.getDifficulty().name());
            worldsData.set(world.getName() + ".PVP", world.getPVP());
            worldsData.set(world.getName() + ".BlockSpawn", BlockSpawn);
            log.info("World data file created");
        }
        try {
            worldsData.save(worldsDataFile);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void evacuateWorld(String[] args, Player player) {
        if (args.length == 1) {
            Location spawn = server.getWorlds().get(0).getSpawnLocation();
            Player[] onlineplayers = server.getOnlinePlayers();
            for (int i = 0; i < onlineplayers.length; i++) {
                if (onlineplayers[i].getWorld().getName().equalsIgnoreCase(args[0].toLowerCase())) {
                    onlineplayers[i].teleport(spawn);
                }
            }
        }
        if (args.length == 2) {
            Location spawn = server.getWorld(args[1]).getSpawnLocation();
            Player[] onlineplayers = server.getOnlinePlayers();
            for (int i = 0; i < onlineplayers.length; i++) {
                if (onlineplayers[i].getWorld().getName().equalsIgnoreCase(args[0].toLowerCase())) {
                    onlineplayers[i].teleport(spawn);
                }
            }
        }
    }

    public void deleteWorld(String[] args, Player player) {
        try {
            File world = server.getWorld(args[0]).getWorldFolder();
            unloadWorld(args, player);
            worldsConfig = new YamlConfiguration();
            worldsConfig.load(worldsConfigFile);
            List worlds = worldsConfig.getList("Worlds.Worlds");
            worlds.remove(args[0]);
            worldsConfig.set("Worlds.Worlds", worlds);
            worldsConfig.save(worldsConfigFile);
            worldsData = new YamlConfiguration();
            worldsData.load(worldsDataFile);
            worldsData.set(args[0], null);
            worldsData.save(worldsDataFile);
            configset.deleteFolder(world);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deleteUID(File worldFolder) {
        File uid = new File(worldFolder.getPath() + File.separator + "uid.dat");
        log.info(uid.getPath());
        uid.delete();
    }

    public void copyWorld(String[] args, Player player) {
        File newlocation = new File(server.getWorldContainer().getPath() + File.separator + args[1]);
        try {
            configset.copyFolder(server.getWorld(args[0]).getWorldFolder(), newlocation);
            deleteUID(newlocation);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void unloadWorld(String[] args, Player player) {
        evacuateWorld(args, player);
        server.unloadWorld(args[0], true);
        if (player != null) {
            player.sendMessage("World " + args[0] + " unloaded");
        }
        log.info("World " + args[0] + " unloaded");
    }

    public void setupWorldsConfig() {
        try {
            String[] BlockSpawn = {"Giant"};
            List<World> worlds = server.getWorlds();
            List list = server.getWorlds();
            list.clear();
            for (int i = 0; i < worlds.size(); i++) {
                list.add(worlds.get(i).getName());
            }
            worldsConfig.load(worldsConfigFile);
            worldsConfig.set("Worlds.Worlds", list);
            worldsConfig.set("Worlds.Defaults.Enviroment", "NORMAL");
            worldsConfig.set("Worlds.Defaults.Mode", "SURVIVAL");
            worldsConfig.set("Worlds.Defaults.Difficulty", "NORMAL");
            worldsConfig.set("Worlds.Defaults.PVP", true);
            worldsConfig.set("Worlds.Defaults.BlockSpawn", BlockSpawn);
            worldsConfig.set("Worlds.Defaults.Game.Type", "Spleef");
            worldsConfig.set("Worlds.Defaults.Game.Teams.Number", 3);
            worldsConfig.set("Worlds.Defaults.Game.MaxPlayers", 30);
            worldsConfig.set("Worlds.Defaults.Game.Teams.Team1.Name", "Blue");
            worldsConfig.set("Worlds.Defaults.Game.Teams.Team1.MaxPlayers", 1);
            worldsConfig.set("Worlds.Defaults.Game.Teams.Team2.Name", "Red");
            worldsConfig.set("Worlds.Defaults.Game.Teams.Team2.MaxPlayers", 1);
            worldsConfig.set("Worlds.Defaults.Game.Teams.Team3.Name", "Spectators");
            worldsConfig.set("Worlds.Defaults.Game.Teams.Team3.MaxPlayers", 28);
            worldsConfig.save(worldsConfigFile);
            log.info("World config generated");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(worldfunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object[] loadWorldData(String world) {
        try {
            worldsData = new YamlConfiguration();
            worldsData.load(worldsDataFile);
            if (worldsData.contains(world + ".Enviroment")) {
                Object[] returnv = {worldsData.getString(world + ".Enviroment"),
                                    worldsData.getString(world + ".Generator"),
                                    worldsData.getString(world + ".Mode"),
                                    worldsData.getString(world + ".Difficulty"),
                                    worldsData.getBoolean(world + ".PVP"),
                                    worldsData.getStringList(world + ".BlockSpawn")};
                return returnv;
            } else {
                log.info("World doesn't exist");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] stringreturn = {" "};
        return stringreturn;
    }

    public String newWorldData(String[] args) {
        World world = server.getWorld(args[0]);
        try {
            worldsData = new YamlConfiguration();
            worldsData.load(worldsDataFile);
            if (!worldsData.contains(world.getName() + ".Enviroment")) {
                if (args.length == 1) {
                    worldsData.set(args[0] + ".Enviroment", "NORMAL");
                    worldsData.set(args[0] + ".Generator", "");
                    worldsData.set(args[0] + ".Mode", "SURVIVAL");
                    worldsData.set(args[0] + ".Difficulty", "NORMAL");
                    worldsData.set(args[0] + ".PVP", true);
                }
                if (args.length == 2) {
                    worldsData.set(args[0] + ".Enviroment", args[1].toUpperCase());
                    worldsData.set(args[0] + ".Generator", "");
                    worldsData.set(args[0] + ".Mode", "SURVIVAL");
                    worldsData.set(args[0] + ".Difficulty", "NORMAL");
                    worldsData.set(args[0] + ".PVP", true);
                }
                if (args.length == 3) {
                    worldsData.set(args[0] + ".Enviroment", args[1].toUpperCase());
                    worldsData.set(args[0] + ".Generator", args[2]);
                    worldsData.set(args[0] + ".Mode", "SURVIVAL");
                    worldsData.set(args[0] + ".Difficulty", "NORMAL");
                    worldsData.set(args[0] + ".PVP", true);
                }
                worldsData.set(args[0] + ".Loaded", true);
                worldsData.save(worldsDataFile);
                worldsConfig = new YamlConfiguration();
                worldsConfig.load(worldsConfigFile);
                List worlds = worldsConfig.getList("Worlds.Worlds");
                worlds.add(world.getName());
                worldsConfig.set("Worlds.Worlds", worlds);
                worldsConfig.save(worldsConfigFile);
                return "World " + world.getName() + " created";
            } else {
                return "World already exists";
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
    }
}
