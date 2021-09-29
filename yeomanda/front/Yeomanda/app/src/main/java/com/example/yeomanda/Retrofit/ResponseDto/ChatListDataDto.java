package com.example.yeomanda.Retrofit.ResponseDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatListDataDto {
    @SerializedName("room_id")
    @Expose
    private String roomId;
    @SerializedName("otherTeamName")
    @Expose
    private String otherTeamName;
    @SerializedName("chatMessages")
    @Expose
    private ChatMessages chatMessages;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOtherTeamName() {
        return otherTeamName;
    }

    public void setOtherTeamName(String otherTeamName) {
        this.otherTeamName = otherTeamName;
    }

    public ChatMessages getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ChatMessages chatMessages) {
        this.chatMessages = chatMessages;
    }
}
