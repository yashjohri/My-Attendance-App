package com.johri.myattendance.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.johri.myattendance.R;
import com.johri.myattendance.models.ClassAttendanceItem;
import com.johri.myattendance.models.Student;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ClassAttendanceAdapter extends RecyclerView.Adapter<ClassAttendanceAdapter.MyHolder> {

    private Context context;
    private ArrayList<ClassAttendanceItem> classAttendanceItems;
    private String org_id;

    public ClassAttendanceAdapter(Context context, ArrayList<ClassAttendanceItem> classAttendanceItems, String org_id) {
        this.context = context;
        this.classAttendanceItems = classAttendanceItems;
        this.org_id = org_id;
    }

    @NonNull
    @Override
    public ClassAttendanceAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cls_attendance_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAttendanceAdapter.MyHolder holder, int position) {
        ClassAttendanceItem item = classAttendanceItems.get(position);

        holder.tvID.setText(item.getStudentID());
        holder.tvName.setText(item.getStudentName());
        holder.percent.setText(item.getPercent()+" % ");

        if(item.getPercent() < 50){
            holder.percent.setTextColor(Color.RED);
        }
        else{
            //holder.percent.setTextColor(context.getResources().getColor(R.color.primary, context.getTheme()));
            holder.percent.setTextColor(ContextCompat.getColor(context, R.color.primary));
        }
    }

    @Override
    public int getItemCount() {
        return classAttendanceItems.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvID, percent;
        ImageButton btnSeeInfo;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvClsAttStudentName);
            tvID = itemView.findViewById(R.id.tvClsAttStudentID);
            percent = itemView.findViewById(R.id.tvClsAttStudentPercent);
            btnSeeInfo = itemView.findViewById(R.id.btnSeeStudent);

            btnSeeInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showStudentInfo(classAttendanceItems.get(getAdapterPosition()).getStudentID());
                }
            });
        }
    }

    private void showStudentInfo(String studentID) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Students").child(studentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                showStudentDialog(student);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showStudentDialog(final Student student) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.student_dialog)
                .create();
        dialog.show();

        final ImageView iv_image = dialog.findViewById(R.id.ivDialogImage);
        TextView tvDetails = dialog.findViewById(R.id.tvDialogDetails);
        TextView tvEmail = dialog.findViewById(R.id.tvDialogEmail);
        TextView tvNumber = dialog.findViewById(R.id.tvDialogNumber);

        FirebaseStorage.getInstance().getReference()
                .child("Profile Pics")
                .child(org_id)
                .child("Students")
                .child(student.getRoll_num())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_image);
            }
        });
        tvDetails.setText(student.getName()+"\n\n"+student.getRoll_num()+"\n\n"+student.getBatch());
        tvEmail.setText(student.getEmail());
        tvNumber.setText(student.getNumber());

        //dialog.show();
    }
}
