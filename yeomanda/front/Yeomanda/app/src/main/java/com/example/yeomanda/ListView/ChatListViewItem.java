package com.example.yeomanda.ListView;

public class ChatListViewItem {

    private String chatListTeamName;
    private String chatListContent;
    private String chatListTime;

    public void setChatListTeamName(String chatListTeamName) {
        this.chatListTeamName = chatListTeamName;
    }
    public void setChatListContent(String chatListContent) {
        this.chatListContent = chatListContent;
    }
    public void setChatListTime(String chatListTime) {
        this.chatListTime = chatListTime;
    }


    public String getChatListTeamName() {
        return this.chatListTeamName;
    }
    public String getChatListContent() {
        return this.chatListContent;
    }
    public String getChatListTime() {
        return this.chatListTime;
    }
}
