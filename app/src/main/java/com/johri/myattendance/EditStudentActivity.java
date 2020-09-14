package com.johri.myattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditStudentActivity extends AppCompatActivity {

    Toolbar tb;
    EditText etEmail, etPass;
    ImageView iv_pic;
    String org_id, id, uid, email;
    StorageReference stref;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        tb = findViewById(R.id.tbStudentSettings);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etEmail = findViewById(R.id.etStudentEmail);
        etPass = findViewById(R.id.etStudentPass);
        iv_pic = findViewById(R.id.btnChooseImageStudent);

        org_id = getIntent().getStringExtra("org_id");
        uid = getIntent().getStringExtra("uid");
        id = getIntent().getStringExtra("id");
        email = getIntent().getStringExtra("email");

        etEmail.setText(email);

        stref = FirebaseStorage.getInstance().getReference();
        setProfilePic();
    }

    private void setProfilePic() {
        stref.child("Profile Pics/"+org_id+"/Students/"+id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_pic);
            }
        });
    }

    public void openChooser(View view) {
        Intent image_intent = new Intent();
        image_intent.setType("image/*");
        image_intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(image_intent, 1);
    }

    private void uploadPicture(){
        stref.child("Profile Pics/"+org_id+"/Students/"+id).putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditStudentActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
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

    public void btnClicked(View view) {

        if(image_uri!=null){
            uploadPicture();
        }

        String new_email = etEmail.getText().toString();
        String new_pass = etPass.getText().toString();

        if(new_email.trim().equals("") && new_pass.trim().equals("")){
            Toast.makeText(EditStudentActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!new_email.equals(email)){

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(new_email);
            ref.child(org_id).child("Students").child(id).child("email").setValue(new_email);

            FirebaseAuth.getInstance().getCurrentUser().updateEmail(new_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditStudentActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(!new_pass.trim().equals("")){
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(new_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditStudentActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}