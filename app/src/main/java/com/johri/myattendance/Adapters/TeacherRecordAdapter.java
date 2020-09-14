package com.johri.myattendance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.johri.myattendance.R;

import java.util.ArrayList;

public class TeacherRecordAdapter extends RecyclerView.Adapter<TeacherRecordAdapter.MyHolder> {

    private Context context;
    private ArrayList<String> dates;
    private String subId, batch, org_id;

    public TeacherRecordAdapter(Context context, ArrayList<String> dates, String batch, String subId, String org_id) {
        this.context = context;
        this.dates = dates;
        this.batch = batch;
        this.subId = subId;
        this.org_id = org_id;
    }

    @NonNull
    @Override
    public TeacherRecordAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.teacher_record_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherRecordAdapter.MyHolder holder, int position) {
        holder.date.setText(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView date;
        ImageButton btnDel;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.tvTeacherClsRecordDate);
            btnDel = itemView.findViewById(R.id.btnDelTeacherClsRecord);

            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child(org_id).child("Attendance").child(batch).child(subId)
                            .child(dates.get(getAdapterPosition())).setValue(null);
                    dates.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }
}
