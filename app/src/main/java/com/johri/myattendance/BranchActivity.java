package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.BranchAdapter;

import java.util.ArrayList;

public class BranchActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    BranchAdapter adapter;
    EditText etBranchName;
    Button btnAddBranch;
    ArrayList<String> branch_list;
    String org_id;
    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

        tb = findViewById(R.id.tbBranches);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbref = FirebaseDatabase.getInstance().getReference();

        etBranchName = findViewById(R.id.etBranchName);
        btnAddBranch = findViewById(R.id.btnAddBranch);

        branch_list = new ArrayList<>();
        rv = findViewById(R.id.rv_branches);
        org_id = getIntent().getStringExtra("org_id");
        adapter = new BranchAdapter(org_id, this, branch_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        updateBranchList();
    }

    private void updateBranchList() {

        branch_list.clear();

        dbref.child(org_id).child("Branches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()==null){
                    Toast.makeText(BranchActivity.this, "No branch found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(DataSnapshot ds:snapshot.getChildren()){
                    branch_list.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void btnClicked(View view) {

        String branch = etBranchName.getText().toString();

        if(branch.equals("")){
            Toast.makeText(BranchActivity.this, "Enter branch", Toast.LENGTH_SHORT).show();
            return;
        }

        dbref.child(org_id).child("Branches").child(branch).setValue(1);
        updateBranchList();
        Toast.makeText(BranchActivity.this, "Branch added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}