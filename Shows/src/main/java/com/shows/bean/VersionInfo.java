package com.shows.bean;

/**
 * 记录版本信息
 * @date    2016年5月24日
 * @version v1.0
 */
public class VersionInfo {

	private int versionCode;//版本号
	private String versionName;//版本名称
	private String downUrl;//下载地址
	private String content;//更新的内容
	private String apkSize;//文件大小
	private boolean isForce;	//是否强制升级
	
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getApkSize() {
		return apkSize;
	}
	public void setApkSize(String apkSize) {
		this.apkSize = apkSize;
	}
	public boolean isForce() {
		return isForce;
	}
	public void setForce(boolean isForce) {
		this.isForce = isForce;
	}

}
