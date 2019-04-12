package com.unipi.cbarbini.zantetour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HelpActivity extends AppCompatActivity {
private TextView txt,txt1,txt2,txt3,txt4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        txt=findViewById(R.id.textbox2);
        txt1=findViewById(R.id.textbox3);
        txt2=findViewById(R.id.textbox4);
        txt3=findViewById(R.id.textbox5);
        txt4=findViewById(R.id.textbox6);

        txt.setText ("This application is an analytic tour guide of the Ionian Island Zakynthos .We hope you spend wonderful days here ,gain new experiences and meet new places.");
        txt1.setText("Creating an account is an approriate procedure in this app. You have to set a unique username.Your data are fully protected!Log out by clicking the 3dot menu.");
        txt2.setText("There are plenty of menu choices. You can see hotels,accomondationsmuseums, You can also see the map and keep your own diary.");
        txt3.setText("Keeping your own notes will make your trip unforgettable! Choose the add button to create a new one or delete by dragging a note!");
        txt4.setText("Navigate to the settings menu in order to turn off Voice speech .");
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
