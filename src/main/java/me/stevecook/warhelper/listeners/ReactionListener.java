package me.stevecook.warhelper.listeners;

import me.stevecook.warhelper.Util;
import me.stevecook.warhelper.WarHelper;
import me.stevecook.warhelper.structure.AlertConnector;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;
import java.util.UUID;

public class ReactionListener extends ListenerAdapter {

    private final WarHelper wh;

    public ReactionListener(WarHelper wh) {
        this.wh = wh;
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
                    UUID uuid = UUID.fromString(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(mb.getFooter()).getText()).substring(0, 36)));
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
                    wh.updateAlertConnector(ac);
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
                    UUID uuid = UUID.fromString(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(mb.getFooter()).getText()).substring(0, 36)));
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
                    wh.updateAlertConnector(ac);
                    Util.updateEmbeds(uuid, wh);
                });
            }
        }
    }

}
