package com.example.smarthome;

import android.graphics.Color;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import java.security.Principal;
import java.util.List;

import static com.example.smarthome.newDevices.user;

public class Lights extends AppCompatActivity {
    FirebaseUser userID = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference light = database.getReference("Devices").child("Lights").child(userID.getUid());
    Croller croller ;
    public Device Dev_light;
    private String ID_light = "-4000BFxC9JzccqvUEuJ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lights);
        croller =  (Croller) findViewById(R.id.crol);
        croller.setIndicatorWidth(10);
        croller.setBackCircleColor(Color.parseColor("#EDEDED"));
        croller.setMainCircleColor(Color.WHITE);
        croller.setMax(10);
        croller.setMin(0);
        croller.setStartOffset(45);
        croller.setLabel("Light: 10%");
        croller.setIsContinuous(true);
        croller.setLabelColor(Color.BLACK);
        croller.setProgressPrimaryColor(Color.parseColor("#0B3C49"));
        croller.setIndicatorColor(Color.parseColor("#0B3C49"));
        croller.setProgressSecondaryColor(Color.parseColor("#EEEEEE"));

        /*
        light.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Dev_light = postSnapshot.getValue(Device.class);
                    ID_light = Dev_light.getID();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });*/



        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {

            @Override
            public void onProgressChanged( Croller croller, int progress) {
                sterLight(progress);

            }
            @Override
            public void onStartTrackingTouch( Croller croller) {}
            @Override
            public void onStopTrackingTouch( Croller croller) {}
        });
    }


    void sterLight(int progress){

        if(progress==10){
            progress--;
            String progres_napis = "Light:" +progress;
            light.child(ID_light).child("stan").setValue(progres_napis);
            progress++;
        }else {
            String progres_napis = "Light:" +progress;
            light.child(ID_light).child("stan").setValue(progres_napis);
        }

        if(progress==0){
            croller.setLabel("Light: OFF");
        }else{
            progress = progress*10;
            croller.setLabel("Light: " + progress + "%");
        }
    }
}
