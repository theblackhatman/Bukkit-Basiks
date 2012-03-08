/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.oa10712.bukkitbasiksmagick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Oa10712
 */
public class BukkitBasiksMagick extends JavaPlugin {

    Logger log = Logger.getLogger("Minecraft");
    static Server server = Bukkit.getServer();
    private YamlConfiguration magickConfig;
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    File magickConfigFile = new File(dataFolder.getPath() + File.separator + "bukkitbasiksconfig.yml");

    @Override
    public void onEnable() {
        try {
            magickConfig = new YamlConfiguration();
            magickConfig.load(magickConfigFile);
            if (magickConfig.contains("Magick.Enabled")) {
            }
            magickConfig.set("Magick.Enabled", true);
            magickConfig.save(magickConfigFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksMagick.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksMagick.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksMagick.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onDisable() {
        try {
            magickConfig = new YamlConfiguration();
            magickConfig.load(magickConfigFile);
            magickConfig.set("Magick.Enabled", false);
            magickConfig.save(magickConfigFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksMagick.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksMagick.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksMagick.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return false;
    }
}
