package com.jin.timetableapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

class API {
    final static String TOKEN = "******************************";
    final static String baseURL = "https://k03c8j1o5a.execute-api.ap-northeast-2.amazonaws.com/v1/programmers";
    final static String KEY = "******************************";
    final static String CONTENT_TYPE = "application/json";
    static URL apiUrl;
    static HttpsURLConnection apiConn;
    static String parameters;

    public static String getLectures(int mode, String value) {
        try {
            if (mode == 1) {
                apiUrl = new URL(baseURL + "/lectures?code=" + value);
            } else if (mode == 2) {
                apiUrl = new URL(baseURL + "/lectures?lecture=" + URLEncoder.encode(value, "utf-8"));
            } else {
                apiUrl = new URL(baseURL + "/lectures");
            }
            connect("GET");
            return new APIgetResult(apiConn).execute().get();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return getMessage();
        }
    }

    public static String getTimetable() {
        try {
            apiUrl = new URL(baseURL + "/timetable?user_key=" + TOKEN);
            connect("GET");
            return new APIgetResult(apiConn).execute().get();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return getMessage();
        }
    }

    public static String getMemo(int mode, String code) {
        try {
            if (mode == 1) {
                apiUrl = new URL(baseURL + "/memo?user_key=" + TOKEN + "&code=" + code);
            } else {
                apiUrl = new URL(baseURL + "/memo?user_key=" + TOKEN);
            }
            connect("GET");
            return new APIgetResult(apiConn).execute().get();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return getMessage();
        }
    }

    public static String postTimetable(String code) {
        try {
            apiUrl = new URL(baseURL + "/timetable");
            connect("POST");
            parameters = "{\"user_key\":\"" + TOKEN + "\",\"code\":\"" + code + "\"}";
            new APIsendParams(apiConn, parameters).execute();
            return new APIgetResult(apiConn).execute().get();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return getMessage();
        }
    }

    public static String deleteTimetable(String code) {
        try {
            apiUrl = new URL(baseURL + "/timetable");
            connect("DELETE");
            parameters = "{\"user_key\":\"" + TOKEN + "\",\"code\":\"" + code + "\"}";
            new APIsendParams(apiConn, parameters).execute();
            return new APIgetResult(apiConn).execute().get();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return getMessage();
        }
    }

    public static String postMemo(String code, String type, String title, String description, String date) {
        try {
            apiUrl = new URL(baseURL + "/memo");
            connect("POST");
            parameters = "{\"user_key\":\"" + TOKEN + "\",\"code\":\"" + code + "\",\"type\":\"" + type
                    + "\",\"title\":\"" + title + "\", \"description\" : \"" + description + "\", \"date\" : \"" + date + "\"}";
            new APIsendParams(apiConn, parameters).execute();
            return new APIgetResult(apiConn).execute().get();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return getMessage();
        }
    }

    public static String deleteMemo(String code, String type) {
        try {
            apiUrl = new URL(baseURL + "/memo");
            connect("DELETE");
            parameters = "{\"user_key\":\"" + TOKEN + "\",\"code\":\"" + code + "\",\"type\":\"" + type + "\"}";
            new APIsendParams(apiConn, parameters).execute();
            return new APIgetResult(apiConn).execute().get();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return getMessage();
        }
    }

    private static void connect(String method) throws IOException {
        apiConn = (HttpsURLConnection) apiUrl.openConnection();
        apiConn.setRequestProperty("x-api-key", KEY);
        apiConn.setRequestProperty("Content-type", CONTENT_TYPE);
        apiConn.setRequestMethod(method);
    }

    public static String getMessage() {
        int status;
        try {
            status = apiConn.getResponseCode();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            status = 0;
        }
        String result;
        switch (status) {
            case 500:
                result = "서버에 문제가 있습니다.\n잠시 후 다시 시도해주세요.";
                break;
            default:
                result = String.valueOf(status);
        }

        return result;
    }

    public static JSONObject getJsonInfo(String jsonText) {
        try {
            return new JSONObject(jsonText);
        } catch (JSONException e) {
            return null;
        }

    }
}
