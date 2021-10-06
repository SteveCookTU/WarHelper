package me.stevecook.warhelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.stevecook.warhelper.listeners.ReactionListener;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.WarMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WarHelper {

    private final List<AlertConnector> alertConnectors;
    private final List<AlertConnector> alertArchive;

    private final JDA jda;
    private final Gson gson;

    public WarHelper() throws LoginException, IOException {
        String token = new String(Files.readAllBytes(Path.of("token.txt")));

        jda = JDABuilder.createDefault(token).setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .setActivity(Activity.competing("WAR"))
                .addEventListeners(new ReactionListener(this))
                .build();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();

        alertConnectors = loadActiveAlerts();
        alertArchive = loadArchivedAlerts();
    }

    public static void main(String[] args) throws LoginException, IOException {
        WarHelper warHelper = new WarHelper();
    }

    public AlertConnector getAlertConnector(UUID uuid) {
        for (AlertConnector a : alertConnectors) {
            if (a.getCode().equals(uuid)) {
                return a;
            }
        }
        return null;
    }

    public void addWarMessage(long guildID, long channelID, long messageID, String toEncode) {
        AlertConnector ac = getAlertConnector(UUID.nameUUIDFromBytes(toEncode.getBytes()));
        if (ac == null) {
            ac = createAlertConnectors(toEncode);
        }
        ac.addWarMessage(guildID, channelID, messageID);
    }

    public boolean isValidWarMessage(long guildID, long channelID, long messageID) {
        for (AlertConnector ac : alertConnectors) {
            if (ac.containsWarMessage(guildID, channelID, messageID)) {
                return true;
            }
        }
        return false;
    }

    public AlertConnector createAlertConnectors(String toEncode) {
        AlertConnector ac = new AlertConnector(toEncode);
        alertConnectors.add(ac);
        return ac;
    }

    public void archiveAlertConnector(UUID uuid) {
        AlertConnector ac = getAlertConnector(uuid);
        alertConnectors.remove(ac);
        alertArchive.add(ac);
        for (WarMessage wm :
                ac.getWarMessages()) {
            Guild g = jda.getGuildById(wm.getGuildID());
            if (g != null) {
                TextChannel tc = g.getTextChannelById(wm.getChannelID());
                if (tc != null) {
                    tc.retrieveMessageById(wm.getMessageID()).queue(message -> {
                        MessageEmbed mb = message.getEmbeds().get(0);
                        if (mb != null) {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle(mb.getTitle());
                            for (MessageEmbed.Field f :
                                    mb.getFields()) {
                                eb.addField(f);
                            }
                            eb.setFooter(Objects.requireNonNull(mb.getFooter()).getText() + " - Archived");

                            message.editMessageEmbeds(eb.build()).queue(message1 -> message1.clearReactions().queue());

                        }
                    });
                }
            }
        }
    }

    public JDA getJda() {
        return jda;
    }

    public List<AlertConnector> loadActiveAlerts() throws IOException {
        if (Files.exists(Path.of("connectors.json"))) {
            return gson.fromJson(new String(Files.readAllBytes(Path.of("connectors.json"))), new TypeToken<List<AlertConnector>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    public List<AlertConnector> loadArchivedAlerts() throws IOException {
        if (Files.exists(Path.of("archive.json"))) {
            return gson.fromJson(new String(Files.readAllBytes(Path.of("archive.json"))), new TypeToken<List<AlertConnector>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    public void saveData() throws IOException {
        Type type = new TypeToken<List<AlertConnector>>() {
        }.getType();
        File f = new File("connectors.json");
        File f2 = new File("archive.json");
        boolean success = f.createNewFile();
        success = f2.createNewFile();

        FileWriter fr = new FileWriter(f);
        fr.write(gson.toJson(alertConnectors, type));
        fr.flush();
        fr.close();

        fr = new FileWriter(f2);
        fr.write(gson.toJson(alertArchive, type));
        fr.flush();
        fr.close();
    }

}
