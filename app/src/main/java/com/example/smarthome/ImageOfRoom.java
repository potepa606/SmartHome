package com.example.smarthome;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;


public class ImageOfRoom extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    public static Button buttonDoor;
    private TextView mTextViewShowUploads;
    EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private FrameLayout layFrame;


    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    //firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference DevicesRef;
    private List<Device> LisaDevices = new ArrayList<>(); // Stwóz tablicę obiektów Device;
    List<Button> przyciski_lista = new ArrayList<>() ;
    List<Rooms> rooms_list = new ArrayList<>(); // Stwóz tablicę obiektów Device
    public static ImageAdapter rAdapter;


    //Pobierz aktualnego uzytkownika
    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final int[] ID2_ = new int[1];

    Mouse mouse;
    Rooms room;
    Bitmap bitmap;
    public static String path;


    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_of_room);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        layFrame = (FrameLayout) findViewById(R.id.layFrame) ;
        registerForContextMenu(mImageView);

        TextView A = new TextView(this);
        A.setText("asssssssss");



        Buttons(); // Oprogramowanie przyciskow
        UpdateFirebaseDataBase();
        wspolzedneMyszy(); // pobieranie x i y myszy

    }



    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Wybierz urzadzenie:");

        final int[] ID_ = new int[1];
        DevicesRef = database.getReference("Devices").child(user.getUid()); // Referencja do bazydanych->Device->USERid
        DevicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                LisaDevices.clear();//Usuń wszystkie obiekty przed zapisaniem od nowa
                ID_[0]=0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Device uploadd = postSnapshot.getValue(Device.class);

                    ID_[0] = parseInt(uploadd.getID().substring(1,2).trim());

                    menu.add(0, ID_[0], 0, uploadd.getName());//groupId, itemId, order, title
                    LisaDevices.add(uploadd);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImageOfRoom.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.option_1:
                LisaDevices.clear();
                przyciski_lista.clear();
                layFrame.removeViewsInLayout(1,layFrame.getChildCount()-1);
                break;
            case R.id.option_2:
                String title = mEditTextFileName.getText().toString().trim();

                if(!przyciski_lista.isEmpty() & bitmap !=null){
                    if(title.equals("")){
                        Toast.makeText(ImageOfRoom.this, "Wpisz nazwę !", Toast.LENGTH_SHORT).show();
                    }else {
                        List<Button> newListaBut =new ArrayList<>(przyciski_lista) ;
                        room = new Rooms(bitmap,newListaBut,mEditTextFileName.getText().toString());

                        rooms_list.add(room);
                        rAdapter = new ImageAdapter(ImageOfRoom.this, rooms_list); // stwórz nowy obiekt i wyslij listę
                        rAdapter.notifyDataSetChanged();
                        layFrame.removeViewsInLayout(1,layFrame.getChildCount()-1);
                        przyciski_lista.clear();
                        mImageView.setImageBitmap(null);
                        mEditTextFileName.setText("");
                        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(this, "Nic nie dodałeś", Toast.LENGTH_SHORT).show();
                break;
            default:
                NEW_Buttonek(item.getItemId(),this);
                break;
              //  return super.onContextItemSelected(item);
        }
        return true;


    }

    public void Buttons(){

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    uploadFile();
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!rooms_list.isEmpty()){
                    OpenImagesActivity();
                }else {
                    Toast.makeText(ImageOfRoom.this, "Brak dodanych obiektów", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
           // path = saveToInternalStorage(bitmap);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), downloadUri.toString());

                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(upload);
                        mProgressBar.setProgress(0);
                        mEditTextFileName.setText("");
                        Toast.makeText(ImageOfRoom.this, "Upload successful", Toast.LENGTH_LONG).show();
                    } else
                    {
                        Toast.makeText(ImageOfRoom.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImageOfRoom.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void OpenImagesActivity(){
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void wspolzedneMyszy(){

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mouse = new Mouse(event.getX(),event.getY());
                return false;
            }
        });

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

                    for(int i=0; i<przyciski_lista.size();i++){

                        if(przyciski_lista.get(i).getId() == ID2_[0]){

                            if(uploadd.getStan().equals("ON")){
                                przyciski_lista.get(i).setEnabled(true);
                            }else if(uploadd.getStan().equals("OFF")){
                                przyciski_lista.get(i).setEnabled(false);
                            }else {
                                przyciski_lista.get(i).setEnabled(false);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void NEW_Buttonek(int ID_, Context context){

        buttonDoor = new Button(context);
        buttonDoor.setBackgroundResource(R.drawable.button_door);
        buttonDoor.setId(ID_);
        buttonDoor.setEnabled(false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = (int) mouse.getY(); // margin in pixels, not dps
        layoutParams.leftMargin = (int) mouse.getX(); // margin in pixels, not dps
        layoutParams.height = 100;
        layoutParams.width = 100;
        buttonDoor.setLayoutParams(layoutParams);
        layFrame.addView(buttonDoor);
        przyciski_lista.add(buttonDoor);
    }

    private void saveData(){
        SharedPreferences sharedPreferences =  getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json_id = gson.toJson("test");
        editor.putString("IDS", json_id);

        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences =  getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json_ids = sharedPreferences.getString("IDS",null);
        String json_bitmaps = sharedPreferences.getString("bitmaps",null);

        Type type_id = new TypeToken<ArrayList<Integer>>(){}.getType();
        Type type_bitmap = new TypeToken<ArrayList<Bitmap>>(){}.getType();

    }
}
