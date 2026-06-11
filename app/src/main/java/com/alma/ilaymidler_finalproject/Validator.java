package com.alma.ilaymidler_finalproject;

import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;

public class Validator {

    private final FirebaseAuth mAuth;
    // אובייקט שאחראי על התחברות והרשמה ב-Firebase.

    public interface LoginCallback {

        void onSuccess();
        // הפונקציה מופעלת כאשר ההתחברות הצליחה.

        void onFailure(String errorMessage);
        // הפונקציה מופעלת כאשר ההתחברות נכשלה.
    }

    public Validator() {
        mAuth = FirebaseAuth.getInstance();
        // מקבל מופע של FirebaseAuth.
    }

    public static boolean isEmailValid(String email) {
        // בודק האם האימייל תקין.

        return email != null
                && !email.trim().isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        // מחזיר true רק אם האימייל לא ריק ובפורמט תקין.
    }

    public static boolean isPasswordValid(String password) {
        // בודק האם הסיסמה תקינה.

        return password != null
                && password.trim().length() >= 6;
        // Firebase דורש לפחות 6 תווים.
    }

    public void loginUser(String email,
                          String password,
                          LoginCallback callback) {
        // הפונקציה מבצעת התחברות למשתמש.

        if (callback == null) {
            return;
            // אם לא נשלח callback אין למי להחזיר תשובה.
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        callback.onSuccess();
                        // אם ההתחברות הצליחה מפעילים הצלחה.
                    } else {

                        String message;

                        if (task.getException() != null) {
                            message = task.getException().getMessage();
                            // לוקחים את הודעת השגיאה של Firebase.
                        } else {
                            message = "Login failed";
                            // הודעת ברירת מחדל.
                        }

                        callback.onFailure(message);
                        // מחזירים את השגיאה למסך.
                    }
                });
    }
}