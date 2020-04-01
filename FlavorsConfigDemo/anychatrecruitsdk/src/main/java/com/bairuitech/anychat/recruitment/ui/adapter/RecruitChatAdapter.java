package com.bairuitech.anychat.recruitment.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bairuitech.anychat.recruitment.R;
import com.bairuitech.anychat.recruitment.logic.model.RecruitChatMessageModel;

import java.util.List;

/**
 * @describe: 聊天展示适配器
 * @author: yyh
 * @createTime: 2019/5/20 9:37
 * @className: RecruitChatAdapter
 */
public class RecruitChatAdapter extends BaseAdapter {

    private Context mContext;

    private List<RecruitChatMessageModel> mLists;

    public RecruitChatAdapter(Context context, List<RecruitChatMessageModel> infoList) {
        this.mContext = context;
        this.mLists = infoList;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sdk_recruit_item_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = convertView.findViewById(R.id.sdk_left_layout);
            viewHolder.leftMsg = (TextView) convertView.findViewById(R.id.sdk_left_msg);
            viewHolder.leftHeadView = convertView.findViewById(R.id.sdk_left_head_view);

            viewHolder.rightLayout = convertView.findViewById(R.id.sdk_right_layout);
            viewHolder.rightMsg = (TextView) convertView.findViewById(R.id.sdk_right_msg);
            viewHolder.rightHeadView = convertView.findViewById(R.id.sdk_right_head_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RecruitChatMessageModel model = mLists.get(position);
        if (model.getType() == RecruitChatMessageModel.TYPE_RECEIVED) {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.leftHeadView.setVisibility(View.VISIBLE);
            viewHolder.leftMsg.setText(model.getContent());

            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.rightHeadView.setVisibility(View.GONE);
        } else {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightHeadView.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(model.getContent());

            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.leftHeadView.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        View leftLayout;
        TextView leftMsg;
        View leftHeadView;

        View rightLayout;
        TextView rightMsg;
        View rightHeadView;
    }
}