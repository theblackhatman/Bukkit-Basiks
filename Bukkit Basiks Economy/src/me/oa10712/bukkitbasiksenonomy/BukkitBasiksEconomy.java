package me.oa10712.bukkitbasiksenonomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.oa10712.bukkitbasikscore.BukkitBasiksCore;
import me.oa10712.bukkitbasikscore.functions.setConfig;
import me.oa10712.bukkitbasiksenonomy.functions.balance;
import me.oa10712.bukkitbasiksenonomy.functions.convert;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitBasiksEconomy extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft");
    Server server = Bukkit.getServer();
    private YamlConfiguration economyConfig;
    public transient balance bal;
    private YamlConfiguration worth;
    protected setConfig configset;
    File dataFolder = new File(server.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "Bukkit Basiks");
    File economyConfigFile = new File(dataFolder.getPath() + File.separator + "bukkitbasiksconfig.yml");
    File worthFile = new File(dataFolder.getPath() + File.separator + "cost.yml");
    private YamlConfiguration userData;
    File userDataFile = new File(dataFolder.getPath() + File.separator + "userData.yml");

    @Override
    public void onDisable() {
        try {
            economyConfig.load(economyConfigFile);
            economyConfig.set("Economy.Enabled", false);
            economyConfig.save(economyConfigFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEnable() {
        try {
            economyConfig = new YamlConfiguration();
            economyConfig.load(economyConfigFile);
            if ((economyConfig.contains("Economy.Sell/Buy_Rate") == false)) {
                setupeconomyConfig();
            }
            if (!worthFile.exists()) {
                setupworth();
            }
            economyConfig.load(economyConfigFile);
            economyConfig.set("Economy.Enabled", true);
            economyConfig.save(economyConfigFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        }

        server.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (cmd.getName().equalsIgnoreCase("sell")) {
            if (player != null) {
                try {
                    userData = new YamlConfiguration();
                    userData.load(userDataFile);
                    Double curcash = userData.getDouble("Users." + player.getName() + ".Money");
                    Double changecash = Double.valueOf(0);
                    if (args[0].equalsIgnoreCase("hand") || args.length == 0) {
                        ItemStack handitem = player.getItemInHand();
                        if (convert.metadata(handitem.getType().getId())) {
                            changecash = convert.getPrice(handitem.getType().name(), handitem.getDurability());
                            if (changecash != 0.0) {
                                userData.set("Users." + player.getName() + ".Money", curcash + changecash);
                                player.sendMessage("Sold " + String.valueOf(handitem.getAmount()) + " " + handitem.getType().name().toLowerCase()
                                        + "s of type " + String.valueOf(handitem.getDurability()) + " for §6$" + changecash);
                                handitem.setTypeId(0);
                                player.setItemInHand(handitem);
                            } else {
                                player.sendMessage("This item cannot be sold to the server");
                            }
                        } else {
                            changecash = convert.getPrice(handitem.getType().name()) * handitem.getAmount();
                            if (changecash != 0.0) {
                                userData.set("Users." + player.getName() + ".Money", curcash + changecash);
                                player.sendMessage("Sold " + String.valueOf(handitem.getAmount()) + " " + handitem.getType().name().toLowerCase()
                                        + "s for §6$" + changecash);
                                handitem.setTypeId(0);
                                player.setItemInHand(handitem);
                            } else {
                                player.sendMessage("This item cannot be sold to the server");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("all")) {
                        PlayerInventory inv = player.getInventory();
                        double totalchange = 0.0;
                        ItemStack item;
                        for (int i = 0; i < 36; i++) {
                            item = inv.getItem(i);
                            userData.load(userDataFile);
                            if (item.getTypeId() == 0) {
                            } else {
                                if (convert.metadata(item.getType().getId())) {
                                    changecash = convert.getPrice(item.getType().name(), item.getDurability());
                                    if (changecash != 0.0) {
                                        userData.set("Users." + player.getName() + ".Money", curcash + changecash);
                                        item.setTypeId(0);
                                        inv.setItem(i, item);
                                        totalchange = totalchange + changecash;
                                    } else {
                                    }
                                } else {
                                    changecash = convert.getPrice(item.getType().name()) * item.getAmount();
                                    if (changecash != 0.0) {
                                        userData.set("Users." + player.getName() + ".Money", curcash + changecash);
                                        item.setTypeId(0);
                                        inv.setItem(i, item);
                                        totalchange = totalchange + changecash;
                                    } else {
                                    }
                                }
                            }
                            userData.save(userDataFile);
                        }
                        player.sendMessage("Sold all for $" + totalchange);
                    }
                    userData.save(userDataFile);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidConfigurationException ex) {
                    Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                log.info("This cannot be run from the Console");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("balance")) {
            try {
                userData = new YamlConfiguration();
                userData.load(userDataFile);
                player.sendMessage("Current Ballance: §6$" + String.valueOf(userData.getDouble("Users." + player.getName() + ".Money")));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //<editor-fold defaultstate="collapsed" desc="buy">
        if (cmd.getName().equalsIgnoreCase("buy")) {
            try {

                worth = new YamlConfiguration();
                economyConfig = new YamlConfiguration();
                userData = new YamlConfiguration();
                userData.load(userDataFile);
                worth.load(worthFile);
                economyConfig.load(economyConfigFile);
                double curcash = userData.getDouble("Users." + player.getName() + ".Money");
                if (args.length == 2) {
                    double quantityrate = Double.valueOf(args[1]) * economyConfig.getDouble("Economy.Sell/Buy_Rate");
                    double changecash = worth.getDouble("cost." + args[0].toLowerCase()) * quantityrate;
                    if (changecash != 0.0) {
                        if (changecash > curcash) {
                            player.sendMessage("You cannot afford this many of this item");
                        } else {
                            userData.set("Users." + player.getName() + ".Money", curcash - changecash);
                            userData.save(userDataFile);
                            player.sendMessage("Bought " + args[1] + " " + args[0] + "(s) for §6$" + changecash);
                            String[] itemdata = splitItem(args[0]);
                            ItemStack bought;
                            if (itemdata.length == 1) {
                                bought = new ItemStack(Material.getMaterial(args[0].toUpperCase()), Integer.valueOf(args[1]));
                            } else {
                                bought = new ItemStack(Material.getMaterial(itemdata[0].toUpperCase()), Integer.parseInt(args[1]), Short.parseShort(itemdata[1]));
                            }
                            player.getInventory().addItem(bought);
                        }
                    } else {
                        player.sendMessage("This item cannot be bought from the server");
                    }
                } else if (args.length == 1) {
                    double changecash = worth.getDouble("cost." + args[0].toLowerCase()) * economyConfig.getDouble("Economy.Sell/Buy_Rate");
                    if (changecash != 0.0) {
                        if (changecash > curcash) {
                            player.sendMessage("You cannot afford this many of this item");
                        } else {
                            userData.set("Users." + player.getName() + ".Money", curcash - changecash);
                            userData.save(userDataFile);
                            player.sendMessage("Bought 1 " + args[0] + "(s) for §6$" + changecash);
                            String[] itemdata = splitItem(args[0]);
                            ItemStack bought;
                            if (itemdata.length == 1) {
                                bought = new ItemStack(Material.getMaterial(args[0].toUpperCase()), 1);
                            } else {
                                bought = new ItemStack(Material.getMaterial(itemdata[0].toUpperCase()), 1, Short.parseShort(itemdata[1]));
                            }
                            player.getInventory().addItem(bought);
                        }
                    } else {
                        player.sendMessage("This item cannot be bought from the server");
                    }
                }
                return true;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="worth">
        if (cmd.getName().equalsIgnoreCase("worth")) {
            try {
                economyConfig = new YamlConfiguration();
                worth = new YamlConfiguration();
                worth.load(worthFile);
                economyConfig.load(economyConfigFile);
                double price = 0.0;
                String name = null;
                if (args.length == 1) {
                    price = worth.getDouble("cost." + args[0].toLowerCase());
                    name = args[0];
                } else {
                    price = worth.getDouble("cost." + player.getItemInHand().getType().name().toLowerCase());
                    name = player.getItemInHand().getType().name().toLowerCase();
                }
                if (price == 0.0) {
                    player.sendMessage("This item cannot be bought or sold to the server");
                } else {
                    double changecash = price * economyConfig.getDouble("Economy.Sell/Buy_Rate");
                    player.sendMessage("Price of 1 " + name + ": Buying §6$" + changecash + "§f, Selling §6$" + price);
                }
                return true;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //</editor-fold>
        if (cmd.getName().equalsIgnoreCase("pay")) {
            try {
                Double transfer = Double.valueOf(args[1]);
                if (transfer > 0.00) {
                    userData = new YamlConfiguration();
                    userData.load(userDataFile);
                    Double playermoney = userData.getDouble("Users." + player.getName() + ".Money");
                    Player recieve = server.getPlayer(args[0]);
                    if (userData.contains("Users." + args[0] + ".Money")) {
                        Double recievemoney = userData.getDouble("Users." + args + ".Money");
                        userData.set("Users." + args[0] + ".Money", recievemoney + transfer);
                        userData.set("Users." + player.getName() + ".Money", playermoney - transfer);
                        userData.save(userDataFile);
                        player.sendMessage("Paid $" + transfer + " to " + recieve.getName());
                        recieve.sendMessage("Recieved $" + transfer + " from " + player.getName());
                    } else {
                        player.sendMessage("Invalid target");
                    }
                }
                return true;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private void setupeconomyConfig() {
        try {
            economyConfig = new YamlConfiguration();
            economyConfig.load(economyConfigFile);
            economyConfig.set("Economy.Sell/Buy_Rate", 1.5);
            economyConfig.save(economyConfigFile);
            log.info("Economy config generated");


        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setupworth() {
        try {
            worth = new YamlConfiguration();
            worth.load(this.getResource("cost.yml"));
            worth.save(worthFile);
            log.info("Worth file generated");


        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @EventHandler
    public void normalLogin(PlayerLoginEvent event) {
        try {
            userData = new YamlConfiguration();
            Player player = event.getPlayer();
            userData.load(userDataFile);
            if (userData.contains("Users." + player.getName() + ".Money") == false) {
                userData.set("Users." + player.getName() + ".Money", 100.0);
            }
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
    public void playerClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if (block.getType() == Material.SIGN_POST) {
                Sign sign = (Sign) state;

                signAction(event.getPlayer(), sign);
            }
            if (block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) state;
                signAction(event.getPlayer(), sign);
            }

        }
    }

    @EventHandler
    public void signPlace(SignChangeEvent event) {
        event.getPlayer().sendMessage("Sign placed");
        String[] lines = event.getLines();
        String firstline = null;
        //<editor-fold defaultstate="collapsed" desc="heal">
        if (lines[0].equalsIgnoreCase("[heal]")) {
            //Double cost = convertCash(lines[1]);
            try {
                Double.parseDouble(lines[1].replace("$", ""));
                if (lines[2].equalsIgnoreCase("full") || lines[2].equalsIgnoreCase("full heal")) {
                    firstline = "§a" + lines[0];
                } else {
                    firstline = "§4" + lines[0];
                }
            } catch (NumberFormatException nfe) {
                firstline = "§4" + lines[0];
            }/*
            if (cost.isNaN()) {
            firstline = "§4" + lines[0];
            } else {
            if (lines[2].equalsIgnoreCase("full") || lines[2].equalsIgnoreCase("full heal")) {
            firstline = "§a" + lines[0];
            } else {
            firstline = "§4" + lines[0];
            }
            }*/
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="spawnmob">
        } else if (lines[0].equalsIgnoreCase("[spawnmob]")) {
            Double cost = convertCash(lines[3]);
            Double number = Double.valueOf(lines[1]);
            EntityType type = EntityType.valueOf(lines[2].toUpperCase());
            if (cost.isNaN()) {
                firstline = "§4" + lines[0];
            } else {
                if (number.isNaN()) {
                    firstline = "§4" + lines[0];
                } else {
                    if (type.isSpawnable()) {
                        firstline = "§a" + lines[0];
                    } else {
                        firstline = "§4" + lines[0];
                    }
                }
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="scroll">
        } else if (lines[0].equalsIgnoreCase("[scroll]")) {
            firstline = "§k" + "abcdefghijklmno";
            event.setLine(1, firstline);
            event.setLine(2, firstline);
            event.setLine(3, firstline);

            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="trade">
        } else if (lines[0].equalsIgnoreCase("[trade]")) {
            Double cost = convertCash(lines[2]);
            String[] itemdata = lines[1].split(":");
            //int number = Integer.getInteger(itemdata[1]);
            String item = getItem(itemdata[0]);
            Material[] materials = Material.values();
            if (cost.isNaN()) {
                firstline = "§4" + lines[0];
            } else {
                Boolean validitem = false;
                for (int i = 0; i < materials.length; i++) {
                    if (Material.valueOf(item.toUpperCase()).equals(materials[i])) {
                        validitem = true;
                    }
                }
                if (validitem) {
                    event.setLine(1, Material.valueOf(item.toUpperCase()).name() + ":");
                    firstline = "§a" + lines[0];
                    event.setLine(3, event.getPlayer().getDisplayName());
                } else {
                    firstline = "§4" + lines[0];
                }
            }
        }
        //</editor-fold>
        event.setLine(0, firstline);
    }

    private String removeSpace(String name) {
        String[] seperate = name.split("_");
        String temp1 = null;
        for (int i = 0; i < seperate.length; i++) {
            if (i == 0) {
                temp1 = seperate[i];
            } else {
                temp1 = temp1 + seperate[i];
            }
        }
        return temp1;
    }

    private String[] splitItem(String string) {
        String[] splitted = string.split(":");
        return splitted;
    }

    private void signAction(Player player, Sign sign) {
        try {
            String[] text = sign.getLines();
            userData = new YamlConfiguration();
            userData.load(userDataFile);
            double curcash = userData.getDouble("Users." + player.getName() + ".Money");
            double changecash = 0.0;
            if (text[0].equalsIgnoreCase("[heal]") || text[0].equalsIgnoreCase("§a[heal]")) {
                changecash = convertCash(text[1]);
                if (changecash > curcash) {
                    player.sendMessage("You cannot afford this");
                } else {
                    if (text[2].equalsIgnoreCase("full") || text[2].equalsIgnoreCase("full heal")) {
                        player.setHealth(20);
                        userData.set("Users." + player.getName() + ".Money", curcash - changecash);
                        player.sendMessage("Healed for §6$" + changecash);
                    } else {
                        player.setHealth(player.getHealth() + Integer.parseInt(text[2]));
                        userData.set("Users." + player.getName() + ".Money", curcash - changecash);
                        player.sendMessage("Healed for §6$" + changecash);
                    }
                }
                userData.save(userDataFile);
            }
            if (text[0].equalsIgnoreCase("[spawnmob]") || text[0].equalsIgnoreCase("§a[spawnmob]")) {
                changecash = convertCash(text[3]);
                if (changecash > curcash) {
                    player.sendMessage("You cannot afford this");
                } else {
                    Location spawnlocat = sign.getBlock().getLocation();
                    EntityType et = EntityType.valueOf(text[2].toUpperCase());
                    for (int i = 0; i < Integer.parseInt(text[1]); i++) {
                        player.getWorld().spawnCreature(spawnlocat, et);
                    }
                    userData.set("Users." + player.getName() + ".Money", curcash - changecash);
                    userData.save(userDataFile);
                    player.sendMessage("Spawned " + text[1] + " " + text[2] + "(s) for §6$" + changecash);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(BukkitBasiksEconomy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private double convertCash(String string) {
        if (string.contains(".")) {
            return Double.valueOf(string.replace("$", ""));
        } else {
            return Double.valueOf(string.replace("$", "") + ".00");
        }
    }

    private String getItem(String string) {
        String item = null;
        if (Double.valueOf(string).isNaN()) {
            item = string.toUpperCase();
        } else {
            int itemnum = Integer.getInteger(string);
            item = Material.getMaterial(itemnum).name().toLowerCase();
        }
        return item;
    }
}
