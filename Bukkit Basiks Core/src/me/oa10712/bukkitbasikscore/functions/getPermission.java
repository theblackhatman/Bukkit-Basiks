/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.oa10712.bukkitbasikscore.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Oa10712
 */
public class getPermission {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = Bukkit.getServer();
    private YamlConfiguration basiksConfig;
    private YamlConfiguration userData;
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    File basiksConfigFile = new File(dataFolder.getPath() + File.separator + "bukkitbasiksconfig.yml");
    File userDataFile = new File(dataFolder.getPath() + File.separator + "userData.yml");

    public boolean getPermission(String task) {
        try {
            basiksConfig = new YamlConfiguration();
            basiksConfig.load(basiksConfigFile);
            return basiksConfig.getBoolean("Settings."+task);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(getPermission.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getPermission.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(getPermission.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean getPermission(String task, Player player) {
        return false;
    }
}
