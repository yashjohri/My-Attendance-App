package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.johri.myattendance.models.User;

public class LoginActivity extends AppCompatActivity {

    Toolbar tbLogin;
    EditText etLoginEmail;
    EditText etLoginPassword;
    FirebaseAuth auth;
    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tbLogin = findViewById(R.id.tbLogin);
        setSupportActionBar(tbLogin);

        etLoginEmail = findViewById(R.id.etLoginUserName);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            gotoNavActivity(user);
        }
    }

    private void gotoNavActivity(final FirebaseUser user) {
        dbref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User myuser = snapshot.getValue(User.class);
                if(myuser==null){
                    return;
                }
                if(myuser.getAdmin()!=null){
                    Intent intent = new Intent(LoginActivity.this, AdminNavActivity.class);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("org_id", myuser.getAdmin().getOrg_id());
                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
                else if(myuser.getTeacher()!=null){
                    Intent intent = new Intent(LoginActivity.this, TeacherNavActivity.class);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("org_id", myuser.getTeacher().getOrg_id());
                    intent.putExtra("teacher_id", myuser.getTeacher().getId());
                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
                else if(myuser.getStudent()!=null){
                    Intent intent = new Intent(LoginActivity.this, StudentNavActivity.class);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("org_id", myuser.getStudent().getOrg_id());
                    intent.putExtra("student_id", myuser.getStudent().getRoll_num());
                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void btnClicked(View view) {

        if(view.getId()==R.id.btnLogin){
            loginUser();
        }
        else if(view.getId()==R.id.btnLoginCreate){
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void loginUser() {
        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if(email.trim().equals("") || password.trim().equals("")){
            Toast.makeText(LoginActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();

                    gotoNavActivity(auth.getCurrentUser());
                }
                else{
                    Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}