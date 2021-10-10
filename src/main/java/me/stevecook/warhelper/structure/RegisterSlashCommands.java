package me.stevecook.warhelper.structure;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class RegisterSlashCommands {

    public static void register(JDA jda) {
        jda.upsertCommand(new CommandData("war", "Base command for war helper")
                .addSubcommands(new SubcommandData("alert", "Creates a war alert with the designated parameters. (Bot admin)")
                        .addOptions(new OptionData(OptionType.STRING, "territory", "Designated territory for the war", true)
                                        .addChoices(new Command.Choice("Brightwood", "brightwood"),
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
                                new OptionData(OptionType.STRING, "time", "Designated time for the war (h:mma) Ex. 4:00PM", true)),
                        new SubcommandData("save", "Manually save current data (Bot admin)"),
                        new SubcommandData("archive", "Archive a way alert and all related alerts based on ID (Bot admin)")
                                .addOptions(new OptionData(OptionType.STRING, "id", "ID of the alert to archive", true)),
                        new SubcommandData("perm", "Add or remove permissions to use admin bot commands (Owner/Server Admin)")
                                .addOptions(new OptionData(OptionType.STRING, "add_remove", "Choice to add or remove the bot admin permission", true)
                                        .addChoices(new Command.Choice("add", "add"),
                                                new Command.Choice("remove", "remove")),
                                        new OptionData(OptionType.ROLE, "role", "Role to give or remove the permission", true)),
                        new SubcommandData("refresh", "Refresh all current embeds (Bot admin)")
                                .addOptions(new OptionData(OptionType.STRING, "id", "ID of the alert to archive", true))))
                .queue();
    }

}
