package com.johri.myattendance.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.johri.myattendance.AddTeacherSubjectActivity;
import com.johri.myattendance.R;
import com.johri.myattendance.models.Student;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class TeacherSubjectAdapter extends RecyclerView.Adapter<TeacherSubjectAdapter.MyHolder> {

    private Context context;
    private ArrayList<TeacherSubject> teacherSubjects;
    private String org_id;

    public TeacherSubjectAdapter(Context context, ArrayList<TeacherSubject> teacherSubjects, String org_id) {
        this.context = context;
        this.teacherSubjects = teacherSubjects;
        this.org_id = org_id;
    }

    @NonNull
    @Override
    public TeacherSubjectAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.teacher_subject_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherSubjectAdapter.MyHolder holder, int position) {
        TeacherSubject teachersubject = teacherSubjects.get(position);

        String teacherInfo = teachersubject.getTeacherID()+" "+teachersubject.getTeacherName();
        String subjectInfo = "";

        if(teachersubject.getTeacher_subjects()!=null){
            for(String subID:teachersubject.getTeacher_subjects().keySet()){

                for(String batch:teachersubject.getTeacher_subjects().get(subID)){

                    subjectInfo += subID+" - "+batch+"\n";
                }
            }
            subjectInfo = subjectInfo.substring(0, subjectInfo.length()-1);
        }

        holder.tvTeacher.setText(teacherInfo);
        holder.tvSubjects.setText(subjectInfo);
    }

    @Override
    public int getItemCount() {
        return teacherSubjects.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvTeacher, tvSubjects;
        ImageButton btnDelTeacherSubject, btnAddTeacherSubjects;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvTeacher = itemView.findViewById(R.id.tvTeacherNameID);
            tvSubjects = itemView.findViewById(R.id.tvSubjectBatch);
            btnDelTeacherSubject = itemView.findViewById(R.id.btnDelTeacherSubject);
            btnAddTeacherSubjects = itemView.findViewById(R.id.btnAddTeacherSubjects);

            btnDelTeacherSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherSubject teachersubject = teacherSubjects.get(getAdapterPosition());
                    removeAttendance(teachersubject);
                    
                    teachersubject.setTeacher_subjects(null);
                    FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").child(teachersubject.getTeacherID()).setValue(teachersubject);
                    teacherSubjects.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            btnAddTeacherSubjects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddTeacherSubjectActivity.class);
                    intent.putExtra("org_id", org_id);
                    intent.putExtra("teacher_id", teacherSubjects.get(getAdapterPosition()).getTeacherID());
                    intent.putExtra("teacher_name", teacherSubjects.get(getAdapterPosition()).getTeacherName());
                    context.startActivity(intent);
                }
            });

            tvTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tvSubjects.getVisibility() == View.VISIBLE){
                        tvSubjects.setVisibility(View.GONE);
                    }
                    else{
                        tvSubjects.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void removeAttendance(TeacherSubject teachersubject) {

        HashMap<String, ArrayList<String>> teacher_subjects = teachersubject.getTeacher_subjects();

        if(teacher_subjects==null){
            return;
        }

        for(String subID:teacher_subjects.keySet()){

            ArrayList<String> batch_names = teacher_subjects.get(subID);

            if(batch_names==null){
                break;
            }

            for(String name:batch_names){

                FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance")
                        .child(name).child(subID).setValue(null);
            }
        }
    }
}
