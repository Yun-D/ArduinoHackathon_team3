package com.example.arduinohackathon_team3.Bean;

import java.io.Serializable;

public class MemberBean implements Serializable {
    public String memId;
    public String memName;
    public String memNum;
    public boolean isAdmin = false;
}
