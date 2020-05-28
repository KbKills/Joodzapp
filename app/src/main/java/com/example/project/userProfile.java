package com.example.project;

public class userProfile
{
    public String userEmail, userAge, userPhone, userUserName;

    public userProfile()
    { }

    public userProfile(String email, String age, String phone, String userName) {
        userEmail = email;
        userAge = age;
        userPhone = phone;
        userUserName = userName;
    }


    public String getEmail() { return userEmail; }

    public void setEmail(String email) {
        userEmail = email;
    }

    public String getAge() {
        return userAge;
    }

    public void setAge(String age) {
        userAge = age;
    }

    public String getPhone() {
        return userPhone;
    }

    public void setPhone(String phone) {
        userPhone = phone;
    }

    public String getUserName() {
        return userUserName;
    }

    public void setUserName(String userName) {
        userUserName = userName;
    }
}
