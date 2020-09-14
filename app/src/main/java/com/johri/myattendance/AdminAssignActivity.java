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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.TeacherSubjectAdapter;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;

public class AdminAssignActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    ArrayList<TeacherSubject> teacherSubjects;
    TeacherSubjectAdapter adapter;
    DatabaseReference dbref;
    ValueEventListener listener;
    String org, org_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assign);

        tb = findViewById(R.id.tbAdminAssign);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbref = FirebaseDatabase.getInstance().getReference();
        org = getIntent().getStringExtra("org");
        org_id = getIntent().getStringExtra("org_id");

        teacherSubjects = new ArrayList<>();
        rv = findViewById(R.id.rv_adminTeacherSubjects);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeacherSubjectAdapter(this, teacherSubjects, org_id);
        rv.setAdapter(adapter);

        updateTeacherSubjects();
    }

    private void updateTeacherSubjects() {

        listener = dbref.child(org_id).child("Assign").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teacherSubjects.clear();

                for(DataSnapshot ds:snapshot.getChildren()){
                    teacherSubjects.add(ds.getValue(TeacherSubject.class));
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

    @Override
    protected void onDestroy() {
        dbref.child(org_id).child("Assign").removeEventListener(listener);
        super.onDestroy();
    }
}