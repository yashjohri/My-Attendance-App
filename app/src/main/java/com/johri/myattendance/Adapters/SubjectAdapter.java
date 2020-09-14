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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.R;
import com.johri.myattendance.models.Subject;
import com.johri.myattendance.models.TeacherSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.MyHolder> {

    private Context context;
    private ArrayList<Subject> subList;
    private String org_id;

    public SubjectAdapter(Context context, ArrayList<Subject> subList, String org_id) {
        this.context = context;
        this.subList = subList;
        this.org_id = org_id;
    }

    @NonNull
    @Override
    public SubjectAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.MyHolder holder, int position) {
        Subject sub = subList.get(position);

        holder.tvSubNameId.setText(sub.getId()+" - "+sub.getName());
        holder.tvSubSemBranch.setText(sub.getBranch()+" - "+sub.getSem());
    }

    @Override
    public int getItemCount() {
        return subList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvSubNameId, tvSubSemBranch;
        ImageButton btnDelSub;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvSubNameId = itemView.findViewById(R.id.tvSubNameId);
            tvSubSemBranch = itemView.findViewById(R.id.tvSubBranchSem);
            btnDelSub = itemView.findViewById(R.id.btnDelSub);

            btnDelSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Subject sub = subList.get(getAdapterPosition());
                    FirebaseDatabase.getInstance().getReference().child(org_id).child("Subjects").child(sub.getId()).setValue(null);
                    subList.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    removeAttendance(sub.getId(), sub.getBranch()+" "+sub.getSem());
                }
            });
        }
    }

    private void removeAttendance(final String subID, final String branchsem){

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        if(ds.getKey().startsWith(branchsem)){
                            FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").child(ds.getKey()).child(subID).setValue(null);
                        }
                    }
                }

                removeAssign(subID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeAssign(final String subID) {

        FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    for(DataSnapshot ds:snapshot.getChildren()){

                        TeacherSubject teacherSubject = ds.getValue(TeacherSubject.class);
                        HashMap<String, ArrayList<String>> teacher_subjects = teacherSubject.getTeacher_subjects();

                        if(teacher_subjects!=null){

                            HashMap<String, ArrayList<String>> new_teacher_subjects = new HashMap<>();

                            for(String subject:teacher_subjects.keySet()){

                                if(!subject.equals(subID)){
                                    new_teacher_subjects.put(subject, teacher_subjects.get(subject));
                                }
                            }

                            teacherSubject.setTeacher_subjects(new_teacher_subjects);
                            FirebaseDatabase.getInstance().getReference().child(org_id).child("Assign")
                                    .child(teacherSubject.getTeacherID()).setValue(teacherSubject);
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
