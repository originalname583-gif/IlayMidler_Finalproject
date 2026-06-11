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
        // הפונקציה הזאת מופעלת כאשר המשתמש לוחץ על מגרש.
    }

    private final List<Court> courtList = new ArrayList<>();
    // רשימת המגרשים שתוצג ב-RecyclerView.

    private final OnCourtClickListener listener;
    // שומר את הפעולה שתתבצע כאשר המשתמש ילחץ על מגרש.

    public CourtAdapter(List<Court> courtList, OnCourtClickListener listener) {

        this.listener = listener;
        // שומר את ה-listener שהתקבל.

        if (courtList != null) {
            this.courtList.addAll(courtList);
            // מעתיק את כל המגרשים שהתקבלו לרשימה של ה-Adapter.
        }
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_court, parent, false);
        // יוצר שורה חדשה לפי העיצוב שנמצא ב-row_court.xml.

        return new CourtViewHolder(view);
        // מחזיר ViewHolder חדש שיחזיק את כל הרכיבים של השורה.
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {

        Court court = courtList.get(position);
        // לוקח את המגרש המתאים לפי המיקום ברשימה.

        if (court == null) {
            return;
            // אם משום מה המגרש ריק, יוצאים מהפונקציה.
        }

        holder.tvCourtName.setText(getSafeText(court.getName(), "Court"));
        // מציג את שם המגרש.

        holder.tvCourtCity.setText(getSafeText(court.getCity(), ""));
        // מציג את העיר של המגרש.

        holder.tvCourtAddress.setText(getSafeText(court.getAddress(), ""));
        // מציג את הכתובת של המגרש.

        holder.tvCourtType.setText(getSafeText(court.getType(), "Court"));
        // מציג את סוג המגרש.

        View.OnClickListener openListener = v -> {

            if (listener != null) {
                listener.onCourtClick(court);
                // מפעיל את הפונקציה שהועברה מה-Activity כאשר לוחצים על המגרש.
            }
        };

        holder.itemView.setOnClickListener(openListener);
        // מאפשר לחיצה על כל השורה.

        holder.btnCourtOpen.setOnClickListener(openListener);
        // מאפשר לחיצה גם על הכפתור.
    }

    @Override
    public int getItemCount() {

        return courtList.size();
        // מחזיר כמה מגרשים יש ברשימה.
        // RecyclerView משתמש בזה כדי לדעת כמה שורות להציג.
    }

    public void updateList(List<Court> newList) {

        courtList.clear();
        // מוחק את הרשימה הישנה.

        if (newList != null) {
            courtList.addAll(newList);
            // מוסיף את כל המגרשים החדשים.
        }

        notifyDataSetChanged();
        // מודיע ל-RecyclerView שהנתונים השתנו וצריך לרענן את המסך.
    }

    private String getSafeText(String text, String defaultText) {

        if (text == null || text.trim().isEmpty()) {
            return defaultText;
            // אם הטקסט ריק או null מחזירים ברירת מחדל.
        }

        return text.trim();
        // מחזירים את הטקסט לאחר ניקוי רווחים מיותרים.
    }

    static class CourtViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourtName;
        // מציג את שם המגרש.

        TextView tvCourtCity;
        // מציג את העיר.

        TextView tvCourtAddress;
        // מציג את הכתובת.

        TextView tvCourtType;
        // מציג את סוג המגרש.

        Button btnCourtOpen;
        // כפתור לפתיחת פרטי המגרש.

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            // מחבר את משתנה שם המגרש לרכיב במסך.

            tvCourtCity = itemView.findViewById(R.id.tvCourtCity);
            // מחבר את משתנה העיר לרכיב במסך.

            tvCourtAddress = itemView.findViewById(R.id.tvCourtAddress);
            // מחבר את משתנה הכתובת לרכיב במסך.

            tvCourtType = itemView.findViewById(R.id.tvCourtType);
            // מחבר את משתנה סוג המגרש לרכיב במסך.

            btnCourtOpen = itemView.findViewById(R.id.btnCourtOpen);
            // מחבר את הכפתור לרכיב במסך.
        }
    }
}