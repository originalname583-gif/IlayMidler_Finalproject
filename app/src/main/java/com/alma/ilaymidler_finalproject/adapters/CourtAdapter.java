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

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder> {

    public interface OnCourtClickListener {
        void onCourtClick(Court court);
    }

    private final List<Court> courtList;
    private final OnCourtClickListener listener;

    public CourtAdapter(List<Court> courtList, OnCourtClickListener listener) {
        this.courtList = courtList != null ? courtList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_court, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = courtList.get(position);

        holder.tvCourtName.setText(court.getName() != null ? court.getName() : "Court");
        holder.tvCourtCity.setText(court.getCity() != null ? court.getCity() : "");
        holder.tvCourtAddress.setText(court.getAddress() != null ? court.getAddress() : "");
        holder.tvCourtType.setText(court.getType() != null ? court.getType() : "Court");

        View.OnClickListener openListener = v -> {
            if (listener != null) listener.onCourtClick(court);
        };

        holder.itemView.setOnClickListener(openListener);
        holder.btnCourtOpen.setOnClickListener(openListener);
    }

    @Override
    public int getItemCount() {
        return courtList.size();
    }

    public void updateList(List<Court> newList) {
        courtList.clear();
        if (newList != null) courtList.addAll(newList);
        notifyDataSetChanged();
    }

    static class CourtViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourtName, tvCourtCity, tvCourtAddress, tvCourtType;
        Button btnCourtOpen;

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            tvCourtCity = itemView.findViewById(R.id.tvCourtCity);
            tvCourtAddress = itemView.findViewById(R.id.tvCourtAddress);
            tvCourtType = itemView.findViewById(R.id.tvCourtType);
            btnCourtOpen = itemView.findViewById(R.id.btnCourtOpen);
        }
    }
}