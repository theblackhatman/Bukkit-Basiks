/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.oa10712.bukkitbasiksfun.functions;

import me.oa10712.bukkitbasiksfun.BukkitBasiksFun;
import org.bukkit.Server;
import org.bukkit.entity.Player;

/**
 *
 * @author Oa10712
 */
public class shock {

    public transient BukkitBasiksFun fun;
    Server server = fun.getServer();

    public boolean shock(String[] args, Player player) {
        if (args.length == 0) {
            player.getWorld().strikeLightning(player.getLocation());
            return true;
        }
        if (args.length == 1) {
            Player shocked = server.getPlayer(args[0]);
            if (shocked.isOnline()) {
                shocked.getWorld().strikeLightning(shocked.getLocation());
                return true;
            }
        } else {
            if (args[args.length - 1].equalsIgnoreCase("*")) {
                for (Player shocked : server.getOnlinePlayers()) {
                    if (shocked.isOnline()) {
                        shocked.getWorld().strikeLightning(shocked.getLocation());
                        return true;
                    }
                    for (String arg : args) {
                        if (arg.equalsIgnoreCase("-k")) {
                            shocked.setHealth(0);
                        }
                    }
                }
            } else {
                Player shocked = server.getPlayer(args[args.length - 1]);
                if (shocked.isOnline()) {
                    shocked.getWorld().strikeLightning(shocked.getLocation());
                    return true;
                }
                for (String arg : args) {
                    if (arg.equalsIgnoreCase("-k")) {
                        shocked.setHealth(0);
                    }
                }
            }
        }
        return false;
    }
}
