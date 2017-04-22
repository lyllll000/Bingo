package com.skyrin.bingo.update;

/**
 * Created by 罗延林 on 2016/9/8 0008.
 * 服务器返回相关参数
 */
public class ServiceVrInfo {

    /**
     * downLoadUrl : http://skyrin-update.oss-cn-beijing.aliyuncs.com/bingo/app-wandoujia-release.apk
     * versionCode : 2
     * versionName : v1.0.1
     * versionRemark : 测试
     * isForceUpdate : false
     * md5: 服务器文件的md5 可与本地进行校验  如果一致则不用再次下载
     */

    private String downLoadUrl;
    private int versionCode;
    private String versionName;
    private String md5;
    /**
     * 升级日志
     */
    private String versionRemark;
    /**
     *  强制升级
     */
    private boolean isForceUpdate;

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

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

    public String getVersionRemark() {
        return versionRemark;
    }

    public void setVersionRemark(String versionRemark) {
        this.versionRemark = versionRemark;
    }

    public boolean isIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
