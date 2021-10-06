package me.stevecook.warhelper.structure;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlertConnector {

    private final UUID code;
    private final Map<Long, Long> tanks;
    private final Map<Long, Long> rdps;
    private final Map<Long, Long> mdps;
    private final Map<Long, Long> healers;
    private final Map<Long, Long> tentative;
    private final Map<Long, Long> notAvailable;
    private final Map<Long, Long> artillery;
    private final List<WarMessage> warMessages;

    public AlertConnector(String toEncode) {
        code = UUID.nameUUIDFromBytes(toEncode.getBytes());
        tanks = new HashMap<>();
        rdps = new HashMap<>();
        mdps = new HashMap<>();
        healers = new HashMap<>();
        tentative = new HashMap<>();
        notAvailable = new HashMap<>();
        artillery = new HashMap<>();
        warMessages = new ArrayList<>();
    }

    public void addTank(long userID, long guildID) {
        if (!getUsers().contains(userID))
            tanks.put(userID, guildID);
    }

    public void addRDPS(long userID, long guildID) {
        if (!getUsers().contains(userID))
            rdps.put(userID, guildID);
    }

    public void addMDPS(long userID, long guildID) {
        if (!getUsers().contains(userID))
            mdps.put(userID, guildID);
    }

    public void addHealer(long userID, long guildID) {
        if (!getUsers().contains(userID))
            healers.put(userID, guildID);
    }

    public void addTentative(long userID, long guildID) {
        if (!getUsers().contains(userID))
            tentative.put(userID, guildID);
    }

    public void addNotAvailable(long userID, long guildID) {
        if (!getUsers().contains(userID))
            notAvailable.put(userID, guildID);
    }

    public void addArtillery(long userID, long guildID) {
        if (!getUsers().contains(userID))
            artillery.put(userID, guildID);
    }

    public void removeTank(long userID, long guildID) {
        if (tanks.containsKey(userID) && tanks.get(userID) == guildID) {
            tanks.remove(userID);
        }
    }

    public void removeRDPS(long userID, long guildID) {
        if (rdps.containsKey(userID) && rdps.get(userID) == guildID) {
            rdps.remove(userID);
        }
    }

    public void removeMDPS(long userID, long guildID) {
        if (mdps.containsKey(userID) && mdps.get(userID) == guildID) {
            mdps.remove(userID);
        }
    }

    public void removeHealer(long userID, long guildID) {
        if (healers.containsKey(userID) && healers.get(userID) == guildID) {
            healers.remove(userID);
        }
    }

    public void removeTentative(long userID, long guildID) {
        if (tentative.containsKey(userID) && tentative.get(userID) == guildID) {
            tentative.remove(userID);
        }
    }

    public void removeNotAvailable(long userID, long guildID) {
        if (notAvailable.containsKey(userID) && notAvailable.get(userID) == guildID) {
            notAvailable.remove(userID);
        }
    }

    public void removeArtillery(long userID, long guildID) {
        if (artillery.containsKey(userID) && artillery.get(userID) == guildID) {
            artillery.remove(userID);
        }
    }

    public Map<Long, Long> getTanks() {
        return tanks;
    }

    public Map<Long, Long> getRDPS() {
        return rdps;
    }

    public Map<Long, Long> getMDPS() {
        return mdps;
    }

    public Map<Long, Long> getHealers() {
        return healers;
    }

    public Map<Long, Long> getTentative() {
        return tentative;
    }

    public Map<Long, Long> getNotAvailable() {
        return notAvailable;
    }

    public Map<Long, Long> getArtillery() {
        return artillery;
    }

    public void addWarMessage(long guildID, long channelID, long messageID) {
        warMessages.add(new WarMessage(guildID, channelID, messageID));
    }

    public void removeWarMessage(long guildID, long channelID, long messageID) {
        warMessages.remove(new WarMessage(guildID, channelID, messageID));
    }

    public List<Long> getGuildIDs() {
        List<Long> ids = new ArrayList<>();
        for (WarMessage wm :
                warMessages) {
            ids.add(wm.getGuildID());
        }
        return ids;
    }

    public boolean containsWarMessage(long guildID, long channelID, long messageID) {
        WarMessage wm = new WarMessage(guildID, channelID, messageID);
        for (WarMessage wm2 : warMessages) {
            if (wm.equals(wm2)) {
                return true;
            }
        }
        return false;
    }

    public List<Long> getUsers() {
        return Stream.of(tanks.keySet(), rdps.keySet(), mdps.keySet(), healers.keySet(), tentative.keySet(), notAvailable.keySet(), artillery.keySet()).flatMap(Set::stream).collect(Collectors.toList());
    }

    public List<WarMessage> getWarMessages() {
        return warMessages;
    }

    public UUID getCode() {
        return code;
    }

}
