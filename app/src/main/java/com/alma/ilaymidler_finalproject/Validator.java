package com.alma.ilaymidler_finalproject;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

public class Validator {

    private FirebaseAuth mAuth;

    public interface LoginCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public Validator() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Check field not empty
    public static boolean isEmailValid(String email) {
        return email != null && !email.trim().isEmpty();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty();
    }

    // Firebase login (checks if account exists!)
    public void loginUser(String email, String password, LoginCallback callback) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();  // email + password match an account
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }
}
