package com.example.behindu.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.behindu.R;
import com.example.behindu.util.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<Location> lastLocation;


    public LocationAdapter(List<com.example.behindu.util.Location> lastLocation) {
        this.lastLocation = lastLocation;
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder{

        TextView cityTv;
        TextView streetTv;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            cityTv = itemView.findViewById(R.id.city_location);
            streetTv = itemView.findViewById(R.id.street_location);
        }
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_location_cell,parent,false);
        LocationViewHolder locationViewHolder = new LocationViewHolder(view);
        return locationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = lastLocation.get(position);
        holder.cityTv.setText(location.getX()+"");
        holder.streetTv.setText(location.getY()+"");

    }

    @Override
    public int getItemCount() {
        return lastLocation.size();
    }
}
