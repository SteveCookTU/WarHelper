package me.stevecook.warhelper.structure;

import me.stevecook.warhelper.structure.enums.Tradeskill;
import me.stevecook.warhelper.structure.enums.Weapon;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class RegisterSlashCommands {

    public static void register(JDA jda) {
        jda.upsertCommand(Commands.slash("war", "Base command for war helper wars").addSubcommands(
                new SubcommandData("alert",
                        "Creates a war alert with the designated parameters. (Bot admin)").addOptions(
                        new OptionData(OptionType.STRING, "server", "Designated server for the war", true),
                        new OptionData(OptionType.STRING, "faction", "Designated faction for the war", true).addChoices(
                                new Command.Choice("Covenant", "covenant"),
                                new Command.Choice("Marauders", "marauders"),
                                new Command.Choice("Syndicate", "syndicate")),
                        new OptionData(OptionType.STRING, "territory", "Designated territory for the war",
                                true).addChoices(new Command.Choice("Brightwood", "brightwood"),
                                new Command.Choice("Cutlass Keys", "cutlass_keys"),
                                new Command.Choice("Ebonscale Reach", "ebonscale_reach"),
                                new Command.Choice("Edengrove", "edengrove"),
                                new Command.Choice("Everfall", "everfall"),
                                new Command.Choice("First Light", "first_light"),
                                new Command.Choice("Great Cleave", "great_cleave"),
                                new Command.Choice("Monarch's Bluffs", "monarchs_bluffs"),
                                new Command.Choice("Mourningdale", "mourningdale"),
                                new Command.Choice("Reekwater", "reekwater"),
                                new Command.Choice("Restless Shore", "restless_shore"),
                                new Command.Choice("Shattered Mountain", "shattered_mountain"),
                                new Command.Choice("Weaver's Fen", "weavers_fen"),
                                new Command.Choice("Windsward", "windsward")),
                        new OptionData(OptionType.STRING, "date", "Designated date for the war (M/d/yyyy)", true),
                        new OptionData(OptionType.STRING, "time", "Designated time for the war (h:mma) Ex. 4:00PM",
                                true)), new SubcommandData("save", "Manually save current data (Bot admin)"),
                new SubcommandData("archive",
                        "Archive a way alert and all related alerts based on ID (Bot admin)").addOptions(
                        new OptionData(OptionType.STRING, "id", "ID of the alert to archive", true)),
                new SubcommandData("perm",
                        "Add or remove permissions to use admin bot commands (Owner/Server Admin)").addOptions(
                        new OptionData(OptionType.STRING, "add_remove",
                                "Choice to add or remove the bot admin permission", true).addChoices(
                                new Command.Choice("add", "add"), new Command.Choice("remove", "remove")),
                        new OptionData(OptionType.ROLE, "role", "Role to give or remove the permission", true)),
                new SubcommandData("refresh", "Refresh all current embeds (Bot admin)").addOptions(
                        new OptionData(OptionType.STRING, "id", "ID of the alert to archive", true)))).queue();

        jda.upsertCommand(Commands.slash("event", "Base command for war helper events.").addSubcommands(
                new SubcommandData("local", "setup for local events").addOptions(
                        new OptionData(OptionType.STRING, "name", "name for the event", true),
                        new OptionData(OptionType.STRING, "territory", "Designated territory for the event",
                                true).addChoices(new Command.Choice("Brightwood", "brightwood"),
                                new Command.Choice("Cutlass Keys", "cutlass_keys"),
                                new Command.Choice("Ebonscale Reach", "ebonscale_reach"),
                                new Command.Choice("Edengrove", "edengrove"),
                                new Command.Choice("Everfall", "everfall"),
                                new Command.Choice("First Light", "first_light"),
                                new Command.Choice("Great Cleave", "great_cleave"),
                                new Command.Choice("Monarch's Bluffs", "monarchs_bluffs"),
                                new Command.Choice("Mourningdale", "mourningdale"),
                                new Command.Choice("Reekwater", "reekwater"),
                                new Command.Choice("Restless Shore", "restless_shore"),
                                new Command.Choice("Shattered Mountain", "shattered_mountain"),
                                new Command.Choice("Weaver's Fen", "weavers_fen"),
                                new Command.Choice("Windsward", "windsward")),
                        new OptionData(OptionType.STRING, "date", "Designated date for the event (M/d/yyyy)", true),
                        new OptionData(OptionType.STRING, "time", "Designated time for the event (h:mma) Ex. 4:00PM",
                                true)), new SubcommandData("world", "setup for world events").addOptions(
                        new OptionData(OptionType.STRING, "server", "server for the event", true),
                        new OptionData(OptionType.STRING, "name", "name for the event", true),
                        new OptionData(OptionType.STRING, "territory", "Designated territory for the event",
                                true).addChoices(new Command.Choice("Brightwood", "brightwood"),
                                new Command.Choice("Cutlass Keys", "cutlass_keys"),
                                new Command.Choice("Ebonscale Reach", "ebonscale_reach"),
                                new Command.Choice("Edengrove", "edengrove"),
                                new Command.Choice("Everfall", "everfall"),
                                new Command.Choice("First Light", "first_light"),
                                new Command.Choice("Great Cleave", "great_cleave"),
                                new Command.Choice("Monarch's Bluffs", "monarchs_bluffs"),
                                new Command.Choice("Mourningdale", "mourningdale"),
                                new Command.Choice("Reekwater", "reekwater"),
                                new Command.Choice("Restless Shore", "restless_shore"),
                                new Command.Choice("Shattered Mountain", "shattered_mountain"),
                                new Command.Choice("Weaver's Fen", "weavers_fen"),
                                new Command.Choice("Windsward", "windsward")),
                        new OptionData(OptionType.STRING, "date", "Designated date for the event (M/d/yyyy)", true),
                        new OptionData(OptionType.STRING, "time", "Designated time for the event (h:mma) Ex. 4:00PM",
                                true)))).queue();

        jda.upsertCommand(Commands.slash("register", "Register user data from new world to discord").addSubcommands(
                new SubcommandData("mainhand", "Set the main hand weapon of your character").addOptions(
                        new OptionData(OptionType.STRING, "weapon", "The weapon to set", true).addChoices(
                                EnumSet.allOf(Weapon.class).stream()
                                        .map(w -> new Command.Choice(w.getLabel(), w.toString()))
                                        .collect(Collectors.toList()))),
                new SubcommandData("secondary", "Set the secondary weapon of your character").addOptions(
                        new OptionData(OptionType.STRING, "weapon", "The weapon to set", true).addChoices(
                                EnumSet.allOf(Weapon.class).stream()
                                        .map(w -> new Command.Choice(w.getLabel(), w.toString()))
                                        .collect(Collectors.toList()))),
                new SubcommandData("weaponlevel", "Set the level of a specific weapon").addOptions(
                        new OptionData(OptionType.STRING, "weapon", "The weapon to set", true).addChoices(
                                EnumSet.allOf(Weapon.class).stream()
                                        .map(w -> new Command.Choice(w.getLabel(), w.toString()))
                                        .collect(Collectors.toList())),
                        new OptionData(OptionType.INTEGER, "level", "The level of the weapon", true)),
                new SubcommandData("level", "Set the level of your character").addOptions(
                        new OptionData(OptionType.INTEGER, "level", "Character level", true)),
                new SubcommandData("gearscore", "Set the overall gear score of your character").addOptions(
                        new OptionData(OptionType.INTEGER, "gearscore", "Gear score number", true)),
                new SubcommandData("tradeskill", "Set the level of a specified tradeskill").addOptions(
                        new OptionData(OptionType.STRING, "skill", "The specified skill", true).addChoices(
                                EnumSet.allOf(Tradeskill.class).stream()
                                        .map(s -> new Command.Choice(s.getLabel(), s.getId() + ""))
                                        .collect(Collectors.toList())),
                        new OptionData(OptionType.INTEGER, "level", "Skill level", true)))).queue();

        jda.upsertCommand(Commands.slash("warstats", "View stats from WarHelper").addSubcommands(
                                new SubcommandData("summary", "Show a general summary of WarHelper stats").addOptions(
                                        new OptionData(OptionType.STRING, "locale", "Pull local or global stats", true).addChoices(
                                                new Command.Choice("Local", "local"), new Command.Choice("Global", "global"))))
                        .addSubcommandGroups(
                                new SubcommandGroupData("search", "Search WarHelper for specific data").addSubcommands(
                                        new SubcommandData("tradeskill",
                                                "Search for members with a specific trade skill and minimum level").addOptions(
                                                new OptionData(OptionType.STRING, "skill", "The specified skill",
                                                        true).addChoices(EnumSet.allOf(Tradeskill.class).stream()
                                                        .map(s -> new Command.Choice(s.getLabel(), s.getId() + ""))
                                                        .collect(Collectors.toList())),
                                                new OptionData(OptionType.INTEGER, "level", "Minimum level to search for")),
                                        new SubcommandData("weapon",
                                                "Search for members with a specific weapon and minimum level").addOptions(
                                                new OptionData(OptionType.STRING, "weapon", "The specified weapon",
                                                        true).addChoices(EnumSet.allOf(Weapon.class).stream()
                                                        .map(w -> new Command.Choice(w.getLabel(), w.toString()))
                                                        .collect(Collectors.toList())),
                                                new OptionData(OptionType.INTEGER, "level", "Minimum level to search for")),
                                        new SubcommandData("gearscore",
                                                "Search for members with a minimum gear score").addOptions(
                                                new OptionData(OptionType.INTEGER, "score", "Minimum score to search for")),
                                        new SubcommandData("level", "Search for members with a minimum level").addOptions(
                                                new OptionData(OptionType.INTEGER, "level", "Minimum level to search for")))))
                .queue();

    }
}
