package com.example.mmh_49.employeemanagement;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by MMH-49 on 7/8/2020.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeViewHolder> implements Filterable, AdapterView.OnItemSelectedListener {

    private Context context;
    private ArrayList<EmployeeModel> listEmployees;
    private ArrayList<EmployeeModel> mArrayList;

    String selected_gender = "Select sex";
    String[] gender = {"Select sex", "Male", "Female", "Other"};

    ///for image:
    ImageView mImageView;
    Button mUploadButton;
    private String image_path = null;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private SqliteDatabase mDatabase;

    ///Constructor initialize
    public EmployeeAdapter(Context context, ArrayList<EmployeeModel> listEmployees) {
        this.context = context;
        this.listEmployees = listEmployees;
        this.mArrayList = listEmployees;
        mDatabase = new SqliteDatabase(context);
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_list_layout, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) { //Here, the employee List is shown!! (V.V.I.)
        final EmployeeModel employeeModel = listEmployees.get(position);

        holder.employee_name.setText("Name: " + employeeModel.getName());
        holder.employee_age.setText("Age: " + employeeModel.getAge());
        holder.employee_gender.setText("Sex: " + employeeModel.getGender());

        try {
            holder.employee_image.setImageURI(Uri.fromFile(new File(employeeModel.getImg())));
        } catch (OutOfMemoryError e) {
        e.printStackTrace();
        } finally {
        }

        holder.edit_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTaskDialog(employeeModel);
            }
        });

        holder.delete_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete row from database
                mDatabase.deleteEmployee(employeeModel.getId());

                //refresh the activity page. (V.V.I........)
                ((Activity)context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    listEmployees = mArrayList;
                } else {

                    ArrayList<EmployeeModel> filteredList = new ArrayList<>();

                    for (EmployeeModel employeeModel : mArrayList) {

                        if (employeeModel.getName().toLowerCase().contains(charString)) {

                            filteredList.add(employeeModel);
                        }
                    }

                    listEmployees = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listEmployees;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listEmployees = (ArrayList<EmployeeModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return listEmployees.size();
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if(position == 0) Toast.makeText(context, "Gender isn't selected!" , Toast.LENGTH_LONG).show();
        else{
            selected_gender = gender[position];
            Toast.makeText(context, gender[position] + " is selected!" , Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void editTaskDialog(final EmployeeModel employeeModel){

        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.add_employee_layout, null);

        final EditText nameField = (EditText)subView.findViewById(R.id.employee_name);
        final EditText ageField = (EditText)subView.findViewById(R.id.employee_age);

        ///For image portion: (V.V.I.)
        mImageView = (ImageView)subView.findViewById(R.id.image_view);
        mUploadButton = (Button)subView.findViewById(R.id.upload_image);
        mUploadButton.setVisibility(View.GONE);

        ///For 'Gender' spinner----
        final Spinner spin = (Spinner)subView.findViewById(R.id.employee_gender);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the gender list
        ArrayAdapter aa = new ArrayAdapter(context, android.R.layout.simple_spinner_item, gender);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        if(employeeModel != null){
            nameField.setText(employeeModel.getName());
            ageField.setText(String.valueOf(employeeModel.getAge()));
            mImageView.setImageURI(Uri.fromFile(new File(employeeModel.getImg())));
            image_path = employeeModel.getImg();
            selected_gender = employeeModel.getGender();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Employee");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("EDIT EMPLOYEE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String name = nameField.getText().toString();
                final String age = ageField.getText().toString();

                if(TextUtils.isEmpty(name)  || selected_gender == "Select sex" || image_path == null){
                    Toast.makeText(context, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    mDatabase.updateEmployee(new EmployeeModel(employeeModel.getId(), name, age, selected_gender, employeeModel.getImg()));
                    //refresh the activity
                    ((Activity)context).finish();
                    context.startActivity(((Activity)context).getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
}