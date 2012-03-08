package me.oa10712.bukkitbasikscore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.oa10712.bukkitbasikscore.functions.getPermission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Oa10712
 */
public class BukkitBasiksCore extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft");
    static Server server = Bukkit.getServer();
    private YamlConfiguration basiksConfig;
    private YamlConfiguration userData;
    public transient getPermission perm;
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    File basiksConfigFile = new File(dataFolder.getPath() + File.separator + "bukkitbasiksconfig.yml");
    File userDataFile = new File(dataFolder.getPath() + File.separator + "userData.yml");

    public File getdata() {
        return dataFolder;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        basiksConfig = new YamlConfiguration();
        if (basiksConfigFile.exists() == false) {
            setupConfig();
        }
        if (userDataFile.exists() == false) {
            try {
                userData = new YamlConfiguration();
                userData.save(userDataFile);
            } catch (IOException ex) {
                Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        server.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void normalLogin(PlayerLoginEvent event) {
        try {
            userData = new YamlConfiguration();
            Player player = event.getPlayer();
            userData.load(userDataFile);
            userData.set("Users." + player.getName() + ".Last Login", System.currentTimeMillis());
            userData.save(userDataFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.LOG) {
            treeDeLog(block);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            spawnFunctions(sender, args);
            //return true;
        }
        if (cmd.getName().equalsIgnoreCase("worldconfig")) {
            if (sender.getName().equalsIgnoreCase("basiks")) {
            } else {
                sender.sendMessage("ACCESS DENIED, THIS IS ONLY ALLOWED FOR PLUGINS");
            }
        }
        if (cmd.getName().equalsIgnoreCase("assasinate")) {
            try {
                assassinate.class.newInstance().assassinate(player, args);
            } catch (InstantiationException ex) {
                Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    //Functions
    public void spawnFunctions(CommandSender sender, String[] args) {
        Player player = null;
        int px = 0;
        int py = 0;
        int pz = 0;
        if (sender instanceof Player) {
            player = (Player) sender;
            px = player.getLocation().getBlockX();
            py = player.getLocation().getBlockY();
            pz = player.getLocation().getBlockZ();
        }
        if (args.length == 0) {
            if (player == null) {
                log.info("This command cannot be run from the console without more arguments");
            } else {
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage("Teleported to spawn");
            }
        } else {
            if (args[0].equalsIgnoreCase("set")) {
                if (args.length == 1) {
                    if (player == null) {
                        log.info("Please supply world and co-ordinates, or run as player");
                    } else {
                        player.getWorld().setSpawnLocation(px, py, pz);
                        player.sendMessage("Spawn for world '" + player.getWorld().getName() + "' set");
                    }
                }
                if (args.length == 5) {
                    server.getWorld(args[1]).setSpawnLocation(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                    log.log(Level.INFO, "Spawn for world ''{0}'' set", player.getWorld().getName());
                } else {
                    player.getWorld().setSpawnLocation(px, py, pz);
                    player.sendMessage("Spawn for world '" + player.getWorld().getName() + "' set");
                }
            }
        }
    }

    private void setupConfig() {
        try {
            String[] extensions = {"Worlds", "Teleports", "Economy", "Fun"};
            basiksConfig = new YamlConfiguration();
            basiksConfig.set("Version", this.getDescription().getVersion());
            basiksConfig.set("Settings.Tree Fall", true);
            basiksConfig.set("Extensions", extensions);
            basiksConfig.save(basiksConfigFile);
            log.info("World config generated");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksCore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void treeDeLog(Block block) {
        block.breakNaturally();
        for (BlockFace face : BlockFace.values()) {
            Block next = block.getRelative(face);
            if (next.getType() == Material.LOG) {
                treeDeLog(next);
            }
        }
    }

    public static class assassinate extends Thread {

        public void assassinate(Player player, String[] args) {
            if (server.getPlayer(args[0]).isOnline()) {
                Player target = server.getPlayer(args[0]);
                //Creature attacker = (Creature) target.getWorld().spawnCreature(target.getWorld().getSpawnLocation(), EntityType.valueOf(args[1].toUpperCase()));
                for (Entity attacker : target.getWorld().getEntities()) {
                    if(attacker instanceof Creature){
                        Creature creature = (Creature) attacker;
                        creature.setTarget(target);
                    }
                }
            }
        }
    }
}
