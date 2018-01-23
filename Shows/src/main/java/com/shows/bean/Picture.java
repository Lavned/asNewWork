package com.shows.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/4/29 0029.
 */
public class Picture {
    private String id;
    private String address;
    private String content;
    private String header;
    private String tel;
    private String title;
    private List<String> pics;
    private String status;
    private String stime;
    private String route;
    private String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setTextDetail(String textDetail) {
        this.textDetail = textDetail;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public void setHttpAddress(String httpAddress) {
        this.httpAddress = httpAddress;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String httpAddress;
    private String code;
    private List<String> images;
    private String textDetail;


    public String getTextDetail() {
        return textDetail;
    }

    public List<String> getImages() {
        return images;
    }

    public String getCode() {
        return code;
    }

    public String getHttpAddress() {
        return httpAddress;
    }

    public String getStime() {
        return stime;
    }




    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
