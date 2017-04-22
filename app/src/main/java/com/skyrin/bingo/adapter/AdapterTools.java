package com.skyrin.bingo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyrin.bingo.R;
import com.skyrin.bingo.modle.Tools;

import java.util.List;

/**
 * Created by admin on 2017/1/23.
 */

public class AdapterTools extends RecyclerView.Adapter<AdapterTools.ToolsHolder> {
    Context context;
    LayoutInflater inflater;
    List<Tools> list;

    public AdapterTools(Context context,List<Tools> list){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
        void onStartClick(View view,int position,int funcId);
        void onSetClick(View view,int position,int funcId);
    }

    OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }

    @Override
    public ToolsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ToolsHolder holder = new ToolsHolder(inflater.inflate(R.layout.item_tools,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ToolsHolder holder, int position) {
        Tools tools = list.get(position);
        final int funcId = tools.getFuncId();
        if (tools!=null){
            Drawable drawable= context.getResources().getDrawable(tools.getIconId());
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tv_name.setText(tools.getName());
            holder.tv_name.setCompoundDrawables(drawable,null,null,null);
            holder.iv_set.setBackgroundResource(tools.getBtnSetId());
            holder.iv_start.setBackgroundResource(tools.getBtnStartId());
        }
        if (itemClickListener!=null){
            final int pos = holder.getLayoutPosition();
            holder.rl_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onStartClick(v,pos,funcId);
                }
            });
            holder.rl_set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onSetClick(v,pos,funcId);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.onItemLongClick(v,pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ToolsHolder extends RecyclerView.ViewHolder{
        View rl_start;
        View rl_set;
        TextView tv_name;
        ImageView iv_start;
        ImageView iv_set;

        public ToolsHolder(View itemView) {
            super(itemView);
            rl_start = itemView.findViewById(R.id.rl_start);
            rl_set = itemView.findViewById(R.id.rl_set);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_start = (ImageView) itemView.findViewById(R.id.iv_start);
            iv_set = (ImageView) itemView.findViewById(R.id.iv_set);
        }
    }
}
