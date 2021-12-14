package me.stevecook.warhelper.structure.enums;


public enum Tradeskill {
    WEAPONSMITHING("Weaponsmithing",0),
    ARMORING("Armoring",1),
    ENGINEERING("Engineering",2),
    JEWELCRAFTING("Jewelcrafting",3),
    ARCANA("Arcana",4),
    COOKING("Cooking",5),
    FURNISHING("Furnishing",6),
    MINING("Mining",7),
    TRACKINGSKINNING("Tracking and Skinning",8),
    FISHING("Fishing",9),
    LOGGING("Logging",10),
    HARVESTING("Harvesting",11),
    SMELTING("Smelting",12),
    STONECUTTING("Stonecutting",13),
    LEATHERWORKING("Leatherworking",14),
    WEAVING("Weaving",15),
    WOODWORKING("Woodworking",16);

    private final int id;
    private final String label;

    Tradeskill(String label, int id) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

}