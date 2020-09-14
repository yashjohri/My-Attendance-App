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

import com.johri.myattendance.AddAttendanceActivity;
import com.johri.myattendance.ClassAttendanceActivity;
import com.johri.myattendance.R;
import com.johri.myattendance.TeacherRecordsActivity;
import com.johri.myattendance.models.TeacherClass;

import java.util.ArrayList;

public class TeacherClassAdapter extends RecyclerView.Adapter<TeacherClassAdapter.MyHolder> {

    private Context context;
    private ArrayList<TeacherClass> classes;
    private String org_id, teacher_id;
    private boolean isView, showRecords;

    public TeacherClassAdapter(Context context, ArrayList<TeacherClass> classes, String org_id, String teacher_id, boolean isView, boolean showRecords) {
        this.context = context;
        this.classes = classes;
        this.org_id = org_id;
        this.teacher_id = teacher_id;
        this.isView = isView;
        this.showRecords = showRecords;
    }

    @NonNull
    @Override
    public TeacherClassAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.class_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherClassAdapter.MyHolder holder, int position) {
        TeacherClass teacherClass = classes.get(position);

        holder.tvSubName.setText(teacherClass.getSubjectName());
        holder.tvSubID.setText(teacherClass.getSubjectID());
        holder.tvBatch.setText(teacherClass.getBatchName());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvSubID, tvSubName, tvBatch;
        ImageButton btnTakeAtt;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvSubID = itemView.findViewById(R.id.tvClsSubjectID);
            tvSubName = itemView.findViewById(R.id.tvClsSubjectName);
            tvBatch = itemView.findViewById(R.id.tvClsBatch);
            btnTakeAtt = itemView.findViewById(R.id.btnTakeAtt);

            if(isView || showRecords){
                btnTakeAtt.setImageResource(R.drawable.ic_eye);
            }

            btnTakeAtt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TeacherClass teacherClass = classes.get(getAdapterPosition());

                    Intent intent;
                    if(isView){
                        intent = new Intent(context, ClassAttendanceActivity.class);
                    }
                    else if(showRecords){
                        intent = new Intent(context, TeacherRecordsActivity.class);
                    }
                    else{
                        intent = new Intent(context, AddAttendanceActivity.class);
                    }

                    intent.putExtra("org_id", org_id);
                    intent.putExtra("batch", teacherClass.getBatchName());
                    intent.putExtra("sub_id", teacherClass.getSubjectID());
                    intent.putExtra("sub_name", teacherClass.getSubjectName());

                    context.startActivity(intent);
                }
            });
        }
    }
}
