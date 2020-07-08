package com.example.mmh_49.employeemanagement;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName(); //For log cat!!

    private SqliteDatabase mDatabase;
    private ArrayList<EmployeeModel> allEmployees = new ArrayList<>();
    private EmployeeAdapter mAdapter;
    String selected_gender="Select sex";
    String[] gender = {"Select sex", "Male", "Female", "Other"};

    ///for image:
    ImageView mImageView;
    Button mUploadButton;
    private String image_path = null;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    //File-import-export
    private static final String FILE_NAME = "employee_data.csv";
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  ///Database onCreate() all pre-tasks
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout fLayout = (FrameLayout) findViewById(R.id.activity_to_do);

        RecyclerView employeeView = (RecyclerView)findViewById(R.id.rv_employeeList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        employeeView.setLayoutManager(linearLayoutManager);
        employeeView.setHasFixedSize(true);

        mDatabase = new SqliteDatabase(this);
        allEmployees = mDatabase.listEmployees();

        if(allEmployees.size() > 0){
            employeeView.setVisibility(View.VISIBLE);
            mAdapter = new EmployeeAdapter(this, allEmployees);
            employeeView.setAdapter(mAdapter);

        }else {
            employeeView.setVisibility(View.GONE);
            Toast.makeText(this, "There are no employees in the database. Start adding new one!", Toast.LENGTH_LONG).show();
        }

        ///All bottom floating button handles:
        FloatingActionButton import_database = (FloatingActionButton) findViewById(R.id.data_import);
        import_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importTaskDatabase(view);
            }
        });
        FloatingActionButton export_database = (FloatingActionButton) findViewById(R.id.data_export);
        export_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportTaskDatabase(view);
            }
        });

        FloatingActionButton add_employee = (FloatingActionButton) findViewById(R.id.add_employee);
        add_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });
    }

    ///File import-export
    private void importTaskDatabase(View view){
        load(view);
    }
    private void exportTaskDatabase(View view){
        save(view);
    }

    public void save(View v) {
        try {
            FileWriter csvWriter = new FileWriter(FILE_NAME);
            int count = 0;
            while (allEmployees.size() > count) {
                csvWriter.append(allEmployees.get(0).getId() + ",");
                csvWriter.append(allEmployees.get(0).getName() + ",");
                csvWriter.append(allEmployees.get(0).getAge() + ",");
                csvWriter.append(allEmployees.get(0).getGender() + ",");
                csvWriter.append(allEmployees.get(0).getImg() + ",");
                count++;
            }
            csvWriter.flush();
            Toast.makeText(this, "Data backed up to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
            csvWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
    public void load(View v) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(FILE_NAME));

            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                EmployeeModel newEmployee = new EmployeeModel(data[1], data[2], data[3], data[4]); ///Data is sent to the model
                mDatabase.addEmployees(newEmployee); ///Insert new employee to database via model (getting values from model)
            }
            Toast.makeText(this, "Data Imported from " + FILE_NAME, Toast.LENGTH_SHORT).show();
            csvReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    ///Image upload-store necessary methods
    private void uploadImageTask(){
        //check runtime permission:
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //permission not granted , request it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            }else{ //permission already granted
                pickImageFromGallery();
            }
        }
        else{ //system os is less than Marshmallow
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery(){
        //Intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); //All type of image!
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            //set image to image view
            Uri selectedImageURI = data.getData();
            File imageFile = new File(getRealPathFromURI(selectedImageURI));
            //String ck_file = "/storage/emulated/0/DCIM/Camera/IMG_20200706_134223.jpg";
            ///Log.w(TAG, "#############MMH49#############Debug--> Image File Path: " + imageFile.toString());
            //mImageView.setImageURI(Uri.fromFile(new File(ck_file)));
            image_path = imageFile.toString();
            mImageView.setImageURI(data.getData());
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }
                else{
                    Toast.makeText(this, "Permission denied!..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if(position == 0) Toast.makeText(getApplicationContext(), "Gender isn't selected!" , Toast.LENGTH_LONG).show();
        else{
            selected_gender = gender[position];
            Toast.makeText(getApplicationContext(), gender[position] + " is selected!" , Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void addTaskDialog(){  //New Employee data store...
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_employee_layout, null); /// 'null' hole... other activity te jabe na!

        ///For image portion: (V.V.I.)
        mImageView = (ImageView)subView.findViewById(R.id.image_view);
        mUploadButton = (Button)subView.findViewById(R.id.upload_image);

        //handle image button click
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageTask();
            }
        });

        final EditText nameField = (EditText)subView.findViewById(R.id.employee_name);
        final EditText ageField = (EditText)subView.findViewById(R.id.employee_age);
        ///For 'Gender' spinner----
        Spinner spin = (Spinner)subView.findViewById(R.id.employee_gender);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the gender list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new EMPLOYEE");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ADD EMPLOYEE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String age = ageField.getText().toString();

                if(TextUtils.isEmpty(name) || selected_gender == "Select sex" || image_path == null){
                    Toast.makeText(MainActivity.this, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    EmployeeModel newEmployee = new EmployeeModel(name, age, selected_gender, image_path); ///Data is sent to the model
                    mDatabase.addEmployees(newEmployee); ///Insert new employee to database via model (getting values from model)
                    image_path = null; //Global Variable

                    finish();
                    startActivity(getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
//        searchView.getQueryHint("Search Employee Name...");
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter!=null)
                    mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
