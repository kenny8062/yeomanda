package com.example.yeomanda.ListView;

public class ChatMessageItem {
    private String msg;
    private String userName;
    private String msgTime;
    private Boolean isMyChat;

    public String getMessage(){
        return msg;
    }

    public void setMessage(String msg){
        this.msg=msg;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName=userName;
    }

    public String getMsgTime() {return msgTime;}

    public void setMsgTime(String MsgTime) {
        this.msgTime=msgTime;
    }

    public Boolean getIsMyChat(){
        return isMyChat;
    }

    public void setIsMyChat(Boolean isMyChat){
        this.isMyChat=isMyChat;
    }
}
