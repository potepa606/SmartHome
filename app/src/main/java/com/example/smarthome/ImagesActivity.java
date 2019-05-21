package com.example.smarthome;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.smarthome.ImageOfRoom.rAdapter;
import static java.lang.Integer.parseInt;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    List<Button> buttons_list_read = new ArrayList<>();
    private FrameLayout layFrame2;
    public static RecyclerView recyclerViewRoom;

    private DatabaseReference mDatabaseRef;

    Button button;
    ImageView img;
    Rooms newRoom;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference DevicesRef;
    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final int[] ID2_ = new int[1];


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_images);

        img=(ImageView)findViewById(R.id.imgPicker);
        layFrame2 = findViewById(R.id.layFrame2);

        //Załaduj przewijanie
        recyclerViewRoom = findViewById(R.id.recyclerViewRoom);
        recyclerViewRoom.setHasFixedSize(true);
        recyclerViewRoom.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRoom.setAdapter(ImageOfRoom.rAdapter); // wyświetl listę
        rAdapter.setOnItemClickListener2(this); // ;włącz klikniecia

        //loadImageFromStorage(ImageOfRoom.path);



        // Zmiana stanów buttons
        UpdateFirebaseDataBase();

    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imgPicker);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    private void UpdateFirebaseDataBase(){

        DevicesRef = database.getReference("Devices").child(user.getUid()); // Referencja do bazydanych->Device->USERid
        DevicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ID2_[0]=0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Device uploadd = postSnapshot.getValue(Device.class);
                    ID2_[0] = parseInt(uploadd.getID().substring(1,2).trim());


                    for(int i=0; i<buttons_list_read.size();i++){

                        if(buttons_list_read.get(i).getId() == ID2_[0]){

                            if(uploadd.getStan().equals("ON")){
                                buttons_list_read.get(i).setEnabled(true);
                            }else if(uploadd.getStan().equals("OFF")){
                                buttons_list_read.get(i).setEnabled(false);
                            }else {
                                buttons_list_read.get(i).setEnabled(false);
                            }

                        }

                    }


                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }


    @Override
    public void onItemClick(int position) {
        layFrame2.removeViewsInLayout(1,layFrame2.getChildCount()-1);
        Bitmap mapBit = ImageOfRoom.rAdapter.getmRooms().get(position).getBitmap();
        List<Button> buttons_list= ImageOfRoom.rAdapter.getmRooms().get(position).getButtons();

        newRoom = new Rooms(mapBit,buttons_list);

        //Załaduj buttony z poprzedniej aktywnosci
        img.setImageBitmap(newRoom.getBitmap());
        buttons_list_read.clear();
        for(int i=0; i<newRoom.getButtons().size(); i++){

            button = new Button(this);
            button.setBackgroundResource(R.drawable.button_door);
            button.setId(newRoom.getButtons().get(i).getId());
            button.setEnabled(false);
            button.setLayoutParams(newRoom.getButtons().get(i).getLayoutParams());
            buttons_list_read.add(button);
            layFrame2.addView(button);

        }
        UpdateFirebaseDataBase();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " , Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDeleteClick(int position) {
        Bitmap bitmap ;
        if(position+1 == ImageOfRoom.rAdapter.getItemCount()){
            bitmap = ImageOfRoom.rAdapter.getmRooms().get(position-1).getBitmap();
        } else{
            bitmap = ImageOfRoom.rAdapter.getmRooms().get(position+1).getBitmap();
        }
        img.setImageBitmap(bitmap);

        layFrame2.removeViewsInLayout(1,layFrame2.getChildCount()-1);
        ImageOfRoom.rAdapter.getmRooms().remove(position);
        recyclerViewRoom.setAdapter(ImageOfRoom.rAdapter); // wyświetl listę
        //Toast.makeText(this, "W:" + ImageOfRoom.rAdapter.getItemCount() + " , Zaznacz: " + position , Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }



}
