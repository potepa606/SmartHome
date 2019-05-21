package com.example.smarthome;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

public class newDevices extends AppCompatActivity implements DeviceAdapter.OnItemClickListener {


    //firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;



    //Pobierz aktualnego uzytkownika
    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    Button SendFireBase, ScannerQR;
    public static TextView textView,textView2,textView3;


    private RecyclerView mRecyclerViewNewD;
    private DeviceAdapter dAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference DevicesRef;
    private List<Device> mDevice;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_devices);

        ////////////////////////////////////////////////////////////////////////
        SendFireBase = (Button) findViewById(R.id.SendFireBase);
        ScannerQR = (Button) findViewById(R.id.ScannerQR);

        textView =(TextView)findViewById(R.id.textView);
        textView2 =(TextView)findViewById(R.id.textView2);
        textView3 =(TextView)findViewById(R.id.textView3);

        mRecyclerViewNewD = findViewById(R.id.recyclerView);
        mRecyclerViewNewD.setHasFixedSize(true);
        mRecyclerViewNewD.setLayoutManager(new LinearLayoutManager(this));

        ////////////////////////////////////////////////////////////////////////



        Buttons(); // Przyciski ich konfiguracja
        UpdateFirebaseDataBase();  //Pobieranie listy urzedzen z bazy danych

    }



    //--------------------------------------------------Przyciski i ich konfiguracja
    private void Buttons(){

        // ------------------------------------------------------------------------- Wyślij nowe Urzadzenie do Bazy
        SendFireBase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String Name = textView.getText().toString();
                String ID = textView2.getText().toString();
                String Stan = textView3.getText().toString();



                if(!Name.equals("") && !ID.equals("") && !Stan.equals("")){
                    Device newDevice = new Device(Name,ID,Stan);
                    DevicesRef.child(ID).setValue(newDevice); // wysyłąnie do bazy

                    textView.setText("");
                    textView2.setText(""); // czyszczenie Textview
                    textView3.setText("");
                    Toast.makeText(newDevices.this, "Wysłano do bazy!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(newDevices.this, "Uzupełnij wszystkie pola !", Toast.LENGTH_LONG).show();
                }
            }

        });
        //-------------------------------------------------------------------------------Skanuj Nowe urzadzenie
        ScannerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weryfikuj_PozwolenieCAM();
            }
        });

    }
    // Sprawdzenie pozwolenia uzycia kamery
    private void weryfikuj_PozwolenieCAM(){

        String perrmision[] = {Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),perrmision[0])== PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(getApplicationContext(),ScanerQR.class));
        }else{

            ActivityCompat.requestPermissions(newDevices.this,perrmision,1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        weryfikuj_PozwolenieCAM();
    }


    //---------------------------------------------------Pobieranie listy urzedzen z bazy danych
    private void UpdateFirebaseDataBase(){

        mDevice = new ArrayList<>(); // Stwóz tablicę obiektów Device
        DevicesRef = database.getReference("Devices").child(user.getUid()); // Referencja do bazydanych->Device->USERid
        dAdapter = new DeviceAdapter(newDevices.this, mDevice); // stwórz nowy obiekt i wyslij listę
        mRecyclerViewNewD.setAdapter(dAdapter); // wyświetl listę
        dAdapter.setOnItemClickListener(newDevices.this); // ;włącz klikniecia

        DevicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDevice.clear();//Usuń wszystkie obiekty przed zapisaniem od nowa
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Device uploadd = postSnapshot.getValue(Device.class);
                    mDevice.add(uploadd);
                }
                dAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(newDevices.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //----------------------------------------------------Funkcje zdarzeń po kliknieciu
    @Override
    public void onItemClick(int position) {

        Device selectedDevice = mDevice.get(position);

        final String selectedName = selectedDevice.getName();
        final String selectedKey = selectedDevice.getID();
        final String AktualnyStan = selectedDevice.getStan();

        switch (AktualnyStan) {
            case "ON":  DevicesRef.child(selectedKey).child("stan").setValue("OFF");
                        Toast.makeText(this, selectedName + " OFF", Toast.LENGTH_SHORT).show();break;
            case "OFF": DevicesRef.child(selectedKey).child("stan").setValue("ON");
                         Toast.makeText(this, selectedName + " ON", Toast.LENGTH_SHORT).show();break;
            default:    DevicesRef.child(selectedKey).child("stan").setValue("OFF");
                        Toast.makeText(this, selectedName + " OFF", Toast.LENGTH_SHORT).show();break;
        }
    }
    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onDeleteClick(int position) {
        Device selectedDevice = mDevice.get(position);
        final String selectedKey = selectedDevice.getID();

        DevicesRef.child(selectedKey).removeValue();
        Toast.makeText(newDevices.this, "Item deleted", Toast.LENGTH_SHORT).show();

    }


}
