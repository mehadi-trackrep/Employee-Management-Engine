package com.example.mmh_49.employeemanagement;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by MMH-49 on 7/8/2020.
 */
public class EmployeeViewHolder extends RecyclerView.ViewHolder {

    public TextView employee_name, employee_age, employee_gender;
    public ImageView employee_image, delete_employee, edit_employee;

    public EmployeeViewHolder(View itemView) {
        super(itemView);
        employee_name = (TextView)itemView.findViewById(R.id.employee_name);
        employee_age = (TextView)itemView.findViewById(R.id.employee_age);
        employee_gender = (TextView)itemView.findViewById(R.id.employee_gender);
        employee_image = (ImageView)itemView.findViewById(R.id.employee_image);
        delete_employee = (ImageView)itemView.findViewById(R.id.delete_employee);
        edit_employee = (ImageView)itemView.findViewById(R.id.edit_employee);
    }
}
