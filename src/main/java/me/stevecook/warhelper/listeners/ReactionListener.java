package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.UserData;
import me.stevecook.warhelper.structure.WarMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReactionListener extends ListenerAdapter {

    private final WarHelper wh;

    public ReactionListener(WarHelper wh) {
        this.wh = wh;
    }

    private static final String[] REACTIONS = {"\uD83D\uDEE1", "\uD83D\uDDE1", "\uD83C\uDFF9", "\uD83E\uDE84", "❤", "\uD83D\uDCA5", "❓", "⛔"};

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.isFromGuild()) {
            if (Objects.requireNonNull(e.getMember()).isOwner() || hasPermission(e.getGuild().getIdLong(), e.getMember().getRoles())) {
                String[] args = e.getMessage().getContentRaw().split(" ");
                if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("!waralert")) {
                        LocalDate date;
                        LocalTime time;
                        try {
                            date = LocalDate.parse(args[2], DateTimeFormatter.ofPattern("M/d/yyyy"));
                            time = LocalTime.parse(args[3].substring(0, args[3].length() - 2) + args[3].substring(args[3].length() - 2).toUpperCase(), DateTimeFormatter.ofPattern("h:mma"));
                        } catch (DateTimeParseException ex) {
                            e.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage("The date or time entered was invalid. Please use the formats MM/dd/yyyy and hh:mma respectively. Ex: 02/10/2022 and 12:30pm").queue());
                            return;
                        }

                        UUID uuid = UUID.nameUUIDFromBytes((date.format(DateTimeFormatter.ofPattern("EEE d. MMM")) + time.format(DateTimeFormatter.ofPattern("hh:mma")) + args[1].toLowerCase()).getBytes());
                        if (!wh.channelContainsWarMessage(e.getGuild().getIdLong(), e.getChannel().getIdLong(), uuid)) {
                            EmbedBuilder eb = new EmbedBuilder();

                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < args[1].length(); i++) {
                                if (args[1].charAt(i) != '_')
                                    builder.append(":regional_indicator_").append(args[1].substring(i, i + 1).toLowerCase()).append(":").append(" ");
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
                            e.getChannel().sendMessage("@everyone").queue(m -> m.editMessageEmbeds(eb.build()).queue(message -> {
                                for(String s : REACTIONS) {
                                    message.addReaction(s).queue();
                                }
                                wh.addWarMessage(message.getGuild().getIdLong(),
                                        message.getChannel().getIdLong(),
                                        message.getIdLong(),
                                        date.format(DateTimeFormatter.ofPattern("EEE d. MMM")) +
                                                time.format(DateTimeFormatter.ofPattern("hh:mma")) +
                                                args[1].toLowerCase());
                            }));
                        }
                        e.getMessage().delete().queue();
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("!warsave")) {
                        try {
                            wh.saveData();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        e.getMessage().delete().queue();
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("!wararchive")) {
                        wh.archiveAlertConnector(UUID.fromString(args[1]));
                        e.getMessage().delete().queue();
                    } else if(args[0].equalsIgnoreCase("!warrefresh")) {
                        updateEmbeds(UUID.fromString(args[1]));
                        e.getMessage().delete().queue();
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("!warperm")) {
                        if(e.getMember().isOwner() || e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                            if(e.getGuild().getRolesByName(args[2].replace('_', ' '), true).size() > 1) {
                                if(args[1].equalsIgnoreCase("add")) {
                                    wh.addPermission(e.getGuild().getIdLong(), e.getGuild().getRolesByName(args[2].replace('_', ' '), true).get(0).getIdLong());
                                } else if(args[1].equalsIgnoreCase("remove")) {
                                    wh.removePermission(e.getGuild().getIdLong(), e.getGuild().getRolesByName(args[2].replace('_', ' '), true).get(0).getIdLong());
                                }
                            }
                        }
                        e.getMessage().delete().queue();
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent e) {
        wh.removeWarMessage(e.getGuild().getIdLong(), e.getChannel().getIdLong(), e.getMessageIdLong());
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (wh.isValidWarMessage(e.getGuild().getIdLong(), e.getChannel().getIdLong(), e.getMessageIdLong())) {
            if (!e.getUser().isBot()) {
                e.getChannel().retrieveMessageById(e.getMessageIdLong()).queue(message -> {
                    MessageEmbed mb = message.getEmbeds().get(0);
                    UUID uuid = UUID.fromString(Objects.requireNonNull(Objects.requireNonNull(mb.getFooter()).getText()));
                    String reactionName = e.getReactionEmote().getName();
                    AlertConnector ac = wh.getAlertConnector(uuid);

                    if (ac.getUsers().contains(e.getUserIdLong()))
                        return;

                    switch (reactionName) {
                        case "\uD83E\uDE84" -> {
                            if (ac.getERDPS().containsKey(e.getUserIdLong()))
                                return;
                            ac.addERDPS(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83C\uDFF9" -> {
                            if (ac.getPRDPS().containsKey(e.getUserIdLong()))
                                return;
                            ac.addPRDPS(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83D\uDEE1" -> {
                            if (ac.getTanks().containsKey(e.getUserIdLong()))
                                return;
                            ac.addTank(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83D\uDDE1" -> {
                            if (ac.getMDPS().containsKey(e.getUserIdLong()))
                                return;
                            ac.addMDPS(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "❤" -> {
                            if (ac.getHealers().containsKey(e.getUserIdLong()))
                                return;
                            ac.addHealer(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83D\uDCA5" -> {
                            if (ac.getArtillery().containsKey(e.getUserIdLong()))
                                return;
                            ac.addArtillery(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "❓" -> {
                            if (ac.getTentative().containsKey(e.getUserIdLong()))
                                return;
                            ac.addTentative(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "⛔" -> {
                            if (ac.getNotAvailable().containsKey(e.getUserIdLong()))
                                return;
                            ac.addNotAvailable(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                    }
                    updateEmbeds(uuid);
                });
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
        if (wh.isValidWarMessage(e.getGuild().getIdLong(), e.getChannel().getIdLong(), e.getMessageIdLong())) {
            if (!Objects.requireNonNull(e.getUser()).isBot()) {
                e.getChannel().retrieveMessageById(e.getMessageIdLong()).queue(message -> {
                    MessageEmbed mb = message.getEmbeds().get(0);
                    UUID uuid = UUID.fromString(Objects.requireNonNull(Objects.requireNonNull(mb.getFooter()).getText()));
                    String reactionName = e.getReactionEmote().getName();

                    AlertConnector ac = wh.getAlertConnector(uuid);

                    if (!ac.getUsers().contains(e.getUserIdLong()))
                        return;

                    switch (reactionName) {
                        case "\uD83E\uDE84" -> {
                            if (!ac.getERDPS().containsKey(e.getUserIdLong()) || ac.getERDPS().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeERDPS(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83C\uDFF9" -> {
                            if (!ac.getPRDPS().containsKey(e.getUserIdLong()) || ac.getPRDPS().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removePRDPS(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83D\uDEE1" -> {
                            if (!ac.getTanks().containsKey(e.getUserIdLong()) || ac.getTanks().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeTank(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83D\uDDE1" -> {
                            if (!ac.getMDPS().containsKey(e.getUserIdLong()) || ac.getMDPS().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeMDPS(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "❤" -> {
                            if (!ac.getHealers().containsKey(e.getUserIdLong()) || ac.getHealers().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeHealer(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "\uD83D\uDCA5" -> {
                            if (!ac.getArtillery().containsKey(e.getUserIdLong()) || ac.getArtillery().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeArtillery(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "❓" -> {
                            if (!ac.getTentative().containsKey(e.getUserIdLong()) || ac.getTentative().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeTentative(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                        case "⛔" -> {
                            if (!ac.getNotAvailable().containsKey(e.getUserIdLong()) || ac.getNotAvailable().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeNotAvailable(e.getUserIdLong(), e.getGuild().getIdLong());
                        }
                    }
                    updateEmbeds(uuid);
                });
            }
        }
    }

    private boolean hasPermission(long guildID, List<Role> roles) {
        return wh.hasPermission(guildID, roles);
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

}
