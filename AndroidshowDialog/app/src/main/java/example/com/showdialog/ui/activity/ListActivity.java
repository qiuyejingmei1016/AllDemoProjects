package example.com.showdialog.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import example.com.showdialog.R;
import example.com.showdialog.RequestField;
import example.com.showdialog.ui.adapter.ChatAdapter;
import example.com.showdialog.ui.view.PopupList;
import example.com.showdialog.utils.StringUtil;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private ListView mListView;
    private ArrayList<String> mPopupMenuItemList;
    private JSONArray mChatJsonLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initData();
    }

    private void initData() {
        mChatJsonLists = new JSONArray();
        try {
            JSONObject json = new JSONObject();
            json.put(RequestField.TYPE, 0);
            json.put(RequestField.CONTENT, "哈哈哈");
            json.put(RequestField.CHAT_TYPE, 1);
            mChatJsonLists.put(json);

            json = new JSONObject();
            json.put(RequestField.TYPE, 0);
            json.put(RequestField.CONTENT, "呵呵呵");
            json.put(RequestField.CHAT_TYPE, 2);
            mChatJsonLists.put(json);

            json = new JSONObject();
            json.put(RequestField.TYPE, 0);
            json.put(RequestField.CONTENT, "哈哈哈哈哈哈");
            json.put(RequestField.CHAT_TYPE, 1);
            mChatJsonLists.put(json);

            json = new JSONObject();
            json.put(RequestField.TYPE, 0);
            json.put(RequestField.CONTENT, "呵呵呵呵呵呵");
            json.put(RequestField.CHAT_TYPE, 2);
            mChatJsonLists.put(json);

            json = new JSONObject();
            json.put(RequestField.TYPE, 0);
            json.put(RequestField.CONTENT, "哈哈哈哈哈哈哈哈哈");
            json.put(RequestField.CHAT_TYPE, 1);
            mChatJsonLists.put(json);

            json = new JSONObject();
            json.put(RequestField.TYPE, 0);
            json.put(RequestField.CONTENT, "呵呵呵呵呵呵呵呵呵呵");
            json.put(RequestField.CHAT_TYPE, 2);
            mChatJsonLists.put(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChatAdapter chatAdapter = new ChatAdapter(this, mChatJsonLists);
        mListView.setAdapter(chatAdapter);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemLongClickListener(this);

        mPopupMenuItemList = new ArrayList<String>();
        mPopupMenuItemList.add("复制");
        mPopupMenuItemList.add("转发");
        mPopupMenuItemList.add("分享");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mChatJsonLists.length() > 0) {
            JSONObject json = null;
            try {
                json = mChatJsonLists.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String content = json.optString(RequestField.CONTENT);
            if (!StringUtil.isNullOrEmpty(content)) {
                int chatType = json.optInt(RequestField.CHAT_TYPE);
                View contentView;
                if (chatType == 1) {
                    contentView = view.findViewById(R.id.tv_chat_myself_content);
                } else {
                    contentView = view.findViewById(R.id.tv_chat_other_content);
                }
                int[] location = new int[2];
                contentView.getLocationOnScreen(location);

                PopupList popupList = new PopupList(contentView.getContext());
                popupList.showPopupListWindow(contentView, position, location[0] + contentView.getWidth() / 2,
                        location[1], mPopupMenuItemList, new PopupList.PopupListListener() {
                            @Override
                            public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                                return true;
                            }

                            @Override
                            public void onPopupListClick(View contextView, int contextPosition, int menuPosition) {
                                Log.e("=====onPopupListClick", "contextPosition:" + contextPosition + "  menuPosition:" + menuPosition);
                                if (menuPosition == 0) {
                                    Toast.makeText(ListActivity.this, "复制", Toast.LENGTH_SHORT).show();
                                } else if (menuPosition == 1) {
                                    Toast.makeText(ListActivity.this, "转发", Toast.LENGTH_SHORT).show();
                                } else if (menuPosition == 2) {
                                    Toast.makeText(ListActivity.this, "分享", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                return true;
            }
        }
        return false;
    }
}