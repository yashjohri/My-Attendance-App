package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.johri.myattendance.models.User;
import com.squareup.picasso.Picasso;

public class AdminNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar tbAdminNav;
    TextView tvAdminDetails;
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
    String uid, org_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nav);

        tbAdminNav = findViewById(R.id.tbAdminNav);
        setSupportActionBar(tbAdminNav);

        tvAdminDetails = findViewById(R.id.tvAdminDetails);
        iv_pic = findViewById(R.id.btnAdminImage);

        dl = findViewById(R.id.adminDL);
        nv = findViewById(R.id.adminNV);
        setupNavigation();

        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
        sref = FirebaseStorage.getInstance().getReference();

        uid = getIntent().getStringExtra("uid");
        org_id = getIntent().getStringExtra("org_id");
        setupPic();
        updateAdminDetails(uid);
    }

    private void setupPic() {

        sref.child("Profile Pics").child(org_id).child("admin/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Toast.makeText(AdminNavActivity.this, org_id, Toast.LENGTH_SHORT).show();
                Picasso.get().load(uri).into(iv_pic);
            }
        });
    }

    private void setupNavigation() {
        //nv.bringToFront();
        toggle = new ActionBarDrawerToggle(this, dl, tbAdminNav, R.string.app_name, R.string.app_name);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        nv.setNavigationItemSelectedListener(this);
    }

    private void updateAdminDetails(String uid) {

        valueEventListener = dbref.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                String info = user.getAdmin().getEmail() + "\n\n" + user.getAdmin().getName() + "\n\n" + user.getAdmin().getOccupation() + "\n\n"
                            + user.getAdmin().getOrg() + " ( " + user.getAdmin().getOrg_id() + " )";
                tvAdminDetails.setText(info);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.adminBranch:
                intent = new Intent(AdminNavActivity.this, BranchActivity.class);
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                startActivity(intent);
                break;

            case R.id.adminSubjects:
                intent = new Intent(AdminNavActivity.this, AdminSubjectsActivity.class);
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                startActivity(intent);
                break;

            case R.id.adminBatch:
                intent = new Intent(AdminNavActivity.this, AdminBatchActivity.class);
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                startActivity(intent);
                break;

            case R.id.adminTeachers:
                intent = new Intent(AdminNavActivity.this, AdminTeachersActivity.class);
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                intent.putExtra("org", user.getAdmin().getOrg());
                startActivity(intent);
                break;

            case R.id.adminStudents:
                intent = new Intent(AdminNavActivity.this, AdminStudentsActivity.class);
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                intent.putExtra("org", user.getAdmin().getOrg());
                startActivity(intent);
                break;

            case R.id.adminAssign:
                intent  = new Intent(AdminNavActivity.this, AdminAssignActivity.class);
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                intent.putExtra("org", user.getAdmin().getOrg());
                startActivity(intent);
                break;

            case R.id.adminViewAttendance:
                intent = new Intent(AdminNavActivity.this, AdminViewActivity.class);
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                startActivity(intent);
                break;

            case R.id.adminSettings:
                intent = new Intent(AdminNavActivity.this, EditAdminActivity.class);
                intent.putExtra("name", user.getAdmin().getName());
                intent.putExtra("email", user.getAdmin().getEmail());
                intent.putExtra("occupation", user.getAdmin().getOccupation());
                intent.putExtra("org", user.getAdmin().getOrg());
                intent.putExtra("org_id", user.getAdmin().getOrg_id());
                startActivityForResult(intent, 1);
                break;

            case R.id.adminLogout:
                dbref.child("Users").child(auth.getCurrentUser().getUid()).removeEventListener(valueEventListener);
                auth.signOut();
                Toast.makeText(AdminNavActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                intent = new Intent(AdminNavActivity.this, LoginActivity.class);
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
    public void onBackPressed() {
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}