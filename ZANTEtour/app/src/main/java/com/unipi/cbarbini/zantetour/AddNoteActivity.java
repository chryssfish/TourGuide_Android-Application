package com.unipi.cbarbini.zantetour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unipi.cbarbini.zantetour.Models.Note;

import java.util.ArrayList;

public class AddNoteActivity extends AppCompatActivity {
    private EditText title;
    private TextView message;
    private Button send_message,btn_voice;

    SharedPreferences preferences;

    //Firebase
    private DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        title=findViewById(R.id.editText1);
        message = findViewById(R.id.textbox);
        send_message = findViewById(R.id.button);
        btn_voice=findViewById(R.id.btn_voice);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(preferences.getInt("voicespeech_button", 1)==0)btn_voice.setVisibility(View.GONE);
        else btn_voice.setVisibility(View.VISIBLE);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stored_title=title.getText().toString();
                String stored_message = message.getText().toString();
                if (stored_message.isEmpty())
                    Toast.makeText(getApplicationContext(), "Write the most adventurous experience", Toast.LENGTH_SHORT).show();
                else if(stored_title.isEmpty())
                    Toast.makeText(getApplicationContext(), "Please fill the title", Toast.LENGTH_SHORT).show();
                else sendMessage(stored_title,stored_message);

            }
        });

        //voice message
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please speak!");
                startActivityForResult(intent,123);
            }
        });
    }
    public void sendMessage(String stored_title,String stored_message) {
        dbref = FirebaseDatabase.getInstance().getReference().child("Notes");
        Note message = new Note(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), stored_title, stored_message);
        dbref.push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    //error
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(),DiaryActivity.class));
                    finish();
                }
            }
        });
    }
    //method for voice messaging
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==123 && resultCode==RESULT_OK){
            ArrayList<String> matches =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result = matches.get(0);
            message.setText(result);
        }
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
