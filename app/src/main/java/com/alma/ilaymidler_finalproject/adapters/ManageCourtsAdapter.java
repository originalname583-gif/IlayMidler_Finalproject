package com.alma.ilaymidler_finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.R;

import java.util.ArrayList;
import java.util.List;

public class ManageCourtsAdapter extends RecyclerView.Adapter<ManageCourtsAdapter.ManageCourtViewHolder> {

    public interface OnCourtActionListener {
        void onEdit(Court court);
        void onDelete(Court court);
    }

    private final List<Court> courts = new ArrayList<>();
    private final OnCourtActionListener listener;

    public ManageCourtsAdapter(OnCourtActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ManageCourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_manage_court, parent, false);
        return new ManageCourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageCourtViewHolder holder, int position) {
        Court court = courts.get(position);

        holder.tvCourtName.setText(court.getName());
        holder.tvCourtCity.setText(court.getCity());
        holder.tvCourtAddress.setText(court.getAddress());
        holder.tvCourtType.setText(court.getType());

        holder.btnEditCourt.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(court);
        });

        holder.btnDeleteCourt.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(court);
        });
    }

    @Override
    public int getItemCount() {
        return courts.size();
    }

    public void updateList(List<Court> newList) {
        courts.clear();
        if (newList != null) {
            courts.addAll(newList);
        }
        notifyDataSetChanged();
    }

    static class ManageCourtViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourtName, tvCourtCity, tvCourtAddress, tvCourtType;
        Button btnEditCourt, btnDeleteCourt;

        public ManageCourtViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            tvCourtCity = itemView.findViewById(R.id.tvCourtCity);
            tvCourtAddress = itemView.findViewById(R.id.tvCourtAddress);
            tvCourtType = itemView.findViewById(R.id.tvCourtType);
            btnEditCourt = itemView.findViewById(R.id.btnEditCourt);
            btnDeleteCourt = itemView.findViewById(R.id.btnDeleteCourt);
        }
    }
}