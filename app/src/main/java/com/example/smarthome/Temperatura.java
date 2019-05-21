package com.example.smarthome;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Temperatura extends AppCompatActivity {

    //Temperatura(){}

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference wykres = database.getReference("wykres") ;
    DatabaseReference tempWykres = database.getReference("labview").child("TempWykres") ;

    TextView napisTemperautry,UstawionaTemp;
    Float pon,wt,sr,czw,pt,sob,nd;
    final String DEGREE  = "\u00b0" + "C"; // stopien do celsjusza

    private SeekBar seekBar; // Ustawienie temp
    private BarChart mChart;

    ArrayList<String> dni =new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_temperatura);
        napisTemperautry = (TextView) findViewById(R.id.temp);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        UstawionaTemp = (TextView) findViewById(R.id.Ustawtemp);
        mChart = (BarChart) findViewById(R.id.graphView);
        mChart.getDescription().setEnabled(false);
        dni.add("pon");dni.add("wt");dni.add("sr");dni.add("czw");dni.add("pt");dni.add("sob");dni.add("nd");

        SeekBar(); // Metoda Ustawienia temperatury
        seekBar.setMax(30);
        UstawionaTemp.setText("");
        XAxis xAxis = mChart.getXAxis();


        temperature(new CallBackTemp() {
            @Override
            public void onCallback(Float pon,Float wt,Float sr,Float czw,Float pt,Float sob,Float nd,Float aktualnaTemp) {
                String aktualnaTemperaturaString = Float.toString(aktualnaTemp);
                napisTemperautry.setText("Aktualna temperatura: " + aktualnaTemperaturaString + DEGREE); // Wypisz temperaturę



                // trzeba napisac warunki dla konwersjii bo porgram sie wysypie
                long x=new Date().getTime();
                DataPoint pointValue = new DataPoint(x,aktualnaTemp);
                wykres.setValue(pointValue); // wysłanie do bazy punktu temperatury

            }
        });

        // Read from the database
        tempWykres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                setData(dataSnapshot); //Wykres z ostetniego tygodnia
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });



    }

    public interface CallBackTemp {
        void onCallback(Float pon,Float wt,Float sr,Float czw,Float pt,Float sob,Float nd,Float aktualnaTemp);
    } // Calback temperature
    public void temperature(final CallBackTemp myCallback) {
        DatabaseReference temperature = database.getReference("labview") ;
        temperature.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float temp = dataSnapshot.child("aktualnaTemp").getValue(Float.class);

                pon = dataSnapshot.child("TempWykres").child("day1").getValue(Float.class);
                wt = dataSnapshot.child("TempWykres").child("day2").getValue(Float.class);
                sr = dataSnapshot.child("TempWykres").child("day3").getValue(Float.class);
                czw = dataSnapshot.child("TempWykres").child("day4").getValue(Float.class);
                pt = dataSnapshot.child("TempWykres").child("day5").getValue(Float.class);
                sob = dataSnapshot.child("TempWykres").child("day6").getValue(Float.class);
                nd = dataSnapshot.child("TempWykres").child("day7").getValue(Float.class);


                myCallback.onCallback(pon,wt,sr,czw,pt,sob,nd,temp);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    } // temperatura z bazy danych


    void SeekBar() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                String temp = Integer.toString(progresValue);
                UstawionaTemp.setText("Ustaw: "+progresValue+DEGREE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DatabaseReference setTemp = database.getReference("labview").child("ustawionaTemp") ;
                String temp = UstawionaTemp.getText().toString().substring(6);
                Toast.makeText(getApplicationContext(), "Ustawiono: "+temp, Toast.LENGTH_SHORT).show();
                setTemp.setValue(Integer.valueOf(temp.substring(1,3).trim()));
                UstawionaTemp.setText("");
            }
        });
    } // Ustawienie temperatury

    void setData(DataSnapshot dataSnapshot){
         ArrayList<BarEntry> yVals = new ArrayList<>();
        int i = 0;
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            Float value = dataSnapshot.child(dni.get(i)).getValue(Float.class);
            yVals.add(new BarEntry(i, value));
            i++;
        }






        BarDataSet set = new BarDataSet(yVals, "Średnia dobowa temperatura");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);

        BarData data = new BarData(set);

        mChart.setData(data);
        mChart.invalidate();
        mChart.animateY(500);
    }
}
