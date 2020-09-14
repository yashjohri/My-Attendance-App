package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

public class ClassAttendanceActivity extends AppCompatActivity {

    Toolbar tb;
    TextView tvClsDetails;
    RecyclerView rv;
    ArrayList<ClassAttendanceItem> classAttendanceItems;
    ClassAttendanceAdapter adapter;
    String org_id, batch, sub_id, sub_name;
    DatabaseReference ref;
    HashMap<String , Attendance> mapAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_attendance);

        tb = findViewById(R.id.tbClsAttendance);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        batch = getIntent().getStringExtra("batch");
        sub_id = getIntent().getStringExtra("sub_id");
        sub_name = getIntent().getStringExtra("sub_name");

        String classinfo = sub_id +" "+sub_name+"\n"+batch;
        tvClsDetails = findViewById(R.id.tvClsAttDetails);
        tvClsDetails.setText(classinfo);

        rv = findViewById(R.id.rv_cls_attendance);
        rv.setLayoutManager(new LinearLayoutManager(this));
        classAttendanceItems = new ArrayList<>();
        adapter = new ClassAttendanceAdapter(this, classAttendanceItems, org_id);
        rv.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference();
        updateClsAttendance();
    }

    private void updateClsAttendance() {

        ref.child(org_id).child("Attendance").child(batch).child(sub_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()==null){
                    Toast.makeText(ClassAttendanceActivity.this, "No Records", Toast.LENGTH_SHORT).show();
                    return;
                }

                mapAttendance = new HashMap<>();

                for(DataSnapshot ds:snapshot.getChildren()){

                    for(DataSnapshot sub_ds:ds.getChildren()){

                        AttendanceItem attendanceItem = sub_ds.getValue(AttendanceItem.class);
                        Attendance attendance = mapAttendance.get(attendanceItem.getStudentID());

                        if(attendance==null){
                            attendance = new Attendance(attendanceItem.getStudentName(), 0, 0);
                        }
                        attendance.setTotal(attendance.getTotal()+1);
                        attendance.setCount(attendance.getCount()+(attendanceItem.isPresent()?1:0));

                        mapAttendance.put(attendanceItem.getStudentID(), attendance);
                    }
                }

                for(String id:mapAttendance.keySet()){

                    Attendance attendance = mapAttendance.get(id);
                    int percent = attendance.getCount()*100/attendance.getTotal();
                    ClassAttendanceItem item = new ClassAttendanceItem(attendance.getName(), id, percent);
                    classAttendanceItems.add(item);
                }

                Collections.sort(classAttendanceItems);
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