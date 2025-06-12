package net.alliancecraft.AllianceBotWebApi.services;

public class SystemInfo {
    public long memory, ping, hd, uptime;

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public long getPing() {
        return ping;
    }

    public void setPing(long ping) {
        this.ping = ping;
    }

    public long getHd() {
        return hd;
    }

    public void setHd(long hd) {
        this.hd = hd;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }
}
