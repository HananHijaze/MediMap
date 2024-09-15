package com.example.medimap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.typeTextView.setText(workout.getWorkoutType());
        holder.durationTextView.setText("Duration: " + workout.getDuration() + " mins");
        holder.repsTextView.setText("Reps: " + workout.getRepetitions());
        holder.setsTextView.setText("Sets: " + workout.getSets());
        holder.locationTextView.setText("Location: " + workout.getLocation());
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, typeTextView, durationTextView, repsTextView, setsTextView, locationTextView;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.workout_name);
            typeTextView = itemView.findViewById(R.id.workout_type);
            durationTextView = itemView.findViewById(R.id.workout_duration);
            repsTextView = itemView.findViewById(R.id.workout_reps);
            setsTextView = itemView.findViewById(R.id.workout_sets);
            locationTextView = itemView.findViewById(R.id.workout_location);
        }
    }
}
