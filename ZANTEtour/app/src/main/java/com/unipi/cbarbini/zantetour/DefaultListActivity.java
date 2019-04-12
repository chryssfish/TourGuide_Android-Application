package com.unipi.cbarbini.zantetour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.cbarbini.zantetour.Models.Attraction;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

public class DefaultListActivity extends AppCompatActivity {
    private String ActivityName;
    private int image;
    private ListView defaultList;
    private FirebaseListAdapter<Attraction> adapter;

    //Firebase
    private DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_list);
        defaultList = findViewById(R.id.listview);

        //what button selected
        ActivityName=getIntent().getExtras().get("ActivityName").toString();

        if(ActivityName.equals("Hotels"))        dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("hotels");
        else if(ActivityName.equals("Food"))     dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("restaurants");
        else if(ActivityName.equals("Museums"))  dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("museums");

        DisplayList(dbref);

    }
    public void DisplayList(final DatabaseReference dbref) {

            adapter = new FirebaseListAdapter<Attraction>(this,Attraction.class, R.layout.activity_default_listlayout,dbref) {

            @Override
            protected void populateView(View v, final Attraction model, int position) {

                  // Get references to the views of activity_default_listlayout.xml
                  ImageView photo= (ImageView) v.findViewById(R.id.image);
                  TextView txt_title = (TextView)v.findViewById(R.id.textView1);

                  // Set values
                  String [] images=model.getImage().split(",");
                  image= getResources().getIdentifier(images[0], "drawable", getPackageName());
                  photo.setImageResource(image);
                  txt_title.setText(model.getTitle());

            }
        };
       //Setting unique keys
        final List<String> keys=new ArrayList<>();
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot : dataSnapshot.getChildren())
                 {
                     keys.add(snapshot.getKey());
                 }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Send id value on  Item Click
        defaultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Attraction Itemlist=adapter.getItem(i);

                Intent intent=new Intent(getApplicationContext(),DisplayingActivity.class);
                intent.putExtra("Key",keys.get(i));
                intent.putExtra("Itemtitle",Itemlist.getTitle());
                intent.putExtra("ActivityName",ActivityName);
                startActivity(intent);

            }
        });
        defaultList.setAdapter(adapter);
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
