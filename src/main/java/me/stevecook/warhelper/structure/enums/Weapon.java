package me.stevecook.warhelper.structure.enums;

public enum Weapon {
    SWORDANDSHIELD("Sword and Shield", "SS"),
    RAPIER("Rapier", "R"),
    HATCHET("Hatchet", "H"),
    SPEAR("Spear", "S"),
    GREATAXE("Great Axe", "GA"),
    WARHAMMER("War Hammer", "WH"),
    BOW("Bow", "B"),
    MUSKET("Musket", "M"),
    FIRESTAFF("Fire Staff", "FS"),
    LIFESTAFF("Life Staff", "LS"),
    ICEGAUNT("Ice Gauntlet", "IG"),
    VOIDGAUNT("Void Gauntlet", "VG");

    private final String label;
    private final String abbreviation;

    Weapon(String label, String abbreviation) {
        this.label = label;
        this.abbreviation = abbreviation;
    }

    public String getLabel() {
        return label;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
