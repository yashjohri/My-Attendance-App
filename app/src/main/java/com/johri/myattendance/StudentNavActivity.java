package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.johri.myattendance.models.Admin;
import com.johri.myattendance.models.Student;
import com.johri.myattendance.models.Teacher;
import com.johri.myattendance.models.User;
import com.squareup.picasso.Picasso;

public class StudentNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar tb;
    TextView tvStudentDetails;
    ImageView iv_pic;
    DrawerLayout dl;
    NavigationView nv;
    ActionBarDrawerToggle toggle;

    FirebaseAuth auth;
    DatabaseReference dbref;
    StorageReference sref;
    User user;
    Intent intent;

    ValueEventListener valueEventListener;
    String uid, org_id, student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_nav);

        tb = findViewById(R.id.tbStudentNav);
        iv_pic = findViewById(R.id.btnStudentImage);
        setSupportActionBar(tb);

        dl = findViewById(R.id.StudentDL);
        nv = findViewById(R.id.StudentNV);
        setupNavigation();

        tvStudentDetails = findViewById(R.id.tvStudentNavDetails);
        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
        sref = FirebaseStorage.getInstance().getReference();

        uid = getIntent().getStringExtra("uid");
        org_id = getIntent().getStringExtra("org_id");
        student_id = getIntent().getStringExtra("student_id");

        setupPic();
        updateStudentDetails(uid);
    }

    private void setupPic() {
        sref.child("Profile Pics/"+org_id+"/Students/"+student_id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_pic);
            }
        });
    }

    private void updateStudentDetails(String uid) {

        valueEventListener = dbref.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if(user==null){
                    showDialog();
                    return;
                }

                String info = user.getStudent().getEmail() + "\n\n" +
                        user.getStudent().getName() + "\n\n" +
                        user.getStudent().getRoll_num() + "\n\n" +
                        user.getStudent().getNumber() + "\n\n" +
                        user.getStudent().getBatch() + "\n\n" +
                        user.getStudent().getOrg() + " ( " + user.getStudent().getOrg_id() + " )";

                tvStudentDetails.setText(info);

                checkStudentExists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkStudentExists() {

        dbref.child(org_id).child("Students").child(student_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                if(!isValid(student)){
                    showDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isValid(Student student) {

        Student curr_student = user.getStudent();

        if(student==null){
            return false;
        }
        if(!curr_student.getName().equals(student.getName())){
            return false;
        }
        if(!curr_student.getRoll_num().equals(student.getRoll_num())){
            return false;
        }
        if(!curr_student.getEmail().equals(student.getEmail())){
            return false;
        }
        if(!curr_student.getNumber().equals(student.getNumber())){
            return false;
        }
        if(!curr_student.getBranch().equals(student.getBranch())){
            return false;
        }
        if(!curr_student.getSem().equals(student.getSem())){
            return false;
        }
        if(!curr_student.getBatch().equals(student.getBatch())){
            return false;
        }
        if(!curr_student.getOrg().equals(student.getOrg())){
            return false;
        }
        if(!curr_student.getOrg_id().equals(student.getOrg_id())){
            return false;
        }

        return true;
    }

    private void showDialog() {
        new AlertDialog.Builder(StudentNavActivity.this)
                .setTitle("Deleting Account")
                .setMessage("Your account is not found")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.getCurrentUser().delete();
                        sref.child("Profile Pics/"+org_id+"/Students/"+student_id).delete();
                        dbref.child("Users").child(uid).setValue(null);
                        intent = new Intent(StudentNavActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        finish();
                    }
                })
                .setCancelable(false)
                .create().show();
    }



    private void setupNavigation(){
        toggle = new ActionBarDrawerToggle(this, dl, tb, R.string.app_name, R.string.app_name);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        nv.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.StudentSubjects:
                intent = new Intent(StudentNavActivity.this, StudentSubjectsActivity.class);
                intent.putExtra("org_id", org_id);
                intent.putExtra("student_id", student_id);
                intent.putExtra("batch_name", user.getStudent().getBatch());
                startActivity(intent);
                break;

            case R.id.StudentSettings:
                intent = new Intent(StudentNavActivity.this, EditStudentActivity.class);
                intent.putExtra("email", user.getStudent().getEmail());
                intent.putExtra("org_id", user.getStudent().getOrg_id());
                intent.putExtra("id", user.getStudent().getRoll_num());
                intent.putExtra("uid", auth.getCurrentUser().getUid());
                startActivityForResult(intent, 1);
                break;

            case R.id.StudentLogout:
                dbref.child("Users").child(auth.getCurrentUser().getUid()).removeEventListener(valueEventListener);
                auth.signOut();
                Toast.makeText(StudentNavActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                intent = new Intent(StudentNavActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
                finish();
                break;
        }

        dl.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK){
            if(data.getIntExtra("update_pic", 0)==1){
                setupPic();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(valueEventListener!=null){
            dbref.child("Users").child(uid).removeEventListener(valueEventListener);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    public void contactAdmin(View view) {
        dbref.child(org_id).child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Admin admin = snapshot.getValue(Admin.class);
                showAdminDialog(admin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAdminDialog(Admin admin) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.student_dialog)
                .create();
        dialog.show();

        final ImageView iv_image = dialog.findViewById(R.id.ivDialogImage);
        TextView tvDetails = dialog.findViewById(R.id.tvDialogDetails);
        TextView tvEmail = dialog.findViewById(R.id.tvDialogEmail);
        TextView tvNumber = dialog.findViewById(R.id.tvDialogNumber);

        FirebaseStorage.getInstance().getReference().child("Profile Pics").child(org_id).child("admin/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_image);
            }
        });
        tvDetails.setText(admin.getName()+"\n\n"+admin.getOccupation());
        tvEmail.setText(admin.getEmail());
        tvNumber.setVisibility(View.GONE);
    }
}