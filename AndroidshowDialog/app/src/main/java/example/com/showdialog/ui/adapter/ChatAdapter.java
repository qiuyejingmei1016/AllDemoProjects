package example.com.showdialog.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import example.com.showdialog.R;
import example.com.showdialog.RequestField;
import example.com.showdialog.utils.StringUtil;

/**
 * @describe: 聊天展示适配器
 * @author: yyh
 * @createTime: 2019/5/20 9:37
 * @className: ChatAdapter
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;

    private JSONArray mLists;

    public ChatAdapter(Context context, JSONArray infoList) {
        this.mContext = context;
        this.mLists = infoList;
    }

    @Override
    public int getCount() {
        if (mLists == null) {
            return 0;
        }
        return mLists.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mLists.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.ivIconOther = (ImageView) convertView.findViewById(R.id.iv_icon_others);
            viewHolder.tvChatOther = (TextView) convertView.findViewById(R.id.tv_chat_other_content);
            viewHolder.ivIconMyself = (ImageView) convertView.findViewById(R.id.iv_icon_myself);
            viewHolder.tvChatMyself = (TextView) convertView.findViewById(R.id.tv_chat_myself_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        JSONObject json = null;
        try {
            json = mLists.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String content = null;
        if (json != null && json.has(RequestField.CONTENT)) {
            content = json.optString(RequestField.CONTENT);
        }
        int contentType = 0;
        if (json != null && json.has(RequestField.TYPE)) {
            contentType = json.optInt(RequestField.TYPE);
        }
        if (contentType == 1) {
            viewHolder.tvChatOther.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            viewHolder.tvChatOther.getPaint().setAntiAlias(true);//抗锯齿
            viewHolder.tvChatMyself.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            viewHolder.tvChatMyself.getPaint().setAntiAlias(true);
        } else {
            viewHolder.tvChatOther.getPaint().setFlags(0);
            viewHolder.tvChatMyself.getPaint().setFlags(0);
        }
        int chatType = 1;
        if (json != null && json.has(RequestField.CHAT_TYPE)) {
            chatType = json.optInt(RequestField.CHAT_TYPE);
        }
        viewHolder.ivIconOther.setVisibility((chatType == 1) ? View.GONE : View.VISIBLE);
        viewHolder.tvChatOther.setVisibility((chatType == 1) ? View.GONE : View.VISIBLE);
        viewHolder.ivIconMyself.setVisibility((chatType == 1) ? View.VISIBLE : View.GONE);
        viewHolder.tvChatMyself.setVisibility((chatType == 1) ? View.VISIBLE : View.GONE);

        if (!StringUtil.isNullOrEmpty(content)) {
            if (chatType == 1) {
                viewHolder.tvChatMyself.setText(content);
            } else {
                viewHolder.tvChatOther.setText(content);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivIconOther;
        private TextView tvChatOther;
        private ImageView ivIconMyself;
        private TextView tvChatMyself;

    }
}