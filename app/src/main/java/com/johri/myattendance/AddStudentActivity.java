package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.models.Attendance;
import com.johri.myattendance.models.Student;

import java.util.ArrayList;

public class AddStudentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar tb;
    EditText etName, etEmail, etRollNo, etNumber;
    Spinner sp_branch, sp_sem, sp_batch;
    ArrayList<String> sem_list, branch_list, batch_list;
    ArrayAdapter<String> sp_sem_adapter, sp_branch_adapter, sp_batch_adapter;
    String sem_selected, branch_selected, batch_selected;
    DatabaseReference dbref;
    String org, org_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        tb = findViewById(R.id.tbAdminAddStudent);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etAdminStudentName);
        etRollNo = findViewById(R.id.etAdminStudentRollNo);
        etEmail = findViewById(R.id.etAdminStudentEmail);
        etNumber = findViewById(R.id.etAdminStudentNumber);
        sp_branch = findViewById(R.id.sp_adminStudent_branch);
        sp_sem = findViewById(R.id.sp_adminStudent_sem);
        sp_batch = findViewById(R.id.sp_adminStudent_batch);

        dbref = FirebaseDatabase.getInstance().getReference();
        org = getIntent().getStringExtra("org");
        org_id = getIntent().getStringExtra("org_id");

        setup_sem_adapter();
        setup_batch_adapter();
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

    private void setup_batch_adapter(){
        batch_list = new ArrayList<>();
        sp_batch_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, batch_list);
        sp_batch_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_batch.setAdapter(sp_batch_adapter);
        sp_batch.setOnItemSelectedListener(this);
    }

    private void setup_branch_adapter(){
        sp_branch_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, branch_list);
        sp_branch_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_branch.setAdapter(sp_branch_adapter);
        sp_branch.setOnItemSelectedListener(this);
    }

    private void update_batches(){

        final String prefix = branch_selected+" "+sem_selected;

        dbref.child(org_id).child("Batches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                batch_list.clear();
                for(DataSnapshot ds:snapshot.getChildren()){

                    if(ds.getKey().startsWith(prefix)){
                        batch_list.add(ds.getKey());
                    }
                }
                sp_batch_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        final String rollno = etRollNo.getText().toString();
        final String number = etNumber.getText().toString();

        if(batch_selected==null || name.equals("") || email.equals("") || rollno.equals("")|| number.equals("")){
            Toast.makeText(AddStudentActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        dbref.child(org_id).child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(rollno)){
                        Toast.makeText(AddStudentActivity.this, "Student exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Student student = new Student(name, rollno, email, number, branch_selected, sem_selected, batch_selected, org, org_id);
                //Attendance attendance = new Attendance(name, rollno, null);

                dbref.child(org_id).child("Students").child(rollno).setValue(student);
                //dbref.child(org_id).child("Attendance").child(batch_selected).child(rollno).setValue(attendance);

                Toast.makeText(AddStudentActivity.this, "Student added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.sp_adminStudent_sem){
            sem_selected = sem_list.get(position);
            update_batches();
        }
        else if(parent.getId()==R.id.sp_adminStudent_branch){
            branch_selected = branch_list.get(position);
            update_batches();
        }
        else {
            if(batch_list.size()!=0){
                batch_selected = batch_list.get(position);
            }
            else {
                batch_selected = null;
            }
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