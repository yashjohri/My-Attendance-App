package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.models.Teacher;
import com.johri.myattendance.models.TeacherSubject;
import com.johri.myattendance.models.User;

public class AddTeacherActivity extends AppCompatActivity {

    Toolbar tb;
    EditText etName, etId, etEmail, etOccupation, etNumber;
    DatabaseReference dbref;
    FirebaseAuth auth;
    String org, org_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        tb = findViewById(R.id.tbAdminAddTeacher);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.etAdminTeacherName);
        etId = findViewById(R.id.etAdminTeacherId);
        etEmail = findViewById(R.id.etAdminTeacherEmail);
        etOccupation = findViewById(R.id.etAdminTeacherOcc);
        etNumber = findViewById(R.id.etAdminTeacherNumber);

        dbref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        org = getIntent().getStringExtra("org");
        org_id = getIntent().getStringExtra("org_id");
    }

    public void btnClicked(View view) {

        final String name = etName.getText().toString();
        final String id = etId.getText().toString();
        final String mail = etEmail.getText().toString();
        final String occ = etOccupation.getText().toString();
        final String number = etNumber.getText().toString();

        if(name.equals("") || id.equals("") || mail.equals("") || occ.equals("") || number.equals("")){
            Toast.makeText(AddTeacherActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        dbref.child(org_id).child("Teachers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(id)){
                        Toast.makeText(AddTeacherActivity.this, "Teacher exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Teacher teacher = new Teacher(name, id, mail, number, occ, org, org_id);
                dbref.child(org_id).child("Teachers").child(id).setValue(teacher);

                TeacherSubject teacherSubject = new TeacherSubject(name, id, null);
                dbref.child(org_id).child("Assign").child(id).setValue(teacherSubject);

                Toast.makeText(AddTeacherActivity.this, "Teacher added", Toast.LENGTH_SHORT).show();
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