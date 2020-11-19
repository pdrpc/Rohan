package br.usjt.rohan.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import br.usjt.rohan.R;
import br.usjt.rohan.model.Location;

public class DashboardActivity extends AppCompatActivity implements FirestoreAdapter.OnListItemLongClick {


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

        locationList.setHasFixedSize(false);
        locationList.setLayoutManager(new LinearLayoutManager(this));
        locationList.setAdapter(adapter);
    }



    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    public void newLocation(View view){
        Intent i = new Intent(DashboardActivity.this, NewLocationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    public void onBackPressed(){
        Intent i = new Intent(DashboardActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    @Override
    public void onItemLongClick(Location snapshot, int position) {
        Log.d("Long Click", "got the looooong click");
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Deletar")
                .setMessage("Tem certeza que deseja deletar " + snapshot.getLocation_name() + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseFirestore.collection(collection).document(snapshot.getLocation_name()).delete();
                        Toast.makeText(DashboardActivity.this, "Local excluído!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}