package com.feildmaster.lib.expeditor;

import org.bukkit.entity.Player;

public class Editor {
    private final Player player;

    public Editor(Player p) {
        player = p;
    }

    // Handle experience
    public void setExp(int exp) {
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        if(exp <= 0) return;

        giveExp(exp);
    }

    public void giveExp(int exp) {
        while(exp > 0) {
            int xp = getExpToLevel()-getExp();
            if(xp > exp)
                xp = exp;
            player.giveExp(xp);
            exp -= xp;
        }
    }

    public void takeExp(int exp) {
        takeExp(exp, true);
    }

    public void takeExp(int exp, boolean fromTotal) {
        int xp = getTotalExp();

        if (fromTotal) {
            xp -= exp;
        } else {
            int m = getExp() - exp;
            if(m < 0) m = 0;
            xp -= getExp() + m;
        }

        setExp(xp);
    }

    // Get experience functions
    public int getExp() {
        return (int) (getExpToLevel() * player.getExp());
    }

    // This function is ugly!
    public int getTotalExp() {
        return getTotalExp(false);
    }
    public int getTotalExp(boolean recalc) {
        if (recalc) recalcTotalExp();
        return player.getTotalExperience();
    }

    public int getLevel() {
        return player.getLevel();
    }

    public int getExpToLevel() {
        return getExpToLevel(getLevel());
    }

    public int getExpToLevel(int i) {
        //return 7 + (i * 7 >> 1); //old val from MC1.5 and below, with level cap 50
    	return i < 17 ? i * 17 : new Double(1.5 * Math.pow(i,2) - 29.5 * i + 360).intValue(); //TODO test
    }

    public void recalcTotalExp() {
        int total = getExp();
        for(int i = 0; i < player.getLevel(); i++) {
            total += getExpToLevel(i);
        }
        player.setTotalExperience(total);
    }
}