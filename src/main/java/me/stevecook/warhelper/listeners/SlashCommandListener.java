package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.UserData;
import me.stevecook.warhelper.structure.WarMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SlashCommandListener implements EventListener {

    private final WarHelper wh;

    private static final String[] REACTIONS = {"\uD83D\uDEE1", "\uD83D\uDDE1", "\uD83C\uDFF9", "\uD83E\uDE84", "❤", "\uD83D\uDCA5", "❓", "⛔"};

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
                                        e.reply("Data saved successfully").queue();
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
                            e.getHook().sendMessage("Main hand set to " + userData.getMainHand() + " level " + userData.getMainHandLevel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                updateEmbeds(ac.getCode());
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
                            e.getHook().sendMessage("Secondary set to " + userData.getSecondary() + " level " + userData.getSecondaryLevel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                updateEmbeds(ac.getCode());
                            }
                        }
                        case "level" -> {
                            if(Objects.requireNonNull(e.getOption("level")).getAsDouble() < 1 || Objects.requireNonNull(e.getOption("level")).getAsDouble() > 60) {
                                e.getHook().sendMessage("Please enter a level from 1 to 60 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setLevel((int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            e.getHook().sendMessage("Level set to " + userData.getLevel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                updateEmbeds(ac.getCode());
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

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < territory.length(); i++) {
                if (territory.charAt(i) != '_')
                    builder.append(":regional_indicator_").append(territory.substring(i, i + 1).toLowerCase()).append(":").append(" ");
                else
                    builder.append("\t");
            }
            eb.setTitle(builder.toString().trim());

            eb.addField(":calendar_spiral: " + date.format(DateTimeFormatter.ofPattern("EEE d. MMM")), "", true);
            eb.addBlankField(true);
            eb.addField(":clock1: " + time.format(DateTimeFormatter.ofPattern("hh:mma")), "", true);

            if (wh.getAlertConnector(uuid) != null) {
                fillEmbed(eb, uuid);
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
                for(String s : REACTIONS) {
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
        e.deferReply(true).queue();
        if(e.getOption("id") == null) {
            return;
        }
        String sUUID = Objects.requireNonNull(e.getOption("id")).getAsString();
        wh.archiveAlertConnector(UUID.fromString(sUUID));
        e.getHook().sendMessage("The specified alert ID has been archived.").queue();
    }

    private void refreshEmbeds(SlashCommandEvent e) {
        e.deferReply(true).queue();
        if(e.getOption("id") == null) {
            return;
        }
        String sUUID = Objects.requireNonNull(e.getOption("id")).getAsString();
        updateEmbeds(UUID.fromString(sUUID));
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

    private void updateEmbeds(UUID uuid) {
        JDA jda = wh.getJda();
        AlertConnector ac = wh.getAlertConnector(uuid);
        if(ac != null) {
            for (WarMessage wm :
                    ac.getWarMessages()) {
                Guild g;
                g = jda.getGuildById(wm.getGuildID());
                assert g != null;
                TextChannel tc = g.getTextChannelById(wm.getChannelID());
                assert tc != null;
                tc.retrieveMessageById(wm.getMessageID()).queue(message -> {
                    MessageEmbed original = message.getEmbeds().get(0);

                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setTitle(original.getTitle());

                    eb.addField(original.getFields().get(0));
                    eb.addBlankField(true);
                    eb.addField(original.getFields().get(2));

                    fillEmbed(eb, uuid);

                    eb.addField(original.getFields().get(original.getFields().size() - 1));

                    eb.setFooter(Objects.requireNonNull(original.getFooter()).getText());

                    message.editMessageEmbeds(eb.build()).queue();

                    for(String s : REACTIONS) {
                        if(message.getReactions().stream().noneMatch(r -> r.getReactionEmote().getName().equalsIgnoreCase(s))) {
                            message.addReaction(s).queue();
                        }
                    }

                });
            }
        }
    }

    private void fillEmbed(EmbedBuilder eb, UUID uuid) {
        JDA jda = wh.getJda();
        AlertConnector ac = wh.getAlertConnector(uuid);
        Guild temp;
        StringBuilder tanks = new StringBuilder();
        StringBuilder erdps = new StringBuilder();
        StringBuilder prdps = new StringBuilder();
        StringBuilder mdps = new StringBuilder();
        StringBuilder healers = new StringBuilder();
        StringBuilder tentative = new StringBuilder();
        StringBuilder notAvailable = new StringBuilder();
        StringBuilder artillery = new StringBuilder();

        List<Long> guilds = ac.getGuildIDs();

        for (long l : guilds) {
            temp = jda.getGuildById(l);
            assert temp != null;

            StringBuilder guildInitials = new StringBuilder();
            guildInitials.append("__");
            for (String word : temp.getName().split(" ")) {
                guildInitials.append(word.substring(0, 1).toUpperCase());
            }
            guildInitials.append("__");

            if (ac.getTanks().containsValue(l)) {
                tanks.append(guildInitials).append("\n");
                for (long id :
                        ac.getTanks().keySet()) {
                    if (ac.getTanks().get(id) == l) {
                        temp = jda.getGuildById(ac.getTanks().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        tanks.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("`")
                                .append(String.format("%d", userData.getMainHandLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getMainHand()))
                                .append(",")
                                .append(String.format("%d", userData.getSecondaryLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getSecondary()))
                                .append("`")
                                .append("\n");
                    }
                }
            }

            if (ac.getERDPS().containsValue(l)) {
                erdps.append(guildInitials).append("\n");
                for (long id :
                        ac.getERDPS().keySet()) {
                    if (ac.getERDPS().get(id) == l) {
                        temp = jda.getGuildById(ac.getERDPS().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        erdps.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("`")
                                .append(String.format("%d", userData.getMainHandLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getMainHand()))
                                .append(",")
                                .append(String.format("%d", userData.getSecondaryLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getSecondary()))
                                .append("`")
                                .append("\n");
                    }
                }
            }

            if (ac.getPRDPS().containsValue(l)) {
                prdps.append(guildInitials).append("\n");
                for (long id :
                        ac.getPRDPS().keySet()) {
                    if (ac.getPRDPS().get(id) == l) {
                        temp = jda.getGuildById(ac.getPRDPS().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        prdps.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("`")
                                .append(String.format("%d", userData.getMainHandLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getMainHand()))
                                .append(",")
                                .append(String.format("%d", userData.getSecondaryLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getSecondary()))
                                .append("`")
                                .append("\n");
                    }
                }
            }

            if (ac.getMDPS().containsValue(l)) {
                mdps.append(guildInitials).append("\n");
                for (long id :
                        ac.getMDPS().keySet()) {
                    if (ac.getMDPS().get(id) == l) {
                        temp = jda.getGuildById(ac.getMDPS().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        mdps.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("`")
                                .append(String.format("%d", userData.getMainHandLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getMainHand()))
                                .append(",")
                                .append(String.format("%d", userData.getSecondaryLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getSecondary()))
                                .append("`")
                                .append("\n");
                    }
                }
            }

            if (ac.getHealers().containsValue(l)) {
                healers.append(guildInitials).append("\n");
                for (long id :
                        ac.getHealers().keySet()) {
                    if (ac.getHealers().get(id) == l) {
                        temp = jda.getGuildById(ac.getHealers().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        healers.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("`")
                                .append(String.format("%d", userData.getMainHandLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getMainHand()))
                                .append(",")
                                .append(String.format("%d", userData.getSecondaryLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getSecondary()))
                                .append("`")
                                .append("\n");
                    }
                }
            }

            if (ac.getTentative().containsValue(l)) {
                tentative.append(guildInitials).append("\n");
                for (long id :
                        ac.getTentative().keySet()) {
                    if (ac.getTentative().get(id) == l) {
                        temp = jda.getGuildById(ac.getTentative().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        tentative.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("\n");
                    }
                }
            }

            if (ac.getNotAvailable().containsValue(l)) {
                notAvailable.append(guildInitials).append("\n");
                for (long id :
                        ac.getNotAvailable().keySet()) {
                    if (ac.getNotAvailable().get(id) == l) {
                        temp = jda.getGuildById(ac.getNotAvailable().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        notAvailable.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("\n");
                    }
                }
            }

            if (ac.getArtillery().containsValue(l)) {
                artillery.append(guildInitials).append("\n");
                for (long id :
                        ac.getArtillery().keySet()) {
                    if (ac.getArtillery().get(id) == l) {
                        temp = jda.getGuildById(ac.getArtillery().get(id));
                        assert temp != null;
                        UserData userData = wh.getUserData(id);
                        artillery.append("`")
                                .append(String.format("%02d", userData.getLevel()))
                                .append("`")
                                .append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName())
                                .append("`")
                                .append(String.format("%d", userData.getMainHandLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getMainHand()))
                                .append(",")
                                .append(String.format("%d", userData.getSecondaryLevel()))
                                .append(" ")
                                .append(UserData.getWeaponAbbreviation(userData.getSecondary()))
                                .append("`")
                                .append("\n");
                    }
                }
            }
        }

        eb.addField(":shield: TANK :shield:", tanks.toString().trim(), true);
        eb.addBlankField(true);
        eb.addField(":dagger: MDPS :dagger:", mdps.toString().trim(), true);
        eb.addBlankField(false);
        eb.addField(":archery: Physical RDPS :archery:", prdps.toString().trim(), true);
        eb.addBlankField(true);
        eb.addField(":magic_wand: Elemental RDPS :magic_wand:", erdps.toString().trim(), true);
        eb.addBlankField(false);
        eb.addField(":heart: HEALER :heart:", healers.toString().trim(), true);
        eb.addBlankField(true);
        eb.addField(":boom: ARTILLERY :boom:", artillery.toString().trim(), true);
        eb.addBlankField(false);
        eb.addField(":question: Tentative :question:", tentative.toString().trim(), true);
        eb.addBlankField(true);
        eb.addField(":no_entry: Not Available :no_entry:", notAvailable.toString().trim(), true);
        eb.addBlankField(false);
    }

}
