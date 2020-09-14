package com.johri.myattendance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.R;
import com.johri.myattendance.models.Student;
import com.johri.myattendance.models.Subject;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.MyHolder> {

    private String org_id;
    private Context context;
    private ArrayList<String> branch_list;

    public BranchAdapter(String org_id, Context context, ArrayList<String> branch_list) {
        this.org_id = org_id;
        this.context = context;
        this.branch_list = branch_list;
    }

    @NonNull
    @Override
    public BranchAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.branch_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchAdapter.MyHolder holder, int position) {
        holder.tvBranchName.setText(branch_list.get(position));
    }

    @Override
    public int getItemCount() {
        return branch_list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvBranchName;
        ImageButton btnDeleteBranch;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvBranchName = itemView.findViewById(R.id.tv_branch_name);
            btnDeleteBranch = itemView.findViewById(R.id.btnDeleteBranch);
            btnDeleteBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String branchName = branch_list.get(getAdapterPosition());
                    FirebaseDatabase.getInstance().getReference(org_id).child("Branches").child(branchName).setValue(null);
                    branch_list.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    removeBatches(branchName);
                }
            });
        }
    }

    private void removeBatches(final String branchName) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Batches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Boolean> mapBatches =new HashMap<>();

                for(DataSnapshot ds:snapshot.getChildren()){
                    String batchName = ds.getKey();
                    if(batchName!=null && batchName.startsWith(branchName)){
                        FirebaseDatabase.getInstance().getReference().child(org_id).child("Batches").child(batchName).setValue(null);
                        FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").child(batchName).setValue(null);
                        mapBatches.put(batchName, true);
                    }
                }

                removeSubjects(branchName, mapBatches);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeSubjects(final String branchName, final HashMap<String, Boolean> mapBatches) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Boolean> mapSubjects = new HashMap<>();

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        Subject subject = ds.getValue(Subject.class);
                        if(subject.getBranch().equals(branchName)){
                            FirebaseDatabase.getInstance().getReference().child(org_id).child("Subjects").child(subject.getId()).setValue(null);
                            mapSubjects.put(subject.getId(), true);
                        }
                    }
                }

                removeStudents(branchName, mapBatches, mapSubjects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeStudents(final String branchName, final HashMap<String, Boolean> mapBatches, final HashMap<String, Boolean> mapSubjects) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        Student student = ds.getValue(Student.class);
                        if(student.getBranch().equals(branchName)){
                            FirebaseDatabase.getInstance().getReference().child(org_id).child("Students").child(student.getRoll_num()).setValue(null);
                        }
                    }
                }

                removeAssign(branchName, mapBatches, mapSubjects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeAssign(final String branchName, final HashMap<String, Boolean> mapBatches, final HashMap<String, Boolean> mapSubjects) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        TeacherSubject teacherSubject = ds.getValue(TeacherSubject.class);
                        HashMap<String, ArrayList<String>> map = teacherSubject.getTeacher_subjects();
                        if(map!=null){

                            HashMap<String, ArrayList<String>> newmap = new HashMap<>();

                            for(String key:map.keySet()){

                                if(!mapSubjects.containsKey(key)){

                                    ArrayList<String> newbatches = new ArrayList<>();

                                    for(String batch:map.get(key)){
                                        if(!mapBatches.containsKey(batch)){
                                            newbatches.add(batch);
                                        }
                                    }

                                    newmap.put(key, newbatches);
                                }
                            }

                            teacherSubject.setTeacher_subjects(newmap);
                        }
                        FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").child(teacherSubject.getTeacherID()).setValue(teacherSubject);
                    }
                }
                removeAttendance(branchName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeAttendance(final String branchName) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){

                    if(ds.getKey().startsWith(branchName)){

                        FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").child(ds.getKey()).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
