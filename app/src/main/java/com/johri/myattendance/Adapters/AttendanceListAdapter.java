package com.johri.myattendance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johri.myattendance.R;
import com.johri.myattendance.models.AttendanceItem;

import java.util.ArrayList;

public class AttendanceListAdapter extends RecyclerView.Adapter<AttendanceListAdapter.MyHolder> {

    private Context context;
    private ArrayList<AttendanceItem> attendanceItems;
    private String org_id, batch, subID;

    public AttendanceListAdapter(Context context, ArrayList<AttendanceItem> attendanceItems, String org_id, String batch, String subID) {
        this.context = context;
        this.attendanceItems = attendanceItems;
        this.org_id = org_id;
        this.batch = batch;
        this.subID = subID;
    }

    @NonNull
    @Override
    public AttendanceListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.attendance_list_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceListAdapter.MyHolder holder, int position) {
        AttendanceItem item = attendanceItems.get(position);

        holder.tvName.setText(item.getStudentName());
        holder.tvID.setText(item.getStudentID());
    }

    @Override
    public int getItemCount() {
        return attendanceItems.size();
    }

    public void saveAttendance(final String date){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").child(batch).child(subID);

        ref.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Toast.makeText(context, snapshot.getKey(), Toast.LENGTH_SHORT).show();
                if(snapshot.getValue()!=null){
                    Toast.makeText(context, "Attendance already taken", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveAttendance(ref, date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveAttendance(DatabaseReference ref, String date){

        for(AttendanceItem item:attendanceItems){
            ref.child(date).child(item.getStudentID()).setValue(item);
        }
        //ref.child(date).setValue(attendanceItems);
        Toast.makeText(context, "Attendance saved", Toast.LENGTH_SHORT).show();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView tvID, tvName;
        CheckBox cb;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvID = itemView.findViewById(R.id.tvAttStudentID);
            tvName = itemView.findViewById(R.id.tvAttStudentName);
            cb = itemView.findViewById(R.id.cbPresent);

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean state = cb.isChecked();
                    cb.setChecked(state);
                    attendanceItems.get(getAdapterPosition()).setPresent(state);
                }
            });
        }
    }
}
