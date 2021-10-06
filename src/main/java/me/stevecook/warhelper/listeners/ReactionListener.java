package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.WarMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.isFromGuild()) {
            if (Objects.requireNonNull(e.getMember()).isOwner() || hasPermission(e.getMember())) {
                String[] args = e.getMessage().getContentRaw().split(" ");
                if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("!waralert")) {
                        LocalDate date;
                        LocalTime time;
                        try {
                            date = LocalDate.parse(args[2], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                            time = LocalTime.parse(args[3], DateTimeFormatter.ofPattern("hh:mma"));
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
                                eb.addField(":archery: RDPS :archery:", "", true);
                                eb.addBlankField(false);
                                eb.addField(":dagger: MDPS :dagger:", "", true);
                                eb.addBlankField(true);
                                eb.addField(":heart: HEALER :heart:", "", true);
                                eb.addBlankField(false);
                                eb.addField(":boom: HEALER :boom:", "", true);
                                eb.addBlankField(false);
                                eb.addField(":question: Tentative :question:", "", true);
                                eb.addBlankField(true);
                                eb.addField(":no_entry: Not Available :no_entry:", "", true);
                                eb.addBlankField(false);
                            }
                            eb.setFooter(uuid.toString());
                            e.getChannel().sendMessage("everyone").queue(m -> m.editMessageEmbeds(eb.build()).queue(message -> {
                                message.addReaction("\uD83D\uDEE1").queue();
                                message.addReaction("\uD83C\uDFF9").queue();
                                message.addReaction("\uD83D\uDDE1").queue();
                                message.addReaction("❤").queue();
                                message.addReaction("\uD83D\uDCA5").queue();
                                message.addReaction("❓").queue();
                                message.addReaction("⛔").queue();
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
                        case "\uD83C\uDFF9" -> {
                            if (ac.getRDPS().containsKey(e.getUserIdLong()))
                                return;
                            ac.addRDPS(e.getUserIdLong(), e.getGuild().getIdLong());
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
                        case "\uD83C\uDFF9" -> {
                            if (!ac.getRDPS().containsKey(e.getUserIdLong()) || ac.getRDPS().get(e.getUserIdLong()) != e.getGuild().getIdLong())
                                return;
                            ac.removeRDPS(e.getUserIdLong(), e.getGuild().getIdLong());
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

    private boolean hasPermission(@NotNull Member m) {
        return wh.hasPermission(m.getGuild().getName(), m.getRoles());
    }

    private void fillEmbed(EmbedBuilder eb, UUID uuid) {
        JDA jda = wh.getJda();
        AlertConnector ac = wh.getAlertConnector(uuid);
        Guild temp;
        StringBuilder tanks = new StringBuilder();
        StringBuilder rdps = new StringBuilder();
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
                        tanks.append(" - ").append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName()).append("\n");
                    }
                }
            }

            if (ac.getRDPS().containsValue(l)) {
                rdps.append(guildInitials).append("\n");
                for (long id :
                        ac.getRDPS().keySet()) {
                    if (ac.getRDPS().get(id) == l) {
                        temp = jda.getGuildById(ac.getRDPS().get(id));
                        assert temp != null;
                        rdps.append(" - ").append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName()).append("\n");
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
                        mdps.append(" - ").append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName()).append("\n");
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
                        healers.append(" - ").append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName()).append("\n");
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
                        tentative.append(" - ").append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName()).append("\n");
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
                        notAvailable.append(" - ").append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName()).append("\n");
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
                        artillery.append(" - ").append(Objects.requireNonNull(temp.getMemberById(id)).getEffectiveName()).append("\n");
                    }
                }
            }
        }

        eb.addField(":shield: TANK :shield:", tanks.toString().trim(), true);
        eb.addBlankField(true);
        eb.addField(":archery: RDPS :archery:", rdps.toString().trim(), true);
        eb.addBlankField(false);
        eb.addField(":dagger: MDPS :dagger:", mdps.toString().trim(), true);
        eb.addBlankField(true);
        eb.addField(":heart: HEALER :heart:", healers.toString().trim(), true);
        eb.addBlankField(false);
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

                eb.setFooter(Objects.requireNonNull(original.getFooter()).getText());

                message.editMessageEmbeds(eb.build()).queue();

            });
        }
    }

}
