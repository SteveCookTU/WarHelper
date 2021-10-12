package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.Util;
import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import net.dv8tion.jda.api.EmbedBuilder;
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
                        Util.updateEmbeds(UUID.fromString(args[1]), wh);
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
                    Util.updateEmbeds(uuid, wh);
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
                    Util.updateEmbeds(uuid, wh);
                });
            }
        }
    }

    private boolean hasPermission(long guildID, List<Role> roles) {
        return wh.hasPermission(guildID, roles);
    }

}
