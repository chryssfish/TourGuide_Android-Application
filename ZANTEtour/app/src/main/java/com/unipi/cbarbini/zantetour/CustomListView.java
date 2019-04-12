package com.unipi.cbarbini.zantetour;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListView extends ArrayAdapter<String> {
    private String[] images;
    int image;
    private Activity context;

    public CustomListView(Activity context,String[] images) {
        super(context,R.layout.activity_displaying_listlayout,images);
        this.context=context;
        this.images=images;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r=convertView;
        ViewHolder viewHolder=null;
        if (r==null)
        {
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.activity_displaying_listlayout,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder) r.getTag();
        }
        image= context.getResources().getIdentifier(images[position], "drawable", context.getPackageName());
        viewHolder.img.setImageResource(image);
        return r;
    }

    class ViewHolder
    {
        ImageView img;

        ViewHolder(View v)
        {
            img= v.findViewById(R.id.image);


        }



    }
}
