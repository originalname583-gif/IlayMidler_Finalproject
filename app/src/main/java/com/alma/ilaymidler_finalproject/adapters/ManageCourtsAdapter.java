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
        // הפונקציה מופעלת כאשר המנהל לוחץ על כפתור העריכה.

        void onDelete(Court court);
        // הפונקציה מופעלת כאשר המנהל לוחץ על כפתור המחיקה.
    }

    private final List<Court> courts = new ArrayList<>();
    // רשימת כל המגרשים שיוצגו במסך ניהול המגרשים.

    private final OnCourtActionListener listener;
    // שומר את הפעולות שיקרו בלחיצה על עריכה או מחיקה.

    // הבנאי של ה-Adapter.
    // מקבל listener שמגדיר מה יקרה בלחיצה על הכפתורים.
    public ManageCourtsAdapter(OnCourtActionListener listener) {
        this.listener = listener;
    }

    // הפונקציה יוצרת שורה חדשה ב-RecyclerView.
    // היא לוקחת את העיצוב row_manage_court.xml.
    @NonNull
    @Override
    public ManageCourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_manage_court, parent, false);

        return new ManageCourtViewHolder(view);
    }

    // הפונקציה ממלאת את הנתונים של המגרש בתוך השורה.
    // כל פעם שמגרש מוצג במסך היא מעדכנת את הטקסטים והכפתורים.
    @Override
    public void onBindViewHolder(@NonNull ManageCourtViewHolder holder, int position) {

        Court court = courts.get(position);
        // לוקח את המגרש המתאים לפי המיקום ברשימה.

        holder.tvCourtName.setText(getSafeText(court.getName(), "Court"));
        // מציג את שם המגרש.

        holder.tvCourtCity.setText(getSafeText(court.getCity(), ""));
        // מציג את העיר.

        holder.tvCourtAddress.setText(getSafeText(court.getAddress(), ""));
        // מציג את הכתובת.

        holder.tvCourtType.setText(getSafeText(court.getType(), ""));
        // מציג את סוג המגרש.

        holder.btnEditCourt.setOnClickListener(v -> {

            if (listener != null) {
                listener.onEdit(court);
                // מפעיל את פעולת העריכה.
            }
        });

        holder.btnDeleteCourt.setOnClickListener(v -> {

            if (listener != null) {
                listener.onDelete(court);
                // מפעיל את פעולת המחיקה.
            }
        });
    }

    // הפונקציה מחזירה כמה מגרשים יש ברשימה.
    // RecyclerView משתמש בזה כדי לדעת כמה שורות להציג.
    @Override
    public int getItemCount() {
        return courts.size();
    }

    // הפונקציה מעדכנת את רשימת המגרשים.
    // משתמשים בה לאחר קבלת נתונים חדשים מ-Firebase.
    public void updateList(List<Court> newList) {

        courts.clear();
        // מוחק את הרשימה הישנה.

        if (newList != null) {
            courts.addAll(newList);
            // מוסיף את כל המגרשים החדשים.
        }

        notifyDataSetChanged();
        // מרענן את ה-RecyclerView.
    }

    // פונקציה שמונעת בעיות אם Firebase מחזיר null או טקסט ריק.
    private String getSafeText(String text, String defaultText) {

        if (text == null || text.trim().isEmpty()) {
            return defaultText;
        }

        return text.trim();
    }

    static class ManageCourtViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourtName;
        // מציג את שם המגרש.

        TextView tvCourtCity;
        // מציג את העיר.

        TextView tvCourtAddress;
        // מציג את הכתובת.

        TextView tvCourtType;
        // מציג את סוג המגרש.

        Button btnEditCourt;
        // כפתור עריכת מגרש.

        Button btnDeleteCourt;
        // כפתור מחיקת מגרש.

        // הבנאי של ViewHolder.
        // מחבר בין המשתנים בקוד לרכיבים ב-row_manage_court.xml.
        public ManageCourtViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            // חיבור לשם המגרש.

            tvCourtCity = itemView.findViewById(R.id.tvCourtCity);
            // חיבור לעיר.

            tvCourtAddress = itemView.findViewById(R.id.tvCourtAddress);
            // חיבור לכתובת.

            tvCourtType = itemView.findViewById(R.id.tvCourtType);
            // חיבור לסוג המגרש.

            btnEditCourt = itemView.findViewById(R.id.btnEditCourt);
            // חיבור לכפתור העריכה.

            btnDeleteCourt = itemView.findViewById(R.id.btnDeleteCourt);
            // חיבור לכפתור המחיקה.
        }
    }
}