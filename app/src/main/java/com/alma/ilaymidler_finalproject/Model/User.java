package com.alma.ilaymidler_finalproject.Model;

public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private boolean isAdmin;

    public User() {
    }

    public User(String id, String firstName, String lastName, String email, String phone, String password, boolean isAdmin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName != null ? firstName : ""; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName != null ? lastName : ""; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email != null ? email : ""; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone != null ? phone : ""; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password != null ? password : ""; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}