package me.stevecook.warhelper.structure;

public class UserData {

    private String mainHand;
    private String secondary;
    private Integer mainHandLevel;
    private Integer secondaryLevel;
    private Integer level;
    private Integer gearScore;

    public UserData() {
        mainHand = "undefined";
        secondary = "undefined";
        mainHandLevel = 0;
        secondaryLevel = 0;
        level = 1;
        gearScore = 0;
    }

    public void setMainHand(String mainHand) {
        this.mainHand = mainHand;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public void setMainHandLevel(int mainHandLevel) {
        this.mainHandLevel = mainHandLevel;
    }

    public void setSecondaryLevel(int secondaryLevel) {
        this.secondaryLevel = secondaryLevel;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setGearScore(int gearScore) { this.gearScore = gearScore; }

    public String getMainHand() {
        return mainHand;
    }

    public String getSecondary() {
        return secondary;
    }

    public Integer getMainHandLevel() {
        return mainHandLevel;
    }

    public Integer getSecondaryLevel() {
        return secondaryLevel;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getGearScore() {
        return gearScore;
    }

    public static String getWeaponAbbreviation(String weaponName) {
        switch (weaponName) {
            case "sword and shield" -> {
                return "SS";
            }
            case "rapier" -> {
                return "R";
            }
            case "hatchet" -> {
                return "H";
            }
            case "spear" -> {
                return "S";
            }
            case "great axe" -> {
                return "GA";
            }
            case "war hammer" -> {
                return "WH";
            }
            case "bow" -> {
                return "B";
            }
            case "musket" -> {
                return "M";
            }
            case "fire staff" -> {
                return "FS";
            }
            case "ice staff", "life staff" -> {
                return "LS";
            }
            case "ice gauntlet" -> {
                return "IG";
            }
            case "void gauntlet" -> {
                return "VG";
            }
            default -> {
                return "N/A";
            }
        }
    }
}
