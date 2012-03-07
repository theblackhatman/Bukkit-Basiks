/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.oa10712.bukkitbasiksteleports.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Oa10712
 */
public class teleport {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = Bukkit.getServer();
    private YamlConfiguration userData;
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    File userDataFile = new File(dataFolder.getPath() + File.separator + "userData.yml");

    public teleport() {
    }

    public void teleport(Player player, Object[] args) {
        if (args.length == 1) {
            if (player == null) {
                log.info("This cannot be run from the console, try a differet set of args");
            } else {

                String arg0 = (String) args[0];
                if (arg0.equalsIgnoreCase("home")) {
                    try {
                        userData = new YamlConfiguration();
                        userData.load(userDataFile);
                        Location location = player.getLocation();
                        location.setX(userData.getDouble("Users." + player.getName() + ".Home.X"));
                        location.setY(userData.getDouble("Users." + player.getName() + ".Home.Y"));
                        location.setZ(userData.getDouble("Users." + player.getName() + ".Home.Z"));
                        location.setWorld(server.getWorld(userData.getString("Users." + player.getName() + ".Home.World")));
                        player.teleport(location);
                        player.sendMessage("Teleported Home");
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(teleport.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(teleport.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidConfigurationException ex) {
                        Logger.getLogger(teleport.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    Location location = null;
                    List<World> worlds = server.getWorlds();
                    Player[] players = server.getOnlinePlayers();
                    //portals
                    for (int i = 0; i < worlds.size(); i++) {
                        if (worlds.get(i).getName().equalsIgnoreCase((String) args[0])) {
                            location = server.getWorld((String) args[0]).getSpawnLocation();
                        }
                    }
                    for (int i = 0; i < players.length; i++) {
                        if (players[i].getName().equalsIgnoreCase((String) args[0])) {
                            location = players[i].getLocation();
                        }
                    }
                    if (location == null) {
                        player.sendMessage("Invalid Destination");
                    } else {
                        player.teleport(location);
                        player.sendMessage((String) args[0]);
                    }
                }
            }
        }
        if (args.length == 2) {
            if (player == null) {
                log.info("This cannot be run from the console, try a differet set of args");
            } else {
                String arg0 = (String) args[0];
                if (arg0.equalsIgnoreCase("world")) {
                    Location location = server.getWorld((String) args[1]).getSpawnLocation();
                    player.teleport(location);
                    player.sendMessage((String) args[0]);
                }
                log.info((String) args[0]);
            }
        }
        if (args.length == 5) {
            String arg0 = (String) args[0];
            if (arg0.equalsIgnoreCase("home")) {
                Location location = player.getLocation();
                location.setX(Double.valueOf((String) args[1]));
                location.setY(Double.valueOf((String) args[2]));
                location.setZ(Double.valueOf((String) args[3]));
                location.setWorld(server.getWorld((String) args[4]));
                player.teleport(location);
                player.sendMessage("Teleported Home");
            }
        }
    }
}
