package com.example.smarthome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.CardView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView urzadzenia, wyloguj, temperatura, ImageOfRoom,Lighits;

    //firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        urzadzenia = (CardView) findViewById(R.id.urzadzenia);
        wyloguj = (CardView) findViewById(R.id.wyloguj);
        temperatura = (CardView) findViewById(R.id.temperatura);
        ImageOfRoom = (CardView) findViewById(R.id.ImageOfRoom);
        Lighits = (CardView) findViewById(R.id.lights);


        // Add click listener to cards
        urzadzenia.setOnClickListener(this);
        wyloguj.setOnClickListener(this);
        temperatura.setOnClickListener(this);
        ImageOfRoom.setOnClickListener(this);
        Lighits.setOnClickListener(this);


        setupFirebaseListener();

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.urzadzenia : Intent intent_urzadzenia = new Intent(this,newDevices.class); startActivity(intent_urzadzenia); break;
            case R.id.temperatura : Intent intent_Temperatura = new Intent(this,Temperatura.class); startActivity(intent_Temperatura); break;
            case R.id.ImageOfRoom : Intent ImageOfRoom = new Intent(this,ImageOfRoom.class); startActivity(ImageOfRoom); break;
            case R.id.lights : Intent Lights = new Intent(this,Lights.class); startActivity(Lights); break;
            case R.id.wyloguj : FirebaseAuth.getInstance().signOut(); break;
            default: break;

        }

    }

    private void setupFirebaseListener(){

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){

                }else{
                    Toast.makeText(MainActivity.this, "Wylogowano", Toast.LENGTH_LONG).show();

                    Intent intent =  new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}