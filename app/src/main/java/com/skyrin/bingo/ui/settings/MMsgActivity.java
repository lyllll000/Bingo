package com.skyrin.bingo.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.skyrin.bingo.R;
import com.skyrin.bingo.adapter.AdapterMsgOne;
import com.skyrin.bingo.common.ui.UIHelper;
import com.skyrin.bingo.common.ui.ViewFindUtils;
import com.skyrin.bingo.database.bll.BMsg;
import com.skyrin.bingo.modle.TMsg;
import com.skyrin.bingo.ui.BaseActivity;

import java.util.List;

/**
 * Created by admin on 2016/12/29.
 */

public class MMsgActivity extends BaseActivity {
    private static final String TAG = "MMsgActivity";

    Context context;
    RecyclerView rc_msg;
    AdapterMsgOne adapter;

    BMsg bMsg;

    List<TMsg> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        bindView();
        iniData();
        setListener();
    }

    @Override
    protected boolean isShowBack() {
        return true;
    }

    @Override
    protected void bindView() {
        setTitle("消息列表");
        View view = getWindow().getDecorView();
        context = MMsgActivity.this;
        rc_msg = ViewFindUtils.find(view,R.id.rc_msg);
        rc_msg.setLayoutManager(new LinearLayoutManager(context));
        rc_msg.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void iniData() {
        bMsg = BMsg.getInstance(context);
        list = bMsg.query();
        adapter = new AdapterMsgOne(context, list);
        rc_msg.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        adapter.setOnItemClickLitener(new AdapterMsgOne.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, TMsg s) {
                showOpterItem(position,s);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"添加新的消息");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                showInsertDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 显示操作选项
     */
    private void showOpterItem(final int pos, final TMsg msg) {
        CharSequence[] items = new CharSequence[]{"更新", "删除"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showUpdateDialog(pos,msg);
                        break;
                    case 1:
                        showDeleteDialog(pos,msg);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 删除提示
     */
    private void showDeleteDialog(final int pos, final TMsg msg) {

        UIHelper.showTipsDialog(context, "确认删除？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (bMsg.delete(msg)) {
                    list.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    UIHelper.ShowToast(context, "已删除~");
                }else {
                    UIHelper.ShowToast(context, "id:"+msg.getId());
                }
            }
        });
    }

    /**
     * 更新
     */
    private void showUpdateDialog(final int pos, final TMsg msg) {
        View v = View.inflate(context, R.layout.view_edt_msg, null);
        final TextInputEditText edt_msg = ViewFindUtils.find(v, R.id.edt_msg);
        edt_msg.setText(msg.getMsg());
        edt_msg.setSelection(0,msg.getMsg().length());

        showImm(edt_msg);

        UIHelper.showEdtDialog(MMsgActivity.this, v, "更新消息", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msgstr = edt_msg.getText().toString();
                if (!TextUtils.isEmpty(msgstr)) {
                    msg.setMsg(msgstr);
                    if (bMsg.update(msg)){
                        adapter.notifyItemChanged(pos);
                        UIHelper.ShowToast(context, "已更新~");
                    }
                } else {
                    UIHelper.ShowToast(context, "什么也没有~");
                }
                hideImm(edt_msg);
            }
        });
    }

    /**
     * 插入数据库
     */
    private void showInsertDialog() {
        View v = View.inflate(context, R.layout.view_edt_msg, null);
        final TextInputEditText edt_msg = ViewFindUtils.find(v, R.id.edt_msg);

        showImm(edt_msg);

        UIHelper.showEdtDialog(MMsgActivity.this, v, "添加消息", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg = edt_msg.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    long id = bMsg.insert(new TMsg(msg,0));
                    list.add(0, new TMsg(msg,id));
                    adapter.notifyItemInserted(0);
                    UIHelper.ShowToast(context, "已添加~");
                } else {
                    UIHelper.ShowToast(context, "什么也没有~");
                }
                hideImm(edt_msg);
            }
        });
    }
}
