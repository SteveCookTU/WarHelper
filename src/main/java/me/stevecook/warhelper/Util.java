package me.stevecook.warhelper;

import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.UserData;
import me.stevecook.warhelper.structure.WarMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Util {

    public static final String[] REACTIONS = {"\uD83D\uDEE1", "\uD83D\uDDE1", "\uD83C\uDFF9", "\uD83E\uDE84", "❤", "\uD83D\uDCA5", "❓", "⛔"};
    public static final String[] EMOJI_LETTERS = {"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1", "\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE", "\uD83C\uDDFF"};

    public static void updateEmbeds(UUID uuid, WarHelper wh) {
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

                    fillEmbed(eb, uuid, wh);

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

    public static void fillEmbed(EmbedBuilder eb, UUID uuid, WarHelper wh) {
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

    public static String convertToEmoji(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '_')
                sb.append(EMOJI_LETTERS[s.toUpperCase().charAt(i) - 97]);
            else
                sb.append("\t");
        }
        return sb.toString();
    }
}
