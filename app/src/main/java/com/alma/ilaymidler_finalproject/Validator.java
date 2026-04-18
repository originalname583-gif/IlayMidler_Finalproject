package com.alma.ilaymidler_finalproject;

import com.google.firebase.auth.FirebaseAuth;

public class Validator {

    private final FirebaseAuth mAuth;

    public interface LoginCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public Validator() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static boolean isEmailValid(String email) {
        return email != null && !email.trim().isEmpty();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty();
    }

    public void loginUser(String email, String password, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";
                        callback.onFailure(message);
                    }
                });
    }
}