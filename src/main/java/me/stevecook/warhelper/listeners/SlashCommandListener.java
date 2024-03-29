package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.Util;
import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.UserData;
import me.stevecook.warhelper.structure.enums.Tradeskill;
import me.stevecook.warhelper.structure.enums.Weapon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class SlashCommandListener implements EventListener {

    private final WarHelper wh;

    public SlashCommandListener(WarHelper wh) {
        this.wh = wh;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof SlashCommandInteractionEvent e) {
            if (e.getName().equalsIgnoreCase("war")) {
                if (e.getSubcommandName() != null) {
                    switch (e.getSubcommandName()) {
                        case "alert" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(),
                                        Objects.requireNonNull(e.getMember()).getRoles()))
                                    createAlert(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true)
                                            .queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "save" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(),
                                        Objects.requireNonNull(e.getMember()).getRoles()))
                                    try {
                                        e.deferReply(true).queue();
                                        wh.saveData();
                                        e.getHook().sendMessage("Data saved successfully").queue();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        e.reply("There was an issue saving data. Please contact the bot developer(s).")
                                                .setEphemeral(true).queue();
                                    }
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true)
                                            .queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "archive" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(),
                                        Objects.requireNonNull(e.getMember()).getRoles()))
                                    archiveAlert(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true)
                                            .queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "refresh" -> {
                            if (e.isFromGuild())
                                if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(),
                                        Objects.requireNonNull(e.getMember()).getRoles())) {
                                    refreshEmbeds(e);
                                } else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true)
                                            .queue();
                            else
                                e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                        }
                        case "perm" -> {
                            if (e.isFromGuild())
                                if (Objects.requireNonNull(e.getMember()).isOwner() ||
                                        e.getMember().hasPermission(Permission.ADMINISTRATOR))
                                    editPerm(e);
                                else
                                    e.reply("You do not have permission to use this command.").setEphemeral(true)
                                            .queue();
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
                            userData.setMainHand(
                                    Weapon.valueOf(Objects.requireNonNull(e.getOption("weapon")).getAsString()));
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Main hand set to " + userData.getMainHand().getLabel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                        case "secondary" -> {
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setSecondary(
                                    Weapon.valueOf(Objects.requireNonNull(e.getOption("weapon")).getAsString()));
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Secondary set to " + userData.getSecondary().getLabel()).queue();
                            for (AlertConnector ac :
                                    wh.getAlertConnectorsWithUserID(e.getUser().getIdLong())) {
                                Util.updateEmbeds(ac.getCode(), wh);
                            }
                        }
                        case "level" -> {
                            if (Objects.requireNonNull(e.getOption("level")).getAsDouble() < 1 ||
                                    Objects.requireNonNull(e.getOption("level")).getAsDouble() > 60) {
                                e.getHook().sendMessage("Please enter a level from 1 to 60 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            userData.setLevel((int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage("Level set to " + userData.getLevel()).queue();
                        }
                        case "gearscore" -> {
                            if (Objects.requireNonNull(e.getOption("gearscore")).getAsDouble() < 0 ||
                                    Objects.requireNonNull(e.getOption("gearscore")).getAsDouble() > 600) {
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
                            if (Objects.requireNonNull(e.getOption("level")).getAsDouble() < 0 ||
                                    Objects.requireNonNull(e.getOption("level")).getAsDouble() > 200) {
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
                            e.getHook().sendMessage(skill.getLabel() + " set to " + userData.getTradeSkill(skill))
                                    .queue();
                        }
                        case "weaponlevel" -> {
                            if (Objects.requireNonNull(e.getOption("level")).getAsDouble() < 0 ||
                                    Objects.requireNonNull(e.getOption("level")).getAsDouble() > 20) {
                                e.getHook().sendMessage("Please enter a level from 0 to 20 (inclusive).").queue();
                                return;
                            }
                            UserData userData = wh.getUserData(e.getUser().getIdLong());
                            Weapon weapon = Weapon.valueOf(Objects.requireNonNull(e.getOption("weapon")).getAsString());
                            userData.setWeaponLevel(weapon,
                                    (int) Objects.requireNonNull(e.getOption("level")).getAsDouble());
                            wh.updateUserData(e.getUser().getIdLong(), userData);
                            e.getHook().sendMessage(weapon.getLabel() + " set to " + userData.getWeaponLevel(weapon))
                                    .queue();
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
                        if (wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(),
                                Objects.requireNonNull(e.getMember()).getRoles()))
                            createAlert(e);
                        else
                            e.reply("You do not have permission to use this command.").setEphemeral(true).queue();
                    else
                        e.reply("This command can only be used in guilds.").setEphemeral(true).queue();
                }
            } else if (e.getName().equalsIgnoreCase("warstats")) {
                if (e.getSubcommandName() != null) {
                    switch (e.getSubcommandName()) {
                        case "summary" -> generateStatsEmbed(e,
                                e.isFromGuild() && !wh.hasPermission(Objects.requireNonNull(e.getGuild()).getIdLong(),
                                        Objects.requireNonNull(e.getMember()).getRoles()));
                        case "tradeskill" -> searchTradeSkills(e);
                        case "weapon" -> searchWeaponLevels(e);
                        case "gearscore" -> searchGearScore(e);
                        case "level" -> searchLevel(e);
                    }
                }
            }
        }
    }

    private void searchTradeSkills(SlashCommandInteractionEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if (e.isFromGuild() && e.getGuild() != null) {
            Tradeskill skill = Arrays.stream(Tradeskill.values())
                    .filter(s -> s.getId() == Objects.requireNonNull(e.getOption("skill")).getAsLong())
                    .toList().get(0);
            int level = e.getOption("level") != null ?
                    (int) (Objects.requireNonNull(e.getOption("level")).getAsLong() > 200 ? 200 :
                            Objects.requireNonNull(
                                    e.getOption("level")).getAsLong()) : 200;
            Map<Long, UserData> userDataMap =
                    wh.getUserData(e.getGuild().getMembers().stream().map(Member::getIdLong).toList());
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("War Helper Search - " + skill.getLabel());
            eb.setDescription("Minimum Level: " + level);
            StringBuilder names = new StringBuilder();
            for (Map.Entry<Long, UserData> ud : userDataMap.entrySet()) {
                if (e.getGuild().getMemberById(ud.getKey()) != null) {
                    if (ud.getValue().getTradeSkill(skill) >= level) {
                        names.append(
                                        Objects.requireNonNull(e.getGuild().getMemberById(ud.getKey())).getEffectiveName())
                                .append("`").append(ud.getValue().getTradeSkill(skill)).append("`\n");
                    }
                }
            }
            eb.addField("", names.toString().trim(), false);
            e.getHook().sendMessageEmbeds(eb.build()).queue();
        } else {
            e.getHook().sendMessage("The local option can only be called within a guild.").queue();
        }
    }

    private void searchWeaponLevels(SlashCommandInteractionEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if (e.isFromGuild() && e.getGuild() != null) {
            Weapon weapon = Weapon.valueOf(Objects.requireNonNull(e.getOption("weapon")).getAsString());
            int level = e.getOption("level") != null ?
                    (int) (Objects.requireNonNull(e.getOption("level")).getAsLong() > 20 ? 20 :
                            Objects.requireNonNull(
                                    e.getOption("level")).getAsLong()) : 20;
            Map<Long, UserData> userDataMap =
                    wh.getUserData(e.getGuild().getMembers().stream().map(Member::getIdLong).toList());
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("War Helper Search - " + weapon.getLabel());
            eb.setDescription("Minimum Level: " + level);
            StringBuilder names = new StringBuilder();
            for (Map.Entry<Long, UserData> ud : userDataMap.entrySet()) {
                if (e.getGuild().getMemberById(ud.getKey()) != null) {
                    if (ud.getValue().getWeaponLevel(weapon) >= level) {
                        names.append(
                                        Objects.requireNonNull(e.getGuild().getMemberById(ud.getKey())).getEffectiveName())
                                .append("`").append(ud.getValue().getWeaponLevel(weapon)).append("`\n");
                    }
                }
            }
            eb.addField("", names.toString().trim(), false);
            e.getHook().sendMessageEmbeds(eb.build()).queue();
        } else {
            e.getHook().sendMessage("The local option can only be called within a guild.").queue();
        }
    }

    private void searchGearScore(SlashCommandInteractionEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if (e.isFromGuild() && e.getGuild() != null) {
            int level = e.getOption("score") != null ?
                    (int) (Objects.requireNonNull(e.getOption("score")).getAsLong() > 600 ? 600 :
                            Objects.requireNonNull(
                                    e.getOption("score")).getAsLong()) : 600;
            Map<Long, UserData> userDataMap =
                    wh.getUserData(e.getGuild().getMembers().stream().map(Member::getIdLong).toList());
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("War Helper Search - Gear Score");
            eb.setDescription("Minimum Score: " + level);
            StringBuilder names = new StringBuilder();
            for (Map.Entry<Long, UserData> ud : userDataMap.entrySet()) {
                if (e.getGuild().getMemberById(ud.getKey()) != null) {
                    if (ud.getValue().getGearScore() >= level) {
                        names.append(
                                        Objects.requireNonNull(e.getGuild().getMemberById(ud.getKey())).getEffectiveName())
                                .append("`").append(ud.getValue().getGearScore()).append("`\n");
                    }
                }
            }
            eb.addField("", names.toString().trim(), false);
            e.getHook().sendMessageEmbeds(eb.build()).queue();
        } else {
            e.getHook().sendMessage("The local option can only be called within a guild.").queue();
        }
    }

    private void searchLevel(SlashCommandInteractionEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if (e.isFromGuild() && e.getGuild() != null) {
            int level = e.getOption("level") != null ?
                    (int) (Objects.requireNonNull(e.getOption("level")).getAsLong() > 60 ? 60 :
                            Objects.requireNonNull(
                                    e.getOption("level")).getAsLong()) : 60;
            Map<Long, UserData> userDataMap =
                    wh.getUserData(e.getGuild().getMembers().stream().map(Member::getIdLong).toList());
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("War Helper Search - Level");
            eb.setDescription("Minimum Level: " + level);
            StringBuilder names = new StringBuilder();
            for (Map.Entry<Long, UserData> ud : userDataMap.entrySet()) {
                if (e.getGuild().getMemberById(ud.getKey()) != null) {
                    if (ud.getValue().getLevel() >= level) {
                        names.append(
                                        Objects.requireNonNull(e.getGuild().getMemberById(ud.getKey())).getEffectiveName())
                                .append("`").append(ud.getValue().getLevel()).append("`\n");
                    }
                }
            }
            eb.addField("", names.toString().trim(), false);
            e.getHook().sendMessageEmbeds(eb.build()).queue();
        } else {
            e.getHook().sendMessage("The local option can only be called within a guild.").queue();
        }
    }

    private void generateStatsEmbed(SlashCommandInteractionEvent e, boolean ephemeral) {
        e.deferReply().setEphemeral(ephemeral).queue();
        int averageGearScore = 0;
        int averageLevel = 0;
        String titleKey = "Global";
        HashMap<Weapon, Integer> mainHandCount = new HashMap<>();
        HashMap<Weapon, Integer> secondaryCount = new HashMap<>();
        int registeredGearScore = 0;
        int registeredLevel = 0;
        for (Weapon w :
                Weapon.values()) {
            mainHandCount.put(w, 0);
            secondaryCount.put(w, 0);
        }
        switch (Objects.requireNonNull(e.getOption("locale")).getAsString()) {
            case "local" -> {
                if (e.isFromGuild() && e.getGuild() != null) {
                    titleKey = e.getGuild().getName();
                    for (Member m : e.getGuild().getMembers()) {
                        if (!m.getUser().isBot()) {
                            UserData userData = wh.getUserData(m.getIdLong());
                            if (userData != null) {
                                if (userData.getLevel() > 1) {
                                    averageLevel += userData.getLevel();
                                    registeredLevel += 1;
                                }
                                if (userData.getGearScore() > 0) {
                                    averageGearScore += userData.getGearScore();
                                    registeredGearScore += 1;
                                }
                                if (userData.getMainHand() != null) {
                                    mainHandCount.put(userData.getMainHand(),
                                            mainHandCount.get(userData.getMainHand()) + 1);
                                }
                                if (userData.getSecondary() != null) {
                                    secondaryCount.put(userData.getSecondary(),
                                            secondaryCount.get(userData.getSecondary()) + 1);
                                }
                            }
                        }
                    }
                    averageGearScore = averageGearScore / registeredGearScore;
                    averageLevel = averageLevel / registeredLevel;

                } else {
                    e.getHook().sendMessage("The local option can only be called within a guild.").queue();
                    return;
                }
            }
            case "global" -> {
                for (UserData userData : wh.getAllUserData()) {
                    if (userData != null) {
                        if (userData.getLevel() > 1) {
                            averageLevel += userData.getLevel();
                            registeredLevel += 1;
                        }
                        if (userData.getGearScore() > 0) {
                            averageGearScore += userData.getGearScore();
                            registeredGearScore += 1;
                        }
                        if (userData.getMainHand() != null) {
                            mainHandCount.put(userData.getMainHand(),
                                    mainHandCount.get(userData.getMainHand()) + 1);
                        }
                        if (userData.getSecondary() != null) {
                            secondaryCount.put(userData.getSecondary(),
                                    secondaryCount.get(userData.getSecondary()) + 1);
                        }
                    }
                }
                averageGearScore = averageGearScore / registeredGearScore;
                averageLevel = averageLevel / registeredLevel;
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("War Helper Stats - " + titleKey);
        eb.addField("", "__**Connected Guilds:**__ " + wh.getJda().getGuilds().size(), false);
        eb.addField("", "__**Average Gear Score:**__ " + averageGearScore, true);
        eb.addBlankField(true);
        eb.addField("", "__**Average Level:**__ " + averageLevel, true);
        eb.addField("", "__**Main Hand Selections:**__", false);
        addWeaponCountsToEmbed(mainHandCount, eb);
        eb.addField("", "__**Secondary Selections:**__", false);
        addWeaponCountsToEmbed(secondaryCount, eb);
        e.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private void addWeaponCountsToEmbed(HashMap<Weapon, Integer> weaponCounts, EmbedBuilder eb) {
        int total = weaponCounts.values().stream().mapToInt(integer -> integer).sum();
        if (total == 0)
            total = 1;
        for (Weapon w : weaponCounts.keySet().stream().sorted(Comparator.comparing(Weapon::getLabel))
                .toList()) {
            StringBuilder percentView = new StringBuilder(weaponCounts.get(w).toString());
            int percent = (int) (((double) weaponCounts.get(w) / total) * 100);
            while (percentView.length() < (percent / 2)) {
                percentView.append(".");
            }
            eb.addField(w.getLabel(), "||`" + percentView + "`|| " + percent + "%", false);
        }
    }

    private void createAlert(SlashCommandInteractionEvent e) {
        LocalDate date;
        LocalTime time;
        try {
            String sDate = Objects.requireNonNull(e.getOption("date")).getAsString();
            String sTime = Objects.requireNonNull(e.getOption("time")).getAsString();
            date = LocalDate.parse(sDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
            time = LocalTime.parse(
                    sTime.substring(0, sTime.length() - 2) + sTime.substring(sTime.length() - 2).toUpperCase(),
                    DateTimeFormatter.ofPattern("h:mma"));
        } catch (DateTimeParseException ex) {
            e.reply("The date or time entered was invalid. Please use the formats MM/dd/yyyy and hh:mma respectively. Ex: 02/10/2022 and 12:30pm")
                    .setEphemeral(true).queue();
            return;
        }
        e.reply("Generating war message.").setEphemeral(true).queue();
        String server = "localevent" + Objects.requireNonNull(e.getGuild()).getName();
        String faction = "event";
        String territory = Objects.requireNonNull(e.getOption("territory")).getAsString();
        if (Objects.requireNonNull(e.getSubcommandName()).equalsIgnoreCase("world") ||
                e.getSubcommandName().equalsIgnoreCase("alert"))
            server = Objects.requireNonNull(e.getOption("server")).getAsString();
        if (e.getSubcommandName().equalsIgnoreCase("alert"))
            faction = Objects.requireNonNull(e.getOption("faction")).getAsString();

        UUID uuid = UUID.nameUUIDFromBytes((date.format(DateTimeFormatter.ofPattern("EEE d. MMM")) +
                time.format(DateTimeFormatter.ofPattern("hh:mma")) + server.toLowerCase() + faction.toLowerCase() +
                territory.toLowerCase()).getBytes());
        if (!wh.channelContainsWarMessage(Objects.requireNonNull(e.getGuild()).getIdLong(), e.getChannel().getIdLong(),
                uuid)) {
            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle(e.getSubcommandName().equalsIgnoreCase("alert") ? "War Alert" :
                    Objects.requireNonNull(e.getOption("name")).getAsString());
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
                    message.addReaction(Emoji.fromUnicode(s)).queue();
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
                        e.getName().equalsIgnoreCase("war") ? "" :
                                Objects.requireNonNull(e.getOption("name")).getAsString(),
                        e.getName().equalsIgnoreCase("war") ? 0 : 1
                );
            });
        }
    }

    private void archiveAlert(SlashCommandInteractionEvent e) {
        e.reply("Archiving is now automated. This command deprecated.").setEphemeral(true).queue();
    }

    private void refreshEmbeds(SlashCommandInteractionEvent e) {
        e.deferReply().setEphemeral(true).queue();
        if (e.getOption("id") == null) {
            return;
        }
        String sUUID = Objects.requireNonNull(e.getOption("id")).getAsString();
        Util.updateEmbeds(UUID.fromString(sUUID), wh);
        e.getHook().sendMessage("All embeds with the ID specified have been refreshed.").queue();
    }

    private void editPerm(SlashCommandInteractionEvent e) {
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
