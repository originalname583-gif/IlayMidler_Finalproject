package com.alma.ilaymidler_finalproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alma.ilaymidler_finalproject.Model.User;
import com.alma.ilaymidler_finalproject.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public interface OnUserClickListener {

        void onUserClick(User user);
        // הפעולה שתופעל כאשר המשתמש לוחץ על משתמש.

        void onLongUserClick(User user);
        // הפעולה שתופעל כאשר המשתמש לוחץ לחיצה ארוכה על משתמש.
    }

    private final List<User> userList = new ArrayList<>();
    // רשימת המשתמשים שתוצג ב-RecyclerView.

    private final OnUserClickListener listener;
    // שומר את הפעולות שיקרו בלחיצה רגילה או ארוכה.

    public UserAdapter(@Nullable OnUserClickListener listener) {

        this.listener = listener;
        // שומר את ה-listener שהתקבל.
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);
        // יוצר שורה חדשה לפי העיצוב row_user.xml.

        return new ViewHolder(view);
        // מחזיר ViewHolder חדש.
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = userList.get(position);
        // לוקח את המשתמש המתאים לפי המיקום ברשימה.

        if (user == null) {
            return;
            // הגנה מפני מצב שבו המשתמש ריק.
        }

        String firstName = getSafeText(user.getFirstName());
        // לוקח את השם הפרטי בצורה בטוחה.

        String lastName = getSafeText(user.getLastName());
        // לוקח את שם המשפחה בצורה בטוחה.

        String email = getSafeText(user.getEmail());
        // לוקח את האימייל בצורה בטוחה.

        String phone = getSafeText(user.getPhone());
        // לוקח את הטלפון בצורה בטוחה.

        holder.tvName.setText((firstName + " " + lastName).trim());
        // מציג את השם המלא.

        holder.tvEmail.setText(email);
        // מציג את האימייל.

        holder.tvPhone.setText(phone);
        // מציג את הטלפון.

        String initials = "";
        // ישמור את ראשי התיבות.

        if (!firstName.isEmpty()) {
            initials += firstName.charAt(0);
            // מוסיף את האות הראשונה של השם הפרטי.
        }

        if (!lastName.isEmpty()) {
            initials += lastName.charAt(0);
            // מוסיף את האות הראשונה של שם המשפחה.
        }

        holder.tvInitials.setText(initials.toUpperCase());
        // מציג את ראשי התיבות באותיות גדולות.

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onUserClick(user);
                // מפעיל לחיצה רגילה על המשתמש.
            }
        });

        holder.itemView.setOnLongClickListener(v -> {

            if (listener != null) {
                listener.onLongUserClick(user);
                // מפעיל לחיצה ארוכה על המשתמש.
            }

            return true;
            // מסמן שהלחיצה הארוכה טופלה.
        });
    }

    @Override
    public int getItemCount() {

        return userList.size();
        // מחזיר כמה משתמשים קיימים ברשימה.
    }

    public void setUserList(List<User> users) {

        userList.clear();
        // מנקה את הרשימה הישנה.

        if (users != null) {
            userList.addAll(users);
            // מוסיף את כל המשתמשים החדשים.
        }

        notifyDataSetChanged();
        // מרענן את ה-RecyclerView.
    }

    private String getSafeText(String text) {

        if (text == null) {
            return "";
            // אם הטקסט לא קיים מחזירים מחרוזת ריקה.
        }

        return text.trim();
        // מחזיר את הטקסט ללא רווחים מיותרים.
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        // מציג את השם המלא.

        TextView tvEmail;
        // מציג את האימייל.

        TextView tvPhone;
        // מציג את הטלפון.

        TextView tvInitials;
        // מציג את ראשי התיבות.

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_user_name);
            // מחבר את שדה השם.

            tvEmail = itemView.findViewById(R.id.tv_user_email);
            // מחבר את שדה האימייל.

            tvPhone = itemView.findViewById(R.id.tv_user_phone);
            // מחבר את שדה הטלפון.

            tvInitials = itemView.findViewById(R.id.tv_user_initials);
            // מחבר את שדה ראשי התיבות.
        }
    }
}