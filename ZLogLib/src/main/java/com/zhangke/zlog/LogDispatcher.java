package com.zhangke.zlog;

import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ZhangKe on 2018/1/11.
 */

class LogDispatcher extends Thread {

    private static final String TAG = "LogDispatcher";

    private final int MAX_LOG_SIZE = 1024 * 1024;

    /**
     * 存储日志的队列
     */
    private LinkedBlockingQueue<LogBean> mLogQueue;
    private String mLogDir;


    LogDispatcher(LinkedBlockingQueue<LogBean> logQueue, String logDir) {
        this.mLogQueue = logQueue;
        this.mLogDir = logDir;
    }

    @Override
    public void run() {
        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while (true) {
                try {
                    LogBean logBean;
                    logBean = mLogQueue.take();
                    saveTextToFile(getLogFilePath(logBean.getLogType()), logBean.getLogText());
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
        }
    }

    /**
     * 普通日志文件名： log1.txt；
     * 错误日志文件名：errorLog1.txt;
     * 每个日志文件最大为 1Mb，超过 1Mb 文件名累加 1.
     *
     * @param logType 日志类型
     * @return 文件绝对路径
     */
    private String getLogFilePath(LogType logType) {
        String returnFileName = "";
        try {
            switch (logType) {
                case ERROR: {
                    returnFileName = getLastLogFileName(mLogDir, "errorLog");
                    break;
                }
                case INFO:
                case WTF:
                case DEBUG: {
                    returnFileName = getLastLogFileName(mLogDir, "log");
                    break;
                }
                case CRASH: {
                    returnFileName = getLastLogFileName(mLogDir, "crash");
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getLogFile: ", e);
        }
        return returnFileName;
    }


    private static String getLastLogFileName(String dir, String logName) {


        String returnFileName = getValidDateStr(new Date()) + "_" + logName + ".txt";;
        File file = new File(dir);
        if (file.exists()) {
            String[] fileArray = file.list();
            if (fileArray != null && fileArray.length > 0) {
                List<String> logList = new ArrayList<>();
                for (String logfile : fileArray) {
                    int index = logfile.lastIndexOf("_");
                    if (index >0) {
                        String subStr = logfile.substring(0, index);
//                       System.out.println("loglists1:" + subStr);
                        Date date1 = stringToDate(subStr, "yyyy_MMdd_HHmm");
//                      System.out.println("date1:" + date1 + " time:" + date1.getTime());
                        if (date1 != null) {
                            long chazhi = System.currentTimeMillis() - date1.getTime();
                            if (chazhi > 24 * 60 * 60 * 1000) {
                                new File(String.format("%s/%s", dir, logfile)).delete();
                            }
                        }else {
                            new File(String.format("%s/%s", dir, logfile)).delete();
                        }
                    }else {
                        new File(String.format("%s/%s", dir, logfile)).delete();
                    }
                }
                returnFileName = getValidDateStr(new Date()) + "_" + logName + ".txt";
            }
        }
        return returnFileName;
    }

    /**
     * 时间转换
     * @param data
     * @return
     */
    public static String getValidDateStr(Date data) {
        String sDate = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        try {
            Date date = sdf1.parse(sdf1.format(data));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmm");
            sDate = sdf.format(date);
        } catch (ParseException e) {
//            Log.e("日期装换方法是把：" + data + "******" + e);
        }
        return sDate;
    }

    /**
     * String转Date
     * @param dateStr
     * @param format
     * @return
     * @author wul
     * 2016-1-17
     */
    public static Date stringToDate(String dateStr, String format) {
        if(dateStr == null || "".equals(dateStr)){
            return null;
        }
        Date date = null;
        //注意format的格式要与日期String的格式相匹配
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
        return date;
    }

    /**
     * 将文本追加到到文件末尾;
     * 文件不存在会创建文件；
     * 父目录不存在会创建父目录，
     * 会判断三级以内的目录是否存在，不存在则创建
     *
     * @param filePath 文件绝对路径（包含文件名）
     * @param text     需要保存的文本
     */
    private void saveTextToFile(String filePath, String text) {
        try {
            File file = new File(filePath);
            if (!new File(file.getParent()).exists()) {
                File parentFile1 = new File(file.getParent());
                if (!parentFile1.exists()) {
                    File parentFile2 = new File(parentFile1.getParent());
                    if (!parentFile2.exists()) {
                        parentFile2.mkdir();
                    }
                    parentFile1.mkdir();
                }
                new File(file.getParent()).mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, true);
            writer.write(text);
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "saveTextToFile: ", e);
        }
    }

    public static void main(String[] argc){
        System.out.print("hello");
    }
}
