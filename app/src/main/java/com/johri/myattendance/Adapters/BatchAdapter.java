package com.johri.myattendance.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.AdminAttendanceActivity;
import com.johri.myattendance.R;
import com.johri.myattendance.models.Batch;
import com.johri.myattendance.models.Student;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.MyHolder> {

    private Context context;
    private ArrayList<Batch> batchList;
    private String org_id;
    private boolean isViewAttendance;

    public BatchAdapter(Context context, ArrayList<Batch> batchList, String org_id, boolean isViewAttendance) {
        this.context = context;
        this.batchList = batchList;
        this.org_id = org_id;
        this.isViewAttendance = isViewAttendance;
    }

    @NonNull
    @Override
    public BatchAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.batch_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchAdapter.MyHolder holder, int position) {
        Batch batch = batchList.get(position);
        holder.tvBatchName.setText(batch.getName());
        holder.tvBatchBranchSem.setText(batch.getBranch()+" - "+batch.getSem());
    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvBatchName, tvBatchBranchSem;
        ImageButton btnDelBatch;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvBatchName = itemView.findViewById(R.id.tvBatchName);
            tvBatchBranchSem = itemView.findViewById(R.id.tvBatchBranchSem);
            btnDelBatch = itemView.findViewById(R.id.btnDelBatch);

            if(isViewAttendance){
                btnDelBatch.setImageResource(R.drawable.ic_eye);
            }
            btnDelBatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isViewAttendance){
                        showAttendance(getAdapterPosition());
                    }
                    else{
                        deleteBatch(getAdapterPosition());
                    }
                }
            });
        }
    }

    private void showAttendance(int position) {
        Intent intent = new Intent(context, AdminAttendanceActivity.class);
        Batch batch = batchList.get(position);
        String batchfullname = batch.getBranch()+" "+batch.getSem()+" "+batch.getName();
        intent.putExtra("batch", batchfullname);
        intent.putExtra("org_id", org_id);
        context.startActivity(intent);
    }

    private void deleteBatch(int position) {

        Batch batch = batchList.get(position);
        String path = batch.getBranch()+" "+batch.getSem()+" "+batch.getName();
        FirebaseDatabase.getInstance().getReference().child(org_id).child("Batches").child(path).setValue(null);
        batchList.remove(position);
        notifyDataSetChanged();

        removeStudents(batch.getBranch()+" "+batch.getSem()+" "+batch.getName());
    }

    private void removeStudents(final String batch_name) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){
                        Student student = ds.getValue(Student.class);
                        if(student.getBatch().equals(batch_name)){
                            FirebaseDatabase.getInstance().getReference().child(org_id).child("Students").child(student.getRoll_num()).setValue(null);
                        }
                    }
                }

                removeAttendance(batch_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeAttendance(String batch_name) {
        FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").child(batch_name).setValue(null);
        removeAssign(batch_name);
    }

    private void removeAssign(final String batch_name) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        TeacherSubject teacherSubject = ds.getValue(TeacherSubject.class);
                        HashMap<String, ArrayList<String>> teacherSubjects = teacherSubject.getTeacher_subjects();

                        if(teacherSubjects!=null){

                            for(String subject:teacherSubjects.keySet()){

                                ArrayList<String> newBatches = new ArrayList<>();

                                for(String batch:teacherSubjects.get(subject)){
                                    if(!batch.equals(batch_name)){
                                        newBatches.add(batch);
                                    }
                                }

                                teacherSubjects.put(subject, newBatches);
                            }

                            teacherSubject.setTeacher_subjects(teacherSubjects);
                            FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").child(teacherSubject.getTeacherID())
                                    .setValue(teacherSubject);
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
