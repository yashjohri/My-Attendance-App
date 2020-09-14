package com.johri.myattendance.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.johri.myattendance.R;
import com.johri.myattendance.models.AttendanceItem;
import com.johri.myattendance.models.Student;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyHolder> {

    private Context context;
    private ArrayList<Student> students;
    private String org_id;

    public StudentAdapter(Context context, ArrayList<Student> students, String org_id) {
        this.context = context;
        this.students = students;
        this.org_id = org_id;
    }

    @NonNull
    @Override
    public StudentAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.MyHolder holder, int position) {
        Student student = students.get(position);

        holder.tvName.setText(student.getName());
        holder.tvID.setText(student.getRoll_num());
        holder.tvBatch.setText(student.getBatch());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvID, tvBatch;
        ImageButton btnDel, btnView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvStudentName);
            tvID = itemView.findViewById(R.id.tvStudentRollNo);
            tvBatch = itemView.findViewById(R.id.tvStudentBatch);
            btnDel = itemView.findViewById(R.id.btnDelStudent);
            btnView = itemView.findViewById(R.id.btnViewStudent);

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(students.get(getAdapterPosition()));
                }
            });

            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Student student = students.get(getAdapterPosition());
                    FirebaseDatabase.getInstance().getReference().child(org_id).child("Students").child(student.getRoll_num()).setValue(null);
                    students.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    removeAttendance(student);
                }
            });
        }
    }

    private void showDialog(Student student) {

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
    }

    private void removeAttendance(final Student student) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").child(student.getBatch()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.getValue()!=null){

                            for(DataSnapshot ds:snapshot.getChildren()){

                                String subID = ds.getKey();

                                for(DataSnapshot sub_ds:ds.getChildren()){

                                    String date = sub_ds.getKey();

                                    FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance")
                                            .child(student.getBatch()).child(subID).child(date).child(student.getRoll_num()).setValue(null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}