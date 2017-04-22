package com.skyrin.bingo.common.ui;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.skyrin.bingo.R;

/**
 * @author Administrator
 */
public class UIHelper {
	private static Toast mToast = null;
	private static AlertDialog nickDialog = null;
	private static float ScreeeW = -1;
	private static float ScreeeH = -1;

	static ProgressDialog pd = null;

	/**
	 * 显示提示弹窗
	 * 
	 * @param context
	 *            上下文
	 * @param text
	 *            提示内容
	 */
	public static void ShowToast(Context context, String text) {
//		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//		} else {
//			mToast.setText(text+"");
//			mToast.setDuration(Toast.LENGTH_SHORT);
//		}
		mToast.show();
	}

	public static void ShowToast(Context context, String text, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, duration);
		} else {
			mToast.setText(text+"");
			mToast.setDuration(duration);
		}
		mToast.show();
	}

	public static void showCunstomDialog(final Context activity,View view){
		AlertDialog.Builder builder = new Builder(activity);
		builder.setView(view);
		AlertDialog dialog = builder.create();
		dialog.show();

        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.color.full_transparent);
        LayoutParams attributes = window.getAttributes();
        attributes.dimAmount = 0.3f;
        window.setLayout((int) (window.getWindowManager().getDefaultDisplay().getWidth()*0.8),LayoutParams.WRAP_CONTENT);
        window.setWindowAnimations(R.style.tips_anim);
        window.setAttributes(attributes);
	}

	/**
	 * <b>显示一个带确定回调的提示对话窗口<p>
	 * <b>listener 点击确定后执行的回调 DialogInterface.OnClickListener
	 * @param activity
	 * @param valus length=4 {title,cancel,ok,msg}
	 * @param listener
	 */
	public static void showTipsDialog(final Context activity, String [] valus, DialogInterface.OnClickListener listener)
	{
		android.support.v7.app.AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle(valus[0])
				.setMessage(valus[3])
				.setNegativeButton(valus[1], null)
				.setPositiveButton(valus[2], listener);
		builder.show();
	}
	/**
	 * <b>显示一个带确定回调的提示对话窗口<p>
	 * <b>listener 点击确定后执行的回调 DialogInterface.OnClickListener
	 */
	public static void showTipsDialog(final Context activity, String msg, DialogInterface.OnClickListener listener){
		android.support.v7.app.AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle("提示")
			.setMessage(msg)
			.setNegativeButton("取消", null)
			.setPositiveButton("确定", listener);
		builder.show();
	}

	/**
	 * 显示一个带跳转意图的提示对话窗口
	 */
	public static void showTipsDialog(final Context activity, String msg, final Intent intent){
		android.support.v7.app.AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle("提示")
			.setMessage(msg)
			.setNegativeButton("取消", null)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (intent!=null) {
						activity.startActivity(intent);
					}
					dialog.dismiss();
				}
			});
		builder.show();
	}

	/**
	 * 显示一带自定义view的个提示对话窗口
	 */
	public static void showTipsDialog(Context activity,
									  View view) {
		android.support.v7.app.AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle("提示")
				.setView(view)
				.setPositiveButton("知道了", null);
		builder.show();
	}

	/**
	 * 显示一带自定义view的个提示对话窗口
	 */
	public static void showTipsDialog(Context activity,
									  View view,String title,String pBtn,String nBtn,DialogInterface.OnClickListener listener) {
		android.support.v7.app.AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle(title)
				.setView(view)
				.setNegativeButton(nBtn,null)
				.setPositiveButton(pBtn, listener);
		builder.show();
	}

	/**
	 * 显示一个提示对话窗口
	 */
	public static void showTipsDialog(Context activity,
			String msg) {
		android.support.v7.app.AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle("提示")
			.setMessage(msg)
			.setPositiveButton("知道了", null);
		builder.show();
	}

	/**
	 * 显示一个添加对话窗口
	 */
	public static void showEdtDialog(Context activity,
									 View v, String title, DialogInterface.OnClickListener listener) {
		android.support.v7.app.AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle(title)
				.setView(v)
				.setNegativeButton("确定", listener);
		builder.show().setCanceledOnTouchOutside(false);
	}

	/**
	 * <b>显示一个带确定回调的提示对话窗口<p>
	 * <b>listener 点击确定后执行的回调 DialogInterface.OnClickListener
	 */
	public static void showTipsDialogOnDeskTop(final Context activity, String msg, DialogInterface.OnClickListener listener) {
		final AlertDialog dialog = new AlertDialog.Builder(activity).create();
		dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);//WindowTpye
		dialog.setTitle("提示");
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(msg);
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialog.dismiss();
					}
				});
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
				listener);
		dialog.show();
	}

	/**
	 * 显示一个系统及带跳转意图的提示对话窗口
	 */
	public static void showTipsDialogOnDeskTop(final Context activity, String msg, final Intent intent) {
		final AlertDialog dialog = new AlertDialog.Builder(activity).create();
		dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);//WindowTpye
		dialog.setTitle("提示");
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(msg);
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialog.dismiss();
					}
				});
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (intent!=null) {
							activity.startActivity(intent);
						}
					}
				});
		dialog.show();
	}

	/**
	 * 显示一个ProgressDialog
	 * 
	 * @param activity
	 * @return
	 */
	public static void showLoadingDialog(Context activity) {
		if (pd == null) {
			pd = new ProgressDialog(activity);
			pd.getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
			pd.setMessage("请稍后...");
			pd.setCancelable(true);
			pd.setCanceledOnTouchOutside(false);
		}
		pd.show();
	}

	/**
	 * 隐藏加载框
	 */
	public static void hideLoingDialog() {
		if (pd != null) {
			pd.dismiss();
		}
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenH(Context context) {
		if (ScreeeH == -1) {
			ScreeeH = context.getResources().getDisplayMetrics().heightPixels;
		}
		return ScreeeH;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenW(Context context) {
		if (ScreeeW == -1) {
			ScreeeW = context.getResources().getDisplayMetrics().widthPixels;
		}
		return ScreeeW;
	}

	/**
	 * px转dp
	 * 
	 * @param px
	 * @return
	 */
	public static float px2Dp(float px, Context context) {
		return px / context.getResources().getDisplayMetrics().density;
	}

	/**
	 * dp转px
	 * 
	 * @param dp
	 * @return
	 */
	public static float dp2Px(float dp, Context context) {
		return dp * context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 用于获取系统状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	public static int getStatusBarHeight(Context ctx) {
		int Identifier = ctx.getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (Identifier > 0) {
			return ctx.getResources().getDimensionPixelSize(Identifier);
		}
		return 0;
	}
}
