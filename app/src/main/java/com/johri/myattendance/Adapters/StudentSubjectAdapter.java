package com.johri.myattendance.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.johri.myattendance.R;
import com.johri.myattendance.models.StudentSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentSubjectAdapter extends RecyclerView.Adapter<StudentSubjectAdapter.MyHolder> {

    private Context context;
    private ArrayList<StudentSubject> studentSubjects;

    public StudentSubjectAdapter(Context context, ArrayList<StudentSubject> studentSubjects) {
        this.context = context;
        this.studentSubjects = studentSubjects;
    }

    @NonNull
    @Override
    public StudentSubjectAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.student_subject_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentSubjectAdapter.MyHolder holder, int position) {
        StudentSubject studentSubject = studentSubjects.get(position);

        String subName = studentSubject.getSubjectID()+" "+studentSubject.getSubjectName();
        holder.tvSubName.setText(subName);

        String percent = String.format("%02d", studentSubject.getPercent()) +" %";
        holder.tvSubPercent.setText(percent);

        if(studentSubject.getPercent()<50){
            holder.tvSubPercent.setTextColor(Color.RED);
        }
        else {
            holder.tvSubPercent.setTextColor(ContextCompat.getColor(context, R.color.primary));
        }
    }

    @Override
    public int getItemCount() {
        return studentSubjects.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvSubName, tvSubPercent;
        ImageButton btnView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvSubName = itemView.findViewById(R.id.tvStudentSubjectName);
            tvSubPercent = itemView.findViewById(R.id.tvStudentSubjectPercent);
            btnView = itemView.findViewById(R.id.btnViewStudentSubject);

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(studentSubjects.get(getAdapterPosition()));
                }
            });
        }
    }

    private void showDialog(StudentSubject studentSubject) {

        String msg = "";
        HashMap<String, Boolean> map = studentSubject.getDatesMap();
        for(String date:map.keySet()){
            msg += date + "      -      " + (map.get(date)?"P":"A") +"\n";
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(R.layout.subject_dates)
                .create();
        dialog.show();

        TextView tvSub = dialog.findViewById(R.id.tvSubName);
        TextView tvDates = dialog.findViewById(R.id.tvSubDates);

        tvSub.setText(studentSubject.getSubjectID()+" "+studentSubject.getSubjectName());
        tvDates.setText(msg);
    }
}
