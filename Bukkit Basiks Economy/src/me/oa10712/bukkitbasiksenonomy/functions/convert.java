/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.oa10712.bukkitbasiksenonomy.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Oa10712
 */
public class convert {
    
    static final Logger log = Logger.getLogger("Minecraft");
    static Server server = Bukkit.getServer();
    private static YamlConfiguration worth;
    static File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    static File worthFile = new File(dataFolder.getPath() + File.separator + "cost.yml");
    
    public static String convertID(int string) {
        switch (string) {
            case 6:
                return "sapling";
        }
        return null;
    }
    
    public static boolean metadata(int string) {
        switch (string) {
            case 6: return true;
            case 43: return true;
            case 263: return true;
            case 351: return true;
        }
        return false;
    }
    
    public static double getPrice(String item) {
        try {
            worth = new YamlConfiguration();
            worth.load(worthFile);
            return worth.getDouble("cost." + item.toLowerCase());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(convert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(convert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(convert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public static double getPrice(String item, int damage) {
        try {
            worth = new YamlConfiguration();
            worth.load(worthFile);
            return worth.getDouble("cost." + item.toLowerCase() + "." + String.valueOf(damage));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(convert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(convert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(convert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
