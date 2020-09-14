package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.models.User;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    DatabaseReference dbref;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            gotoNavActivity(user);
        }
        else{
            handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },2000);
        }
    }

    private void gotoNavActivity(final FirebaseUser user) {

        dbref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User myuser = snapshot.getValue(User.class);
                if(myuser==null){
                    Intent intent=  new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
                else if(myuser.getAdmin()!=null){
                    Intent intent = new Intent(MainActivity.this, AdminNavActivity.class);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("org_id", myuser.getAdmin().getOrg_id());
                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
                else if(myuser.getTeacher()!=null){
                    Intent intent = new Intent(MainActivity.this, TeacherNavActivity.class);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("org_id", myuser.getTeacher().getOrg_id());
                    intent.putExtra("teacher_id", myuser.getTeacher().getId());
                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
                else if(myuser.getStudent()!=null){
                    Intent intent = new Intent(MainActivity.this, StudentNavActivity.class);
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
}