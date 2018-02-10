package com.example.ggavi.registeration.lee2;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2018-02-07.
 */

public class SmsNumSelectManager extends StringRequest {

    final static private String URL = "http://ggavi2000.cafe24.com/smsList.php";
    private Map<String, String> parameters;

    public SmsNumSelectManager(String userID, String smsNum1, String numsName, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("smsNum1", smsNum1);
        parameters.put("numsName",numsName);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
