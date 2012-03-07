package me.oa10712.bukkitbasiksworlds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.oa10712.bukkitbasikscore.functions.setConfig;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Oa10712
 */
public class gamefunctions {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = Bukkit.getServer();
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    private transient BukkitBasiksWorlds worlds;
    private transient worldfunctions worldfunct;
    private YamlConfiguration gameData;
    public transient setConfig configset;
    private YamlConfiguration worldsConfig;
    File worldsConfigFile = new File(dataFolder.getPath() + File.separator + "bukkitbasiksconfig.yml");
    File gameDataFile;

    gamefunctions() {
        gameData = new YamlConfiguration();
        worldsConfig = new YamlConfiguration();
    }

    public void team(String[] args, Player player) {
        World world = null;
        List arguments = Arrays.asList(args);
        if (player != null) {
            world = player.getWorld();
        }
        List allworlds = server.getWorlds();
        int i;
        for (i = 0; i < allworlds.size(); i++) {
            if (server.getWorlds().get(i).getName().equalsIgnoreCase(args[0])) {
                world = server.getWorlds().get(i);
                arguments.remove(1);
            }
        }
        if (world != null) {
            String[] arg = (String[]) arguments.toArray();
            if (arg[0].equalsIgnoreCase("rename")) {
                renameTeam(arg, player, world);
            }
        } else {
            log.info("World not found");
        }
    }

    public void loadGame(String[] args, Player player) {
        try {
            if (args.length > 1) {
                if (player != null) {
                    player.sendMessage("To many args");
                }
                log.info("To many args");
            }
            File game = new File(dataFolder.getPath() + File.separator + "games" + File.separator + args[0]);
            File worldfolder = new File(server.getWorldContainer().getPath() + args[0]);
            configset.copyFolder(game, worldfolder);
            worldfunctions.deleteUID(server.getWorld(args[0]).getWorldFolder());
            worldfunct.createWorld(args, player);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksWorlds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveGame(String[] args, Player player) {
        File worldfolder = new File(dataFolder.getPath() + File.separator + "games" + File.separator + args[0]);
        File game = new File(server.getWorld(args[0]).getWorldFolder().getPath());
        gameDataFile = new File(worldfolder.getPath() + File.separator + "gamedata.yml");
        setupGameData(args, gameDataFile);
        try {
            copyFolder(game, worldfolder);
        } catch (IOException ex) {
            log.info("ERROR, ERROR!");
        }
        worldfunctions.deleteUID(game);
        log.info("Game saved, don't forget to /deleteworld the original before loading.");
        if (player != null) {
            player.sendMessage("Game saved, don't forget to /deleteworld the original before loading.");
        }
    }

    public void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
                System.out.println("Directory copied from "
                        + src + "  to " + dest);
            }
            if (!src.exists()) {
                log.info("Folder Doesn't exist!");
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public void setupGameData(String[] args, File datafile) {
        try {
            File gameFolder = new File(dataFolder.getPath() + File.separator + "games" + File.separator + args[0] + File.separator + "gamedata.yml");
            gameData = new YamlConfiguration();
            worldsConfig.load(worldsConfigFile);
            String name = args[0];
            int teams = 0;
            int maxplayers = 0;
            if (args.length == 4) {
                gameData.set("Game.Type", args[1]);
                gameData.set("Game.Teams.Number", args[2]);
                teams = Integer.parseInt(args[2]);
                maxplayers = Integer.parseInt(args[3]);
                gameData.set("Game.MaxPlayers", args[3]);
            } else if (args.length == 3) {
                gameData.set("Game.Type", args[1]);
                gameData.set("Game.Teams.Number", args[2]);
                teams = Integer.parseInt(args[2]);
                maxplayers = worldsConfig.getInt("Extensions.Worlds.Defaults.Game.MaxPlayers");
                gameData.set("Game.MaxPlayers", worldsConfig.getInt("Extensions.Worlds.Defaults.Game.MaxPlayers"));
            } else if (args.length == 2) {
                gameData.set("Game.Type", args[1]);
                gameData.set("Game.Teams.Number", worldsConfig.getInt("Extensions.Worlds.Defaults.Game.Teams.Number"));
                maxplayers = worldsConfig.getInt("Extensions.Worlds.Defaults.Game.MaxPlayers");
                teams = worldsConfig.getInt("Extensions.Worlds.Defaults.Game.Teams");
                gameData.set("Game.MaxPlayers", worldsConfig.getInt("Extensions.Worlds.Defaults.Game.MaxPlayers"));
            } else if (args.length == 1) {
                gameData.set("Game.Type", worldsConfig.getString("Extensions.Worlds.Defaults.Game.Type"));
                gameData.set("Game.Teams.Number", worldsConfig.getInt("Extensions.Worlds.Defaults.Game.Teams.Number"));
                teams = worldsConfig.getInt("Extensions.Worlds.Defaults.Game.Teams");
                maxplayers = worldsConfig.getInt("Extensions.Worlds.Defaults.Game.MaxPlayers");
                gameData.set("Game.MaxPlayers", worldsConfig.getInt("Extensions.Worlds.Defaults.Game.MaxPlayers"));
            }
            for (int i = 1; i <= teams; i++) {
                gameData.set("Teams.Team" + String.valueOf(i) + ".Name", "Team" + String.valueOf(i));
                gameData.set("Teams.Team" + String.valueOf(i) + ".MaxPlayers", String.valueOf(maxplayers / teams));
            }
            gameData.save(datafile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(gamefunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gamefunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(gamefunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void renameTeam(String[] args, Player player, World world) {
        gameData = new YamlConfiguration();
        if (player == null) {
            File dataFile = new File(server.getWorldContainer().getPath() + File.separator + world.getName() + File.separator + "gamedata.yml");
            if (dataFile.exists() != true) {
                log.info("This is not a game world");
            } else {
                try {
                    gameData.load(dataFile);
                    int i = 0;
                    String name = null;
                    for (i = 0; i < gameData.getInt("Game.Teams"); i++) {
                        if (gameData.getConfigurationSection("Teams.Team" + String.valueOf(i)).getName().equalsIgnoreCase(args[1])) {
                            name = gameData.getConfigurationSection("Teams.Team" + String.valueOf(i)).getName();
                        }
                        if (gameData.getString("Teams.Team" + String.valueOf(i) + ".Name").equalsIgnoreCase(args[1])) {
                            name = gameData.getConfigurationSection("Teams.Team" + String.valueOf(i)).getName();
                        }
                    }
                    gameData.set("Teams." + name + ".Name", args[2]);
                    gameData.save(dataFile);
                } catch (Exception ex) {
                    Logger.getLogger(gamefunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void joinTeam(String[] args, Player player) {
        try {
            gameDataFile = new File(player.getWorld().getWorldFolder().getPath() + File.separator + "gamedata.yml");
            gameData.load(gameDataFile);
            String team = args[0];
        } catch (FileNotFoundException ex) {
            Logger.getLogger(gamefunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gamefunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(gamefunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
