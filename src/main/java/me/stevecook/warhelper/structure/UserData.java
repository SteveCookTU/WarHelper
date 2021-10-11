package me.stevecook.warhelper.structure;

public class UserData {

    private String mainHand;
    private String secondary;
    private int mainHandLevel;
    private int secondaryLevel;
    private int level;

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

    public String getMainHand() {
        return mainHand;
    }

    public String getSecondary() {
        return secondary;
    }

    public int getMainHandLevel() {
        return mainHandLevel;
    }

    public int getSecondaryLevel() {
        return secondaryLevel;
    }

    public int getLevel() {
        return level;
    }
}
