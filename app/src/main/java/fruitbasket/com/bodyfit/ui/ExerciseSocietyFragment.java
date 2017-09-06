package fruitbasket.com.bodyfit.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fruitbasket.com.bodyfit.R;

/**
 * Created by Administrator on 2016/11/10.
 *
 */
public class ExerciseSocietyFragment extends Fragment {
    public static final String TAG="ExerciseSocietyFragment";

    ListView listview;
    private List<ItemContainer> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_exercise_society,container,false);
        init(view);

        mData=getData();
        SocietyAdapter adapter= new SocietyAdapter(getActivity(), (ArrayList) mData);
        listview.setAdapter(adapter);
        return view;
    }

    private void init(View view){
        listview= (ListView) view.findViewById(R.id.listView);
    }
    private ArrayList getData() {
        ArrayList<ItemContainer> list = new ArrayList<ItemContainer>();
        Integer headID;
        String name,content;

        for(int i=0;i<20;i++) {
            ArrayList<Map<String,Object>> imagelist=new ArrayList<Map<String,Object>>();
            headID=(Integer)R.drawable.society_head;
            name="张三"+i;
            content="";
            for(int j=0;j<i+1;j++){
                content+="我爱学习 ";
            }
            for(int j=0;j<i+1;j++) {
                if(j==9)
                    break;
                Map<String,Object> map=new HashMap<String,Object>();
                map.put("image",R.drawable.society_head);
                imagelist.add(map);
            }
            ItemContainer itemContainer=new ItemContainer(headID,name,content,imagelist);

            list.add(itemContainer);
        }
        return list;
    }
}

class ItemContainer{
    public int headID;
    public String name;
    public String content;
    public ArrayList imageArray;
    public ItemContainer(int h,String n,String con,ArrayList list){
        headID=h;
        name=n;
        content=con;
        imageArray=list;
    }
}

class SocietyAdapter extends BaseAdapter{
    public static final String TAG="SocietyAdapter";

    private Context context;
    private List<ItemContainer> item;

    public SocietyAdapter(Context con,ArrayList list){
        context=con;
        item=list;
    }

    @Override
    public int getCount() {
        return item==null?0:item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;

        ItemContainer itemContainer=item.get(position);
        if(convertView==null){
            viewholder=new ViewHolder();
            convertView=View.inflate(context,R.layout.society_list_item,null);
            viewholder.image= (ImageView) convertView.findViewById(R.id.society_headcul);
            viewholder.name= (TextView) convertView.findViewById(R.id.name);
            viewholder.content= (TextView) convertView.findViewById(R.id.content);
            viewholder.picture= (GridView) convertView.findViewById(R.id.picture);
            viewholder.comment= (Button) convertView.findViewById(R.id.society_comment);

            convertView.setTag(viewholder);
        }else{
            viewholder= (ViewHolder) convertView.getTag();
        }

        viewholder.image.setImageResource(itemContainer.headID);
        viewholder.name.setText(itemContainer.name);
        viewholder.content.setText(itemContainer.content);

        SimpleAdapter simpleAdapter=new SimpleAdapter(context,itemContainer.imageArray,R.layout.cell,new String[]{"image"},new int[]{R.id.imageView});
        viewholder.picture.setAdapter(simpleAdapter);

        viewholder.picture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "click on " + position, Toast.LENGTH_SHORT).show();
            }
        });
        viewholder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "comment", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
    class ViewHolder{
        private ImageView image;
        private TextView name;
        private TextView content;
        private GridView picture;
        private TextView time;
        private Button comment;
    }
}