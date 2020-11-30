package br.usjt.rohan.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import br.usjt.rohan.R;
import br.usjt.rohan.model.Location;
import maes.tech.intentanim.CustomIntent;

public class DashboardActivity extends AppCompatActivity implements FirestoreAdapter.OnListItemLongClick {


    private static final int GPS_REQUEST_PERMISSION_CODE = 1001;
    private RecyclerView locationList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private FirestoreAdapter adapter;
    private String collection;
    SharedPreferences sharedPreferences;

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
        adapter = new FirestoreAdapter(options, this, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        locationList.setHasFixedSize(true);
        locationList.setLayoutManager(linearLayoutManager);
        locationList.setAdapter(adapter);

//        final SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
//        snapHelper.attachToRecyclerView(locationList);
//
//        locationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                View view = snapHelper.findSnapView(linearLayoutManager);
//                int pos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
//                RecyclerView.ViewHolder holder = locationList.findViewHolderForAdapterPosition(pos);
//                if(holder != null) {
//                    CardView cardView = holder.itemView.findViewById(R.id.cardViewLista);
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        cardView.animate().alpha(1).scaleY(1).setDuration(550).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//                    } else {
//                        cardView.animate().alpha(0).scaleY(0).setDuration(550).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

        CustomIntent.customType(DashboardActivity.this, "fadein-to-fadeout");

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
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
        return true;
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