package com.example.qrscanner;

public class Attendance {
    private String studentName;
    private String teamName;
    private String idNumber;
    private String course;
    private String timeIn;
    private String timeOut;

    public Attendance(String studentName, String teamName, String idNumber, String course, String timeIn, String timeOut) {
        this.studentName = studentName;
        this.teamName = teamName;
        this.idNumber = idNumber;
        this.course = course;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getCourse() {
        return course;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }
}
