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
        void onLongUserClick(User user);
    }

    private final List<User> userList = new ArrayList<>();
    private final OnUserClickListener listener;

    public UserAdapter(@Nullable OnUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String email = user.getEmail() != null ? user.getEmail() : "";
        String phone = user.getPhone() != null ? user.getPhone() : "";

        holder.tvName.setText((firstName + " " + lastName).trim());
        holder.tvEmail.setText(email);
        holder.tvPhone.setText(phone);

        String initials = "";
        if (!firstName.isEmpty()) initials += firstName.charAt(0);
        if (!lastName.isEmpty()) initials += lastName.charAt(0);

        holder.tvInitials.setText(initials.toUpperCase());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onUserClick(user);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongUserClick(user);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> users) {
        userList.clear();
        if (users != null) {
            userList.addAll(users);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvInitials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvPhone = itemView.findViewById(R.id.tv_user_phone);
            tvInitials = itemView.findViewById(R.id.tv_user_initials);
        }
    }
}