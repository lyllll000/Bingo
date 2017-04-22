package com.skyrin.bingo.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.blankj.utilcode.utils.StringUtils;
import com.google.gson.Gson;
import com.skyrin.bingo.AppConstants;
import com.skyrin.bingo.common.util.MetaUtil;
import com.skyrin.bingo.common.util.StringUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 罗延林 on 2016/9/8 0008.
 */
public class AppUpdate {

    private static final String TAG = "AppUpdate";

    /**
     * 上下文
     */
    Context context;

    /**
     * xml地址
     */
    String checkUrl;

    ProgressDialog pd = null;

    public static boolean isNeedUpdate = false;

    //保存的文件名
    public static String apkCacheName = "app.apk";

//    /**
//     * 对话框view
//     */
//    View view;

    /**
     * 检查更新失败
     */
    static final int CHECK_FAILED = -1;
    /**
     * 检查更新成功
     */
    static final int CHECK_SUCCESSFUL = 1;
    /**
     * 需要更新
     */
    static final int CHECK_NEED_UPDATE = 2;
    /**
     * 不需要更新
     */
    static final int CHECK_NOT_NEED_UPDATE = 3;
    /**
     * 返回数据有误
     */
    static final int CHECK_DATA_ERR = 4;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_FAILED:
                    Toast.makeText(context, "检查更新失败，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                case CHECK_SUCCESSFUL:
                    break;
                case CHECK_NEED_UPDATE:
                    isNeedUpdate = true;
                    ServiceVrInfo info = (ServiceVrInfo) msg.obj;
                    showAlertDialog(info.getVersionRemark(), info.getDownLoadUrl(),info.getMd5());
                    break;
                case CHECK_NOT_NEED_UPDATE:
                    isNeedUpdate = false;
                    Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
                    break;
                case CHECK_DATA_ERR:
                    Toast.makeText(context, "检查更新失败，结果有误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * @param context
     * @param checkUrl 检查更新接口
     */
    public AppUpdate(Context context, String checkUrl) {
        this.context = context;
        this.checkUrl = checkUrl;
    }

    public String getAppCachePath(Context context){
        return context.getExternalCacheDir().getPath()+ File.separator;
    }

    /**
     * 显示提示更新对话窗
     */
    private void showAlertDialog(String msg, final String url, final String md5) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle("发现新版本")
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (url != null) {

                            if (!isSameFile(md5)){
                                //不是同一个文件则下载
                                downLoadApk(url);
                            }else {
                                //相同文件则直接安装了
                                String path = getAppCachePath(context);
                                installApk(context,path);
                            }
                        }
                    }
                })
                .setNegativeButton("以后再说", null);
        builder.show();
    }

    /**
     * 比对json返回的md5和本地文件md5是否一致
     * @param md5
     * @return
     */
    private boolean isSameFile(String md5) {
        if (StringUtils.isEmpty(md5)){
            return false;
        }
        String path = getAppCachePath(context)+apkCacheName;
        return FileUtils.isFileExists(path)&&FileUtils.getFileMD5ToString(path).equals(md5);
    }

    /**
     * 下载安装apk
     */
    private void downLoadApk(String url) {
        new DownloadTask(context).execute(url);
    }

    /**
     * 显示加载框
     */
    private void showLoingDialog() {
        if (pd == null) {
            pd = new ProgressDialog(context);
            pd.setMessage("请稍后...");
            pd.setCancelable(true);
            pd.setCanceledOnTouchOutside(false);
        }
        pd.show();
    }

    /**
     * 隐藏加载框
     */
    private void hideLoingDialog() {
        if (pd != null) {
            pd.dismiss();
        }
    }


    /**
     * 请求xml升级文件
     */
    public void update() {
        showLoingDialog();
        if (this.checkUrl.isEmpty()){
            this.checkUrl= "http://www.baidu.com";
        }
        Request request = new Request.Builder().get().url(this.checkUrl).addHeader("X-Bmob-Application-Id","5942f61191432c4033b52d453667b173").addHeader("X-Bmob-REST-API-Key","4d9e3f74a707fb532b3f795ecf753005").build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideLoingDialog();
                //失败提示
                handler.sendEmptyMessage(CHECK_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideLoingDialog();
                //Json转换格式
                if (response.code() == 200 && response.isSuccessful()) {

                    String s = response.body().string();

//                    LogUtil.d(TAG,"json:"+s);

                    if (StringUtil.isEmpty(s)||!s.substring(0,3).contains("{")){
                        handler.sendEmptyMessage(CHECK_DATA_ERR);
                        return;
                    }

                    ServiceVrInfo info = new Gson().fromJson(s, ServiceVrInfo.class);

                    //根据渠道下载对应的apk
                    if (info!=null){
                        String channel = MetaUtil.getMetaData(context, AppConstants.Channel.NAME_UM);
//                        LogUtil.d(TAG,"channel:"+channel);
                        info.setDownLoadUrl(info.getDownLoadUrl()+"app-release-"+channel.toLowerCase()+".apk");
                    }

                    checkUpdate(info);
                }else {
                    //失败提示
                    handler.sendEmptyMessage(CHECK_FAILED);
                }
            }
        });
    }

    /**
     * 根据结果相应提示
     *
     * @param info 服务器返回结果
     */
    private void checkUpdate(ServiceVrInfo info) {
        if (info == null) {
            handler.sendEmptyMessage(CHECK_DATA_ERR);
            return;
        }
        if (info.getVersionCode() > VersionManager.getVerCode(this.context, VersionManager.getPackageName(this.context))) {
            Message message = new Message();
            message.what = CHECK_NEED_UPDATE;
            message.obj = info;
            handler.sendMessage(message);
            return;
        } else {
            handler.sendEmptyMessage(CHECK_NOT_NEED_UPDATE);
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context,String apkPath) {
        if (TextUtils.isEmpty(apkPath)){
            Toast.makeText(context,"更新失败！请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(
                Uri.fromFile(new File(apkPath
                        + apkCacheName)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
