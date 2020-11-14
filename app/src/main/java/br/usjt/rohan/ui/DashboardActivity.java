package br.usjt.rohan.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

import br.usjt.rohan.R;
import br.usjt.rohan.model.Location;

public class DashboardActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int GPS_REQUEST_PERMISSION_CODE = 1001;
    private RecyclerView locationList;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> {

        };
        locationList = findViewById(R.id.firestore_list);
        firebaseFirestore = FirebaseFirestore.getInstance();
        //Query
            Query query = firebaseFirestore.collection("Locations");
        //RecyclerView Opções
            FirestoreRecyclerOptions<Location> options = new FirestoreRecyclerOptions.Builder<Location>()
                    .setQuery(query, Location.class)
                    .build();
            adapter = new FirestoreRecyclerAdapter<Location, LocationViewHolder>(options) {

                @NonNull
                @Override
                public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
                    return new LocationViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull LocationViewHolder holder, int position, @NonNull Location model) {
                    holder.location_name.setText(model.getLocation_name());
                    holder.description.setText(model.getDescription());
                    holder.coordinates.setText(model.getCoordinates());
                    holder.dt_created.setText(model.getDt_created());
                }
            };

            locationList.setHasFixedSize(true);
            locationList.setLayoutManager(new LinearLayoutManager(this));
            locationList.setAdapter(adapter);


    }
    private class LocationViewHolder extends RecyclerView.ViewHolder{

        private TextView location_name;
        private TextView description;
        private TextView coordinates;
        private TextView dt_created;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            location_name = itemView.findViewById(R.id.textViewLocationName);
            description = itemView.findViewById(R.id.textViewLocationDescription);
            coordinates = itemView.findViewById(R.id.textViewLocationCoordinates);
            dt_created = itemView.findViewById(R.id.textViewLocationDtCreated);
        }
    }



    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        10,
                        locationListener
                );
            }else{ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_REQUEST_PERMISSION_CODE);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GPS_REQUEST_PERMISSION_CODE) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        10,
                        locationListener);
                }
            }else{
                Toast.makeText(this, "Sem permissões", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void newLocation(View view){
        startActivity(new Intent(this, NewLocationActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
