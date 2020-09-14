package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.TeacherClassAdapter;
import com.johri.myattendance.models.Subject;
import com.johri.myattendance.models.TeacherClass;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class TeacherViewActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    TeacherClassAdapter adapter;
    ArrayList<TeacherClass> classes;
    HashMap<String, String> mapSubs;
    String org_id, teacher_id;
    boolean showRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view);

        tb = findViewById(R.id.tbTeacherView);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        teacher_id = getIntent().getStringExtra("teacher_id");
        showRecords = getIntent().getBooleanExtra("showRecords", false);

        rv = findViewById(R.id.rv_viewAtt_cls);
        rv.setLayoutManager(new LinearLayoutManager(this));
        classes = new ArrayList<>();
        if(showRecords){
            adapter = new TeacherClassAdapter(this, classes, org_id, teacher_id, false, true);
            tb.setTitle("View Records");
        }
        else{
            adapter = new TeacherClassAdapter(this, classes, org_id, teacher_id, true, false);
        };
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