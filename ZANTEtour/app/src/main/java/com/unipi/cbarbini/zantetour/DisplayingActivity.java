package com.unipi.cbarbini.zantetour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.cbarbini.zantetour.Models.Attraction;
import com.unipi.cbarbini.zantetour.Models.Comment;

import java.util.ArrayList;

public class DisplayingActivity extends AppCompatActivity {
    private String Key,ActivityName,Itemtitle;
    private String []  arrayimages;
    private int photo;
    private float rating=1;

    SharedPreferences preferences;

    private FirebaseListAdapter<Attraction> adapter;
    private FirebaseListAdapter<Comment> adapter_comm;

    CustomListView customliview_object;

    private ImageView image,image_stars,image_location,image_telephone;
    private TextView txt_title,txt_info,txt_stars,txt_contact,txt_address,txt_telephone,txt_rate;
    private Button btn_gotoUrl,btn_info,btn_photographs,btn_comments,btn_voice,btn_send;
    private ListView listview_photographs,listview_comments;
    private EditText edt_comment;
    private RatingBar ratingBar;

    //Firebase
    private DatabaseReference dbref;
    private DatabaseReference dbref_comments;
    private DatabaseReference dbref_add_comments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaying);

        btn_gotoUrl=findViewById(R.id.button1);
        btn_info=findViewById(R.id.button2);
        btn_photographs=findViewById(R.id.button3);
        btn_comments=findViewById(R.id.button4);

        image=findViewById(R.id.image);
        image_location=findViewById(R.id.image_location);
        image_telephone=findViewById(R.id.image_telephone);
        image_stars=findViewById(R.id.image_stars);

        txt_stars=findViewById(R.id.txt_stars);
        txt_title=findViewById(R.id.textView1);
        txt_address=findViewById(R.id.txt_ad);
        txt_telephone=findViewById(R.id.txt_telephone);
        txt_info=findViewById(R.id.txt_info);
        txt_contact=findViewById(R.id.txt_contact);
        txt_rate=findViewById(R.id.txt_rate);


        listview_photographs=findViewById(R.id.listview_photographs);
        listview_comments=findViewById(R.id.listview_comments);

        btn_voice=findViewById(R.id.btn_voice);
        btn_send=findViewById(R.id.btn_send);

        ratingBar=findViewById(R.id.ratingBar);
        ratingBar.setMax(5);

        edt_comment=findViewById(R.id.edt_comment);

        Key=getIntent().getExtras().get("Key").toString();
        ActivityName=getIntent().getExtras().get("ActivityName").toString();
        Itemtitle=getIntent().getExtras().get("Itemtitle").toString();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        //Setting forms
        SetVisibleGone();


        //Setting dbref
        InitializeActivityLayout();


        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangeLayoutButtonColor(btn_info,btn_photographs,btn_comments);
                SetVisibleGone();
                SetInfo();

            }
        });
        btn_photographs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangeLayoutButtonColor(btn_photographs,btn_info,btn_comments);
                SetVisibleGone();
                SetPhotographs();
            }
        });
        btn_comments.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

                ChangeLayoutButtonColor(btn_comments,btn_photographs,btn_info);
                SetVisibleGone();
                SetComments();
           }
       });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        rating=ratingBar.getRating();
                    }
                });

                String stored_message =edt_comment.getText().toString();
                if (stored_message.isEmpty())
                    Toast.makeText(getApplicationContext(), "Hey!Write your review", Toast.LENGTH_SHORT).show();
                else sendMessage(String.valueOf((int)rating),stored_message);
                // Clear the input
                edt_comment.setText("");
            }
        });
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
    private void InitializeActivityLayout()
    {
        ChangeLayoutButtonColor(btn_info,btn_photographs,btn_comments);
        if(ActivityName.equals("Hotels"))
        {
            dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("hotels").child(Key);

        }
        else if(ActivityName.equals("Food"))
        {
            dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("restaurants").child(Key);
        }
        else if(ActivityName.equals("Museums"))
        {
            dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("museums").child(Key);
            btn_comments.setVisibility(View.GONE);
        }

         SetBasics();

    }
    private void ChangeLayoutButtonColor(Button button1,Button button2,Button button3)
    {
        button1.setBackgroundColor(Color.parseColor("#FF8FB1DB"));
        button1.setTypeface(null, Typeface.BOLD);
        button2.setBackgroundColor(Color.parseColor("#FFE0DDDD"));
        button2.setTypeface(null, Typeface.NORMAL);
        button3.setBackgroundColor(Color.parseColor("#FFE0DDDD"));
        button3.setTypeface(null, Typeface.NORMAL);

    }
    private void SetBasics()
    {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Attraction newAttraction =dataSnapshot.getValue(Attraction.class);


                txt_stars.setText(newAttraction.getStars());
                txt_title.setText(newAttraction.getTitle());
                arrayimages=newAttraction.getImage().split(",");
                photo= getResources().getIdentifier(arrayimages[0], "drawable", getPackageName());
                image.setImageResource(photo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        SetInfo();
    }
    private void SetInfo()
    {
         txt_info.setVisibility(View.VISIBLE);
         txt_address.setVisibility(View.VISIBLE);
         txt_telephone.setVisibility(View.VISIBLE);
         txt_contact.setVisibility(View.VISIBLE);
         image_location.setVisibility(View.VISIBLE);
         image_telephone.setVisibility(View.VISIBLE);

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Attraction newAttraction =dataSnapshot.getValue(Attraction.class);

               txt_info.setText(newAttraction.getGeneralinfo());
               txt_info.setMovementMethod(new ScrollingMovementMethod());
               txt_address.setText(newAttraction.getAddress());
               txt_telephone.setText(newAttraction.getTelephone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void callNumber(View view)
    {
        callPhoneNumber();
    }
    public void selectTelephoneNumber()
    {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Attraction newAttraction =dataSnapshot.getValue(Attraction.class);
                goToUrl(newAttraction.getWebsite());
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" +newAttraction.getTelephone()));
                startActivity(callIntent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(DisplayingActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                selectTelephoneNumber();

            }
            else {
                selectTelephoneNumber();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber();
            } else {
                Toast.makeText(DisplayingActivity.this, "Permission not granted ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendMessage(String ratingValue,String stored_message) {
        dbref_add_comments= FirebaseDatabase.getInstance().getReference().child("Comments").child(Itemtitle);
        Comment comment = new Comment(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),ratingValue,stored_message);
        dbref_add_comments.push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    //error
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    DisplayMessages();
                }
            }
        });
    }
    private void SetPhotographs()
    {
        listview_photographs.setVisibility(View.VISIBLE);
        customliview_object =new CustomListView(this, arrayimages);
        listview_photographs.setAdapter(customliview_object);
    }

    private void SetComments()
    {
        listview_comments.setVisibility(View.VISIBLE);
        edt_comment.setVisibility(View.VISIBLE);
        btn_send.setVisibility(View.VISIBLE);
        txt_rate.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);

        if(preferences.getInt("voicespeech_button", 1)==0) btn_voice.setVisibility(View.GONE);
        else btn_voice.setVisibility(View.VISIBLE);

        DisplayMessages();



    }
    private void SetVisibleGone()
    {


        btn_gotoUrl.setVisibility(View.GONE);
        image_stars.setVisibility(View.GONE);
        txt_stars.setVisibility(View.GONE);
        txt_info.setVisibility(View.GONE);
        image_telephone.setVisibility(View.GONE);
        image_location.setVisibility(View.GONE);
        txt_telephone.setVisibility(View.GONE);
        txt_address.setVisibility(View.GONE);
        listview_comments.setVisibility(View.GONE);
        listview_photographs.setVisibility(View.GONE);
        edt_comment.setVisibility(View.GONE);
        btn_send.setVisibility(View.GONE);
        btn_voice.setVisibility(View.GONE);
        txt_contact.setVisibility(View.GONE);
        txt_rate.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);

        if(ActivityName.equals("Hotels"))
        {

            btn_gotoUrl.setVisibility(View.VISIBLE);
            image_stars.setVisibility(View.VISIBLE);
            txt_stars.setVisibility(View.VISIBLE);
        }
    }
    public void OpenChrome(View v)
    {
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Attraction newAttraction =dataSnapshot.getValue(Attraction.class);
                goToUrl(newAttraction.getWebsite());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void DisplayMessages() {
        //refer to comments
        dbref_comments = FirebaseDatabase.getInstance().getReference().child("Comments").child(Itemtitle);

        adapter_comm = new FirebaseListAdapter<Comment>(this, Comment.class, R.layout.activity_displaying_listlayout1, dbref_comments) {

            @Override
            protected void populateView(View v, Comment model, int position) {
                //activity_displaying_listlayout1.xml layout file

                TextView txt_user = (TextView) v.findViewById(R.id.textView);
                TextView txt_me = (TextView) v.findViewById(R.id.textView5);
                TextView txt_message = (TextView) v.findViewById(R.id.textView2);
                TextView txt_time = (TextView) v.findViewById(R.id.textView3);
                TextView txt_stars= (TextView) v.findViewById(R.id.txt_stars);
                RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.relativelayout);
                ImageView imgstars = (ImageView) v.findViewById(R.id.image_stars);
                ImageView senderimage = (ImageView) v.findViewById(R.id.senderimage);
                ImageView groupusersimage = (ImageView) v.findViewById(R.id.groupusersimage);

                //populate view my message
                if (model.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    relativeLayout.setBackgroundColor(Color.parseColor("#e6e2ff"));
                    groupusersimage.setVisibility(View.GONE);
                    senderimage.setVisibility(View.VISIBLE);

                    txt_me.setText("Me");
                    txt_user.setText("");

                }
                //populate view others users messages
                else {
                    relativeLayout.setBackgroundColor(Color.parseColor("#f2d4ce"));
                    senderimage.setVisibility(View.GONE);
                    groupusersimage.setVisibility(View.VISIBLE);

                    txt_user.setText(model.getUser());
                    txt_me.setText("");

                }
                txt_stars.setText(model.getStars());
                txt_message.setText(model.getMessage());
                txt_time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));

            }
        };

        listview_comments.setAdapter(adapter_comm);
    }
    //method for voice messaging
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==123 && resultCode==RESULT_OK){
            ArrayList<String> matches =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result = matches.get(0);
            edt_comment.setText(result);
        }
    }
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
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
