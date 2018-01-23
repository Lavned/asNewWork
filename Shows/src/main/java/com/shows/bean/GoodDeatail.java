package com.shows.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/5/9 0009.
 */
public class GoodDeatail {

    private  String id;
    private String accom;
    private String content;
    private String header;
    private String describes;
    private String infor;
    private String post;
    private String price;
    private String name;
    private String sum;
    private String fnum;

    public String getFnum() {
        return fnum;
    }

    public void setFnum(String fnum) {
        this.fnum = fnum;
    }

    private List<String> luckyCustom;

    public List<String> getLuckyCustom() {
        return luckyCustom;
    }

    public void setLuckyCustom(List<String> luckyCustom) {
        this.luckyCustom = luckyCustom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    private List<String> pics;

    private List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setAccom(String accom) {
        this.accom = accom;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getAccom() {
        return accom;
    }

    public String getContent() {
        return content;
    }



    public String getDescribes() {
        return describes;
    }

    public String getInfor() {
        return infor;
    }

    public String getPost() {
        return post;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public List<String> getPics() {
        return pics;
    }
}
