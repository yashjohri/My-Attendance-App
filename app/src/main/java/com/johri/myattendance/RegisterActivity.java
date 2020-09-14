package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.models.Admin;
import com.johri.myattendance.models.OrgDetail;
import com.johri.myattendance.models.Student;
import com.johri.myattendance.models.Teacher;
import com.johri.myattendance.models.User;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegName, etRegOccupation, etRegOrg, etRegOrgId, etRegEmail, etRegPassword;
    EditText etTSID, etTSOrgID, etTSPassword;
    LinearLayout ll_admin, ll_ts;
    FirebaseAuth auth;
    DatabaseReference dbref;
    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tb = findViewById(R.id.tbRegister);
        setSupportActionBar(tb);

        etRegName = findViewById(R.id.etRegisterName);
        etRegOccupation = findViewById(R.id.etRegisterOccupation);
        etRegOrg = findViewById(R.id.etRegisterOrg);
        etRegOrgId = findViewById(R.id.etRegisterOrgId);
        etRegEmail = findViewById(R.id.etRegisterEmail);
        etRegPassword = findViewById(R.id.etRegisterPassword);

        etTSID = findViewById(R.id.etRegisterTSID);
        etTSOrgID = findViewById(R.id.etRegisterTSOrgID);
        etTSPassword = findViewById(R.id.etRegisterTSPassword);

        ll_admin = findViewById(R.id.ll_Admin);
        ll_ts = findViewById(R.id.ll_TS);

        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
    }

    public void btnClicked(View view) {

        if(view.getId()==R.id.btnRegister){
            checkOrgId();
        }
        else if(view.getId()==R.id.btnRegisterTS){
            checkTS();
        }
    }

    private void checkTS() {
        String id = etTSID.getText().toString();
        String org_id = etTSOrgID.getText().toString();
        String password = etTSPassword.getText().toString();

        if(id.trim().equals("") || org_id.trim().equals("") || password.trim().equals("")){
            Toast.makeText(RegisterActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        checkTeacher(id, org_id, password);
    }

    private void checkTeacher(final String id, final String org_id, final String password) {

        dbref.child(org_id).child("Teachers").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Teacher teacher = snapshot.getValue(Teacher.class);
                if(teacher!=null){
                    final User myuser = new User(null, teacher, null);

                    auth.createUserWithEmailAndPassword(teacher.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                dbref.child("Users").child(user.getUid()).setValue(myuser);

                                Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, TeacherNavActivity.class);
                                intent.putExtra("uid", user.getUid());
                                intent.putExtra("org_id", myuser.getTeacher().getOrg_id());
                                intent.putExtra("teacher_id", myuser.getTeacher().getId());
                                startActivity(intent);
                                finishAffinity();
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    checkStudent(id, org_id, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkStudent(String id, String org_id, final String password) {

        dbref.child(org_id).child("Students").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);

                if(student!=null){
                    final User myuser = new User(null, null, student);

                    auth.createUserWithEmailAndPassword(student.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                dbref.child("Users").child(user.getUid()).setValue(myuser);

                                Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, StudentNavActivity.class);
                                intent.putExtra("uid", user.getUid());
                                intent.putExtra("org_id", myuser.getStudent().getOrg_id());
                                intent.putExtra("student_id", myuser.getStudent().getRoll_num());
                                startActivity(intent);
                                finishAffinity();
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "User details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkOrgId() {
        final String org_id = etRegOrgId.getText().toString();
        if(org_id.trim().equals("")){
            Toast.makeText(RegisterActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        dbref.child("IDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(org_id)){
                        Toast.makeText(RegisterActivity.this, "Enter different ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                registerAdmin();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void registerAdmin() {
        String name = etRegName.getText().toString();
        final String occupation = etRegOccupation.getText().toString();
        final String org = etRegOrg.getText().toString();
        final String org_id = etRegOrgId.getText().toString();
        final String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();

        if(name.trim().equals("") || occupation.trim().equals("") || org.trim().equals("") || org_id.trim().equals("") || email.trim().equals("") || password.trim().equals("")){
            Toast.makeText(RegisterActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        final Admin admin = new Admin(name, email, occupation, org, org_id);
        final User newuser = new User(admin, null, null);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    dbref.child("Users").child(user.getUid()).setValue(newuser);
                    dbref.child("IDs").child(org_id).setValue(org);
                    dbref.child(org_id).child("Admin").setValue(admin);

                    Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, AdminNavActivity.class);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("org_id", org_id);
                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void rbtnClicked(View view) {
        boolean isChecked = ((RadioButton)view).isChecked();

        if(isChecked && view.getId()==R.id.rbtnAdmin){
            ll_admin.setVisibility(View.VISIBLE);
            ll_ts.setVisibility(View.GONE);
        }
        else if(isChecked && view.getId()==R.id.rbtnTS){
            ll_admin.setVisibility(View.GONE);
            ll_ts.setVisibility(View.VISIBLE);
        }
    }
}