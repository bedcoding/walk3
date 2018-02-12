package com.example.ggavi.registeration.lee2;

public class BTSmsNumber {
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setSmsNum1(String smsNum1) {
        this.smsNum1 = smsNum1;
    }

    private String numsName;
    private String userID;
    private String smsNum1;
    private String smsText;

    public String getNumsName() {
        return numsName;
    }

    public void setNumsName(String numsName) {
        this.numsName = numsName;
    }



    public String getUserID() {
        return userID;
    }

    public String getSmsNum1() {
        return smsNum1;
    }



    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }



    public BTSmsNumber(String userID, String smsNum1, String numsName,String smsText){
        this.userID = userID;
        this.smsNum1 = smsNum1;
        this.numsName = numsName;
        this.smsNum1 = smsText;
    }

}
