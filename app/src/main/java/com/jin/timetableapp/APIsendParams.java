package com.jin.timetableapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by JIN on 10/31/2019.
 */

public class APIsendParams extends AsyncTask<Void, Void, Void> {
    HttpsURLConnection apiConn;
    String params;

    APIsendParams(HttpsURLConnection apiConn, String params) {
        this.apiConn = apiConn;
        this.params = params;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            apiConn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(apiConn.getOutputStream(), "utf-8");
            writer.write(params);
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }
        return null;
    }
}
