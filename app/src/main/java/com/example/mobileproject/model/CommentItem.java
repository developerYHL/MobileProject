package com.example.mobileproject.model;

public class CommentItem {
    String contents;
    String nickname;

    public CommentItem(){

    }

    public CommentItem(String nickname, String comment) {
        this.nickname = nickname;
        this.contents = comment;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
