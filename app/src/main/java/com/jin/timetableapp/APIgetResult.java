package com.jin.timetableapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by JIN on 10/31/2019.
 */

public class APIgetResult extends AsyncTask<Void, Void, String> {
    HttpsURLConnection apiConn;

    APIgetResult(HttpsURLConnection apiConn) {
        this.apiConn = apiConn;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(apiConn.getInputStream(), "utf-8"));
            String result = "";
            while (true) {
                String buf = reader.readLine();
                if (buf == null) {
                    break;
                }
                result += buf;
            }
            reader.close();
            return result;

        } catch (IOException e) {
            return API.getMessage();
        }
    }

    private JSONArray getJsonArray(String jsonText) {
        JSONArray result;
        try {
            JSONArray jsonArray = new JSONArray(jsonText);
            result = jsonArray;
        } catch (JSONException e) {
            result = null;
        }

        return result;
    }
}
