package com.shows.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/5/9 0009.
 */
public class InfoDeatail {

    private String id;
    private String writer;
    private String content;
    private String heade;
    private String tel;
    private String time;
    private List<String> pics;

    private String likedNum;
    private String  commentNum;


    public String getLikedNum() {
        return likedNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setLikedNum(String likedNum) {
        this.likedNum = likedNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeade(String heade) {
        this.heade = heade;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getWriter() {
        return writer;
    }

    public String getContent() {
        return content;
    }

    public String getHeade() {
        return heade;
    }

    public String getTel() {
        return tel;
    }

    public String getTime() {
        return time;
    }

    public List<String> getPics() {
        return pics;
    }
}
