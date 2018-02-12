package com.example.ggavi.registeration.lee1;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2018-02-05.
 */

public class smsDB_Manager extends StringRequest {

    final static private String URL = "http://ggavi2000.cafe24.com/smsAdd.php";
    private Map<String, String> parameters;

    public smsDB_Manager(String userID, String smsNum1, String numsName, String smsText, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("smsNum1", smsNum1);
        parameters.put("numsName",numsName);
        parameters.put("smsText",smsText);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
