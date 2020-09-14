package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.StudentAdapter;
import com.johri.myattendance.Adapters.TeacherAdapter;
import com.johri.myattendance.models.Student;
import com.johri.myattendance.models.Teacher;

import java.util.ArrayList;

public class AdminStudentsActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    String org, org_id;
    DatabaseReference dbref;
    ValueEventListener listener;
    ArrayList<Student> students;
    StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_students);

        tb = findViewById(R.id.tbAdminStudents);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbref = FirebaseDatabase.getInstance().getReference();
        org_id = getIntent().getStringExtra("org_id");
        org = getIntent().getStringExtra("org");

        students = new ArrayList<>();
        rv = findViewById(R.id.rv_adminStudents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(this, students, org_id);
        rv.setAdapter(adapter);

        updateStudent_RV();
    }

    private void updateStudent_RV() {

        listener = dbref.child(org_id).child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()==null){
                    Toast.makeText(AdminStudentsActivity.this, "No student found", Toast.LENGTH_SHORT).show();
                    return;
                }

                students.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    students.add(ds.getValue(Student.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void btnClicked(View view) {
        Intent intent = new Intent(AdminStudentsActivity.this, AddStudentActivity.class);
        intent.putExtra("org", org);
        intent.putExtra("org_id", org_id);
        startActivity(intent);
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
        dbref.child(org_id).child("Students").removeEventListener(listener);
        super.onDestroy();
    }
}