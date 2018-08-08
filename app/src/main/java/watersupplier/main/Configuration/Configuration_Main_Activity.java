package watersupplier.main.Configuration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import watersupplier.main.Adapter.Configuration_ListAdapter;
import watersupplier.main.Customer.Add_Edit_Customer_Activity;
import watersupplier.main.Customer.Customer_List_Activity;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.Main.First_Menu;
import watersupplier.main.NewThings.NewThingsButtonsActivity;
import watersupplier.main.Product.Product_Setup;
import watersupplier.main.R;
import watersupplier.main.Utils.AppUtills;

/**
 * Created by Aman Gupta on 20/1/17.
 */

public class Configuration_Main_Activity extends Common_ActionBar_Abstract implements View.OnClickListener,AdapterView.OnItemClickListener {

    //UI Variable
    private ListView config_listView;
    private Configuration_ListAdapter configuration_listAdapter;

    //Non-UI variable

    private ArrayList<String> menu_namesAL;
    private ArrayList<Integer> menu_iconAL;
    private ArrayList<String> detailsAL;
    private int SelectedPosition=-1;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_main);
        initialization();
        generateEvents();
        screenWidth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_back:
                Intent intent = new Intent(Configuration_Main_Activity.this, First_Menu.class);
                startActivity(intent);
                AppUtills.givefinishEffect(this);
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtills.givefinishEffect(this);
        finish();
    }

    private void initialization() {
        getSupportActionBar().setTitle(R.string.configuration_page);
        config_listView = (ListView) findViewById(R.id.config_list);
        menu_iconAL = new ArrayList<Integer>();
        menu_namesAL = new ArrayList<String>();
        detailsAL = new ArrayList<String>();

        configuration_listAdapter = new Configuration_ListAdapter(Configuration_Main_Activity.this,Configuration_Main_Activity.this,menu_namesAL,menu_iconAL,detailsAL);

        config_listView.setAdapter(configuration_listAdapter);
        configuration_listAdapter.notifyDataSetChanged();
        database = FirebaseDatabase.getInstance();
        databaseReference=database.getReference(getString(R.string.app_name)).child(AppUtills.getUserName(Configuration_Main_Activity.this));


    }

    @Override
    protected void onResume() {
        super.onResume();
        addMenuIcons();
    }

    private void addMenuIcons() {
        menu_iconAL.clear();
        menu_namesAL.clear();
        detailsAL.clear();

        menu_iconAL.add(R.drawable.backup);
        menu_namesAL.add(getString(R.string.backup_page));
        detailsAL.add("click to take Data Backup..");

        menu_iconAL.add(R.drawable.restore);
        menu_namesAL.add(getString(R.string.restore_page));
        detailsAL.add("to restore backed up data..");

        menu_iconAL.add(R.drawable.archive2);
        menu_namesAL.add(getString(R.string.archive_page));
        detailsAL.add("delete the unwanted data..");

        menu_iconAL.add(R.drawable.app_settings);
        menu_namesAL.add(getString(R.string.applicationSetting_page));
        detailsAL.add("configure application here..");

        menu_iconAL.add(R.drawable.printer);
        menu_namesAL.add(getString(R.string.printerSetting_page));
        detailsAL.add("select printer..");

        menu_iconAL.add(R.drawable.backup_img);
        menu_namesAL.add("New Things");
        detailsAL.add("select things..");

       /* menu_iconAL.add(R.drawable.printer);
        menu_namesAL.add("Printer Setting");
        menu_iconAL.add(R.drawable.printer);
        menu_namesAL.add("Printer Setting");
        menu_iconAL.add(R.drawable.printer);
        menu_namesAL.add("Printer Setting");
        menu_iconAL.add(R.drawable.printer);
        menu_namesAL.add("Printer Setting");*/



    }

//    Backup Code
//    http://stackoverflow.com/questions/41256500/in-android-where-does-the-file-downloaded-from-firebase-storage-get-stored

    private void generateEvents() {
        config_listView.setOnItemClickListener(this);
    }

    private void screenWidth() {
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectedPosition=position;
        if(SelectedPosition==0){

            Intent intent = new Intent(this,Android_Plot_Activity.class);
            startActivity(intent);

        }
        else if(SelectedPosition==5){

            Intent intent = new Intent(this,NewThingsButtonsActivity.class);
            startActivity(intent);
            AppUtills.giveIntentEffect(this);
        }

//        if(menu_namesAL.contains(getString(R.string.backup_page))){
//            Firebase firebaseStorage = FirebaseStorage.getInstance();
//            final StorageManager storageReference = databaseReference.getReferenceFromUrl("gs://ldq-app-d2e6b.appspot.com");

            /*button_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String fileName = new String(editText_file.getText().toString());

                    StorageReference childReference = storageReference.child("Quizzes");

                    StorageReference fileReference = storageReference.child("Quizzes/" + fileName + ".pdf");

                    File localFile = null;

                    try {
                        localFile = File.createTempFile(fileName, "pdf");
                    }
                    catch (IOException ioe){
                        Toast.makeText(getContext(), "File creation failed", Toast.LENGTH_SHORT).show();
                    }

                    fileReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(),"File downloaded",LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Download failed. Try again!", LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(getContext(),"Button working",LENGTH_SHORT).show();
                }
            });*/
        }
         /*else if(menu_namesAL.contains(getString(R.string.restore_page))){
            Toast.makeText(this,"Restore",Toast.LENGTH_SHORT).show();
        }*/

//    }
}
