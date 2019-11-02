package com.jin.timetableapp;

/**
 * Created by JIN on 10/28/2019.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimeTableActivity extends AppCompatActivity {
    private Toast toast_back;
    private long lastTimeBackPressed;
    private long lastTimeButtonPressed;

    Toolbar toolbar;

    TextView tv_month, tv_prev, tv_today, tv_next, tv_digInfo, tv_digBtn,
            tv_digHomework, tv_digHomeworkContent, tv_digExam, tv_digExamContent, tv_digStudy, tv_digStudyContent;
    TextView[] tv_days;
    FrameLayout[] fl_days;
    LinearLayout ll_digWriteMemo;
    EditText et_digTitle, et_digDescription;
    RelativeLayout rl_digHomework, rl_digExam, rl_digStudy;
    RadioGroup rg_digType;
    Intent intent;

    SimpleDateFormat formatter_month, formatter_date, formatter_pointer;
    Calendar cal, cal_block;
    String cal_month, cal_pointer, week_pointer, detail_title, detail_code, memo_type;
    String[] days;
    int day_pointer, block_idx;
    int[][] colour;
    public static ArrayList<Object[]> blocks;
    ArrayList<Object[]> memos;
    AlertDialog detail_dialog;
    boolean flag_homework, flag_exam, flag_study, flag_writeMemo;

    public static Activity TTActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        toolbar = (Toolbar) findViewById(R.id.toolbar_time_table);
        toolbar.setTitle(R.string.bar_timetable);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        toast_back = Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);

        tv_month = (TextView) findViewById(R.id.month);
        tv_prev = (TextView) findViewById(R.id.prev_week);
        tv_today = (TextView) findViewById(R.id.today);
        tv_next = (TextView) findViewById(R.id.next_week);
        tv_days = new TextView[5];
        tv_days[0] = (TextView) findViewById(R.id.mon);
        tv_days[1] = (TextView) findViewById(R.id.tue);
        tv_days[2] = (TextView) findViewById(R.id.wed);
        tv_days[3] = (TextView) findViewById(R.id.thu);
        tv_days[4] = (TextView) findViewById(R.id.fri);
        fl_days = new FrameLayout[5];
        fl_days[0] = (FrameLayout) findViewById(R.id.timetable_mon);
        fl_days[1] = (FrameLayout) findViewById(R.id.timetable_tue);
        fl_days[2] = (FrameLayout) findViewById(R.id.timetable_wed);
        fl_days[3] = (FrameLayout) findViewById(R.id.timetable_thu);
        fl_days[4] = (FrameLayout) findViewById(R.id.timetable_fri);

        cal = Calendar.getInstance();
        cal_block = Calendar.getInstance();
        formatter_month = new SimpleDateFormat("yyyy년 M월");
        formatter_date = new SimpleDateFormat("d");
        formatter_pointer = new SimpleDateFormat("yyyy-MM-dd");
        days = new String[5];
        days[0] = "Mon\n";
        days[1] = "Tue\n";
        days[2] = "Wed\n";
        days[3] = "Thu\n";
        days[4] = "Fri\n";

        colour = new int[7][3]; // count, back colour, text colour
        colour[0] = new int[]{0, R.color.GRAY_light, R.color.WHITE};
        colour[1] = new int[]{0, R.color.GREEN, R.color.WHITE};
        colour[2] = new int[]{0, R.color.BROWN, R.color.WHITE};
        colour[3] = new int[]{0, R.color.PURPLE, R.color.WHITE};
        colour[4] = new int[]{0, R.color.BLUE, R.color.WHITE};
        colour[5] = new int[]{0, R.color.RED, R.color.WHITE};
        colour[6] = new int[]{0, R.color.EMERALD, R.color.WHITE};

        blocks = new ArrayList<>();
        memos = new ArrayList<>();

        TTActivity = this;

        setTable();
        setToday();
    }

    private void setToday() {
        cal = Calendar.getInstance();
        cal_month = formatter_month.format(cal.getTime());
        tv_month.setText(cal_month);
        cal_pointer = formatter_pointer.format(cal.getTime());
        day_pointer = -1;
        for (int i = 0; i < 5; i++) {
            cal.set(Calendar.DAY_OF_WEEK, i + 2);
            tv_days[i].setText(days[i] + formatter_date.format(cal.getTime()));
            if (cal_pointer.equals(formatter_pointer.format(cal.getTime()))) {
                day_pointer = i;
                tv_days[i].setTextColor(getResources().getColor(R.color.THEME));
            } else {
                tv_days[i].setTextColor(getResources().getColor(R.color.BLACK));
            }
        }
        week_pointer = formatter_pointer.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, day_pointer + 2);
        setMemo();
    }

    private void moveWeek(int del) {
        cal.add(Calendar.DATE, del);
        for (int i = 0; i < 5; i++) {
            cal.set(Calendar.DAY_OF_WEEK, i + 2);
            tv_days[i].setText(days[i] + formatter_date.format(cal.getTime()));
            if (day_pointer == i) {
                cal_month = formatter_month.format(cal.getTime());
                tv_month.setText(cal_month);
            }
        }
        week_pointer = formatter_pointer.format(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, day_pointer + 2);
        setMemo();
    }

    private void selectDay(int day) {
        if (day_pointer != day) {
            if (day_pointer != -1) {
                tv_days[day_pointer].setTextColor(getResources().getColor(R.color.BLACK));
            }
            day_pointer = day;
            tv_days[day_pointer].setTextColor(getResources().getColor(R.color.THEME));
            cal.set(Calendar.DAY_OF_WEEK, day + 2);
            cal_month = formatter_month.format(cal.getTime());
            tv_month.setText(cal_month);
        }
    }

    private void setTable() {
        String result = API.getTimetable();
        JSONObject jsonObject = API.getJsonInfo(result);
        try {
            JSONArray jo = jsonObject.getJSONArray("Items");
            for (int i = 0; i < jo.length(); i++) {
                String result_lecture = API.getLectures(1, jo.getJSONObject(i).getString("lecture_code"));
                JSONObject jo_lecture = API.getJsonInfo(result_lecture).getJSONArray("Items").getJSONObject(0);

                String code = jo_lecture.getString("code");
                String[] startTime = jo_lecture.getString("start_time").split(":");
                String[] endTime = jo_lecture.getString("end_time").split(":");
                float weight1 = (Float.valueOf(startTime[0]) - 9f) + Float.valueOf(startTime[1]) / 60f;
                float weight2 = (Float.valueOf(endTime[0]) - Float.valueOf(startTime[0])) + (Float.valueOf(endTime[1]) - Float.valueOf(startTime[1])) / 60f;

                int colour_idx = 0;
                int colour_count = colour[0][0];
                for (int j = 0; j < colour.length; j++) {
                    if (colour[j][0] <= colour_count) {
                        colour_idx = j;
                    }
                }
                colour[colour_idx][0] += 1;

                String result_memo = API.getMemo(1, code);
                JSONArray jo_memo = API.getJsonInfo(result_memo).getJSONArray("Items");
                String memo_homework = null;
                String memo_exam = null;
                String memo_study = null;
                for (int j = 0; j < jo_memo.length(); j++) {
                    String type = jo_memo.getJSONObject(j).getString("type");
                    String[] date = jo_memo.getJSONObject(j).getString("date").split("-");
                    cal_block.set(Calendar.YEAR, Integer.valueOf(date[0]));
                    cal_block.set(Calendar.MONTH, Integer.valueOf(date[1]) - 1);
                    cal_block.set(Calendar.DATE, Integer.valueOf(date[2]));
                    cal_pointer = formatter_pointer.format(cal_block.getTime());
                    int pointer = 0;
                    for (int k = 0; k < 5; k++) {
                        cal_block.set(Calendar.DAY_OF_WEEK, k + 2);
                        if (cal_pointer.equals(formatter_pointer.format(cal_block.getTime()))) {
                            pointer = k;
                            break;
                        }
                    }
                    cal_block.set(Calendar.DAY_OF_WEEK, 6);
                    memos.add(new Object[]{formatter_pointer.format(cal_block.getTime()), pointer, code, type});
                    switch (type) {
                        case "HOMEWORK":
                            memo_homework = jo_memo.getJSONObject(j).getString("title");
                            break;
                        case "EXAM":
                            memo_exam = jo_memo.getJSONObject(j).getString("title");
                            break;
                        case "STUDY":
                            memo_study = jo_memo.getJSONObject(j).getString("title");
                            break;
                    }
                }

                JSONArray jo_day = jo_lecture.getJSONArray("dayofweek");
                for (int j = 0; j < jo_day.length(); j++) {
                    setBlock(code, jo_lecture.getString("lecture"), jo_lecture.getString("location"),
                            weight1, weight2, jo_day.getString(j), memo_homework, memo_exam, memo_study, colour_idx);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBlock(String code, String title, String location, float w1, float w2, String day, String homework, String exam, String study, int colour_idx) {
        int id = block_idx + 10_000_000;
        block_idx++;
        int dayAt = 0;

        View digView = View.inflate(this, R.layout.content_table_block, null);

        ImageView digMargin = digView.findViewById(R.id.block_margin);
        digMargin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, w1));

        LinearLayout digBlock = digView.findViewById(R.id.block_block);
        digBlock.setId(id);
        digBlock.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, w2));
        digBlock.setBackgroundColor(getResources().getColor(colour[colour_idx][1]));

        TextView digTitle = digView.findViewById(R.id.block_title);
        digTitle.setText(title);
        digTitle.setTextColor(getResources().getColor(colour[colour_idx][2]));

        TextView digLoc = digView.findViewById(R.id.block_loc);
        digLoc.setText(location);
        digLoc.setTextColor(getResources().getColor(colour[colour_idx][2]));

        TextView digHomework = digView.findViewById(R.id.block_homework);
        if (homework != null) {
            digHomework.setText("[과제] " + homework);
        }
        digHomework.setTextColor(getResources().getColor(colour[colour_idx][2]));
        digHomework.setVisibility(digView.GONE);

        TextView digExam = digView.findViewById(R.id.block_exam);
        if (exam != null) {
            digExam.setText("[시험] " + exam);
        }
        digExam.setTextColor(getResources().getColor(colour[colour_idx][2]));
        digExam.setVisibility(digView.GONE);

        TextView digStudy = digView.findViewById(R.id.block_study);
        if (study != null) {
            digStudy.setText("[스터디] " + study);
        }
        digStudy.setTextColor(getResources().getColor(colour[colour_idx][2]));
        digStudy.setVisibility(digView.GONE);

        switch (day) {
            case "월":
                fl_days[0].addView(digView);
                dayAt = 0;
                break;
            case "화":
                fl_days[1].addView(digView);
                dayAt = 1;
                break;
            case "수":
                fl_days[2].addView(digView);
                dayAt = 2;
                break;
            case "목":
                fl_days[3].addView(digView);
                dayAt = 3;
                break;
            case "금":
                fl_days[4].addView(digView);
                dayAt = 4;
                break;
        }

        blocks.add(new Object[]{code, id, dayAt, digView, digHomework, digExam, digStudy, w1, w2});
    }

    private void setMemo() {
        for (Object[] block : blocks) {
            ((TextView) block[4]).setVisibility(((View) block[3]).GONE);
            ((TextView) block[5]).setVisibility(((View) block[3]).GONE);
            ((TextView) block[6]).setVisibility(((View) block[3]).GONE);
            for (Object[] memo : memos) {
                if (memo[0].equals(week_pointer) && memo[1] == block[2] && memo[2] == block[0]) {
                    switch ((String) memo[3]) {
                        case "HOMEWORK":
                            ((TextView) block[4]).setVisibility(((View) block[3]).VISIBLE);
                            break;
                        case "EXAM":
                            ((TextView) block[5]).setVisibility(((View) block[3]).VISIBLE);
                            break;
                        case "STUDY":
                            ((TextView) block[6]).setVisibility(((View) block[3]).VISIBLE);
                            break;
                    }
                }
            }
        }
    }

    private void setDetailBox(Object[] block) {
        try {
            String result_lecture = API.getLectures(1, (String) block[0]);
            JSONObject jo_lecture = API.getJsonInfo(result_lecture).getJSONArray("Items").getJSONObject(0);

            String time = jo_lecture.getString("start_time") + " - " + jo_lecture.getString("end_time") + " |";
            for (int j = jo_lecture.getJSONArray("dayofweek").length() - 1; j >= 0; j--) {
                time += " (" + jo_lecture.getJSONArray("dayofweek").getString(j) + "),";
            }
            time = time.replace("화), (월", "월), (화");
            time = time.substring(0, time.length() - 1);
            detail_title = jo_lecture.getString("lecture");
            detail_code = (String) block[0];

            View digView = View.inflate(TimeTableActivity.this, R.layout.content_lecture_detail, null);
            final TextView tv_digTitle = digView.findViewById(R.id.lecture_title);
            tv_digTitle.setText(detail_title);
            ImageView iv_digDelete = digView.findViewById(R.id.lecture_delete);
            iv_digDelete.setId((int) block[1] - 1_000_000);
            iv_digDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                        new AlertDialog.Builder(TimeTableActivity.this)
                                .setTitle(detail_title)
                                .setMessage("해당 과목을 시간표에서\n삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                            API.deleteTimetable(detail_code);
                                            int idx1 = -1;
                                            int idx2 = -1;
                                            int count = 0;
                                            for (Object[] block : blocks) {
                                                if (block[0].equals(detail_code)) {
                                                    fl_days[(int) block[2]].removeView((View) block[3]);
                                                    if (idx1 == -1) {
                                                        idx1 = count;
                                                    } else {
                                                        idx2 = count;
                                                    }
                                                }
                                                count++;
                                            }
                                            if (idx2 != -1) {
                                                blocks.remove(idx2);
                                            }
                                            blocks.remove(idx1);

                                            detail_dialog.cancel();
                                            new AlertDialog.Builder(TimeTableActivity.this)
                                                    .setTitle(detail_title)
                                                    .setMessage("해당 과목을 시간표에서\n삭제하였습니다.")
                                                    .setPositiveButton("확인", null)
                                                    .show();

                                            lastTimeButtonPressed = System.currentTimeMillis();
                                        }
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();


                        lastTimeButtonPressed = System.currentTimeMillis();
                    }
                }
            });
            TextView tv_digTime = digView.findViewById(R.id.lecture_time);
            tv_digTime.setText(time);
            TextView tv_digCode = digView.findViewById(R.id.lecture_code);
            tv_digCode.setText("교과목 코드 : " + detail_code);
            TextView tv_digProf = digView.findViewById(R.id.lecture_prof);
            tv_digProf.setText("담당 교수 : " + jo_lecture.getString("professor"));
            TextView tv_digLoc = digView.findViewById(R.id.lecture_loc);
            tv_digLoc.setText("강의실 : " + jo_lecture.getString("location"));

            String result_memo = API.getMemo(1, detail_code);
            JSONArray jo_memo = API.getJsonInfo(result_memo).getJSONArray("Items");
            tv_digInfo = digView.findViewById(R.id.lecture_memo_info);
            rl_digHomework = digView.findViewById(R.id.lecture_homework_layout);
            rl_digExam = digView.findViewById(R.id.lecture_exam_layout);
            rl_digStudy = digView.findViewById(R.id.lecture_study_layout);
            tv_digHomework = digView.findViewById(R.id.lecture_homework);
            tv_digExam = digView.findViewById(R.id.lecture_exam);
            tv_digStudy = digView.findViewById(R.id.lecture_study);
            tv_digHomeworkContent = digView.findViewById(R.id.lecture_homework_content);
            tv_digExamContent = digView.findViewById(R.id.lecture_exam_content);
            tv_digStudyContent = digView.findViewById(R.id.lecture_study_content);
            ImageView iv_digHomeworkDelete = digView.findViewById(R.id.lecture_homework_delete);
            ImageView iv_digExamDelete = digView.findViewById(R.id.lecture_exam_delete);
            ImageView iv_digStudyDelete = digView.findViewById(R.id.lecture_study_delete);
            tv_digBtn = digView.findViewById(R.id.lecture_btn);
            flag_homework = false;
            flag_exam = false;
            flag_study = false;
            for (int i = 0; i < jo_memo.length(); i++) {
                JSONObject jo = jo_memo.getJSONObject(i);
                switch (jo.getString("type")) {
                    case "HOMEWORK":
                        tv_digHomework.setText("[과제] " + jo.getString("title") + "   (" + jo.getString("date") + ")");
                        tv_digHomeworkContent.setText(jo.getString("description"));
                        tv_digInfo.setVisibility(View.GONE);
                        rl_digHomework.setVisibility(View.VISIBLE);
                        flag_homework = true;
                        break;
                    case "EXAM":
                        tv_digExam.setText("[시험] " + jo.getString("title") + "   (" + jo.getString("date") + ")");
                        tv_digExamContent.setText(jo.getString("description"));
                        tv_digInfo.setVisibility(View.GONE);
                        rl_digExam.setVisibility(View.VISIBLE);
                        flag_exam = true;
                        break;
                    case "STUDY":
                        tv_digStudy.setText("[스터디] " + jo.getString("title") + "   (" + jo.getString("date") + ")");
                        tv_digStudyContent.setText(jo.getString("description"));
                        tv_digInfo.setVisibility(View.GONE);
                        rl_digStudy.setVisibility(View.VISIBLE);
                        flag_study = true;
                        break;
                }
            }
            iv_digHomeworkDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                        new AlertDialog.Builder(TimeTableActivity.this)
                                .setTitle(tv_digHomework.getText().toString())
                                .setMessage("이 메모를 삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                            flag_homework = false;
                                            rl_digHomework.setVisibility(View.GONE);
                                            API.deleteMemo(detail_code, "HOMEWORK");
                                            Object subject = null;
                                            for (Object[] memo : memos) {
                                                if (memo[2].equals(detail_code) && memo[3].equals("HOMEWORK")) {
                                                    subject = memo;
                                                }
                                            }
                                            memos.remove(subject);
                                            setMemo();
                                            if (!flag_homework && !flag_exam && !flag_study) {
                                                tv_digInfo.setVisibility(View.VISIBLE);
                                            }

                                            lastTimeButtonPressed = System.currentTimeMillis();
                                        }
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();

                        lastTimeButtonPressed = System.currentTimeMillis();
                    }
                }
            });
            iv_digExamDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                        new AlertDialog.Builder(TimeTableActivity.this)
                                .setTitle(tv_digExam.getText().toString())
                                .setMessage("이 메모를 삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                            flag_exam = false;
                                            rl_digExam.setVisibility(View.GONE);
                                            API.deleteMemo(detail_code, "EXAM");
                                            Object subject = null;
                                            for (Object[] memo : memos) {
                                                if (memo[2].equals(detail_code) && memo[3].equals("EXAM")) {
                                                    subject = memo;
                                                }
                                            }
                                            memos.remove(subject);
                                            setMemo();
                                            if (!flag_homework && !flag_exam && !flag_study) {
                                                tv_digInfo.setVisibility(View.VISIBLE);
                                            }

                                            lastTimeButtonPressed = System.currentTimeMillis();
                                        }
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();

                        lastTimeButtonPressed = System.currentTimeMillis();
                    }
                }
            });
            iv_digStudyDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                        new AlertDialog.Builder(TimeTableActivity.this)
                                .setTitle(tv_digStudy.getText().toString())
                                .setMessage("이 메모를 삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                                            flag_study = false;
                                            rl_digStudy.setVisibility(View.GONE);
                                            API.deleteMemo(detail_code, "STUDY");
                                            Object subject = null;
                                            for (Object[] memo : memos) {
                                                if (memo[2].equals(detail_code) && memo[3].equals("STUDY")) {
                                                    subject = memo;
                                                }
                                            }
                                            memos.remove(subject);
                                            setMemo();
                                            if (!flag_homework && !flag_exam && !flag_study) {
                                                tv_digInfo.setVisibility(View.VISIBLE);
                                            }

                                            lastTimeButtonPressed = System.currentTimeMillis();
                                        }
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();

                        lastTimeButtonPressed = System.currentTimeMillis();
                    }
                }
            });

            ll_digWriteMemo = digView.findViewById(R.id.lecture_write_memo);
            memo_type = null;
            rg_digType = digView.findViewById(R.id.lecture_memo_type);
            rg_digType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i) {
                        case R.id.type_homework:
                            memo_type = "HOMEWORK";
                            break;
                        case R.id.type_exam:
                            memo_type = "EXAM";
                            break;
                        case R.id.type_study:
                            memo_type = "STUDY";
                            break;
                    }
                }
            });
            et_digTitle = digView.findViewById(R.id.lecture_memo_title);
            et_digDescription = digView.findViewById(R.id.lecture_memo_description);
            flag_writeMemo = false;
            tv_digBtn.setText(formatter_month.format(cal.getTime()) + " " + formatter_date.format(cal.getTime()) + "일 메모 추가");
            tv_digBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.lecture_btn) {
                        if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                            if (!flag_writeMemo) {
                                ll_digWriteMemo.setVisibility(View.VISIBLE);
                                tv_digBtn.setText(formatter_month.format(cal.getTime()) + " " + formatter_date.format(cal.getTime()) + "일 메모 등록");

                                flag_writeMemo = true;
                            } else {
                                String title = et_digTitle.getText().toString();
                                String description = et_digDescription.getText().toString();
                                if (memo_type == null) {
                                    new AlertDialog.Builder(TimeTableActivity.this)
                                            .setMessage("메모 타입을 선택해주세요.")
                                            .setPositiveButton("확인", null)
                                            .show();
                                } else if (memo_type.equals("HOMEWORK") && flag_homework) {
                                    new AlertDialog.Builder(TimeTableActivity.this)
                                            .setMessage("[과제] 타입의 메모는\n더 이상 추가할 수 없습니다.")
                                            .setPositiveButton("확인", null)
                                            .show();
                                } else if (memo_type.equals("EXAM") && flag_exam) {
                                    new AlertDialog.Builder(TimeTableActivity.this)
                                            .setMessage("[시험] 타입의 메모는\n더 이상 추가할 수 없습니다.")
                                            .setPositiveButton("확인", null)
                                            .show();
                                } else if (memo_type.equals("STUDY") && flag_study) {
                                    new AlertDialog.Builder(TimeTableActivity.this)
                                            .setMessage("[스터디] 타입의 메모는\n더 이상 추가할 수 없습니다.")
                                            .setPositiveButton("확인", null)
                                            .show();
                                } else if (title.equals("")) {
                                    new AlertDialog.Builder(TimeTableActivity.this)
                                            .setMessage("메모 제목을 입력해주세요.")
                                            .setPositiveButton("확인", null)
                                            .show();
                                } else if (description.equals("")) {
                                    new AlertDialog.Builder(TimeTableActivity.this)
                                            .setMessage("메모 내용을 입력해주세요.")
                                            .setPositiveButton("확인", null)
                                            .show();
                                } else {
                                    String date = formatter_pointer.format(cal.getTime());
                                    API.postMemo(detail_code, memo_type, title, description, date);
                                    cal.set(Calendar.DAY_OF_WEEK, 6);
                                    memos.add(new Object[]{formatter_pointer.format(cal.getTime()), day_pointer, detail_code, memo_type});
                                    cal.set(Calendar.DAY_OF_WEEK, day_pointer + 2);

                                    switch (memo_type) {
                                        case "HOMEWORK":
                                            flag_homework = true;
                                            tv_digHomework.setText("[과제] " + title + "   (" + date + ")");
                                            tv_digHomeworkContent.setText(description);
                                            rl_digHomework.setVisibility(View.VISIBLE);
                                            for (Object[] b : blocks) {
                                                if (b[0].equals(detail_code)) {
                                                    ((TextView) b[4]).setText("[과제] " + title);
                                                }
                                            }
                                            break;
                                        case "EXAM":
                                            flag_exam = true;
                                            tv_digExam.setText("[시험] " + title + "   (" + date + ")");
                                            tv_digExamContent.setText(description);
                                            rl_digExam.setVisibility(View.VISIBLE);
                                            for (Object[] b : blocks) {
                                                if (b[0].equals(detail_code)) {
                                                    ((TextView) b[5]).setText("[시험] " + title);
                                                }
                                            }
                                            break;
                                        case "STUDY":
                                            flag_study = true;
                                            tv_digStudy.setText("[스터디] " + title + "   (" + date + ")");
                                            tv_digStudyContent.setText(description);
                                            rl_digStudy.setVisibility(View.VISIBLE);
                                            for (Object[] b : blocks) {
                                                if (b[0].equals(detail_code)) {
                                                    ((TextView) b[6]).setText("[스터디] " + title);
                                                }
                                            }
                                            break;
                                    }
                                    setMemo();

                                    ll_digWriteMemo.setVisibility(View.GONE);
                                    tv_digInfo.setVisibility(View.GONE);
                                    rg_digType.clearCheck();
                                    et_digTitle.setText("");
                                    et_digDescription.setText("");
                                    tv_digBtn.setText(formatter_month.format(cal.getTime()) + " " + formatter_date.format(cal.getTime()) + "일 메모 추가");

                                    flag_writeMemo = false;
                                }
                            }
                        }

                        lastTimeButtonPressed = System.currentTimeMillis();
                    }
                }

            });

            detail_dialog = new AlertDialog.Builder(TimeTableActivity.this)
                    .setView(digView)
                    .show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prev_week:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    moveWeek(-7);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.today:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    setToday();

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.next_week:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    moveWeek(7);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.mon:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(0);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.tue:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(1);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.wed:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(2);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.thu:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(3);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.fri:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(4);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.timetable_mon:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(0);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.timetable_tue:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(1);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.timetable_wed:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(2);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.timetable_thu:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(3);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            case R.id.timetable_fri:
                if (System.currentTimeMillis() - lastTimeButtonPressed > 100) {
                    selectDay(4);

                    lastTimeButtonPressed = System.currentTimeMillis();
                }
                break;
            default:
                for (final Object[] block : blocks) {
                    if (view.getId() == (int) block[1]) {
                        if (System.currentTimeMillis() - lastTimeButtonPressed > 1000) {
                            selectDay((int) block[2]);
                            setDetailBox(block);

                            lastTimeButtonPressed = System.currentTimeMillis();
                            break;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tb_search) {
            if (System.currentTimeMillis() - lastTimeButtonPressed > 1000) {
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);

                lastTimeButtonPressed = System.currentTimeMillis();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        toast_back.show();

        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            toast_back.cancel();
            finish();
        }

        lastTimeBackPressed = System.currentTimeMillis();
    }
}
