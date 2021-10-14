package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.Util;
import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.UserData;
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
import java.util.Objects;
import java.util.UUID;

public class SlashCommandListener implements EventListener {

    private final WarHelper wh;

    public SlashCommandListener(WarHelper wh) {
        this.wh = wh;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof SlashCommandEvent e) {
            if(e.getName().equalsIgnoreCase("war")) {
                if(e.getSubcommandName() != null) {
                    switch(e.getSubcommandName()) {
                        case "alert" -> {
                            if(e.isFromGuild())
                                if(wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
                                    createAlert(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "save" -> {
                            if(e.isFromGuild())
                                if(wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
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
                            if(e.isFromGuild())
                                if(wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
                                    archiveAlert(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "refresh" -> {
                            if(e.isFromGuild())
                                if(wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(), Objects.requireNonNull(e.getMember()).getRoles()))
                                    refreshEmbeds(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "perm" -> {
                            if(e.isFromGuild())
                                if(Objects.requireNonNull(e.getMember()).isOwner() || e.getMember().hasPermission(Permission.ADMINISTRATOR))
                                    editPerm(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                    }
                }
            } else if(e.getName().equalsIgnoreCase("register")) {
                if(e.getSubcommandName() != null) {
                    e.deferReply(true).queue();
                    switch(e.getSubcommandName()) {
                        case "mainhand" -> {
                            if(Objects.requireNonNull(e.getOption("level")).getAsDouble() < 0 || Objects.requireNonNull(e.getOption("level")).getAsDouble() > 20) {
                                e.getHook().sendMessage("Please enter a level from 0 to 20 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setMainHand(Objects.requireNonNull(e.getOption("weapon")).getAsString());
                            userData.setMainHandLevel((int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Main hand set to " + userData.getMainHand() + " level " + userData.getMainHandLevel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                        case "secondary" -> {
                            if(Objects.requireNonNull(e.getOption("level")).getAsDouble() < 0 || Objects.requireNonNull(e.getOption("level")).getAsDouble() > 20) {
                                e.getHook().sendMessage("Please enter a level from 0 to 20 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setSecondary(Objects.requireNonNull(e.getOption("weapon")).getAsString());
                            userData.setSecondaryLevel((int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Secondary set to " + userData.getSecondary() + " level " + userData.getSecondaryLevel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                        case "level" -> {
                            if(Objects.requireNonNull(e.getOption("level")).getAsDouble() < 1 || Objects.requireNonNull(e.getOption("level")).getAsDouble() > 60) {
                                e.getHook().sendMessage("Please enter a level from 1 to 60 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setLevel((int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Level set to " + userData.getLevel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createAlert(SlashCommandEvent e) {
        LocalDate date;
        LocalTime time;
        if(e.getOption("date") == null || e.getOption("time") == null || e.getOption("territory") == null) {
            return;
        }
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
        String territory = Objects.requireNonNull(e.getOption("territory")).getAsString();

        UUID uuid = UUID.nameUUIDFromBytes((date.format(DateTimeFormatter.ofPattern("EEE d. MMM")) + time.format(DateTimeFormatter.ofPattern("hh:mma")) + territory.toLowerCase()).getBytes());
        if (!wh.channelContainsWarMessage(Objects.requireNonNull(e.getGuild()).getIdLong(), e.getChannel().getIdLong(), uuid)) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle(Util.convertToEmoji(territory));

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
            e.getChannel().sendMessage("@everyone").queue(m -> m.editMessageEmbeds(eb.build()).queue( message -> {
                for(String s : Util.REACTIONS) {
                    message.addReaction(s).queue();
                }
                wh.addWarMessage(message.getGuild().getIdLong(),
                        message.getChannel().getIdLong(),
                        message.getIdLong(),
                        date.format(DateTimeFormatter.ofPattern("EEE d. MMM")) +
                                time.format(DateTimeFormatter.ofPattern("hh:mma")) +
                                territory.toLowerCase());
            }));
        }
    }

    private void archiveAlert(SlashCommandEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if(e.getOption("id") == null) {
            return;
        }
        String sUUID = Objects.requireNonNull(e.getOption("id")).getAsString();
        wh.archiveAlertConnector(UUID.fromString(sUUID));
        e.getHook().sendMessage("The specified alert ID has been archived.").queue();
    }

    private void refreshEmbeds(SlashCommandEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if(e.getOption("id") == null) {
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
        switch(sOption) {
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
