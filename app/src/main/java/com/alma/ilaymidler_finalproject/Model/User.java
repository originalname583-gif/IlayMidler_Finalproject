package com.alma.ilaymidler_finalproject.Model;

public class User {

    private String id;
    // מזהה המשתמש ב-Firebase.

    private String fname;
    // שם פרטי כמו שנשמר אצלך במסד.

    private String lname;
    // שם משפחה כמו שנשמר אצלך במסד.

    private String firstName;
    // תמיכה בשם נוסף למקרה שמשתמשים נשמרו בשם firstName.

    private String lastName;
    // תמיכה בשם נוסף למקרה שמשתמשים נשמרו בשם lastName.

    private String email;
    // אימייל המשתמש.

    private String phone;
    // טלפון המשתמש.

    private String password;
    // סיסמה.

    private boolean isAdmin;
    // האם המשתמש מנהל.

    public User() {
        // Firebase צריך בנאי ריק.
    }

    public User(String id, String firstName, String lastName, String email, String phone, String password, boolean isAdmin) {
        this.id = id;
        this.fname = firstName;
        this.lname = lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
        // שומר את השם גם כ-fname/lname וגם כ-firstName/lastName כדי שלא יהיו בעיות תאימות.
    }

    public String getId() {
        return id != null ? id : "";
        // מחזיר id בצורה בטוחה.
    }

    public void setId(String id) {
        this.id = id;
        // מעדכן id.
    }

    public String getFirstName() {
        if (firstName != null && !firstName.trim().isEmpty()) {
            return firstName.trim();
        }

        if (fname != null && !fname.trim().isEmpty()) {
            return fname.trim();
        }

        return "";
        // מחזיר שם פרטי גם אם הוא נשמר כ-firstName וגם אם הוא נשמר כ-fname.
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.fname = firstName;
        // מעדכן את שני השדות כדי לשמור תאימות.
    }

    public String getLastName() {
        if (lastName != null && !lastName.trim().isEmpty()) {
            return lastName.trim();
        }

        if (lname != null && !lname.trim().isEmpty()) {
            return lname.trim();
        }

        return "";
        // מחזיר שם משפחה גם אם הוא נשמר כ-lastName וגם אם הוא נשמר כ-lname.
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.lname = lastName;
        // מעדכן את שני השדות.
    }

    public String getFname() {
        return getFirstName();
        // מחזיר שם פרטי.
    }

    public void setFname(String fname) {
        this.fname = fname;
        this.firstName = fname;
        // חשוב ל-Firebase אם השדה נקרא fname.
    }

    public String getLname() {
        return getLastName();
        // מחזיר שם משפחה.
    }

    public void setLname(String lname) {
        this.lname = lname;
        this.lastName = lname;
        // חשוב ל-Firebase אם השדה נקרא lname.
    }

    public String getFullName() {
        return (getFirstName() + " " + getLastName()).trim();
        // מחזיר שם מלא.
    }

    public String getEmail() {
        return email != null ? email : "";
        // מחזיר אימייל.
    }

    public void setEmail(String email) {
        this.email = email;
        // מעדכן אימייל.
    }

    public String getPhone() {
        if (phone == null) return "";
        return phone.replace("\"", "").trim();
        // מחזיר טלפון נקי.
    }

    public void setPhone(String phone) {
        this.phone = phone;
        // מעדכן טלפון.
    }

    public String getPassword() {
        return password != null ? password : "";
        // מחזיר סיסמה.
    }

    public void setPassword(String password) {
        this.password = password;
        // מעדכן סיסמה.
    }

    public boolean isAdmin() {
        return isAdmin;
        // מחזיר האם המשתמש מנהל.
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        // מעדכן הרשאת מנהל.
    }
}