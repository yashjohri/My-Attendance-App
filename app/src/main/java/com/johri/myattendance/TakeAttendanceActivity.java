package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.TeacherClassAdapter;
import com.johri.myattendance.R;
import com.johri.myattendance.models.Subject;
import com.johri.myattendance.models.TeacherClass;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class TakeAttendanceActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    TeacherClassAdapter adapter;
    String org_id, teacher_id;
    ArrayList<TeacherClass> classes;
    HashMap<String, String> mapSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        tb = findViewById(R.id.tbTakeAtt);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        teacher_id = getIntent().getStringExtra("teacher_id");

        classes = new ArrayList<>();
        rv = findViewById(R.id.rv_takeAtt_cls);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeacherClassAdapter(this, classes, org_id, teacher_id, false, false);
        rv.setAdapter(adapter);

        mapSubs = new HashMap<>();
        updateMapSubs();
    }

    private void updateMapSubs() {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){

                    Subject subject = ds.getValue(Subject.class);
                    mapSubs.put(subject.getId(), subject.getName());
                }

                updateClasses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateClasses() {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").child(teacher_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TeacherSubject teacherSubject = snapshot.getValue(TeacherSubject.class);

                HashMap<String, ArrayList<String>> subs = teacherSubject.getTeacher_subjects();

                if(subs!=null){
                    classes.clear();
                    for(String subID:subs.keySet()){

                        String subName = mapSubs.get(subID);

                        for(String cls:subs.get(subID)){

                            TeacherClass teacherClass = new TeacherClass(subName, subID, cls);
                            classes.add(teacherClass);
                        }
                    }
                    adapter.notifyDataSetChanged();
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