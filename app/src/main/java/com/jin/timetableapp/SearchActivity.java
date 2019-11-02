package com.jin.timetableapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JIN on 10/30/2019.
 */

public class SearchActivity extends AppCompatActivity {
    private long lastTimeButtonPressed;

    LinearLayout ll_search;
    EditText et_search;

    String[][] search_results;
    ArrayList<Object[]> blocks;
    AlertDialog add_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ll_search = (LinearLayout) findViewById(R.id.layout_search);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showResult(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        blocks = TimeTableActivity.blocks;

        init(0, null);
    }

    private void init(int mode, String value) {
        String result = API.getLectures(mode, value);
        JSONObject jsonObject = API.getJsonInfo(result);
        try {
            ll_search.removeAllViews();
            int count = jsonObject.getInt("Count");
            search_results = new String[count][8];
            addInfoPanel(count);
            JSONArray jo = jsonObject.getJSONArray("Items");
            for (int i = 0; i < jo.length(); i++) {
                if (i == 50) {
                    // 최대 50개까지 검색 결과를 나타냅니다.
                    break;
                }
                search_results[i][5] = jo.getJSONObject(i).getString("start_time");
                search_results[i][6] = jo.getJSONObject(i).getString("end_time");
                search_results[i][7] = "";
                String time = search_results[i][5] + " - " + search_results[i][6] + " |";
                for (int j = jo.getJSONObject(i).getJSONArray("dayofweek").length() - 1; j >= 0; j--) {
                    time += " (" + jo.getJSONObject(i).getJSONArray("dayofweek").getString(j) + "),";
                    search_results[i][7] += jo.getJSONObject(i).getJSONArray("dayofweek").getString(j) + ",";
                }
                time = time.replace("화), (월", "월), (화");
                search_results[i][7] = search_results[i][7].substring(0, search_results[i][7].length() - 1);

                addPanel(i, jo.getJSONObject(i).getString("lecture"), time.substring(0, time.length() - 1), jo.getJSONObject(i).getString("code"),
                        jo.getJSONObject(i).getString("professor"), jo.getJSONObject(i).getString("location"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addInfoPanel(int count) {
        String msg;
        if (count == 0) {
            msg = "검색결과가 없습니다.";
        } else {
            msg = count + "개의 검색결과가 있습니다.";
        }

        LinearLayout.LayoutParams params_info = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params_info.setMargins((int) getResources().getDimension(R.dimen.default_margin), 0,
                (int) getResources().getDimension(R.dimen.default_margin), (int) getResources().getDimension(R.dimen.default_margin));

        TextView tv_info = new TextView(this);
        tv_info.setLayoutParams(params_info);
        tv_info.setText(msg);
        tv_info.setTextColor(getResources().getColor(R.color.GRAY));
        tv_info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv_info.setGravity(Gravity.CENTER);

        ll_search.addView(tv_info);
    }

    private void addPanel(int idx, String title, String time, String code, String prof, String loc) {
        search_results[idx][0] = title;
        search_results[idx][1] = time;
        search_results[idx][2] = "교과목 코드 : " + code;
        search_results[idx][3] = "담당 교수 : " + prof;
        search_results[idx][4] = "강의실 : " + loc;

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) getResources().getDimension(R.dimen.default_margin), 0,
                (int) getResources().getDimension(R.dimen.default_margin), (int) getResources().getDimension(R.dimen.default_margin));

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins((int) getResources().getDimension(R.dimen.searchbox_margin), (int) getResources().getDimension(R.dimen.searchbox_margin),
                (int) getResources().getDimension(R.dimen.searchbox_margin), (int) getResources().getDimension(R.dimen.searchbox_margin));

        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params3.setMargins((int) getResources().getDimension(R.dimen.searchbox_margin), 0,
                (int) getResources().getDimension(R.dimen.searchbox_margin), (int) getResources().getDimension(R.dimen.searchbox_margin));

        LinearLayout ll_box = new LinearLayout(this);
        ll_box.setId(1000000 + idx);
        ll_box.setLayoutParams(params1);
        ll_box.setBackgroundResource(android.R.drawable.editbox_background_normal);
        ll_box.setOrientation(LinearLayout.VERTICAL);
        ll_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - lastTimeButtonPressed > 1000) {
                    View digView = View.inflate(SearchActivity.this, R.layout.content_add_lecture, null);
                    TextView tv_digTitle = digView.findViewById(R.id.search_title);
                    tv_digTitle.setText(search_results[view.getId() - (int) 1000000][0]);
                    TextView tv_digTime = digView.findViewById(R.id.search_time);
                    tv_digTime.setText(search_results[view.getId() - (int) 1000000][1]);
                    TextView tv_digCode = digView.findViewById(R.id.search_code);
                    tv_digCode.setText(search_results[view.getId() - (int) 1000000][2]);
                    TextView tv_digProf = digView.findViewById(R.id.search_prof);
                    tv_digProf.setText(search_results[view.getId() - (int) 1000000][3]);
                    TextView tv_digLoc = digView.findViewById(R.id.search_loc);
                    tv_digLoc.setText(search_results[view.getId() - (int) 1000000][4]);
                    TextView tv_digAdd = digView.findViewById(R.id.search_add);
                    tv_digAdd.setId((int) 100000 + view.getId() - (int) 1000000);
                    tv_digAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                String[] startTime = search_results[view.getId() - (int) 100000][5].split(":");
                                String[] endTime = search_results[view.getId() - (int) 100000][6].split(":");
                                float weight1 = (Float.valueOf(startTime[0]) - 9f) + Float.valueOf(startTime[1]) / 60f;
                                float weight2 = (Float.valueOf(endTime[0]) - Float.valueOf(startTime[0])) + (Float.valueOf(endTime[1]) - Float.valueOf(startTime[1])) / 60f;
                                boolean flag = true;
                                String[] dayAt = search_results[view.getId() - (int) 100000][7].split(",");
                                for (Object[] block : blocks) {
                                    for (String day : dayAt) {
                                        int day_idx = 0;
                                        switch (day) {
                                            case "월":
                                                day_idx = 0;
                                                break;
                                            case "화":
                                                day_idx = 1;
                                                break;
                                            case "수":
                                                day_idx = 2;
                                                break;
                                            case "목":
                                                day_idx = 3;
                                                break;
                                            case "금":
                                                day_idx = 4;
                                                break;
                                        }
                                        if ((int) block[2] == day_idx && !((float) block[7] >= weight1 + weight2 ||
                                                ((float) block[7] + (float) block[8]) <= weight1)) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (!flag) {
                                        break;
                                    }
                                }

                                if (flag) {
                                    API.postTimetable(search_results[view.getId() - (int) 100000][2].substring(9));
                                    TimeTableActivity.TTActivity.finish();
                                    new AlertDialog.Builder(SearchActivity.this)
                                            .setTitle(search_results[view.getId() - (int) 100000][0])
                                            .setMessage("추가되었습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                                        add_dialog.cancel();
                                                        startActivity(new Intent(SearchActivity.this, TimeTableActivity.class));
                                                        SearchActivity.this.finish();

                                                        lastTimeButtonPressed = System.currentTimeMillis();
                                                    }
                                                }
                                            })
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialogInterface) {
                                                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                                        add_dialog.cancel();
                                                        startActivity(new Intent(SearchActivity.this, TimeTableActivity.class));
                                                        SearchActivity.this.finish();

                                                        lastTimeButtonPressed = System.currentTimeMillis();
                                                    }
                                                }
                                            })
                                            .show();
                                } else {
                                    new AlertDialog.Builder(SearchActivity.this)
                                            .setTitle(search_results[view.getId() - (int) 100000][0])
                                            .setMessage("시간표에 시간이 겹치는 과목이 있어\n추가할 수 없습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                                        add_dialog.cancel();

                                                        lastTimeButtonPressed = System.currentTimeMillis();
                                                    }
                                                }
                                            })
                                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialogInterface) {
                                                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                                        add_dialog.cancel();

                                                        lastTimeButtonPressed = System.currentTimeMillis();
                                                    }
                                                }
                                            })
                                            .show();
                                }

                                lastTimeButtonPressed = System.currentTimeMillis();
                            }
                        }
                    });

                    add_dialog = new AlertDialog.Builder(SearchActivity.this)
                            .setView(digView)
                            .show();

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
            }
        });

        TextView tv_title = new TextView(this);
        tv_title.setLayoutParams(params2);
        tv_title.setText(search_results[idx][0]);
        tv_title.setTextColor(getResources().getColor(R.color.THEME));
        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tv_title.setTypeface(tv_title.getTypeface(), Typeface.BOLD);

        TextView tv_time = new TextView(this);
        tv_time.setLayoutParams(params3);
        tv_time.setText(search_results[idx][1]);
        tv_time.setTextColor(getResources().getColor(R.color.BLACK));
        tv_time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

        TextView tv_code = new TextView(this);
        tv_code.setLayoutParams(params3);
        tv_code.setText(search_results[idx][2]);
        tv_code.setTextColor(getResources().getColor(R.color.GRAY));
        tv_code.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        TextView tv_prof = new TextView(this);
        tv_prof.setLayoutParams(params3);
        tv_prof.setText(search_results[idx][3]);
        tv_prof.setTextColor(getResources().getColor(R.color.GRAY));
        tv_prof.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        TextView tv_place = new TextView(this);
        tv_place.setLayoutParams(params3);
        tv_place.setText(search_results[idx][4]);
        tv_place.setTextColor(getResources().getColor(R.color.GRAY));
        tv_place.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        ll_box.addView(tv_title);
        ll_box.addView(tv_time);
        ll_box.addView(tv_code);
        ll_box.addView(tv_prof);
        ll_box.addView(tv_place);
        ll_search.addView(ll_box);
    }

    private void showResult(CharSequence cs) {
        if (et_search.getText().toString().equals("")) {
            init(0, null);
        } else {
            init(2, cs.toString());
        }
    }
}
