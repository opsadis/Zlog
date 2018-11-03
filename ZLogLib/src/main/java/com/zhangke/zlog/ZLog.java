package com.zhangke.zlog;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Logging helper class.
 * Created by ZhangKe on 2018/1/11.
 */

public class ZLog {

    private static LogQueue mLogQueue;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINA);
    private static Boolean saveToFile = true;
    //是否显示在终端
    private static Boolean printOnTerminal = true;

    private static String mLogDir;

    public static synchronized void openSaveToFile(){
        saveToFile = true;
    }

    public static synchronized void closeSaveToFile(){
        saveToFile = false;
    }
    public static synchronized void openPrintOnTerminal(){
        printOnTerminal = true;
    }

    public static synchronized void closePrintOnTerminal(){
        printOnTerminal = false;
    }

    /**
     * 初始化 ZldLog，
     * 使用前应该先调用一下次方法
     */
    public static synchronized void Init(String logDir){
        mLogDir = logDir;
        mLogQueue = new LogQueue(logDir);
        mLogQueue.start();
    }

    /**
     * 初始化
     * @param logDir
     * @param saveToFile 是否保存到文件
     * @param printOnTerminal 是否logcat显示到终端
     */
    public static synchronized void Init(String logDir,Boolean saveToFile, Boolean printOnTerminal){
        mLogDir = logDir;
        mLogQueue = new LogQueue(logDir);
        mLogQueue.start();
        ZLog.saveToFile = saveToFile;
        ZLog.printOnTerminal = printOnTerminal;
    }

    public static void e(String TAG, String text){
        if (printOnTerminal)
            e(TAG, text, true);
    }

    public static void e(String TAG, String text, boolean saveToFile){
        if (printOnTerminal)
            Log.e(TAG, text);
        if(mLogQueue != null && ZLog.saveToFile && saveToFile) {
            mLogQueue.add(new LogBean(buildMessage(TAG, text), LogType.ERROR));
        }
    }

    public static void e(String TAG, String text, Throwable e){
        if (printOnTerminal)
            e(TAG, text, e, true);
    }

    public static void e(String TAG, String text, Throwable e, boolean saveToFile){
        if (printOnTerminal)
            Log.e(TAG, "e: ", e);
        if(mLogQueue != null && ZLog.saveToFile && saveToFile) {
            mLogQueue.add(new LogBean(buildMessage(TAG, String.format("%s--->%s", text, e.toString())), LogType.ERROR));
        }
    }

    public static void d(String TAG, String text){
        d(TAG, text, true);
    }

    public static void d(String TAG, String text, boolean saveToFile){
        if (printOnTerminal)
            Log.d(TAG, text);
        if(mLogQueue != null && ZLog.saveToFile && saveToFile) {
            mLogQueue.add(new LogBean(buildMessage(TAG, text), LogType.DEBUG));
        }
    }

    public static void i(String TAG, String text){
        i(TAG, text, true);
    }

    public static void i(String TAG, String text, boolean saveToFile){
        if (printOnTerminal)
            Log.i(TAG, text);
        if(mLogQueue != null && ZLog.saveToFile && saveToFile) {
            mLogQueue.add(new LogBean(buildMessage(TAG, text), LogType.INFO));
        }
    }

    public static void i(String TAG, String text, Throwable e){
        i(TAG, text, e, true);
    }

    public static void i(String TAG, String text, Throwable e, boolean saveToFile){
        if (printOnTerminal)
            Log.i(TAG, String.format("%s--->%s", text, e.toString()));
        if(mLogQueue != null && ZLog.saveToFile && saveToFile) {
            mLogQueue.add(new LogBean(buildMessage(TAG, String.format("%s--->%s", text, e.toString())), LogType.INFO));
        }
    }

    public static void wtf(String TAG, String text){
        if (printOnTerminal)
            wtf(TAG, text, true);
    }

    public static void wtf(String TAG, String text, boolean saveToFile){
        if (printOnTerminal)
            Log.wtf(TAG, text);
        if(mLogQueue != null && ZLog.saveToFile && saveToFile) {
            mLogQueue.add(new LogBean(buildMessage(TAG, text), LogType.WTF));
        }
    }

    public static void crash(String TAG, String text){
        if (printOnTerminal)
            Log.e(TAG, text);
        if(mLogQueue != null && ZLog.saveToFile) {
            mLogQueue.add(new LogBean(buildMessage(TAG, text), LogType.CRASH));
        }
    }

    private static String buildMessage(String TAG, String text){
        try {
            StringBuilder sbLog = new StringBuilder();
            sbLog.append(simpleDateFormat.format(new Date()));
            sbLog.append("/");
            sbLog.append(TAG);
            sbLog.append("--->");
            sbLog.append(text);
            sbLog.append("\n");
            return sbLog.toString();
        }catch(Exception e){
            if (printOnTerminal)
                Log.e(TAG, "buildMessage: ", e);
            return "";
        }
    }
}
