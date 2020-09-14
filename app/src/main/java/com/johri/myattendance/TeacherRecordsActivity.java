package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.Adapters.TeacherRecordAdapter;

import java.util.ArrayList;

public class TeacherRecordsActivity extends AppCompatActivity {

    Toolbar tb;
    RecyclerView rv;
    ArrayList<String> dates;
    TeacherRecordAdapter adapter;
    DatabaseReference ref;
    TextView tvRecordDetails;
    String org_id, batch, sub_id, sub_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_records);

        tb = findViewById(R.id.tbClsRecords);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        batch = getIntent().getStringExtra("batch");
        sub_id = getIntent().getStringExtra("sub_id");
        sub_name = getIntent().getStringExtra("sub_name");

        tvRecordDetails = findViewById(R.id.tvClsRecordDetails);
        String details = sub_id+" "+sub_name+"\n"+batch;
        tvRecordDetails.setText(details);

        rv = findViewById(R.id.rv_cls_record);
        rv.setLayoutManager(new LinearLayoutManager(this));
        dates = new ArrayList<>();
        adapter = new TeacherRecordAdapter(this, dates, batch, sub_id, org_id);
        rv.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference();
        updateDates();
    }

    private void updateDates() {

        ref.child(org_id).child("Attendance").child(batch).child(sub_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){
                    dates.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void btnClicked(View view) {

        if(view.getId()==R.id.btnDelAllTeacherRecord){
            showDialog();
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(TeacherRecordsActivity.this)
                .setTitle("Delete All?")
                .setMessage("All records will be deleted")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllRecords();
                    }
                })
                .setCancelable(true)
                .create().show();
    }

    private void deleteAllRecords() {

        ref.child(org_id).child("Attendance").child(batch).child(sub_id).setValue(null);
        dates.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}