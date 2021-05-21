package com.olliesafety2.simpleapplocker.admin_side.user;

public class Users {
    private String Name;
    private String Email;
    private String UID;
    private String PhoneNum;
    private Double WeeklySpeed;
    private Double MonthlySpeed;
    private double Latitude;
    private double Longitude;
    private String Status;

    public Users() {
    }



    public Users(String Name, String Email, String PhoneNum,Double WeeklySpeed, Double MonthlySpeed, String Status) {
        this.Name = Name;
        this.Email = Email;
        this.PhoneNum=PhoneNum;
        this.WeeklySpeed = WeeklySpeed;
        this.MonthlySpeed = MonthlySpeed;
        this.Status = Status;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID=UID;
    }

    public String getName() {
        return Name;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public String getEmail() {
        return Email;
    }

    public Double getWeeklySpeed() {
        return WeeklySpeed;
    }

    public Double getMonthlySpeed() {
        return MonthlySpeed;
    }

    public String getStatus() {
        return Status;
    }


}
