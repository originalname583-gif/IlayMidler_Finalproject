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
    // Adapter שאחראי להציג את רשימת ההזמנות שלי בתוך RecyclerView.

    public interface OnCancelClickListener {
        // ממשק שמגדיר פעולה שתקרה כאשר המשתמש לוחץ על ביטול הזמנה.

        void onCancelClicked(ReservationDisplayItem item);
        // פונקציה שמופעלת כשמשתמש לוחץ על כפתור ביטול.
        // הפעולה האמיתית נכתבת במסך שמשתמש ב-Adapter.
    }

    private final List<ReservationDisplayItem> items = new ArrayList<>();
    // רשימה ששומרת את כל ההזמנות שיוצגו במסך.

    private final OnCancelClickListener listener;
    // שומר את הפעולה שתתבצע כשילחצו על כפתור ביטול.

    public MyReservationsAdapter(OnCancelClickListener listener) {
        // הבנאי של ה-Adapter.
        // הוא מקבל listener כדי לדעת מה לעשות כשמשתמש לוחץ על ביטול.

        this.listener = listener;
        // שומר את ה-listener בתוך המשתנה של המחלקה.
    }

    @NonNull
    @Override
    public MyReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // הפונקציה יוצרת שורה חדשה ברשימה.
        // היא לוקחת את העיצוב row_my_reservation.xml ומכינה אותו לתצוגה.

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_reservation, parent, false);
        // יוצר View חדש לפי קובץ העיצוב של שורת הזמנה.

        return new MyReservationViewHolder(view);
        // מחזיר ViewHolder שמחזיק את כל הרכיבים של השורה.
    }

    @Override
    public void onBindViewHolder(@NonNull MyReservationViewHolder holder, int position) {
        // הפונקציה מכניסה את נתוני ההזמנה לתוך השורה במסך.
        // היא מופעלת לכל שורה שמוצגת ברשימה.

        ReservationDisplayItem item = items.get(position);
        // לוקח את הפריט המתאים לפי המיקום ברשימה.

        if (item == null) {
            // בודק אם הפריט ריק כדי למנוע קריסה.

            return;
            // אם הפריט ריק, יוצאים מהפונקציה.
        }

        Reservation reservation = item.getReservation();
        // מוציא מתוך הפריט את פרטי ההזמנה.

        Court court = item.getCourt();
        // מוציא מתוך הפריט את פרטי המגרש.

        holder.tvCourtName.setText(court != null ? getSafeText(court.getName(), "Court") : "Court");
        // מציג את שם המגרש.
        // אם המגרש לא קיים, מציג "Court".

        holder.tvCourtCity.setText(court != null ? getSafeText(court.getCity(), "") : "");
        // מציג את העיר של המגרש.

        holder.tvCourtAddress.setText(court != null ? getSafeText(court.getAddress(), "") : "");
        // מציג את הכתובת של המגרש.

        String date = reservation != null ? getSafeText(reservation.getBookingDate(), "") : "";
        // שומר את תאריך ההזמנה בצורה בטוחה.

        String startTime = reservation != null ? getSafeText(reservation.getStartTime(), "") : "";
        // שומר את שעת ההתחלה בצורה בטוחה.

        String endTime = reservation != null ? getSafeText(reservation.getEndTime(), "") : "";
        // שומר את שעת הסיום בצורה בטוחה.

        holder.tvReservationDate.setText("Date: " + date);
        // מציג את תאריך ההזמנה במסך.

        holder.tvReservationTime.setText("Time: " + startTime + " - " + endTime);
        // מציג את שעות ההזמנה במסך.

        holder.btnCancelReservation.setOnClickListener(v -> {
            // מגדיר מה יקרה כשלוחצים על כפתור ביטול הזמנה.

            if (listener != null) {
                // בודק שיש listener כדי למנוע קריסה.

                listener.onCancelClicked(item);
                // מפעיל את פעולת הביטול שנכתבה במסך שמשתמש ב-Adapter.
            }
        });
    }

    @Override
    public int getItemCount() {
        // הפונקציה מחזירה כמה הזמנות יש ברשימה.
        // RecyclerView משתמש בזה כדי לדעת כמה שורות להציג.

        return items.size();
        // מחזיר את מספר הפריטים ברשימה.
    }

    public void updateList(List<ReservationDisplayItem> newItems) {
        // הפונקציה מעדכנת את רשימת ההזמנות.
        // משתמשים בה אחרי שמקבלים הזמנות חדשות מ-Firebase.

        items.clear();
        // מוחק את הרשימה הישנה.

        if (newItems != null) {
            // בודק שהרשימה החדשה לא ריקה/null.

            items.addAll(newItems);
            // מוסיף את כל ההזמנות החדשות לרשימה.
        }

        notifyDataSetChanged();
        // מודיע ל-RecyclerView שהנתונים השתנו וצריך לרענן את המסך.
    }

    private String getSafeText(String text, String defaultText) {
        // פונקציה שעוזרת להציג טקסט בצורה בטוחה.
        // אם הטקסט ריק או null, היא מחזירה טקסט ברירת מחדל.

        if (text == null || text.trim().isEmpty()) {
            // בודק אם הטקסט לא קיים או ריק.

            return defaultText;
            // מחזיר טקסט ברירת מחדל.
        }

        return text.trim();
        // מחזיר את הטקסט בלי רווחים מיותרים בהתחלה ובסוף.
    }

    static class MyReservationViewHolder extends RecyclerView.ViewHolder {
        // מחלקה שמחזיקה את הרכיבים של שורה אחת ברשימת ההזמנות.

        TextView tvCourtName, tvCourtCity, tvCourtAddress, tvReservationDate, tvReservationTime;
        // משתנים שמייצגים את הטקסטים שמוצגים בשורה.

        Button btnCancelReservation;
        // משתנה שמייצג את כפתור ביטול ההזמנה.

        public MyReservationViewHolder(@NonNull View itemView) {
            // הבנאי של ViewHolder.
            // הוא מחבר בין המשתנים בקוד לבין הרכיבים ב-row_my_reservation.xml.

            super(itemView);
            // שולח את העיצוב למחלקת האב RecyclerView.ViewHolder.

            tvCourtName = itemView.findViewById(R.id.tvCourtName);
            // מחבר את שם המגרש לרכיב המתאים ב-XML.

            tvCourtCity = itemView.findViewById(R.id.tvCourtCity);
            // מחבר את העיר לרכיב המתאים ב-XML.

            tvCourtAddress = itemView.findViewById(R.id.tvCourtAddress);
            // מחבר את הכתובת לרכיב המתאים ב-XML.

            tvReservationDate = itemView.findViewById(R.id.tvReservationDate);
            // מחבר את תאריך ההזמנה לרכיב המתאים ב-XML.

            tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
            // מחבר את שעות ההזמנה לרכיב המתאים ב-XML.

            btnCancelReservation = itemView.findViewById(R.id.btnCancelReservation);
            // מחבר את כפתור הביטול לרכיב המתאים ב-XML.
        }
    }
}