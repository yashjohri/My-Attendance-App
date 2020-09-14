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
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.BatchAdapter;
import com.johri.myattendance.models.Batch;

import java.util.ArrayList;

public class AdminBatchActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    DatabaseReference dbref;
    String org_id;
    ValueEventListener listener;
    ArrayList<Batch> batches;
    BatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_batch);

        tb = findViewById(R.id.tbAdminBatch);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rv = findViewById(R.id.rv_adminBatch);
        dbref = FirebaseDatabase.getInstance().getReference();
        org_id = getIntent().getStringExtra("org_id");

        batches = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BatchAdapter(this, batches, org_id, false);
        rv.setAdapter(adapter);

        update_batch_list();
    }

    private void update_batch_list() {

        listener = dbref.child(org_id).child("Batches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()==null){
                    Toast.makeText(AdminBatchActivity.this, "No batch found", Toast.LENGTH_SHORT).show();
                    return;
                }

                batches.clear();
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

    public void btnClicked(View view) {
        Intent intent = new Intent(AdminBatchActivity.this, AddBatchActivity.class);
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
        dbref.child(org_id).child("Batches").removeEventListener(listener);
        super.onDestroy();
    }
}