package me.stevecook.warhelper;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import me.stevecook.warhelper.listeners.ReactionListener;
import me.stevecook.warhelper.listeners.SlashCommandListener;
import me.stevecook.warhelper.structure.AlertConnector;
import me.stevecook.warhelper.structure.RegisterSlashCommands;
import me.stevecook.warhelper.structure.UserData;
import me.stevecook.warhelper.structure.WarMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bson.Document;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class WarHelper {

    private final List<AlertConnector> alertConnectors;
    private final List<AlertConnector> alertArchive;
    private final Map<Long, List<Long>> permissions;
    private final Map<Long, UserData> userDataMap;

    private final JDA jda;
    private final Gson gson;
    private final MongoClient mongoClient;

    public WarHelper() throws LoginException, IOException {
        String token = new String(Files.readAllBytes(Path.of("token.txt")));

        if(Files.exists(Path.of("dbLogin.txt"))) {
            String dbLogin = new String(Files.readAllBytes(Path.of("dbLogin.txt")));
            ConnectionString connectionString = new ConnectionString(dbLogin);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .build();
            mongoClient = MongoClients.create(settings);
        } else {
            mongoClient = null;
        }

        jda = JDABuilder.createDefault(token).setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .setActivity(Activity.competing("WAR"))
                .addEventListeners(new ReactionListener(this), new SlashCommandListener(this))
                .build();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();

        alertConnectors = loadActiveAlerts();
        alertArchive = loadArchivedAlerts();
        permissions = loadPermissions();
        userDataMap = loadUserData();
    }

    public static void main(String[] args) throws LoginException, IOException {
        WarHelper warHelper = new WarHelper();
    }

    public AlertConnector getAlertConnector(UUID uuid) {
        for (AlertConnector a : getAlertConnectors()) {
            if (a.getCode().equals(uuid)) {
                return a;
            }
        }
        return null;
    }

    public void addWarMessage(long guildID, long channelID, long messageID, String toEncode, String date, String time, String server, String faction, String territory) {
        AlertConnector ac = getAlertConnector(UUID.nameUUIDFromBytes(toEncode.getBytes()));
        if (ac == null) {
            ac = createAlertConnectors(toEncode, date, time, server, faction, territory);
        }
        ac.addWarMessage(guildID, channelID, messageID);
        updateAlertConnector(ac);
    }

    public void removeWarMessage(long guildID, long channelID, long messageID) {
        for (AlertConnector a : getAlertConnectors()) {
            if(a.removeWarMessage(guildID, channelID, messageID)) {
                updateAlertConnector(a);
                return;
            }
        }

        for (AlertConnector a : alertArchive) {
            if(a.removeWarMessage(guildID, channelID, messageID)) {
                updateAlertArchive(a);
                return;
            }
        }
    }

    public boolean channelContainsWarMessage(long guildID, long channelID, UUID uuid) {
        for(AlertConnector ac : getAlertConnectors()) {
            if(ac.getCode().equals(uuid) && ac.channelContainsWarMessage(guildID, channelID))
                return true;
        }
        return false;
    }

    public UserData addUserData(long userID) {
        if(mongoClient != null) {
            MongoCollection<Document> userDataCol = mongoClient.getDatabase("warhelperDB").getCollection("UserData");
            Document result = userDataCol.find(Filters.exists(Long.toString(userID)))
                    .projection(Projections.excludeId())
                    .first();
            if(result == null) {
                UserData userData = new UserData();
                JsonObject jsonObject = new JsonObject();
                jsonObject.add(Long.toString(userID), gson.toJsonTree(userData, new TypeToken<UserData>(){}.getType()));
                userDataCol.insertOne(Document.parse(jsonObject.toString()));
                return userData;
            }
        } else {
            if(!userDataMap.containsKey(userID)) {
                UserData newData = new UserData();
                userDataMap.put(userID, newData);
                return newData;
            }
        }
        return null;
    }

    public UserData getUserData(long userID) {
        if(mongoClient != null) {
            MongoCollection<Document> userDataCol = mongoClient.getDatabase("warhelperDB").getCollection("UserData");
            Document result = userDataCol.find(Filters.exists(Long.toString(userID)))
                    .projection(Projections.excludeId())
                    .first();
            if(result != null) {
                Map<Long, UserData> temp = gson.fromJson(result.toJson(), new TypeToken<Map<Long, UserData>>(){}.getType());
                return temp.get(userID);
            }
        } else {
            if(userDataMap.containsKey(userID))
                return userDataMap.get(userID);
        }
        return addUserData(userID);
    }

    public void updateUserData(long userID, UserData userData) {
        if(mongoClient != null) {
            MongoCollection<Document> userDataCol = mongoClient.getDatabase("warhelperDB").getCollection("UserData");
            Document result = userDataCol.find(Filters.exists(Long.toString(userID))).projection(Projections.excludeId()).first();
            if(result != null) {
                Map<Long, UserData> temp = new TreeMap<>();
                temp.put(userID, userData);
                Document d = Document.parse(gson.toJson(temp, new TypeToken<Map<Long, UserData>>(){}.getType()));
                userDataCol.deleteOne(Filters.exists(Long.toString(userID)));
                JsonObject jsonObject = new JsonObject();
                jsonObject.add(Long.toString(userID), gson.toJsonTree(userData, new TypeToken<UserData>(){}.getType()));
                userDataCol.insertOne(Document.parse(jsonObject.toString()));
            }
        }
    }

    public boolean hasPermission(long guildID, List<Role> roles) {
        if(mongoClient != null) {
            MongoCollection<Document> permCol = mongoClient.getDatabase("warhelperDB").getCollection("Permissions");
            Document result = permCol.find(Filters.exists(Long.toString(guildID))).projection(Projections.excludeId()).first();
            if(result != null) {
                Map<Long, List<Long>> temp = gson.fromJson(result.toJson(), new TypeToken<Map<Long, List<Long>>>(){}.getType());
                return roles.stream().anyMatch(r -> temp.get(guildID).contains(r.getIdLong()));
            } else {
                return false;
            }
        }
        return permissions.containsKey(guildID) && roles.stream().anyMatch(r -> permissions.get(guildID).contains(r.getIdLong()));
    }

    public void addPermission(long guildID, long roleID) {
        if(mongoClient != null) {
            MongoCollection<Document> permCol = mongoClient.getDatabase("warhelperDB").getCollection("Permissions");
            Document result = permCol.find(Filters.exists(Long.toString(guildID))).projection(Projections.excludeId()).first();
            if(result != null) {
                permCol.updateOne(Filters.exists(Long.toString(guildID)), Updates.addToSet(Long.toString(guildID), roleID), new UpdateOptions().upsert(true));
            } else {
                JsonObject jsonObject = new JsonObject();
                List<Long> roleIDs = new ArrayList<>();
                roleIDs.add(roleID);
                jsonObject.add(Long.toString(guildID), gson.toJsonTree(roleIDs, new TypeToken<List<Long>>(){}.getType()));
                permCol.insertOne(Document.parse(jsonObject.toString()));
            }
        } else {
            if(permissions.containsKey(guildID) && !permissions.get(guildID).contains(roleID)) {
                permissions.get(guildID).add(roleID);
            } else {
                List<Long> temp = new ArrayList<>();
                temp.add(roleID);
                permissions.put(guildID, temp);
            }
        }

    }

    public void removePermission(long guildID, long roleID){
        if(mongoClient != null) {
            MongoCollection<Document> permCol = mongoClient.getDatabase("warhelperDB").getCollection("Permissions");
            Document result = permCol.find(Filters.exists(Long.toString(guildID))).projection(Projections.excludeId()).first();
            if(result != null) {
                permCol.updateOne(Filters.exists(Long.toString(guildID)), Updates.pull(Long.toString(guildID), roleID));
            }
        } else {
            if(permissions.containsKey(guildID)) {
                permissions.get(guildID).remove(roleID);
            }
        }
    }

    public boolean isValidWarMessage(long guildID, long channelID, long messageID) {
        for (AlertConnector ac : getAlertConnectors()) {
            if (ac.containsWarMessage(guildID, channelID, messageID)) {
                return true;
            }
        }
        return false;
    }

    public AlertConnector createAlertConnectors(String toEncode, String date, String time, String server, String faction, String territory) {
        AlertConnector ac = new AlertConnector(toEncode, date, time, server, faction, territory);
        if(mongoClient != null) {
            MongoCollection<Document> acCol = mongoClient.getDatabase("warhelperDB").getCollection("AlertConnectors");
            Document result = acCol.find(Filters.eq("code", UUID.nameUUIDFromBytes(toEncode.getBytes()).toString())).projection(Projections.excludeId()).first();
            if(result == null) {
                acCol.insertOne(Document.parse(gson.toJson(ac, new TypeToken<AlertConnector>(){}.getType())));
            } else {
                return null;
            }
        } else {
            if(!alertConnectors.contains(ac))
                alertConnectors.add(ac);
            else
                return null;
        }
        return ac;
    }

    public void updateAlertConnector(AlertConnector ac) {
        if(mongoClient != null) {
            MongoCollection<Document> acCol = mongoClient.getDatabase("warhelperDB").getCollection("AlertConnectors");
            Document result = acCol.find(Filters.eq("code", ac.getCode().toString())).projection(Projections.excludeId()).first();
            if(result != null) {
                acCol.deleteOne(Filters.eq("code", ac.getCode().toString()));
                acCol.insertOne(Document.parse(gson.toJson(ac, new TypeToken<AlertConnector>(){}.getType())));
            }
        }
    }

    public void updateAlertArchive(AlertConnector ac) {
        if(mongoClient != null) {
            MongoCollection<Document> aaCol = mongoClient.getDatabase("warhelperDB").getCollection("AlertArchive");
            Document result = aaCol.find(Filters.eq("code", ac.getCode().toString())).projection(Projections.excludeId()).first();
            if(result != null) {
                aaCol.deleteOne(Filters.eq("code", ac.getCode().toString()));
                aaCol.insertOne(Document.parse(gson.toJson(ac, new TypeToken<AlertConnector>(){}.getType())));
            }
        }
    }

    public void archiveAlertConnector(UUID uuid) {
        if(mongoClient != null) {
            MongoCollection<Document> acCol = mongoClient.getDatabase("warhelperDB").getCollection("AlertConnectors");
            Document result = acCol.find(Filters.eq("code", uuid.toString())).projection(Projections.excludeId()).first();
            if(result != null) {
                acCol.deleteOne(result);
                MongoCollection<Document> aaCol = mongoClient.getDatabase("warhelperDB").getCollection("AlertArchive");
                aaCol.insertOne(result);
            }
        } else {
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
    }

    public List<AlertConnector> getAlertConnectorsWithUserID(long userID) {
        return getAlertConnectors().stream().filter(ac -> ac.getUsers().contains(userID)).collect(Collectors.toList());
    }

    public List<AlertConnector> getAlertConnectors() {
        if(mongoClient != null) {
            MongoCollection<Document> acCol = mongoClient.getDatabase("warhelperDB").getCollection("AlertConnectors");
            FindIterable<Document> results = acCol.find();
            List<AlertConnector> connectors = new ArrayList<>();
            for (Document d :
                    results) {
                connectors.add(gson.fromJson(d.toJson(), new TypeToken<AlertConnector>(){}.getType()));
            }
            return connectors;
        }
        return alertConnectors;
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

    public Map<Long, List<Long>> loadPermissions() throws IOException {
        if(Files.exists(Path.of("permissions.json"))) {
            return gson.fromJson(new String(Files.readAllBytes(Path.of("permissions.json"))), new TypeToken<Map<Long, List<Long>>>() {
            }.getType());
        }
        return new HashMap<>();
    }

    public Map<Long, UserData> loadUserData() throws IOException {
        if(Files.exists(Path.of("userdata.json"))) {
            return gson.fromJson(new String(Files.readAllBytes(Path.of("userdata.json"))), new TypeToken<Map<Long, UserData>>() {
            }.getType());
        }
        return new TreeMap<>();
    }

    public void saveData() throws IOException {
            Type type = new TypeToken<List<AlertConnector>>() {
            }.getType();

            Type type2 = new TypeToken<Map<Long, List<Long>>>(){}.getType();

            Type type3 = new TypeToken<Map<Long, UserData>>(){}.getType();

            File f = new File("connectors.json");
            File f2 = new File("archive.json");
            File f3 = new File("permissions.json");
            File f4 = new File("userdata.json");
            boolean success = f.createNewFile();
            success = f2.createNewFile();
            success = f3.createNewFile();
            success = f4.createNewFile();

            FileWriter fr = new FileWriter(f);
            fr.write(gson.toJson(alertConnectors, type));
            fr.flush();
            fr.close();

            fr = new FileWriter(f2);
            fr.write(gson.toJson(alertArchive, type));
            fr.flush();
            fr.close();

            fr = new FileWriter(f3);
            fr.write(gson.toJson(permissions, type2));
            fr.flush();
            fr.close();

            fr = new FileWriter(f4);
            fr.write(gson.toJson(userDataMap, type3));
            fr.flush();
            fr.close();
    }
}
