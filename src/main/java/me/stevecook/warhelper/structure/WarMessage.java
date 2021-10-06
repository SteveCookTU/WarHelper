package me.stevecook.warhelper.structure;

public class WarMessage {

    private final long GUILD_ID;
    private final long CHANNEL_ID;
    private final long MESSAGE_ID;

    public WarMessage(long g, long c, long m) {
        GUILD_ID = g;
        CHANNEL_ID = c;
        MESSAGE_ID = m;
    }

    public long getGuildID() {
        return GUILD_ID;
    }

    public long getChannelID() {
        return CHANNEL_ID;
    }

    public long getMessageID() {
        return MESSAGE_ID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WarMessage wm) {
            return wm.MESSAGE_ID == MESSAGE_ID && wm.CHANNEL_ID == CHANNEL_ID && wm.GUILD_ID == GUILD_ID;
        }
        return false;
    }
}
