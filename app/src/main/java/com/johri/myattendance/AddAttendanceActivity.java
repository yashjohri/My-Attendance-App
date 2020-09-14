package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.AttendanceListAdapter;
import com.johri.myattendance.models.AttendanceItem;
import com.johri.myattendance.models.Student;

import java.util.ArrayList;
import java.util.Calendar;

public class AddAttendanceActivity extends AppCompatActivity {

    Toolbar tb;
    TextView tvClsDetails;
    Button btnSetDate;
    RecyclerView rv;
    AttendanceListAdapter adapter;
    ArrayList<AttendanceItem> attendanceItems;
    String org_id, batch, sub_id, sub_name, date;
    int my_day, my_month, my_year;
    DatePickerDialog.OnDateSetListener listener;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance);

        tb = findViewById(R.id.tbAddAttendance);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();
        my_day = calendar.get(Calendar.DAY_OF_MONTH);
        my_month = calendar.get(Calendar.MONTH);
        my_year = calendar.get(Calendar.YEAR);
        btnSetDate = findViewById(R.id.btnSetDate);
        setDate();
        setDateListener();

        org_id = getIntent().getStringExtra("org_id");
        batch = getIntent().getStringExtra("batch");
        sub_id = getIntent().getStringExtra("sub_id");
        sub_name = getIntent().getStringExtra("sub_name");

        tvClsDetails = findViewById(R.id.tvClsDetails);
        String cls_info = sub_id+" "+sub_name+"\n"+batch;
        tvClsDetails.setText(cls_info);

        rv = findViewById(R.id.rv_attendance_students);
        rv.setLayoutManager(new LinearLayoutManager(this));
        attendanceItems = new ArrayList<>();
        adapter = new AttendanceListAdapter(this, attendanceItems, org_id, batch, sub_id);
        rv.setAdapter(adapter);

        updateAttendanceList();
    }

    private void setDate() {
        String format = "%02d";
        date = String.format(format,my_day)+" - "+String.format(format,my_month+1)+" - "+(my_year%100);
        btnSetDate.setText(date);
    }

    private void setDateListener(){
        listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                my_day = dayOfMonth;
                my_month = month;
                my_year = year;
                calendar.set(year, month, dayOfMonth);
                setDate();
            }
        };
    }

    private void updateAttendanceList() {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                attendanceItems.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Student student = ds.getValue(Student.class);
                    if(student.getBatch().equals(batch)){
                        AttendanceItem item = new AttendanceItem(student.getName(), student.getRoll_num(), false);
                        attendanceItems.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnClicked(View view) {

        if(view.getId()==R.id.btnSaveAtt){
            adapter.saveAttendance(date);
        }
        else if(view.getId()==R.id.btnSetDate){
            new DatePickerDialog(AddAttendanceActivity.this, listener, my_year, my_month, my_day).show();
        }
    }
}