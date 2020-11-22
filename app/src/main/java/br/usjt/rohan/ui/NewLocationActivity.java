package br.usjt.rohan.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import br.usjt.rohan.R;
import maes.tech.intentanim.CustomIntent;

public class NewLocationActivity extends AppCompatActivity {

    private static final String TAG = "NewLocationActivity";
    int LOCATION_REQUEST_CODE = 1001;
    private static final String KEY_NAME = "location_name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COORDINATES = "coordinates";
    private static final String KEY_DT_CREATED = "dt_created";

    private EditText editTextLocationName;
    private EditText editTextLocationDesc;
    private TextView textViewLocationLat;
    private TextView textViewLocationLon;
    private String collection;
    SharedPreferences sharedPreferences;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult == null) {
                return;
            } else {
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
                textViewLocationLat.setText("Lat : " + latitude);
                textViewLocationLon.setText("Lon : " + longitude);
            }
        }
    };

    LocationRequest locationRequest;

    public double latitude;
    public double longitude;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    GregorianCalendar gc = new GregorianCalendar();
    String dataCriada = sdf.format(gc.getTime());



    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString("darkMode", null).equals("ativado")){
            setTheme(R.style.AppThemeDark);
            SharedPreferences.Editor editorIf = sharedPreferences.edit();
            editorIf.putString("darkMode", "ativado");
            editorIf.apply();
        }else {
            setTheme(R.style.AppTheme);
            SharedPreferences.Editor editorIf = sharedPreferences.edit();
            editorIf.putString("darkMode", "desativado");
            editorIf.apply();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        collection = auth.getUid();

        editTextLocationName = findViewById(R.id.editTextLocationName);
        editTextLocationDesc = findViewById(R.id.editTextLocationDesc);
        textViewLocationLat = findViewById(R.id.textViewLocationLat);
        textViewLocationLon = findViewById(R.id.textViewLocationLon);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        CustomIntent.customType(NewLocationActivity.this, "fadein-to-fadeout");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem switch_dark_mode = menu.findItem(R.id.act_bar_switch);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch widget_switch = switch_dark_mode.getActionView().findViewById(R.id.switch_widget);

        widget_switch.setChecked(sharedPreferences.getString("darkMode", null).equals("ativado"));

        widget_switch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                SharedPreferences.Editor editorIf = sharedPreferences.edit();
                editorIf.putString("darkMode", "ativado");
                editorIf.apply();
            }else{
                SharedPreferences.Editor editorIf = sharedPreferences.edit();
                editorIf.putString("darkMode", "desativado");
                editorIf.apply();
            }
            startActivity(new Intent(this, NewLocationActivity.class));
            finish();
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkSettingAndStarUpdates();
        } else {
            askLocationPermission();
        }
    }

    private void checkSettingAndStarUpdates(){
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException){
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(NewLocationActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG, "askLocationPemission: You should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkSettingAndStarUpdates();
            } else {

            }
        }
    }

    public void saveLocation(View view) {
        String location_name = editTextLocationName.getEditableText().toString();
        String description = editTextLocationDesc.getEditableText().toString();
        String dt_created = dataCriada;
        String coordinates = ("Lat: " + latitude + System.getProperty("line.separator") + "Long: " + longitude);

        Map<String, Object> location = new HashMap<>();
        location.put(KEY_NAME, location_name);
        location.put(KEY_DESCRIPTION, description);
        location.put(KEY_COORDINATES, coordinates);
        location.put(KEY_DT_CREATED, dt_created);

        firebaseFirestore.collection(collection).document(location_name).set(location)
                .addOnSuccessListener((result)->{
                    Toast.makeText(this, "Sucesso!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(NewLocationActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                    cleanData();
                    finish();
                }).addOnFailureListener((error)->{
            Toast.makeText(this, "Falha!", Toast.LENGTH_SHORT).show();
        });

    }

    private void cleanData() {
        editTextLocationDesc.setText("");
        editTextLocationName.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i = new Intent(NewLocationActivity.this, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

}