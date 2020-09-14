package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.SubjectAdapter;
import com.johri.myattendance.models.Subject;

import java.util.ArrayList;

public class AdminSubjectsActivity extends AppCompatActivity {

    Toolbar tb;
    Button btnAddSubs;
    RecyclerView rv;
    DatabaseReference dbref;
    String org_id;
    ValueEventListener listener;
    ArrayList<Subject> subjects;
    SubjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_subjects);

        tb = findViewById(R.id.tbAdminSub);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnAddSubs = findViewById(R.id.btnAddSubs);
        dbref = FirebaseDatabase.getInstance().getReference();
        org_id = getIntent().getStringExtra("org_id");

        subjects = new ArrayList<>();
        rv = findViewById(R.id.rv_adminSub);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter(this, subjects, org_id);
        rv.setAdapter(adapter);

        update_subjects();
    }

    private void update_subjects() {

        listener = dbref.child(org_id).child("Subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()==null){
                    Toast.makeText(AdminSubjectsActivity.this, "No subjects found", Toast.LENGTH_SHORT).show();
                    return;
                }
                subjects.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Subject s = ds.getValue(Subject.class);
                    subjects.add(s);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void btnClicked(View view) {
        Intent intent = new Intent(AdminSubjectsActivity.this, AddSubjectActivity.class);
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
        dbref.child(org_id).child("Subjects").removeEventListener(listener);
        super.onDestroy();
    }
}