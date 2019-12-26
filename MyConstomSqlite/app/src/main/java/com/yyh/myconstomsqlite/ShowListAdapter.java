package com.yyh.myconstomsqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yyh.myconstomsqlite.model.CollectionModel;

import java.util.List;

/**
 * @describe: 坐席列表适配器
 * @author: yyh
 * @createTime: 2018/6/26 18:34
 * @className: ShowListAdapter
 */
public class ShowListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CollectionModel> mLists;

    public ShowListAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<CollectionModel> list) {
        this.mLists = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mLists != null ? mLists.size() : 0;
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
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
            holder.content = (TextView) convertView.findViewById(R.id.show_view);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        CollectionModel collectionModel = mLists.get(position);
        if (collectionModel != null) {
            holder.content.setText("type: " + collectionModel.getType() + " tilte: " + collectionModel.getTitle()
                    + " content: " + collectionModel.getContent() + " time: " + collectionModel.getTime());
        }
        return convertView;
    }

    class Holder {
        TextView content;
    }
}
