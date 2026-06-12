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
        // פעולה בלחיצה רגילה על משתמש.

        void onLongUserClick(User user);
        // פעולה בלחיצה ארוכה על משתמש.
    }

    private final List<User> userList = new ArrayList<>();
    // רשימת המשתמשים שמוצגת במסך.

    private final OnUserClickListener listener;
    // שומר את פעולות הלחיצה.

    public UserAdapter(@Nullable OnUserClickListener listener) {
        this.listener = listener;
        // שומר את ה-listener.
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);
        // יוצר שורה לפי row_user.xml.

        return new ViewHolder(view);
        // מחזיר ViewHolder חדש.
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        // לוקח משתמש לפי המיקום ברשימה.

        if (user == null) {
            return;
            // אם המשתמש ריק, עוצרים.
        }

        String fullName = user.getFullName();
        // מקבל שם מלא בצורה בטוחה מה-User model.

        String email = user.getEmail();
        // מקבל אימייל.

        String phone = user.getPhone();
        // מקבל טלפון.

        holder.tvName.setText(fullName.isEmpty() ? "No name" : fullName);
        // מציג שם, ואם אין שם מציג No name.

        holder.tvEmail.setText(email.isEmpty() ? "No email" : email);
        // מציג אימייל.

        holder.tvPhone.setText(phone.isEmpty() ? "No phone" : phone);
        // מציג טלפון.

        holder.tvInitials.setText(createInitials(user));
        // מציג ראשי תיבות בעיגול הכחול.

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
                // מפעיל לחיצה רגילה.
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongUserClick(user);
                // מפעיל לחיצה ארוכה.
            }

            return true;
            // אומר שהלחיצה הארוכה טופלה.
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
        // מחזיר כמה משתמשים יש ברשימה.
    }

    public void setUserList(List<User> users) {
        userList.clear();
        // מנקה רשימה ישנה.

        if (users != null) {
            userList.addAll(users);
            // מוסיף משתמשים חדשים.
        }

        notifyDataSetChanged();
        // מרענן את הרשימה.
    }

    private String createInitials(User user) {
        String firstName = user.getFirstName();
        // שם פרטי.

        String lastName = user.getLastName();
        // שם משפחה.

        String initials = "";
        // כאן נשמור ראשי תיבות.

        if (!firstName.isEmpty()) {
            initials += firstName.charAt(0);
            // מוסיף אות ראשונה של שם פרטי.
        }

        if (!lastName.isEmpty()) {
            initials += lastName.charAt(0);
            // מוסיף אות ראשונה של שם משפחה.
        }

        if (initials.isEmpty() && !user.getEmail().isEmpty()) {
            initials += user.getEmail().charAt(0);
            // אם אין שם בכלל, משתמשים באות הראשונה של האימייל.
        }

        return initials.toUpperCase();
        // מחזיר ראשי תיבות באות גדולה.
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvPhone, tvInitials;
        // רכיבי הטקסט של שורת משתמש.

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // מחבר את השורה ל-ViewHolder.

            tvName = itemView.findViewById(R.id.tv_user_name);
            // שם המשתמש.

            tvEmail = itemView.findViewById(R.id.tv_user_email);
            // אימייל המשתמש.

            tvPhone = itemView.findViewById(R.id.tv_user_phone);
            // טלפון המשתמש.

            tvInitials = itemView.findViewById(R.id.tv_user_initials);
            // ראשי תיבות בעיגול.
        }
    }
}