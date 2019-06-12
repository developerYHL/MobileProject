package com.example.mobileproject.model;

public class CommentItem {
    String comment;
    String nickname;

    public CommentItem(){

    }

    public CommentItem(String nickname, String comment, String downloadUrl, String uid) {
        this.nickname = nickname;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
