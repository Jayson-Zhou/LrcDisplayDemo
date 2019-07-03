package com.example.lrcdisplaydemo;

import android.text.format.DateUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcHandle {

    private static final String TAG = "LrcHandle:";

    // lrc文件中的文字集合
    private List<String> mWords;

    // lrc文件中的时间集合
    private List<Integer> mTimeList;

    public LrcHandle() {
        mWords = new ArrayList<>();
        mTimeList = new ArrayList<>();
    }

    public static String formatTimeToMinute(long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        String minutes = String.format(Locale.getDefault(), "%02d", m);
        return minutes;
    }

    public static String formatTimeToSecond(long milli) {
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String seconds = String.format(Locale.getDefault(), "%02d", s);
        return seconds;
    }

    public static String formatTime(long milli) {
        return formatTimeToMinute(milli) + ":" + formatTimeToSecond(milli);
    }

    public List<Integer> getTimeList() {
        return mTimeList;
    }

    public List<String> getWords() {
        return mWords;
    }

    /**
     * 以文件的方式读取Lrc数据
     *
     * @param lrcFile
     */
    public void loadLrcFile(File lrcFile) {
        if (lrcFile == null || !lrcFile.exists()) {
            Log.d(TAG, "文件不存在");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile), "GB2312"));
            String line;
            while ((line = br.readLine()) != null) {
                parseLrcData(line);
                if ((line.contains("[ar:")) || (line.contains("[ti:"))
                        || (line.contains("[by:"))) {
                    line = line.substring(line.indexOf(":") + 1, line.indexOf("]"));
                } else {
                    String ss = line.substring(line.indexOf("["), line.indexOf("]") + 1);
                    line = line.replace(ss, "");
                }
                mWords.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 以流的方式读取Lrc数据
     *
     * @param in
     */
    public void loadLrcFile(InputStream in) {
        if (in == null) {
            Log.d(TAG, "文件不存在");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GB2312"));
            String line;
            while ((line = br.readLine()) != null) {
                parseLrcData(line);
                if ((line.contains("[ar:")) || (line.contains("[ti:"))
                        || (line.contains("[by:"))) {
//                    line = line.substring(line.indexOf(":") + 1, line.indexOf("]"));
                    continue;
                } else {
                    String ss = line.substring(line.indexOf("["), line.indexOf("]") + 1);
                    line = line.replace(ss, "");
                    mWords.add(line);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "文件打开失败");
        }
    }

    /**
     * 解析时间数据
     *
     * @param data
     */
    public void parseLrcData(String data) {
        Matcher matcher = Pattern.compile("\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(data);
        if (matcher.find()) {
            String str = matcher.group();
            mTimeList.add(timeHandler(str.substring(1, str.length() - 1)));
        }

    }

    /**
     * 分离出时间
     *
     * @param str
     * @return 返回转换成毫秒形式的时间
     */
    private int timeHandler(String str) {
        str = str.replace(".", ":");
        String[] timeData = str.split(":");

        // 分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);

//        Log.d(TAG, minute + ":" + second + ":" + millisecond);

        // 计算上一行与下一行的时间转换为毫秒数
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;

        return currentTime;
    }

}
