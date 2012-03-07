/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.oa10712.bukkitbasikscore.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Oa10712
 */
public class setConfig {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = Bukkit.getServer();

    public setConfig() {
    }

    public void worldConfig(HashMap<String, Object> configDefaults, YamlConfiguration config, File configFile) {
        for (String key : configDefaults.keySet()) {
            config.set(key, configDefaults.get(key));
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
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

    public void deleteFolder(File world) {
        if (world.isDirectory()) {
            String files[] = world.list();
            for (String file : files) {
                File srcFile = new File(world, file);
                deleteFolder(srcFile);
            }
        }
            world.delete();
        log.info("Folder "+world.getName()+" deleted");
    }
}
