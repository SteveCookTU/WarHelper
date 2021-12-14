package me.stevecook.warhelper.structure;

import me.stevecook.warhelper.structure.enums.Tradeskill;
import me.stevecook.warhelper.structure.enums.Weapon;

import java.util.*;

public class UserData {

    private Weapon mainHand;
    private Weapon secondary;
    private Integer level;
    private Integer gearScore;
    private final Map<Tradeskill, Integer> tradeSkills;
    private final Map<Weapon, Integer> weapons;

    public UserData() {
        mainHand = null;
        secondary = null;
        level = 1;
        gearScore = 0;
        tradeSkills = new LinkedHashMap<>();
        weapons = new LinkedHashMap<>();
        EnumSet<Tradeskill> skills = EnumSet.allOf(Tradeskill.class);
        for(Tradeskill s : skills) {
            tradeSkills.put(s, 0);
        }
        EnumSet<Weapon> weaponList = EnumSet.allOf(Weapon.class);
        for(Weapon s : weaponList) {
            weapons.put(s, 0);
        }
    }

    public void setMainHand(Weapon mainHand) {
        this.mainHand = mainHand;
    }

    public void setSecondary(Weapon secondary) {
        this.secondary = secondary;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setGearScore(int gearScore) { this.gearScore = gearScore; }

    public void setTradeSkill(Tradeskill skill, int level) {
        tradeSkills.put(skill, level);
    }

    public void setWeaponLevel(Weapon weapon, int level) {
        weapons.put(weapon, level);
    }

    public Weapon getMainHand() {
        return mainHand;
    }

    public Weapon getSecondary() {
        return secondary;
    }

    public Integer getMainHandLevel() {
        if(mainHand == null) {
            return 0;
        }
        return weapons.get(mainHand);
    }

    public Integer getSecondaryLevel() {
        if(secondary == null) {
            return 0;
        }
        return weapons.get(secondary);
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getGearScore() {
        return gearScore;
    }

    public Integer getTradeSkill(Tradeskill skill) { return tradeSkills.get(skill); }

    public Map<Tradeskill, Integer> getTradeSkills() { return tradeSkills; }

    public Integer getWeaponLevel(Weapon weapon) {
        return weapons.get(weapon);
    }

}
