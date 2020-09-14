package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.ClassAttendanceAdapter;
import com.johri.myattendance.models.Attendance;
import com.johri.myattendance.models.AttendanceItem;
import com.johri.myattendance.models.ClassAttendanceItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AdminAttendanceActivity extends AppCompatActivity {

    Toolbar tb;
    TextView tvDetails;
    String org_id, batch;
    RecyclerView rv;
    ArrayList<ClassAttendanceItem> classAttendanceItems;
    ClassAttendanceAdapter adapter;
    DatabaseReference dbref;
    HashMap<String, Attendance> mapAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);

        tb = findViewById(R.id.tbAdminAttendance);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        batch = getIntent().getStringExtra("batch");

        tvDetails = findViewById(R.id.tv_admin_attendance_details);
        tvDetails.setText(batch);

        rv = findViewById(R.id.rv_adminAttendance);
        rv.setLayoutManager(new LinearLayoutManager(this));
        classAttendanceItems = new ArrayList<>();
        adapter = new ClassAttendanceAdapter(this, classAttendanceItems, org_id);
        rv.setAdapter(adapter);

        dbref = FirebaseDatabase.getInstance().getReference();
        mapAttendance = new HashMap<>();
        updateAttendanceList();
    }

    private void updateAttendanceList() {

        dbref.child(org_id).child("Attendance").child(batch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){

                    for(DataSnapshot sub_ds:ds.getChildren()){

                        for(DataSnapshot sub_sub_ds:sub_ds.getChildren()){

                            AttendanceItem item = sub_sub_ds.getValue(AttendanceItem.class);

                            Attendance attendance = mapAttendance.get(item.getStudentID());
                            if(attendance==null){
                                attendance = new Attendance(item.getStudentName(), 0, 0);
                                mapAttendance.put(item.getStudentID(), attendance);
                            }

                            attendance.setTotal(attendance.getTotal()+1);
                            attendance.setCount(attendance.getCount()+(item.isPresent()?1:0));

                        }
                    }
                }

                for(String id:mapAttendance.keySet()){

                    Attendance attendance = mapAttendance.get(id);
                    ClassAttendanceItem item = new ClassAttendanceItem(attendance.getName(), id, attendance.getCount()*100/attendance.getTotal());
                    classAttendanceItems.add(item);
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
}