package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.Util;
import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.UserData;
import me.stevecook.warhelper.structure.enums.Tradeskill;
import me.stevecook.warhelper.structure.enums.Weapon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class SlashCommandListener implements EventListener {

    private final WarHelper wh;

    public SlashCommandListener(WarHelper wh) {
        this.wh = wh;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof SlashCommandEvent e) {
            if (e.getName().equalsIgnoreCase("war")) {
                if (e.getSubcommandName() != null) {
                    switch (e.getSubcommandName()) {
                        case "alert" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
                                    createAlert(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "save" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
                                    try {
                                        e.deferReply(true).queue();
                                        wh.saveData();
                                        e.getHook().sendMessage("Data saved successfully").queue();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        e.reply("There was an issue saving data. Please contact the bot developer(s).").setEphemeral(true).queue();
                                    }
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "archive" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
                                    archiveAlert(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "refresh" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles())) {
                                    refreshEmbeds(e);
                                } else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "perm" -> {
                            if (e.isFromGuild())
                                if (Objects.requireNonNull(e.getMember()).isOwner() || e.getMember().hasPermission(Permission.ADMINISTRATOR))
                                    editPerm(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                    }
                }
            } else if (e.getName().equalsIgnoreCase("register")) {
                if (e.getSubcommandName() != null) {
                    e.deferReply(true).queue();
                    switch (e.getSubcommandName()) {
                        case "mainhand" -> {
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setMainHand(Weapon.valueOf(Objects.requireNonNull(e.getOption("weapon")).getAsString()));
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Main hand set to " + userData.getMainHand().getLabel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                        case "secondary" -> {
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setSecondary(Weapon.valueOf(Objects.requireNonNull(e.getOption("weapon")).getAsString()));
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Secondary set to " + userData.getSecondary().getLabel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                        case "level" -> {
                            if (Objects.requireNonNull(e.getOption("level")).getAsDouble() < 1 || Objects.requireNonNull(e.getOption("level")).getAsDouble() > 60) {
                                e.getHook().sendMessage("Please enter a level from 1 to 60 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setLevel((int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Level set to " + userData.getLevel()).queue();
                        }
                        case "gearscore" -> {
                            if (Objects.requireNonNull(e.getOption("gearscore")).getAsDouble() < 0 || Objects.requireNonNull(e.getOption("gearscore")).getAsDouble() > 600) {
                                e.getHook().sendMessage("Please enter a gear score from 0 to 600 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setGearScore((int) Objects.requireNonNull(e.getOption("gearscore")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Gear score set to " + userData.getGearScore()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                        case "tradeskill" -> {
                            if (Objects.requireNonNull(e.getOption("level")).getAsDouble() < 0 || Objects.requireNonNull(e.getOption("level")).getAsDouble() > 200) {
                                e.getHook().sendMessage("Please enter a level from 0 to 200 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            Tradeskill skill = Arrays.stream(Tradeskill.values())
                                    .filter(s -> s.getId() == Objects.requireNonNull(e.getOption("skill")).getAsLong())
                                    .toList().get(0);
                            userData.setTradeSkill(skill,
                                    (int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage(skill.getLabel() + " set to " + userData.getTradeSkill(skill)).queue();
                        }
                        case "weaponlevel" -> {
                            if (Objects.requireNonNull(e.getOption("level")).getAsDouble() < 0 || Objects.requireNonNull(e.getOption("level")).getAsDouble() > 20) {
                                e.getHook().sendMessage("Please enter a level from 0 to 20 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            Weapon weapon = Weapon.valueOf(Objects.requireNonNull(e.getOption("weapon")).getAsString());
                            userData.setWeaponLevel(weapon,
                                    (int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage(weapon.getLabel() + " set to " + userData.getWeaponLevel(weapon)).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                    }
                }
            } else if (e.getName().equalsIgnoreCase("event")) {
                if (e.getSubcommandName() != null) {
                    if (e.isFromGuild())
                        if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
                            createAlert(e);
                        else
                            e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                    else
                        e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                }
            }
        }
    }

    private void createAlert(SlashCommandEvent e) {
        LocalDate date;
        LocalTime time;
        try {
            String sDate = Objects.requireNonNull(e.getOption("date")).getAsString();
            String sTime = Objects.requireNonNull(e.getOption("time")).getAsString();
            date = LocalDate.parse(sDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
            time = LocalTime.parse(sTime.substring(0, sTime.length() - 2) + sTime.substring(sTime.length() - 2).toUpperCase(), DateTimeFormatter.ofPattern("h:mma"));
        } catch (DateTimeParseException ex) {
            e.reply("The date or time entered was invalid. Please use the formats MM/dd/yyyy and hh:mma respectively. Ex: 02/10/2022 and 12:30pm").setEphemeral(true).queue();
            return;
        }
        e.reply("Generating war message.").setEphemeral(true).queue();
        String server = "localevent" + Objects.requireNonNull(e.getGuild()).getName();
        String faction = "event";
        String territory = Objects.requireNonNull(e.getOption("territory")).getAsString();
        if(Objects.requireNonNull(e.getSubcommandName()).equalsIgnoreCase("world") || e.getSubcommandName().equalsIgnoreCase("alert"))
            server = Objects.requireNonNull(e.getOption("server")).getAsString();
        if(e.getSubcommandName().equalsIgnoreCase("alert"))
            faction = Objects.requireNonNull(e.getOption("faction")).getAsString();

        UUID uuid = UUID.nameUUIDFromBytes((date.format(DateTimeFormatter.ofPattern("EEE d. MMM")) + time.format(DateTimeFormatter.ofPattern("hh:mma")) + server.toLowerCase() + faction.toLowerCase() + territory.toLowerCase()).getBytes());
        if (!wh.channelContainsWarMessage(Objects.requireNonNull(e.getGuild()).getIdLong(), e.getChannel().getIdLong(), uuid)) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle(e.getSubcommandName().equalsIgnoreCase("alert") ? "War Alert" : Objects.requireNonNull(e.getOption("name")).getAsString());
            eb.setDescription(Util.convertToEmoji(territory));

            eb.addField(":calendar_spiral: " + date.format(DateTimeFormatter.ofPattern("EEE d. MMM")), "", true);
            eb.addBlankField(true);
            eb.addField(":clock1: " + time.format(DateTimeFormatter.ofPattern("hh:mma")), "", true);

            if (wh.getAlertConnector(uuid) != null) {
                Util.fillEmbed(eb, uuid, wh);
            } else {
                eb.addField(":shield: TANK :shield:", "", true);
                eb.addBlankField(true);
                eb.addField(":dagger: MDPS :dagger:", "", true);
                eb.addBlankField(false);
                eb.addField(":archery: Physical RDPS :archery:", "", true);
                eb.addBlankField(true);
                eb.addField(":magic_wand: Elemental RDPS :magic_wand:", "", true);
                eb.addBlankField(false);
                eb.addField(":heart: HEALER :heart:", "", true);
                eb.addBlankField(true);
                eb.addField(":boom: ARTILLERY :boom:", "", true);
                eb.addBlankField(false);
                eb.addField(":question: Tentative :question:", "", true);
                eb.addBlankField(true);
                eb.addField(":no_entry: Not Available :no_entry:", "", true);
                eb.addBlankField(false);
            }
            eb.addField("NOTE", "Remember to use '/register' to register your in-game data.", false);
            eb.setFooter(uuid.toString());
            String finalFaction = faction;
            String finalServer = server;
            e.getChannel().sendMessageEmbeds(eb.build()).queue(message -> {
                for (String s : Util.REACTIONS) {
                    message.addReaction(s).queue();
                }
                wh.addWarMessage(message.getGuild().getIdLong(),
                        message.getChannel().getIdLong(),
                        message.getIdLong(),
                        date.format(DateTimeFormatter.ofPattern("EEE d. MMM")) +
                                time.format(DateTimeFormatter.ofPattern("hh:mma")) +
                                finalServer.toLowerCase() +
                                finalFaction.toLowerCase() +
                                territory.toLowerCase(),
                        date.format(DateTimeFormatter.ofPattern("EEE d. MMM")),
                        time.format(DateTimeFormatter.ofPattern("hh:mma")),
                        finalServer.toLowerCase(),
                        finalFaction.toLowerCase(),
                        territory.toLowerCase(),
                        e.getName().equalsIgnoreCase("war") ? "" : Objects.requireNonNull(e.getOption("name")).getAsString(),
                        e.getName().equalsIgnoreCase("war") ? 0 : 1
                );
            });
        }
    }

    private void archiveAlert(SlashCommandEvent e) {
        e.reply("Archiving is now automated. This command deprecated.").setEphemeral(true).queue();
    }

    private void refreshEmbeds(SlashCommandEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if (e.getOption("id") == null) {
            return;
        }
        String sUUID = Objects.requireNonNull(e.getOption("id")).getAsString();
        Util.updateEmbeds(UUID.fromString(sUUID), wh);
        e.getHook().sendMessage("All embeds with the ID specified have been refreshed.").queue();
    }

    private void editPerm(SlashCommandEvent e) {
        e.deferReply(true).queue();
        String sOption = Objects.requireNonNull(e.getOption("add_remove")).getAsString();
        long roleID = Objects.requireNonNull(e.getOption("role")).getAsRole().getIdLong();
        switch (sOption) {
            case "add" -> {
                wh.addPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), roleID);
                e.getHook().sendMessage("The bot admin permission has been added to the specified role.").queue();
            }
            case "remove" -> {
                wh.removePermission(Objects.requireNonNull(e.getGuild()).getIdLong(), roleID);
                e.getHook().sendMessage("The bot admin permission has been removed from the specified role.").queue();
            }
        }
    }

}
