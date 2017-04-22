package com.skyrin.bingo.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skyrin.bingo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 罗延林 on 2016/9/12 0012.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    Context context;

    //进度对话框
    AlertDialog dialog;

    //对话框view
    View view;

    //进度条
    ProgressBar pb;

    //进度百分比
    TextView tv;

    //用户是否取消了更新
    boolean cancelUpdate = false;

    /**
     * @param context 上下文
     */
    public DownloadTask(Context context) {
        this.context = context;
        this.view = View.inflate(context, R.layout.progress_view,null);
        this.pb = (ProgressBar) view.findViewById(R.id.progressBar);
        this.tv = (TextView) view.findViewById(R.id.tv);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("正在更新")
                .setCancelable(false)
                .setView(view)
                .setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelUpdate = true;
                        //取消提交结果
                        cancel(true);
                    }
                });

        dialog = builder.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        //存储路径
//        String savePath = Environment
//                .getExternalStorageDirectory()
//                + "/download/";

        String savePath = context.getExternalCacheDir().getPath()+File.separator;

        //下载进度
        int progress;

        try {
            URL url = new URL(strings[0]);
            if (TextUtils.isEmpty(strings[0])) {
                //url为空
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.connect();
                    //文件大小
                    int fileLength = conn.getContentLength();
                    //创建输入流
                    InputStream inputStream = conn.getInputStream();

                    //判断文件目录是否存在，不存在则创建
                    File downloadDir = new File(savePath);
                    if (!downloadDir.exists()) {
                        downloadDir.mkdirs();
                    }

                    File apkFile = new File(savePath, AppUpdate.apkCacheName);

                    FileOutputStream fos = new FileOutputStream(apkFile);

                    int count = 0;

                    //缓存大小
                    byte bff[] = new byte[1024];
                    do {
                        int numRead = inputStream.read(bff);
                        count += numRead;
                        //计算进度
                        progress = (int) (((float)count / fileLength) * 100);
                        //更新进度
                        publishProgress(progress);
                        //下载完成跳出循环
                        if (numRead <= 0) {
                            break;
                        }
                        //写入文件
                        fos.write(bff, 0, numRead);
                    } while (!cancelUpdate);

                    fos.close();
                    inputStream.close();

                } else {
                    //外置存储卡不可用
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (dialog != null) {
                dialog.dismiss();
            }
            return null;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        return savePath;
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        pb.setProgress(values[0]);
        tv.setText(values[0] + "%");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        AppUpdate.installApk(context,s);
    }
}
