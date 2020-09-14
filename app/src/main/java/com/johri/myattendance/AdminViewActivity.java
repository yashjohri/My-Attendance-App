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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.BatchAdapter;
import com.johri.myattendance.models.Batch;
import com.johri.myattendance.models.Subject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminViewActivity extends AppCompatActivity {

    Toolbar tb;
    String org_id;
    DatabaseReference dbref;
    RecyclerView rv;
    ArrayList<Batch> batches;
    BatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);


        tb = findViewById(R.id.tbAdminView);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        dbref = FirebaseDatabase.getInstance().getReference();

        batches = new ArrayList<>();
        rv = findViewById(R.id.rv_batch_attendance);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BatchAdapter(this, batches, org_id, true);
        rv.setAdapter(adapter);

        updateBatches();
    }

    private void updateBatches() {
        dbref.child(org_id).child("Batches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){
                    batches.add(ds.getValue(Batch.class));
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