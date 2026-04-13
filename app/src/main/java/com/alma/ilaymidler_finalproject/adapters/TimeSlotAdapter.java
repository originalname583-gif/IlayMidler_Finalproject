package com.alma.ilaymidler_finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.TimeSlot;
import com.alma.ilaymidler_finalproject.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    public interface OnReserveClickListener {
        void onReserve(TimeSlot slot);
    }

    private final List<TimeSlot> slots;
    private final OnReserveClickListener listener;

    public TimeSlotAdapter(List<TimeSlot> slots, OnReserveClickListener listener) {
        this.slots = slots != null ? slots : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot slot = slots.get(position);
        holder.tvTimeRange.setText(slot.getStartTime() + " - " + slot.getEndTime());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";

        if (slot.isReserved()) {
            if (currentUserId.equals(slot.getReservedByUserId())) {
                holder.tvSlotStatus.setText("Reserved by you");
                holder.btnReserve.setText("Reserved");
            } else {
                holder.tvSlotStatus.setText("Already taken");
                holder.btnReserve.setText("Unavailable");
            }

            holder.btnReserve.setEnabled(false);
            holder.btnReserve.setAlpha(0.65f);
            holder.btnReserve.setOnClickListener(null);
        } else {
            holder.tvSlotStatus.setText("Available now");
            holder.btnReserve.setText("Reserve");
            holder.btnReserve.setEnabled(true);
            holder.btnReserve.setAlpha(1f);
            holder.btnReserve.setOnClickListener(v -> {
                if (listener != null) listener.onReserve(slot);
            });
        }
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public void updateList(List<TimeSlot> newList) {
        slots.clear();
        if (newList != null) slots.addAll(newList);
        notifyDataSetChanged();
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeRange, tvSlotStatus;
        Button btnReserve;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvSlotStatus = itemView.findViewById(R.id.tvSlotStatus);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }
}