package com.example.earthquake.Ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.earthquake.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static com.example.earthquake.R.layout.custom_info_window;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private  Context context;
    private LayoutInflater layoutInflater;
    private View view;

    public CustomInfoWindow(Context context) {
        this.context=context;
        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(custom_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView title=(TextView)view.findViewById(R.id.title_id);
        title.setText(marker.getTitle());

        TextView magnitude=(TextView)view.findViewById(R.id.magnitude_id);
        magnitude.setText(marker.getSnippet());

        return view;
    }
}
