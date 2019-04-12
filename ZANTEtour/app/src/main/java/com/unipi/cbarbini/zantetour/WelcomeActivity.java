package com.unipi.cbarbini.zantetour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
private Button btn_hotel,btn_food,btn_museums,btn_map,btn_diary,btn_settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btn_hotel=findViewById(R.id.btn_hotel);
        btn_food=findViewById(R.id.btn_food);
        btn_museums =findViewById(R.id.btn_museums);
        btn_map=findViewById(R.id.btn_map);
        btn_diary=findViewById(R.id.btn_diary);
        btn_settings=findViewById(R.id.btn_settings);

        btn_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ActivityName=new Intent(getApplicationContext(),DefaultListActivity.class);
                ActivityName.putExtra("ActivityName","Hotels");
                startActivity(ActivityName);
            }
        });
        btn_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ActivityName=new Intent(getApplicationContext(),DefaultListActivity.class);
                ActivityName.putExtra("ActivityName","Food");
                startActivity(ActivityName);
            }
        });
        btn_museums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ActivityName=new Intent(getApplicationContext(),DefaultListActivity.class);
                ActivityName.putExtra("ActivityName","Museums");
                startActivity(ActivityName);
            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),DiaryActivity.class));
            }
        });
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            }
        });
    }
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Settings:
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                break;
            case R.id.Help:
                startActivity(new Intent(getApplicationContext(),HelpActivity.class));
                break;
            case R.id.Logout:
                 LogOut();
                 break;
        }
        return false;
    }
    //signout
    private void LogOut()
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
