package me.stevecook.warhelper.structure;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlertConnector {

    private final UUID code;
    private final Map<Long, Long> tanks;
    private final Map<Long, Long> erdps;
    private final Map<Long, Long> prdps;
    private final Map<Long, Long> mdps;
    private final Map<Long, Long> healers;
    private final Map<Long, Long> tentative;
    private final Map<Long, Long> notAvailable;
    private final Map<Long, Long> artillery;
    private final List<WarMessage> warMessages;

    public AlertConnector(String toEncode) {
        code = UUID.nameUUIDFromBytes(toEncode.getBytes());
        tanks = new HashMap<>();
        erdps = new HashMap<>();
        prdps = new HashMap<>();
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

    public void addERDPS(long userID, long guildID) {
        if (!getUsers().contains(userID))
            erdps.put(userID, guildID);
    }

    public void addPRDPS(long userID, long guildID) {
        if (!getUsers().contains(userID))
            prdps.put(userID, guildID);
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

    public void removeERDPS(long userID, long guildID) {
        if (erdps.containsKey(userID) && erdps.get(userID) == guildID) {
            erdps.remove(userID);
        }
    }

    public void removePRDPS(long userID, long guildID) {
        if (prdps.containsKey(userID) && prdps.get(userID) == guildID) {
            prdps.remove(userID);
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

    public Map<Long, Long> getERDPS() {
        return erdps;
    }

    public Map<Long, Long> getPRDPS() { return prdps; }

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

    public boolean removeWarMessage(long guildID, long channelID, long messageID) {
        return warMessages.remove(new WarMessage(guildID, channelID, messageID));
    }

    public List<Long> getGuildIDs() {
        List<Long> ids = new ArrayList<>();
        for (WarMessage wm :
                warMessages) {
            if(!ids.contains(wm.getGuildID()))
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

    public boolean channelContainsWarMessage(long guildID, long channelID) {
        for(WarMessage wm : warMessages) {
            if(wm.getGuildID() == guildID && wm.getChannelID() == channelID) {
                return true;
            }
        }
        return false;
    }

    public List<Long> getUsers() {
        return Stream.of(tanks.keySet(), erdps.keySet(), prdps.keySet(), mdps.keySet(), healers.keySet(), tentative.keySet(), notAvailable.keySet(), artillery.keySet()).flatMap(Set::stream).collect(Collectors.toList());
    }

    public List<WarMessage> getWarMessages() {
        return warMessages;
    }

    public UUID getCode() {
        return code;
    }

}
