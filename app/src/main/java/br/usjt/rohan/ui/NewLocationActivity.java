package br.usjt.rohan.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CarrierConfigManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import br.usjt.rohan.R;

public class NewLocationActivity extends AppCompatActivity {
    private static final String TAG = "NewLocationActivity";

    private static final String KEY_NAME = "location_name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COORDINATES = "coordinates";
    private static final String KEY_DT_CREATED = "dt_created";

    private EditText editTextLocationName;
    private EditText editTextLocationDesc;
    private String collection;

    public double latitude;
    public double longitude;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    GregorianCalendar gc = new GregorianCalendar();
    String dataCriada = sdf.format(gc.getTime());

    private LocationManager locationManager;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        collection = auth.getUid();

        editTextLocationName = findViewById(R.id.editTextLocationName);
        editTextLocationDesc = findViewById(R.id.editTextLocationDesc);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        onLocationChanged(location);

    }

    public void onLocationChanged (Location location){
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
    }


    public void saveLocation(View view) {
        String location_name = editTextLocationName.getEditableText().toString();
        String description = editTextLocationDesc.getEditableText().toString();
        String dt_created = dataCriada;
        String coordinates = ("Long: " + longitude + System.getProperty("line.separator") + " Lat: " + latitude + System.getProperty("line.separator"));

        Map<String, Object> location = new HashMap<>();
        location.put(KEY_NAME, location_name);
        location.put(KEY_DESCRIPTION, description);
        location.put(KEY_COORDINATES, coordinates);
        location.put(KEY_DT_CREATED, dt_created);

        db.collection(collection).document(location_name).set(location)
                .addOnSuccessListener((result)->{
                    Toast.makeText(this, "Sucesso!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener((error)->{
            Toast.makeText(this, "Falha!", Toast.LENGTH_SHORT).show();
        });

        startActivity(new Intent(this, DashboardActivity.class));
    }
}