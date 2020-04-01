package com.bairuitech.anychat.f2fvideo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairuitech.anychat.f2fvideo.R;
import com.bairuitech.anychat.f2fvideo.logic.model.WaitRecruitModel;
import com.bairuitech.anychat.f2fvideo.utils.EmptyUtils;

import java.util.List;

/**
 * @describe: 待办信息展示适配器
 * @author: yyh
 * @createTime: 2020/2/18 9:28
 * @className: MessageAdapter
 */
public class MessageAdapter extends BaseAdapter {

    private Context mContext;

    private List<WaitRecruitModel.WaitRecruitMessage> mLists;

    public void setLists(List<WaitRecruitModel.WaitRecruitMessage> mLists) {
        this.mLists = mLists;
        notifyDataSetChanged();
    }

    public MessageAdapter(Context context, List<WaitRecruitModel.WaitRecruitMessage> list) {
        this.mContext = context;
        this.mLists = list;
    }

    @Override
    public int getCount() {
        if (mLists == null) {
            return 0;
        }
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.contentView = (TextView) convertView.findViewById(R.id.message_content);
            viewHolder.timeView = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WaitRecruitModel.WaitRecruitMessage message = mLists.get(position);
        if (message != null) {
            viewHolder.contentView.setText("您有一条新的面试信息");
            viewHolder.timeView.setText(EmptyUtils.getNotNullString(message.getStartTimeName()));
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView contentView;
        private TextView timeView;
    }
}