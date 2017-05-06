package org.davidliebman.android.distro;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dave on 4/30/17.
 */

public class ADListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<ADPackageInfo> data;

    private static LayoutInflater inflater=null;
    //public ImageLoader imageLoader;

    public ADListAdapter(Activity a, ArrayList<ADPackageInfo> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public ADListAdapter(Activity a, ArrayList<ADPackageInfo> d, ArrayList<ADPackageInfo> l) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < l.size(); i ++) {
            for (int j = 0; j < data.size(); j ++) {
                if (l.get(i).packageName.contentEquals(data.get(j).packageName)) {
                    data.get(j).packageIsNew = true;
                }
            }
        }
    }

    @Override
    public int getCount() {
        if (data == null ) return 0;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View vi=convertView;
        if(convertView==null) vi = inflater.inflate(R.layout.row_layout_complex, null);

        TextView name = (TextView)vi.findViewById(R.id.name); // title
        TextView version = (TextView)vi.findViewById(R.id.version); // artist name
        TextView section = (TextView)vi.findViewById(R.id.section); // duration
        ImageView thumb_image_check = (ImageView)vi.findViewById(R.id.list_image_check); // thumb image
        ImageView thumb_image_uncheck = (ImageView) vi.findViewById(R.id.list_image_uncheck);

        ADPackageInfo info ;
        if (data != null ) info = data.get(position);
        else info = new ADPackageInfo();

        name.setText(info.packageName);//song.get(CustomizedListView.KEY_TITLE));
        version.setText(info.packageVersion);//song.get(CustomizedListView.KEY_ARTIST));
        section.setText(info.packageSection);//song.get(CustomizedListView.KEY_DURATION));
        //imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);

        if (info.packageIsNew) {
            thumb_image_check.setVisibility(View.VISIBLE);
            thumb_image_uncheck.setVisibility(View.GONE);
        }
        else {
            thumb_image_check.setVisibility(View.GONE);
            thumb_image_uncheck.setVisibility(View.VISIBLE);
        }

        return vi;

    }
}
