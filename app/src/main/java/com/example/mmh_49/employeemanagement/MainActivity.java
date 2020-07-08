package com.example.mmh_49.employeemanagement;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    //private static final String TAG = MainActivity.class.getSimpleName();

    private SqliteDatabase mDatabase;
    private ArrayList<EmployeeModel> allEmployees = new ArrayList<>();
    private EmployeeAdapter mAdapter;
    String selected_gender="Select sex";

    String[] gender = {"Select sex", "Male", "Female", "Other"};

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

        FloatingActionButton add_employee = (FloatingActionButton) findViewById(R.id.add_employee);
        add_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });
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

    private void addTaskDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_employee_layout, null); /// 'null' hole... other activity te jabe na!

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

        ///For Image uploading:

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new EMPLOYEE");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ADD EMPLOYEE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String age = ageField.getText().toString();

                if(TextUtils.isEmpty(name) || selected_gender == "Select sex"){
                    Toast.makeText(MainActivity.this, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    EmployeeModel newEmployee = new EmployeeModel(name, age, selected_gender, null); ///Data is sent to the model
                    mDatabase.addEmployees(newEmployee); ///Insert new employee to database via model (getting values from model)

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
