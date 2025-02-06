package com.example.workability;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workability.R;
import com.example.workability.SharedExperience;
import java.util.List;

public class ExperiencesAdapter extends RecyclerView.Adapter<ExperiencesAdapter.ViewHolder> {
    private Context context;
    private List<SharedExperience> experiencesList;

    public ExperiencesAdapter(Context context, List<SharedExperience> experiencesList) {
        this.context = context;
        this.experiencesList = experiencesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.experience_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedExperience experience = experiencesList.get(position);
        holder.userEmail.setText(experience.getUserEmail());
        holder.disabilityType.setText("Type: " + experience.getDisabilityType());
        holder.experienceText.setText(experience.getExperienceText());
    }

    @Override
    public int getItemCount() {
        return experiencesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, disabilityType, experienceText;

        public ViewHolder(View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            disabilityType = itemView.findViewById(R.id.disabilityType);
            experienceText = itemView.findViewById(R.id.experienceText);
        }
    }
}
