package utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Settings {
    @JsonProperty("discordToken")
    private String discordToken;

    @JsonProperty("discordToken")
    public String getDiscordToken() {
        return discordToken;
    }

    @JsonProperty("discordToken")
    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
    }

    @JsonProperty("channelId")
    private String channelId;

    @JsonProperty("channelId")
    public String getChannelId() {
        return channelId;
    }

    @JsonProperty("channelId")
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @JsonProperty("chatFormat")
    private String chatFormat;

    @JsonProperty("chatFormat")
    public String getChatFormat() {
        return chatFormat;
    }

    @JsonProperty("chatFormat")
    public void setChatFormat(String chatFormat) {
        this.chatFormat = chatFormat;
    }

    @JsonProperty("joinFormat")
    private String joinFormat;

    @JsonProperty("joinFormat")
    public String getJoinFormat() {
        return joinFormat;
    }

    @JsonProperty("joinFormat")
    public void setJoinFormat(String joinFormat) {
        this.joinFormat = joinFormat;
    }

    @JsonProperty("leaveFormat")
    private String leaveFormat;

    @JsonProperty("leaveFormat")
    public String getLeaveFormat() {
        return leaveFormat;
    }

    @JsonProperty("leaveFormat")
    public void setLeaveFormat(String leaveFormat) {
        this.leaveFormat = leaveFormat;
    }

    @JsonProperty("announcementFormat")
    private String announcementFormat;

    @JsonProperty("announcementFormat")
    public String getAnnouncementFormat() {
        return announcementFormat;
    }

    @JsonProperty("announcementFormat")
    public void setAnnouncementFormat(String announcementFormat) {
        this.announcementFormat = announcementFormat;
    }

    @JsonProperty("deathFormat")
    private String deathFormat;

    @JsonProperty("deathFormat")
    public String getDeathFormat() {
        return deathFormat;
    }

    @JsonProperty("deathFormat")
    public void setDeathFormat(String deathFormat) {
        this.deathFormat = deathFormat;
    }

    @JsonProperty("startFormat")
    private String startFormat;

    @JsonProperty("startFormat")
    public String getStartFormat() {
        return startFormat;
    }

    @JsonProperty("startFormat")
    public void setStartFormat(String startFormat) { this.startFormat = startFormat; }

    @JsonProperty("stopFormat")
    private String stopFormat;

    @JsonProperty("stopFormat")
    public String getStopFormat() {
        return stopFormat;
    }

    @JsonProperty("stopFormat")
    public void setStopFormat(String stopFormat) {
        this.stopFormat = stopFormat;
    }

    @JsonProperty("advancementFormat")
    private String advancementFormat;

    @JsonProperty("advancementFormat")
    public String getAdvancementFormat() {
        return advancementFormat;
    }

    @JsonProperty("advancementFormat")
    public void setAdvancementFormat(String advancementFormat) {
        this.advancementFormat = advancementFormat;
    }

    @JsonProperty("emoteFormat")
    private String emoteFormat;

    @JsonProperty("emoteFormat")
    public String getEmoteFormat() {
        return emoteFormat;
    }

    @JsonProperty("emoteFormat")
    public void setEmoteFormat(String emoteFormat) {
        this.emoteFormat = emoteFormat;
    }

    @JsonProperty("discordToMinecraftFormat")
    private String discordToMinecraftFormat;

    @JsonProperty("discordToMinecraftFormat")
    public String getDiscordToMinecraftFormat() {
        return discordToMinecraftFormat;
    }

    @JsonProperty("discordToMinecraftFormat")
    public void setDiscordToMinecraftFormat(String discordToMinecraftFormat) {
        this.discordToMinecraftFormat = discordToMinecraftFormat;
    }
}
