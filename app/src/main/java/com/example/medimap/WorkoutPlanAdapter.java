package com.example.medimap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medimap.roomdb.WorkoutRoom;

import java.util.List;


public class WorkoutPlanAdapter extends RecyclerView.Adapter<WorkoutPlanAdapter.WorkoutViewHolder> {

    private List<WorkoutRoom> workouts;

    public WorkoutPlanAdapter(List<WorkoutRoom> workouts) {
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_card, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutRoom workout = workouts.get(position);
        holder.workoutName.setText(workout.getName());
        holder.workoutType.setText(workout.getWorkouttype());
        holder.workoutDuration.setText("Duration: " + workout.getDuration() + " minutes");
        holder.workoutRepsSets.setText("Reps: " + workout.getRepetitions() + " Sets: " + workout.getSets());
        holder.workoutLocation.setText("Location: " + workout.getLocation());
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName, workoutType, workoutDuration, workoutRepsSets, workoutLocation;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workoutName);
            workoutType = itemView.findViewById(R.id.workoutType);
            workoutDuration = itemView.findViewById(R.id.workoutDuration);
            workoutRepsSets = itemView.findViewById(R.id.workoutRepsSets);
            workoutLocation = itemView.findViewById(R.id.workoutLocation);
        }
    }
}
