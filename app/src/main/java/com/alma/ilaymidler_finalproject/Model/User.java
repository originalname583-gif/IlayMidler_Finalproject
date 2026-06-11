package com.alma.ilaymidler_finalproject.Model;

public class User {

    private String id;
    // מזהה ייחודי של המשתמש ב-Firebase.

    private String fname;
    // השם הפרטי של המשתמש.

    private String lname;
    // שם המשפחה של המשתמש.

    private String email;
    // כתובת האימייל של המשתמש.

    private String phone;
    // מספר הטלפון של המשתמש.

    private String password;
    // סיסמת המשתמש.

    private boolean isAdmin;
    // קובע האם המשתמש הוא מנהל או משתמש רגיל.

    public User() {
        // בנאי ריק.
        // Firebase חייב אותו כדי ליצור אובייקט בעת טעינת נתונים מהמסד.
    }

    public User(String id,
                String firstName,
                String lastName,
                String email,
                String phone,
                String password,
                boolean isAdmin) {

        this.id = id;
        // שומר את מזהה המשתמש.

        this.fname = firstName;
        // שומר את השם הפרטי.

        this.lname = lastName;
        // שומר את שם המשפחה.

        this.email = email;
        // שומר את האימייל.

        this.phone = phone;
        // שומר את הטלפון.

        this.password = password;
        // שומר את הסיסמה.

        this.isAdmin = isAdmin;
        // שומר האם המשתמש הוא מנהל.
    }

    public String getId() {

        return id != null ? id : "";
        // מחזיר את מזהה המשתמש.
        // אם הערך null מחזיר מחרוזת ריקה.
    }

    public void setId(String id) {

        this.id = id;
        // מעדכן את מזהה המשתמש.
    }

    public String getFirstName() {

        return fname != null ? fname : "";
        // מחזיר את השם הפרטי.
    }

    public void setFirstName(String firstName) {

        this.fname = firstName;
        // מעדכן את השם הפרטי.
    }

    public String getLastName() {

        return lname != null ? lname : "";
        // מחזיר את שם המשפחה.
    }

    public void setLastName(String lastName) {

        this.lname = lastName;
        // מעדכן את שם המשפחה.
    }

    public String getEmail() {

        return email != null ? email : "";
        // מחזיר את האימייל.
    }

    public void setEmail(String email) {

        this.email = email;
        // מעדכן את האימייל.
    }

    public String getPhone() {

        if (phone == null) {
            return "";
            // אם הטלפון לא קיים מחזירים מחרוזת ריקה.
        }

        return phone.replace("\"", "").trim();
        // מנקה גרשיים ורווחים מיותרים מהטלפון ומחזיר אותו.
    }

    public void setPhone(String phone) {

        this.phone = phone;
        // מעדכן את מספר הטלפון.
    }

    public String getPassword() {

        return password != null ? password : "";
        // מחזיר את הסיסמה.
    }

    public void setPassword(String password) {

        this.password = password;
        // מעדכן את הסיסמה.
    }

    public boolean isAdmin() {

        return isAdmin;
        // מחזיר האם המשתמש הוא מנהל.
        // true = מנהל
        // false = משתמש רגיל
    }

    public void setAdmin(boolean admin) {

        isAdmin = admin;
        // מעדכן את הרשאת המנהל של המשתמש.
    }

    @Override
    public String toString() {

        return "User{" +
                "id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
        // מחזיר תיאור טקסטואלי של המשתמש.
        // שימושי ל-Log, Debug ובדיקות.
    }
}