package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.johri.myattendance.models.Admin;
import com.johri.myattendance.models.User;
import com.squareup.picasso.Picasso;

import java.nio.IntBuffer;

public class EditAdminActivity extends AppCompatActivity {

    Toolbar tb;
    EditText et_name, et_occupation, et_org, et_email, et_pass, etOrgID;
    ImageView iv_pic;
    Intent intent;
    User user;
    DatabaseReference dbref;
    FirebaseAuth auth;
    StorageReference stref;
    boolean emaildone, passdone;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin);

        tb = findViewById(R.id.tbSettings);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbref = FirebaseDatabase.getInstance().getReference();
        stref = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        intent = getIntent();
        user = new User(new Admin(
                intent.getStringExtra("name"),
                intent.getStringExtra("email"),
                intent.getStringExtra("occupation"),
                intent.getStringExtra("org"),
                intent.getStringExtra("org_id")
        ), null, null);

        et_name = findViewById(R.id.etName);
        et_occupation = findViewById(R.id.etOccupation);
        et_org = findViewById(R.id.etOrg);
        et_email = findViewById(R.id.etEmail);
        et_pass = findViewById(R.id.etPassword);
        etOrgID = findViewById(R.id.etOrgID);
        iv_pic = findViewById(R.id.btnChooseImage);

        et_name.setText(user.getAdmin().getName());
        et_org.setText(user.getAdmin().getOrg());
        et_occupation.setText(user.getAdmin().getOccupation());
        et_email.setText(user.getAdmin().getEmail());
        setProfilePic();
    }

    private void setProfilePic() {
        stref.child("Profile Pics/"+user.getAdmin().getOrg_id()+"/admin").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_pic);
            }
        });
    }

    public void btnClicked(View view) {

        if(view.getId()==R.id.btnResetID){
            resetID();
            return;
        }
        if(image_uri!=null){
            uploadPicture();
        }

        String name = et_name.getText().toString();
        String occupation = et_occupation.getText().toString();
        String email = et_email.getText().toString();
        String pass = et_pass.getText().toString();

        if(name.equals(user.getAdmin().getName())
                && email.equals(user.getAdmin().getEmail()) && occupation.equals(user.getAdmin().getOccupation()) && pass.trim().equals("")){
            return;
        }
        if(name.equals("") || occupation.equals("") || email.equals("") || pass.equals("")){
            Toast.makeText(EditAdminActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        user.getAdmin().setEmail(email);
        user.getAdmin().setName(name);
        user.getAdmin().setOccupation(occupation);

        dbref.child("Users").child(auth.getCurrentUser().getUid()).setValue(user);
        dbref.child(user.getAdmin().getOrg_id()).child("Admin").setValue(user.getAdmin());

        emaildone = false;
        passdone = false;

        auth.getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(EditAdminActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                    //emaildone = true;
                }
            }
        });

        auth.getCurrentUser().updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(EditAdminActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                    //passdone = true;
                }
            }
        });

        Toast.makeText(EditAdminActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
    }

    private void resetID() {
        final String org_id = etOrgID.getText().toString();
        final String org = et_org.getText().toString();

        if(org_id.equals("") || org.equals("") || org_id.equals(user.getAdmin().getOrg_id())){
            Toast.makeText(EditAdminActivity.this, "Enter new name and ID", Toast.LENGTH_SHORT).show();
            return;
        }

        dbref.child("IDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.getKey().equals(org_id)){
                        Toast.makeText(EditAdminActivity.this, "Enter different ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                showDialog(org_id, org);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog(final String org_id, final String org) {

        new AlertDialog.Builder(EditAdminActivity.this)
                .setTitle("Reset ID")
                .setMessage("This will delete all data associated with college")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbref.child(user.getAdmin().getOrg_id()).setValue(null);
                        dbref.child("IDs").child(user.getAdmin().getOrg_id()).setValue(null);

                        user.getAdmin().setOrg_id(org_id);
                        user.getAdmin().setOrg(org);

                        dbref.child("Users").child(auth.getCurrentUser().getUid()).setValue(user);
                        dbref.child("IDs").child(org_id).setValue(user.getAdmin().getOrg());
                        dbref.child(org_id).child("Admin").setValue(user.getAdmin());
                    }
                })
                .create().show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openChooser(View view) {
        Intent image_intent = new Intent();
        image_intent.setType("image/*");
        image_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(image_intent, 1);
    }

    private void uploadPicture(){
        stref.child("Profile Pics").child(user.getAdmin().getOrg_id()).child("admin/").putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditAdminActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("update_pic", 1);
                    setResult(RESULT_OK, intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            image_uri = data.getData();
            iv_pic.setImageURI(image_uri);
        }
        else{
            image_uri = null;
        }
    }
}