package com.example.mmh_49.employeemanagement;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.lang.*;

import static android.graphics.Color.*;

public class MainActivity extends AppCompatActivity {

    private Button btn_new_employee;
    private boolean isButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_new_employee = (Button) findViewById(R.id.btn_new_employee);
        btn_new_employee.setOnClickListener(new View.OnClickListener() { // Then you should add add click listener for your button.
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_new_employee) {
                    isButtonClicked = !isButtonClicked; // toggle the boolean flag
                    System.out.printf("Button clicked!");
//                    btn_new_employee.setAlpha((float) 0.5);
                   // btn_new_employee.setBackgroundColor(Color.parseColor("#e68a02"));
                }
            }
        });
    }
}
