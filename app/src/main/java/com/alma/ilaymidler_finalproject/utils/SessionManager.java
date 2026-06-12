package com.alma.ilaymidler_finalproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alma.ilaymidler_finalproject.Model.User;

public class SessionManager {

    private static final String PREF_NAME = "MyPrefs";
    // שם קובץ ה-SharedPreferences שבו נשמור את הנתונים.

    private static final String KEY_UID = "uid";
    // מפתח לשמירת מזהה המשתמש.

    private static final String KEY_FIRST_NAME = "firstName";
    // מפתח לשמירת השם הפרטי.

    private static final String KEY_LAST_NAME = "lastName";
    // מפתח לשמירת שם המשפחה.

    private static final String KEY_EMAIL = "email";
    // מפתח לשמירת האימייל.

    private static final String KEY_PHONE = "phone";
    // מפתח לשמירת מספר הטלפון.

    private static final String KEY_IS_ADMIN = "isAdmin";
    // מפתח לשמירת האם המשתמש מנהל.

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    // מפתח לשמירת מצב התחברות.

    public static void saveUser(Context context, User user) {
        // הפונקציה שומרת את כל פרטי המשתמש בזיכרון המקומי של הטלפון.
        // משתמשים בה אחרי Login או Register.

        if (context == null || user == null) {
            return;
            // אם אין Context או User לא ניתן לשמור.
        }

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // פותח את קובץ השמירה המקומי.

        SharedPreferences.Editor editor = prefs.edit();
        // מאפשר לערוך את הנתונים.

        editor.putString(KEY_UID, user.getId());
        // שומר את מזהה המשתמש.

        editor.putString(KEY_FIRST_NAME, user.getFirstName());
        // שומר את השם הפרטי.

        editor.putString(KEY_LAST_NAME, user.getLastName());
        // שומר את שם המשפחה.

        editor.putString(KEY_EMAIL, user.getEmail());
        // שומר את האימייל.

        editor.putString(KEY_PHONE, user.getPhone());
        // שומר את מספר הטלפון.

        editor.putBoolean(KEY_IS_ADMIN, user.isAdmin());
        // שומר האם המשתמש הוא מנהל.

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        // שומר שהמשתמש מחובר.

        editor.apply();
        // מבצע את השמירה בפועל.
    }

    public static String getUid(Context context) {
        // מחזיר את מזהה המשתמש ששמור בטלפון.

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_UID, "");
        // אם אין uid מחזיר טקסט ריק.
    }

    public static String getFirstName(Context context) {
        // מחזיר את השם הפרטי ששמור בטלפון.

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_FIRST_NAME, "");
        // אם אין שם מחזיר טקסט ריק.
    }

    public static String getLastName(Context context) {
        // מחזיר את שם המשפחה ששמור בטלפון.

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LAST_NAME, "");
        // אם אין שם משפחה מחזיר טקסט ריק.
    }

    public static String getEmail(Context context) {
        // מחזיר את האימייל ששמור בטלפון.

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_EMAIL, "");
        // אם אין אימייל מחזיר טקסט ריק.
    }

    public static String getPhone(Context context) {
        // מחזיר את הטלפון ששמור בטלפון.

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_PHONE, "");
        // אם אין טלפון מחזיר טקסט ריק.
    }

    public static boolean isAdmin(Context context) {
        // מחזיר האם המשתמש ששמור הוא מנהל.

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_IS_ADMIN, false);
        // אם אין מידע מחזיר false.
    }

    public static boolean isLoggedIn(Context context) {
        // מחזיר האם המשתמש נשמר כמחובר.

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_IS_LOGGED_IN, false);
        // אם אין מידע מחזיר false.
    }

    public static void clearSession(Context context) {
        // הפונקציה מוחקת את כל הנתונים של המשתמש מהטלפון.
        // משתמשים בה בזמן Logout.

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // פותח את קובץ השמירה.

        prefs.edit()
                .clear()
                .apply();
        // מוחק את כל הנתונים שנשמרו.
    }
}