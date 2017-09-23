package cn.kingsleychung.sportsexpert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by JJ on 2017/8/29.
 */
public class MyAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private List<Map<String,Object>> data;
    static class ViewHolder{
        public TextView title;
        public TextView from;
        public TextView content;
    }
    public MyAdapter(Context context, List<Map<String,Object>> Newdata) {
        //根据context上下文加载布局，这里的是Demo17Activity本身，即this
        this.mInflater = LayoutInflater.from(context);
        this.data = Newdata;
    }
    @Override
    public int getCount() {
        //How many items are in the data set represented by this Adapter.
        // 在此适配器中所代表的数据集中的条目数
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        // Get the data item associated with the specified position in the data set.
        // 获取数据集中与指定索引对应的数据项
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        //Get the row id associated with the specified position in the list.
        // 获取在列表中与指定索引对应的行id
        return position;
    }

    //Get a View that displays the data at the specified position in the data set.
    //获取一个在数据集中指定索引的视图来显示数据
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //如果缓存convertView为空，则需要创建View
        if(convertView == null)
        {
            holder = new ViewHolder();
            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.list_item,null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView)convertView.findViewById(R.id.content);
            holder.from = (TextView)convertView.findViewById(R.id.from);
            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.title.setText((String) data.get(position).get("title"));
        holder.content.setText((String)data.get(position).get("content"));
        holder.from.setText((String)data.get(position).get("from"));
        return  convertView;
    }

    public void UpdateData(List<Map<String,Object>> Newdata) {
        this.data = Newdata;
    }
}
