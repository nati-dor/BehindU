package com.example.behindu.adapters;

import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.behindu.R;
import com.example.behindu.util.LastLocation;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<LastLocation> lastLocation;


    public LocationAdapter(List<LastLocation> lastLocation) {
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
        LastLocation location = lastLocation.get(position);
        List<Address> addressesList;
        try {
            Geocoder gcd = new Geocoder(holder.itemView.getContext(), Locale.getDefault());
            addressesList = gcd.getFromLocation(location.getGeoPoint().getLatitude(),location.getGeoPoint().getLongitude(), 1);
            String address = addressesList.get(0).getAdminArea()+"," + addressesList.get(0).getSubAdminArea()+ ",\n" + addressesList.get(0).getAddressLine(0);
            holder.cityTv.setText(address);
            holder.streetTv.setText(location.getTimestamp().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return lastLocation.size();
    }

}


