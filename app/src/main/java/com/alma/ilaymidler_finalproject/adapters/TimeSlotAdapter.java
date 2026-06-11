package com.alma.ilaymidler_finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.TimeSlot;
import com.alma.ilaymidler_finalproject.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    public interface OnReserveClickListener {

        void onReserve(TimeSlot slot);
        // הפעולה שתתבצע כאשר המשתמש לוחץ על כפתור הזמנה.
        // הפעולה עצמה נכתבת במסך שמשתמש ב-Adapter.
    }

    // רשימת כל שעות ההזמנה שיוצגו ב-RecyclerView.
    private final List<TimeSlot> slots;

    // שומר את הפעולה שתופעל כאשר המשתמש יבצע הזמנה.
    private final OnReserveClickListener listener;

    // בנאי של ה-Adapter.
    // מקבל רשימת שעות ואת הפעולה שתתבצע בלחיצה על Reserve.
    public TimeSlotAdapter(List<TimeSlot> slots, OnReserveClickListener listener) {

        this.slots = slots != null ? slots : new ArrayList<>();
        // אם הרשימה שהתקבלה ריקה, יוצרים רשימה חדשה כדי למנוע קריסה.

        this.listener = listener;
        // שומר את ה-listener.
    }

    // יוצר שורה חדשה לפי העיצוב row_time_slot.xml.
    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_time_slot, parent, false);

        return new TimeSlotViewHolder(view);
    }

    // מכניס את נתוני השעה לתוך השורה המתאימה ב-RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {

        TimeSlot slot = slots.get(position);
        // לוקח את השעה המתאימה לפי המיקום ברשימה.

        if (slot == null) {
            return;
            // הגנה מפני מצב חריג שבו השעה ריקה.
        }

        holder.tvTimeRange.setText(
                slot.getStartTime() + " - " + slot.getEndTime()
        );
        // מציג את טווח השעות של ההזמנה.

        String currentUserId = "";

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        // שומר את מזהה המשתמש המחובר כרגע.

        if (slot.isReserved()) {
            // אם השעה כבר תפוסה.

            if (currentUserId.equals(slot.getReservedByUserId())) {
                // בודק האם המשתמש הנוכחי הוא זה שהזמין.

                holder.tvSlotStatus.setText("Reserved by you");
                // מציג שההזמנה שייכת למשתמש.

                holder.tvSlotStatus.setTextColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.primary
                        )
                );

                holder.btnReserve.setText("Reserved");
                // משנה את טקסט הכפתור.

                holder.btnReserve.setBackgroundResource(
                        R.drawable.rounded_button_reserved_you
                );
                // משנה את עיצוב הכפתור.

            } else {
                // אם מישהו אחר הזמין את השעה.

                holder.tvSlotStatus.setText("Already taken");

                holder.tvSlotStatus.setTextColor(
                        ContextCompat.getColor(
                                holder.itemView.getContext(),
                                R.color.danger
                        )
                );

                holder.btnReserve.setText("Unavailable");

                holder.btnReserve.setBackgroundResource(
                        R.drawable.rounded_button_taken
                );
            }

            holder.btnReserve.setEnabled(false);
            // מבטל אפשרות ללחוץ על הכפתור.

            holder.btnReserve.setAlpha(1f);
            // שומר על נראות מלאה של הכפתור.

            holder.btnReserve.setOnClickListener(null);
            // מסיר כל פעולה מהכפתור.

        } else {
            // אם השעה פנויה.

            holder.tvSlotStatus.setText("Available");

            holder.tvSlotStatus.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.success
                    )
            );

            holder.btnReserve.setText("Reserve");

            holder.btnReserve.setBackgroundResource(
                    R.drawable.rounded_button_success
            );

            holder.btnReserve.setEnabled(true);
            // מאפשר לבצע הזמנה.

            holder.btnReserve.setAlpha(1f);

            holder.btnReserve.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onReserve(slot);
                    // מפעיל את פעולת ההזמנה שנכתבה ב-Activity.
                }
            });
        }
    }

    // מחזיר כמה שעות קיימות ברשימה.
    // RecyclerView משתמש בזה כדי לדעת כמה שורות להציג.
    @Override
    public int getItemCount() {
        return slots.size();
    }

    // מעדכן את רשימת השעות לאחר קבלת נתונים חדשים מ-Firebase.
    public void updateList(List<TimeSlot> newList) {

        slots.clear();
        // מוחק את הרשימה הישנה.

        if (newList != null) {
            slots.addAll(newList);
            // מוסיף את כל הנתונים החדשים.
        }

        notifyDataSetChanged();
        // מרענן את ה-RecyclerView.
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {

        TextView tvTimeRange;
        // מציג את טווח השעות.

        TextView tvSlotStatus;
        // מציג האם השעה פנויה או תפוסה.

        Button btnReserve;
        // כפתור הזמנה.

        // מחבר את המשתנים לרכיבים ב-row_time_slot.xml.
        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            // חיבור לשעות.

            tvSlotStatus = itemView.findViewById(R.id.tvSlotStatus);
            // חיבור לסטטוס.

            btnReserve = itemView.findViewById(R.id.btnReserve);
            // חיבור לכפתור הזמנה.
        }
    }
}