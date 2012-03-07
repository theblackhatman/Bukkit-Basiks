/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.oa10712.bukkitbasiksfun;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.oa10712.bukkitbasikscore.BukkitBasiksCore;
import me.oa10712.bukkitbasiksfun.functions.shock;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


/**
 *
 * @author Oa10712
 */
public class BukkitBasiksFun extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = this.getServer();
    private YamlConfiguration funConfig;
    public transient BukkitBasiksCore core;
    File datafolder = core.getdata();
    File funConfigFile = new File(datafolder.getPath() + File.separator + "bukkitbasiksconfig.yml");
    public transient shock shock;

    @Override
    public void onDisable() {
        try {
            funConfig = new YamlConfiguration();
            funConfig.load(funConfigFile);
            funConfig.set("Fun.Enabled", false);
            funConfig.save(funConfigFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksFun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksFun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksFun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEnable() {
        try {
            funConfig = new YamlConfiguration();
            funConfig.load(funConfigFile);
            funConfig.set("Fun.Enabled", true);
            funConfig.save(funConfigFile);
            server.getPluginManager().registerEvents(this, this);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksFun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksFun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksFun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (cmd.getName().equalsIgnoreCase("shock")) {
            shock.shock(args, player);
        }
        return false;
    }
}
