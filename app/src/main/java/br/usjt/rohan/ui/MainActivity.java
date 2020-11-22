package br.usjt.rohan.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.usjt.rohan.R;
import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPass;
    private FirebaseAuth auth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString("darkMode", null) == null){
            SharedPreferences.Editor editorIf = sharedPreferences.edit();
            editorIf.putString("darkMode", "desativado");
            editorIf.apply();
        }

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
        setContentView(R.layout.activity_main);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPass = findViewById(R.id.editTextPass);
        auth = FirebaseAuth.getInstance();
        CustomIntent.customType(MainActivity.this, "fadein-to-fadeout");

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
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        return true;
    }

    private void restartActivity() {
        onStop();
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    public void newUser (View view){
        startActivity(new Intent(this, NewUserActivity.class));
    }

    public void login (View v){
        String email = editTextEmail.getEditableText().toString();
        String senha = editTextPass.getEditableText().toString();
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener((result)->{
            Toast.makeText(this, "Bem vindo!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class));
            cleanData();
        }).addOnFailureListener((error)->{
            Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_LONG).show();
        });

    }

    private void cleanData() {
        editTextEmail.setText("");
        editTextPass.setText("");
    }

}