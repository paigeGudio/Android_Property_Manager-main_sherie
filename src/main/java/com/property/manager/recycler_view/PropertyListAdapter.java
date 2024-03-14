package com.property.manager.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.property.manager.R;
import com.property.manager.model.Property;

import java.util.ArrayList;
import java.util.List;

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

    private Context context;
    private List<Property> properties;

    public PropertyListAdapter(Context context, List<Property> properties){
        this.context = context;
        this.properties = properties;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(R.layout.list_item_property, parent, false);
        PropertyViewHolder pvh= new PropertyViewHolder(view, context);
        pvh.properties=new ArrayList<Property>();
        pvh.properties.addAll(properties);

        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
          Property property = properties.get(position);
          holder.bindProperty(property);

    }

    @Override
    public int getItemCount() {
        return properties.size();
    }
}
