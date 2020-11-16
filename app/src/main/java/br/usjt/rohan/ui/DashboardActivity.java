package br.usjt.rohan.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

import br.usjt.rohan.R;
import br.usjt.rohan.model.Location;

public class DashboardActivity extends AppCompatActivity implements FirestoreAdapter.OnListItemLongClick {

    private static final String TAG = "DashboardActivity";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int GPS_REQUEST_PERMISSION_CODE = 1001;
    private RecyclerView locationList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private FirestoreAdapter adapter;
    private String collection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> {

        };
        locationList = findViewById(R.id.firestore_list);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        collection = auth.getUid();


        //Query
            Query query = firebaseFirestore.collection(collection);
        //RecyclerView Opções
            FirestoreRecyclerOptions<Location> options = new FirestoreRecyclerOptions.Builder<Location>()
                    .setQuery(query, Location.class)
                    .build();
            adapter = new FirestoreAdapter(options, this);

            locationList.setHasFixedSize(true);
            locationList.setLayoutManager(new LinearLayoutManager(this));
            locationList.setAdapter(adapter);
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

    @Override
    public void onItemLongClick(Location snapshot, int position) {
        firebaseFirestore.collection(collection).document(snapshot.getLocation_name()).delete().addOnSuccessListener((result)->{
            Toast.makeText(this, "Local excluído!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener((error)->{
            Toast.makeText(this, "Oops, something went wrong", Toast.LENGTH_LONG).show();
        });
    }
}
