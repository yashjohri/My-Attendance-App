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
import com.johri.myattendance.Adapters.StudentSubjectAdapter;
import com.johri.myattendance.models.AttendanceItem;
import com.johri.myattendance.models.StudentSubject;
import com.johri.myattendance.models.Subject;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentSubjectsActivity extends AppCompatActivity {

    Toolbar tb;
    TextView tvOverall;
    String org_id, student_id, batch_name;
    DatabaseReference dbref;
    RecyclerView rv;
    ArrayList<StudentSubject> studentSubjects;
    StudentSubjectAdapter adapter;
    HashMap<String, StudentSubject> subject_map;
    int total, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_subjects);

        tb = findViewById(R.id.tbStudentSubjects);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        student_id = getIntent().getStringExtra("student_id");
        batch_name = getIntent().getStringExtra("batch_name");

        dbref = FirebaseDatabase.getInstance().getReference();
        tvOverall = findViewById(R.id.tvOverallPercent);

        rv = findViewById(R.id.rvStudentSubjects);
        rv.setLayoutManager(new LinearLayoutManager(this));
        studentSubjects = new ArrayList<>();
        subject_map = new HashMap<>();
        adapter = new StudentSubjectAdapter(this, studentSubjects);
        rv.setAdapter(adapter);

        updateMap();
    }

    private void updateMap() {

        dbref.child(org_id).child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){

                    Subject subject = ds.getValue(Subject.class);

                    if(batch_name.startsWith(subject.getBranch()+" "+subject.getSem())){
                        subject_map.put(subject.getId(), new StudentSubject(subject.getName(), subject.getId(), null, 0));
                    }
                }
                updateRV();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateRV() {

        total = 0;
        count = 0;

        dbref.child(org_id).child("Attendance").child(batch_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        String subjectID = ds.getKey();
                        if(subject_map.containsKey(subjectID)){

                            int subTotal = 0;
                            int subCount = 0;
                            StudentSubject studentSubject = subject_map.get(subjectID);

                            for(DataSnapshot sub_ds:ds.getChildren()){

                                String date = sub_ds.getKey();

                                for(DataSnapshot att:sub_ds.getChildren()){

                                    if(att.getKey().equals(student_id)){

                                        AttendanceItem item = att.getValue(AttendanceItem.class);

                                        HashMap<String, Boolean> mapdates = studentSubject.getDatesMap();

                                        if(mapdates==null){
                                            mapdates = new HashMap<>();
                                        }

                                        if(item.isPresent()){
                                            count++;
                                            subCount++;
                                        }
                                        total++;
                                        subTotal++;

                                        mapdates.put(date, item.isPresent());
                                        studentSubject.setDatesMap(mapdates);

                                        break;
                                    }
                                }
                            }

                            int percent = subCount*100/subTotal;
                            studentSubject.setPercent(percent);
                            studentSubjects.add(studentSubject);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    int total_percent = count*100/total;
                    tvOverall.setText("Overall percentage = "+String.format("%02d", total_percent)+" %");
                }
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