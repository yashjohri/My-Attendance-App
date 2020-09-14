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
import com.johri.myattendance.models.Teacher;
import com.johri.myattendance.models.User;
import com.squareup.picasso.Picasso;

public class TeacherNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar tbTeacherNav;
    TextView tvTeacherDetails, tvcontactAdmin;
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
    String uid, org_id, teacher_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_nav);

        tbTeacherNav = findViewById(R.id.tbTeacherNav);
        setSupportActionBar(tbTeacherNav);

        tvTeacherDetails = findViewById(R.id.tvTeacherNavDetails);
        tvcontactAdmin = findViewById(R.id.tvTeacherNavContactAdmin);
        iv_pic = findViewById(R.id.btnTeacherImage);

        dl = findViewById(R.id.teacherDL);
        nv = findViewById(R.id.teacherNV);
        setupNavigation();

        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
        sref = FirebaseStorage.getInstance().getReference();

        uid = getIntent().getStringExtra("uid");
        org_id = getIntent().getStringExtra("org_id");
        teacher_id = getIntent().getStringExtra("teacher_id");

        setupPic();
        updateTeacherDetails(uid);
    }

    private void setupPic() {
        sref.child("Profile Pics/"+org_id+"/Teachers/"+teacher_id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_pic);
            }
        });
    }

    private void checkTeacherExists() {

        dbref.child(org_id).child("Teachers").child(teacher_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Teacher teacher = snapshot.getValue(Teacher.class);
                if(!isValid(teacher)){
                    showDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isValid(Teacher teacher) {

        Teacher curr_teacher = user.getTeacher();

        if(teacher==null){
            return false;
        }
        if(!curr_teacher.getEmail().equals(teacher.getEmail())){
            return false;
        }
        if(!curr_teacher.getOrg().equals(teacher.getOrg())){
            return false;
        }
        if(!curr_teacher.getOrg_id().equals(teacher.getOrg_id())){
            return false;
        }
        if(!curr_teacher.getOccupation().equals(teacher.getOccupation())){
            return false;
        }
        if(!curr_teacher.getNumber().equals(teacher.getNumber())){
            return false;
        }
        if(!curr_teacher.getName().equals(teacher.getName())){
            return false;
        }

        return true;
    }

    private void showDialog() {
        new AlertDialog.Builder(TeacherNavActivity.this)
                .setTitle("Deleting Account")
                .setMessage("Your account is not found")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.getCurrentUser().delete();
                        sref.child("Profile Pics/"+org_id+"/Teachers/"+teacher_id).delete();
                        dbref.child("Users").child(uid).setValue(null);
                        intent = new Intent(TeacherNavActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        finish();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void setupNavigation() {
        //nv.bringToFront();
        toggle = new ActionBarDrawerToggle(this, dl, tbTeacherNav, R.string.app_name, R.string.app_name);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        nv.setNavigationItemSelectedListener(this);
    }


    private void updateTeacherDetails(String uid){

        valueEventListener = dbref.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if(user==null){
                    showDialog();
                    return;
                }

                String info = user.getTeacher().getEmail() + "\n\n" + user.getTeacher().getName() + "\n\n" +
                              user.getTeacher().getId() + "\n\n" + user.getTeacher().getNumber() + "\n\n" +
                              user.getTeacher().getOccupation() + "\n\n" + user.getTeacher().getOrg() +
                              " ( " + user.getTeacher().getOrg_id() + " )";

                tvTeacherDetails.setText(info);

                checkTeacherExists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.teacherTakeAtt:
                intent = new Intent(TeacherNavActivity.this, TakeAttendanceActivity.class);
                intent.putExtra("org_id", user.getTeacher().getOrg_id());
                intent.putExtra("teacher_id", user.getTeacher().getId());
                startActivity(intent);
                break;

            case R.id.teacherViewAtt:
                intent = new Intent(TeacherNavActivity.this, TeacherViewActivity.class);
                intent.putExtra("org_id", user.getTeacher().getOrg_id());
                intent.putExtra("teacher_id", user.getTeacher().getId());
                startActivity(intent);
                break;

            case R.id.teacherRecords:
                intent = new Intent(TeacherNavActivity.this, TeacherViewActivity.class);
                intent.putExtra("showRecords", true);
                intent.putExtra("org_id", user.getTeacher().getOrg_id());
                intent.putExtra("teacher_id", user.getTeacher().getId());
                startActivity(intent);
                break;

            case R.id.teacherSettings:
                intent = new Intent(TeacherNavActivity.this, EditTeacherActivity.class);
                intent.putExtra("email", user.getTeacher().getEmail());
                intent.putExtra("org_id", user.getTeacher().getOrg_id());
                intent.putExtra("id", user.getTeacher().getId());
                intent.putExtra("uid", auth.getCurrentUser().getUid());
                startActivityForResult(intent, 1);
                break;

            case R.id.adminLogout:
                dbref.child("Users").child(auth.getCurrentUser().getUid()).removeEventListener(valueEventListener);
                auth.signOut();
                Toast.makeText(TeacherNavActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                intent = new Intent(TeacherNavActivity.this, LoginActivity.class);
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
        //tvNumber.setText(student.getNumber());
    }
}