package com.johri.myattendance.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.johri.myattendance.R;
import com.johri.myattendance.models.Teacher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.MyHolder> {

    private Context context;
    private ArrayList<Teacher> teachers;
    private String org_id;

    public TeacherAdapter(Context context, ArrayList<Teacher> teachers, String org_id) {
        this.context = context;
        this.teachers = teachers;
        this.org_id = org_id;
    }

    @NonNull
    @Override
    public TeacherAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.teacher_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherAdapter.MyHolder holder, int position) {
        Teacher teacher = teachers.get(position);

        holder.tvName.setText(teacher.getName());
        holder.tvID.setText(teacher.getId());
        holder.tvOcc.setText(teacher.getOccupation());
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvID, tvOcc;
        ImageButton btnDel, btnView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvTeacherName);
            tvID = itemView.findViewById(R.id.tvTeacherID);
            tvOcc = itemView.findViewById(R.id.tvTeacherOcc);
            btnDel = itemView.findViewById(R.id.btnDelTeacher);
            btnView = itemView.findViewById(R.id.btnViewTeacher);

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(teachers.get(getAdapterPosition()));
                }
            });

            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Teacher teacher = teachers.get(getAdapterPosition());
                    FirebaseDatabase.getInstance().getReference().child(org_id).child("Teachers").child(teacher.getId()).setValue(null);
                    FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").child(teacher.getId()).setValue(null);
                    teachers.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void showDialog(Teacher teacher) {
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
                .child("Teachers")
                .child(teacher.getId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_image);
            }
        });
        tvDetails.setText(teacher.getName()+"\n\n"+teacher.getId()+"\n\n"+teacher.getOccupation());
        tvEmail.setText(teacher.getEmail());
        tvNumber.setText(teacher.getNumber());
    }
}
