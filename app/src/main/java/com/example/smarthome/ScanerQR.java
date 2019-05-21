package com.example.smarthome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanerQR extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    ZXingScannerView scannerView;

    String wynik_skanowania;

    public static String ID_Device, Nazwa, Stan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);


    }

    @Override
    public void handleResult(Result result) {

        wynik_skanowania = result.getText();
        if(wynik_skanowania.startsWith("ID")){
            ID_Device = wynik_skanowania.substring(3,23); // przypisz ID
            Nazwa = wynik_skanowania.substring(30,35); // przypisz ID
            Stan = wynik_skanowania.substring(41,42); // Stan
        }




        newDevices.textView.setText(Nazwa);
        newDevices.textView2.setText(ID_Device);
        newDevices.textView3.setText(Stan);

        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }


}
