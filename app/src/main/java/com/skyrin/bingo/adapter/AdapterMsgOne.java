package com.skyrin.bingo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skyrin.bingo.R;
import com.skyrin.bingo.modle.TMsg;

import java.util.List;

/**
 * Created by 罗延林 on 2016/10/9 0009.
 */

public class AdapterMsgOne extends RecyclerView.Adapter<AdapterMsgOne.MsgViewHolder> {

    Context context;
    LayoutInflater inflater;
    List<TMsg> msgs;

    public AdapterMsgOne(Context context, List<TMsg> msgs) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.msgs = msgs;
    }

    /**
     * 增加点击监听接口
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position, TMsg s);

        void onItemLongClick(View view, int position);
    }

    private AdapterMsgOne.OnItemClickLitener itemClickLitener;

    public void setOnItemClickLitener(AdapterMsgOne.OnItemClickLitener itemClickLitener) {
        this.itemClickLitener = itemClickLitener;
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MsgViewHolder holder = new MsgViewHolder(inflater.inflate(R.layout.item_msg_one, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(final MsgViewHolder holder, int position) {
        holder.tv.setText(msgs.get(position).getMsg());
        //设置监听
        if (itemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    itemClickLitener.onItemClick(v, position, msgs.get(position));
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    itemClickLitener.onItemLongClick(v, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    class MsgViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MsgViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_msg_one);
        }
    }
}
