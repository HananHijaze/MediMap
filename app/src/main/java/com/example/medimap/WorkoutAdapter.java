package com.example.medimap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.medimap.server.Workout;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts;

    public WorkoutAdapter(List<Workout> workouts) {
        this.workouts = workouts;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_card, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.nameTextView.setText(workout.getName());
        holder.durationTextView.setText("Duration: " + workout.getDuration() + " mins");
        holder.repsTextView.setText("Reps: " + workout.getRepetitions());
        holder.setsTextView.setText("Sets: " + workout.getSets());
        holder.locationTextView.setText("Location: " + workout.getLocation());
        String description = workout.getDescription();

// Check for substrings in the description and set the corresponding image
        if (description.contains("upper-body")) {
            holder.typeTextView.setText("upper-body");
            holder.imageView.setImageResource(R.drawable.upperbody); // upper-body.jpeg
        } else if (description.contains("lower-body")) {
            holder.typeTextView.setText("lower-body");
           holder.imageView.setImageResource(R.drawable.lowerbody); // lower-body.jpeg
        } else if (description.contains("full-body")) {
            holder.typeTextView.setText("full-body");
          holder.imageView.setImageResource(R.drawable.fullbody);  // full-body.jpeg
        } else if (description.contains("core")) {
            holder.typeTextView.setText("core");
           holder.imageView.setImageResource(R.drawable.core);       // core.jpeg
        }

    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, typeTextView, durationTextView, repsTextView, setsTextView, locationTextView;
        public ImageView imageView;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.workout_name);
            typeTextView = itemView.findViewById(R.id.workout_type);
            durationTextView = itemView.findViewById(R.id.workout_duration);
            repsTextView = itemView.findViewById(R.id.workout_reps);
            setsTextView = itemView.findViewById(R.id.workout_sets);
            locationTextView = itemView.findViewById(R.id.workout_location);
            imageView = itemView.findViewById(R.id.body_image);
        }
    }
}
