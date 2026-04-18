package com.alma.ilaymidler_finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.Court;
import com.alma.ilaymidler_finalproject.Model.Reservation;
import com.alma.ilaymidler_finalproject.Model.ReservationDisplayItem;
import com.alma.ilaymidler_finalproject.R;

import java.util.ArrayList;
import java.util.List;

public class MyReservationsAdapter extends RecyclerView.Adapter<MyReservationsAdapter.MyReservationViewHolder> {

    public interface OnCancelClickListener {
        void onCancelClicked(ReservationDisplayItem item);
    }

    private final List<ReservationDisplayItem> items = new ArrayList<>();
    private final OnCancelClickListener listener;

    public MyReservationsAdapter(OnCancelClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_reservation, parent, false);
        return new MyReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyReservationViewHolder holder, int position) {
        ReservationDisplayItem item = items.get(position);
        Reservation reservation = item.getReservation();
        Court court = item.getCourt();

        holder.tvCourtName.setText(court != null ? court.getName() : "Court");
        holder.tvCourtCity.setText(court != null ? court.getCity() : "");
        holder.tvCourtAddress.setText(court != null ? court.getAddress() : "");
        holder.tvReservationDate.setText("Date: " + (reservation != null ? reservation.getBookingDate() : ""));
        holder.tvReservationTime.setText("Time: " +
                (reservation != null ? reservation.getStartTime() : "") +
                " - " +
                (reservation != null ? reservation.getEndTime() : ""));

        holder.btnCancelReservation.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<ReservationDisplayItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    static class MyReservationViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourtName, tvCourtCity, tvCourtAddress, tvReservationDate, tvReservationTime;
        Button btnCancelReservation;

        public MyReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            tvCourtCity = itemView.findViewById(R.id.tvCourtCity);
            tvCourtAddress = itemView.findViewById(R.id.tvCourtAddress);
            tvReservationDate = itemView.findViewById(R.id.tvReservationDate);
            tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
            btnCancelReservation = itemView.findViewById(R.id.btnCancelReservation);
        }
    }
}