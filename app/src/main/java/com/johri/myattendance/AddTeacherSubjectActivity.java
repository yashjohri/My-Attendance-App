package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.models.Batch;
import com.johri.myattendance.models.Subject;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTeacherSubjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar tb;
    TextView tvTeacherDetails;
    Spinner sp_sem, sp_subject, sp_batch;
    ArrayList<String> sem_list, sub_list, batch_list;
    ArrayAdapter<String> sp_sem_adapter, sp_sub_addapter, sp_batch_adapter;
    String org_id, teacher_id, teacher_name;
    DatabaseReference dbref;
    String sem_selected, sub_selected, batch_selected;
    HashMap<String, String> mapSubs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher_subject);

        tb = findViewById(R.id.tbAdminAddTeacherSubject);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        org_id = getIntent().getStringExtra("org_id");
        teacher_name = getIntent().getStringExtra("teacher_name");
        teacher_id = getIntent().getStringExtra("teacher_id");
        dbref = FirebaseDatabase.getInstance().getReference();

        tvTeacherDetails = findViewById(R.id.tvAssignTeacherDetails);
        tvTeacherDetails.setText(teacher_id+" - "+teacher_name);

        sp_sem = findViewById(R.id.sp_assign_sem);
        sp_subject = findViewById(R.id.sp_assign_subject);
        sp_batch = findViewById(R.id.sp_assign_batch);

        setup_sem_adapter();
        setup_sub_adapter();
        setup_batch_adapter();
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

    private void setup_sub_adapter(){
        sub_list = new ArrayList<>();
        sp_sub_addapter = new ArrayAdapter<>(this, R.layout.spinner_item, sub_list);
        sp_sub_addapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_subject.setAdapter(sp_sub_addapter);
        sp_subject.setOnItemSelectedListener(this);
    }

    private void setup_batch_adapter(){
        batch_list = new ArrayList<>();
        sp_batch_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, batch_list);
        sp_batch_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_batch.setAdapter(sp_batch_adapter);
        sp_batch.setOnItemSelectedListener(this);
    }

    private void updateBatches(){

        batch_list.clear();

        dbref.child(org_id).child("Batches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){

                    Batch batch = ds.getValue(Batch.class);

                    if(batch.getSem().equals(sem_selected)){
                        batch_list.add(batch.getBranch()+" "+batch.getSem()+" "+batch.getName());
                    }
                }
                sp_batch_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateSubjects(){

        sub_list.clear();
        mapSubs.clear();

        dbref.child(org_id).child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){

                    Subject subject = ds.getValue(Subject.class);

                    if(batch_selected!=null && subject.getSem().equals(sem_selected) && batch_selected.startsWith(subject.getBranch())){
                        String fullName = subject.getId()+" "+subject.getName();
                        sub_list.add(fullName);
                        mapSubs.put(fullName, subject.getId());
                    }
                }
                sp_sub_addapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void btnClicked(View view) {

        if(sem_selected==null || sub_selected==null || batch_selected==null){
            return;
        }
        checkSubjectAssigned();
    }

    private void checkSubjectAssigned() {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean assign = true;

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        TeacherSubject teacherSubject = ds.getValue(TeacherSubject.class);
                        HashMap<String, ArrayList<String>> teacher_subjects = teacherSubject.getTeacher_subjects();

                        if(teacher_subjects!=null){

                            ArrayList<String> batches = teacher_subjects.get(sub_selected);

                            if(batches!=null){

                                for(String batch_name:batches){
                                    if(batch_name.equals(batch_selected)){
                                        assign = false;
                                        Toast.makeText(AddTeacherSubjectActivity.this, "Subject already assigned", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        }
                        if(!assign){
                            break;
                        }
                    }
                }
                if(assign){
                    assignSubject();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void assignSubject() {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").child(teacher_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                TeacherSubject teacherSubject = snapshot.getValue(TeacherSubject.class);

                if(teacherSubject==null){
                    teacherSubject = new TeacherSubject();
                    teacherSubject.setTeacherID(teacher_id);
                    teacherSubject.setTeacherName(teacher_name);
                }

                HashMap<String, ArrayList<String>> teacher_subjects = teacherSubject.getTeacher_subjects();

                if(teacher_subjects==null){
                    teacher_subjects = new HashMap<>();
                }

                ArrayList<String> batches = teacher_subjects.get(sub_selected);

                if(batches==null){
                    batches = new ArrayList<>();
                }
                batches.add(batch_selected);
                teacher_subjects.put(sub_selected, batches);
                teacherSubject.setTeacher_subjects(teacher_subjects);

                FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign")
                        .child(teacher_id).setValue(teacherSubject);
                Toast.makeText(AddTeacherSubjectActivity.this, "Subject assigned", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.sp_assign_sem){
            sem_selected = sem_list.get(position);

            batch_selected = null;
            sub_selected = null;
            sub_list.clear();
            sp_sub_addapter.notifyDataSetChanged();

            updateBatches();
        }
        else if(parent.getId()==R.id.sp_assign_batch){

            if(batch_list.size()!=0) {
                batch_selected = batch_list.get(position);
                updateSubjects();
            }
            else {
                batch_selected = null;
            }
        }
        else {
            if(sub_list.size()!=0){
                sub_selected = mapSubs.get(sub_list.get(position));
            }
            else {
                sub_selected = null;
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