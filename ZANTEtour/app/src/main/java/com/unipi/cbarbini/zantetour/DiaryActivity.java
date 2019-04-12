package com.unipi.cbarbini.zantetour;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.cbarbini.zantetour.Models.Attraction;
import com.unipi.cbarbini.zantetour.Models.Note;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {
    Button btn_addnote;

    private ListView listview_messages;
    private FirebaseListAdapter<Note> adapter;

    //Firebase
    private DatabaseReference dbref;
    //Setting unique keys
    final List<String> keys=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        listview_messages = findViewById(R.id.listviewme);
        btn_addnote=findViewById(R.id.button);
        DisplayMessages();

        btn_addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddNoteActivity.class));
                finish();
            }
        });

        listview_messages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Note Itemlist=adapter.getItem(i);
                AskOption(keys.get(i),Itemlist.getTitle(),i);

                return false;
            }
        });

    }
    private void AskOption(final String Key, String titleItem, final int i)
    {
                AlertDialog.Builder builder =new AlertDialog.Builder(this, R.style.MyDialogTheme);
                builder.setTitle("Delete");
                builder.setMessage("Do you want to Delete note " + titleItem);

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        keys.remove(i);
                        FirebaseDatabase.getInstance().getReference().child("Notes").child(Key).removeValue();
                        adapter.notifyDataSetChanged();
                    }

                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                AlertDialog alert = builder.create();
                builder.show();


    }
    public void DisplayMessages() {
        //refer to messages
        dbref=FirebaseDatabase.getInstance().getReference().child("Notes");
        adapter = new FirebaseListAdapter<Note>(this,Note.class, R.layout.activity_diary_listlayout,dbref) {

            @Override
            protected void populateView(View v,final Note model, int position) {


                // Get references to the views of activity_diary_listlayout.xmllistlayout.xml
                TextView txt_title = (TextView)v.findViewById(R.id.textView);
                TextView txt_note = (TextView)v.findViewById(R.id.textView2);
                TextView txt_time = (TextView)v.findViewById(R.id.textView3);
                if (model.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))
                {
                    // Set their text
                    txt_title.setText(model.getTitle());
                    txt_note.setText(model.getNote());

                    // Format the date before showing it
                    txt_time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getTime()));

                }

            }
        };

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

        listview_messages.setAdapter(adapter);



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
