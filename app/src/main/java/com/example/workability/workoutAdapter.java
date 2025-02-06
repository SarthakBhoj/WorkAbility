package com.example.workability;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class workoutAdapter extends RecyclerView.Adapter<workoutAdapter.MyViewHolder> {

    private List<workout_item> examList;
    Context context;
    // Constructor
    public workoutAdapter(List<workout_item> examList) {
        this.examList = examList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        workout_item examItem = examList.get(position);

        holder.workout_Name.setText(examItem.getName());
        holder.Benefits.setText(examItem.getBenefits());
        holder.info.setText(examItem.getMessage());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WorkoutLayout.class);
                intent.putExtra("email", "" + examList.get(position).name);
//                    Toast.makeText(context, girlClassArrayList.get(position).Stud_Number, Toast.LENGTH_SHORT).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    // ViewHolder class
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView workout_Name, Benefits,info;
        RelativeLayout relativeLayout;
        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            context = itemView.getContext();
            workout_Name = itemView.findViewById(R.id.workout_Name);
            Benefits = itemView.findViewById(R.id.Benefits);
            info = itemView.findViewById(R.id.additional_info);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
