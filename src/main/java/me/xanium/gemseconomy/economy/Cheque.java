/*
 * Copyright Xanium Development (c) 2013-2018. All Rights Reserved.
 * Any code contained within this document, and any associated APIs with similar branding
 * are the sole property of Xanium Development. Distribution, reproduction, taking snippets or claiming
 * any contents as your own will break the terms of the license, and void any agreements with you, the third party.
 * Thank you.
 */

package me.xanium.gemseconomy.economy;

import me.xanium.gemseconomy.GemsEconomy;
import me.xanium.gemseconomy.nbt.NBTItem;
import me.xanium.gemseconomy.utils.UtilString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Cheque {

    private static final GemsEconomy plugin = GemsEconomy.getInstance();
    private static ItemStack chequeBaseItem;
    private static String nbt_value = "value";
    private static String nbt_currency = "currency";

    public static void setChequeBase(){
        ItemStack item = new ItemStack(Material.valueOf(plugin.getConfig().getString("cheque.material")), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(UtilString.colorize(plugin.getConfig().getString("cheque.name")));
        meta.setLore(UtilString.colorize(plugin.getConfig().getStringList("cheque.lore")));
        item.setItemMeta(meta);
        chequeBaseItem = item;
    }

    public ItemStack writeCheque(String creatorName, Currency currency, double amount) {
        if (creatorName.equals("CONSOLE")) {
            creatorName = UtilString.colorize(plugin.getConfig().getString("cheque.console_name"));
        }
        List<String> formatLore = new ArrayList<>();

        for (String baseLore2 : chequeBaseItem.getItemMeta().getLore()) {
            formatLore.add(baseLore2.replace("{amount}", String.valueOf(amount)).replace("{currency}", currency.getPlural()).replace("{player}", creatorName));
        }
        ItemStack ret = chequeBaseItem.clone();
        NBTItem nbt = new NBTItem(ret);
        ItemMeta meta = nbt.getItem().getItemMeta();
        meta.setLore(formatLore);
        nbt.getItem().setItemMeta(meta);
        nbt.setString(nbt_currency, currency.getPlural());
        nbt.setString(nbt_value, String.valueOf(amount));
        return nbt.getItem();
    }

    public static boolean isAValidCheque(NBTItem itemstack) {
        if (itemstack.getItem().getType() == chequeBaseItem.getType() && itemstack.getString(nbt_value) != null && itemstack.getString(nbt_currency) != null && itemstack.getItem().getItemMeta().hasLore()) {
            String display = chequeBaseItem.getItemMeta().getDisplayName();
            if(itemstack.getItem().getItemMeta().getDisplayName().equals(display) && itemstack.getItem().getItemMeta().hasLore()){
                return (itemstack.getItem().getItemMeta().getDisplayName().equals(display) && itemstack.getItem().getItemMeta().getLore().size() == chequeBaseItem.getItemMeta().getLore().size());
            }
            return false;
        }
        return false;
    }

    public static double getChequeValue(NBTItem itemstack) {
        if (itemstack.getString(nbt_currency) != null && itemstack.getString(nbt_value) != null) {
            return Double.parseDouble(itemstack.getString(nbt_value));
        }
        return 0;
    }

    public static Currency getChequeCurrency(NBTItem item){
        if (item.getString(nbt_currency) != null && item.getString(nbt_value) != null) {
            return AccountManager.getCurrency(item.getString(nbt_currency));
        }
        return AccountManager.getDefaultCurrency();
    }
}
