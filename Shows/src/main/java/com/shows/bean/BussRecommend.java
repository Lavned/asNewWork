package com.shows.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1 0001.
 */
public class BussRecommend {

    public String getId() {
        return id;
    }

    public String getBirfe() {
        return birfe;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getHttpaddress() {
        return httpaddress;
    }

    public String getContent() {
        return content;
    }

    public String getLinkman() {
        return linkman;
    }

    public String getInfo() {
        return info;
    }

    public String getTel() {
        return tel;
    }

    public String getFeature() {
        return feature;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirfe(String birfe) {
        this.birfe = birfe;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setHttpaddress(String httpaddress) {
        this.httpaddress = httpaddress;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    private String id;
    private String birfe;
    private String title;
    private String address;
    private String httpaddress;
    private String content;
    private String linkman;
    private String info;
    private String tel;
    private String feature;
    private List<String> pics;
    private String headimage;

    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }
}
