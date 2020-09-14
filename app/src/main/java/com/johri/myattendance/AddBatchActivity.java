package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.models.Batch;

import java.util.ArrayList;

public class AddBatchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar tb;
    EditText etBatchName;
    Button btnAddBatch;
    Spinner sp_branch, sp_sem;
    DatabaseReference dbref;
    String org_id;
    ArrayList<String> sem_list, branch_list;
    ArrayAdapter<String> sp_sem_adapter, sp_branch_adapter;
    String sem_selected, branch_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_batch);

        tb = findViewById(R.id.tbAdminAddBatch);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etBatchName = findViewById(R.id.etAdminBatchName);
        sp_branch = findViewById(R.id.sp_adminBatch_branch);
        sp_sem = findViewById(R.id.sp_adminBatch_sem);

        dbref = FirebaseDatabase.getInstance().getReference();
        org_id = getIntent().getStringExtra("org_id");

        setup_sem_adapter();
        update_branches();
    }

    private void setup_sem_adapter() {
        sem_list = new ArrayList<>();
        for(int i=1; i<=8; i++){
            sem_list.add("Sem "+i);
        }
        sp_sem_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, sem_list);
        sp_sem_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_sem.setAdapter(sp_sem_adapter);
        sp_sem.setOnItemSelectedListener(this);
    }

    private void setup_branch_adapter(){
        sp_branch_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, branch_list);
        sp_branch_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_branch.setAdapter(sp_branch_adapter);
        sp_branch.setOnItemSelectedListener(this);
    }

    private void update_branches(){
        branch_list = new ArrayList<>();

        dbref.child(org_id).child("Branches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    branch_list.add(ds.getKey());
                }

                setup_branch_adapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void btnClicked(View view) {

        final String batchname = etBatchName.getText().toString();

        if(batchname.equals("")){
            Toast.makeText(AddBatchActivity.this, "Enter data", Toast.LENGTH_SHORT).show();
            return;
        }

        final String path = branch_selected+" "+sem_selected+" "+batchname;

        dbref.child(org_id).child("Batches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(path)){
                        Toast.makeText(AddBatchActivity.this, "Batch already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Batch batch = new Batch(batchname, branch_selected, sem_selected, org_id);
                dbref.child(org_id).child("Batches").child(path).setValue(batch);
                Toast.makeText(AddBatchActivity.this, "Batch added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.sp_adminBatch_sem){
            sem_selected = sem_list.get(position);
        }
        else {
            branch_selected = branch_list.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}