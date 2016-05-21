package com.example.wc.meecdatatest1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button stop, start, thread_less, thread_add, size_add, size_less;
    private TextView show, show_thread_count, show_size_count;

    private long countTime;
    private boolean isStop;
    private int count;
    private int countsize, countthread;
    private final int COUNTSIZE = 10; //初始化拷被大小
    private final int COUNTTHREAD = 4;//初始化线程数
    private long firstTime = 0;

    public static synchronized void setCountThread(Boolean isChange) {
        if (isChange) countThread++;
        else countThread--;
    }

    private static int countThread;
    private int nuber;
    private ArrayList<String> detleFirleName = new ArrayList<>();
    private static File logFile;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_main);
        thread_less = (Button) findViewById(R.id.thread_less);
        thread_add = (Button) findViewById(R.id.thread_add);
        size_add = (Button) findViewById(R.id.size_add);
        size_less = (Button) findViewById(R.id.size_less);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        show = (TextView) findViewById(R.id.show);
        show_thread_count = (TextView) findViewById(R.id.show_thread_count);
        show_size_count = (TextView) findViewById(R.id.show_size_count);
        countthread = COUNTTHREAD;
        countsize = COUNTSIZE;
        show_thread_count.setText("countthread:" + countthread);
        show_size_count.setText("size:" + countsize + "M");
        thread_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countthread > 1 && countthread <= 16)
                    countthread--;
                show_thread_count.setText("countthread:" + countthread);
            }
        });
        thread_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countthread >= 1 && countthread < 16) {
                    countthread++;
                }
                show_thread_count.setText("countthread:" + countthread);
            }
        });
        size_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countsize >= 10 && countsize < 200) {
                    countsize = countsize + 10;
                }
                show_size_count.setText("size:" + countsize + "M");
            }
        });
        size_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countsize > 10 && countsize <= 200)
                    countsize = countsize - 10;
                show_size_count.setText("size:" + countsize + "M");

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(getBaseContext(), "再按一次停止测试", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                } else {
                    show.setText("测试停止");
                    countTime = 0;
                    countThread = 0;
                    countsize = COUNTSIZE;
                    countthread = COUNTTHREAD;
                    count = 0;
                    cuntmax = 0;
                    firstsize=0;
                    isStop = true;
                    start.setEnabled(true);
                    thread_less.setEnabled(true);
                    thread_add.setEnabled(true);
                    size_add.setEnabled(true);
                    size_less.setEnabled(true);

                }


            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show.setText("正在写入数据...");
                countTime = System.currentTimeMillis();
                String logFileName = "EmccDataTest" + "-" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date()) + "_log.txt";
                logFile = new File(Environment.getExternalStorageDirectory().getPath(), logFileName);
                isStop = false;

                new Thread("manthread") {
                    @Override
                    public void run() {
                        gotest();
                    }
                }.start();
                deltefileThread mythreath = new deltefileThread("deletethread");
                mythreath.start();
                start.setEnabled(false);
                thread_less.setEnabled(false);
                thread_add.setEnabled(false);
                size_add.setEnabled(false);
                size_less.setEnabled(false);
            }
        });
    }

    private class writerThread extends Thread {
        String size;
        String file_name1;

        public writerThread(String size, String file_name1) {
            super("writeThread_" + nuber);
            setCountThread(true);
            this.size = size;
            this.file_name1 = file_name1;
        }

        @Override
        public void run() {
            try {
                fileWrite(size, file_name1);
                setCountThread(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void gotest() {
        try {
            while (!isStop) {
                if (countThread < countthread) {
                    nuber++;
                    String file_name1 = Environment.getExternalStorageDirectory().getPath() + "/data_" + nuber;
                    File cunta = new File(file_name1);
                    detleFirleName.add(file_name1);
                    if (cunta.exists()) {
                        continue;
                    }
                    new writerThread(countsize + "", file_name1).start();
                }
            }
        } catch (Exception e) {
        }


    }

    private synchronized void sendmesge(double speed1) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = speed1;
        handler.sendMessage(msg);

    }

    private int fileWrite(String size, String file_name1) throws IOException {
        int l = 9;
        long startTime = System.currentTimeMillis();
        try {
            Process process = Runtime.getRuntime().exec("dd if=/dev/zero of=" + file_name1 + " bs=1m  count=" + size);
            if (isStop) {
                process.destroy();
                return l;
            }
            l = process.waitFor();
        } catch (IOException e) {
            Log.e("runtime", e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endtTime = System.currentTimeMillis();
        BigDecimal speed = new BigDecimal(Integer.valueOf(size) / ((endtTime - startTime) / 1000.0D));
        double speed1 = speed.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        sendmesge(speed1);
        return l;

    }

    private double cuntmax;
    private double firstsize;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (!isStop) {
                        count++;
                        cuntmax = cuntmax + (countsize * 0.00097);
                        long endtiem = System.currentTimeMillis();
                        BigDecimal countTime1 = new BigDecimal((endtiem - countTime) / 60000.0D);
                        BigDecimal countszie = new BigDecimal(cuntmax);
                        double countTime2 = countTime1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double countszie1 = countszie.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                        BigDecimal speed = new BigDecimal(cuntmax / ((endtiem - countTime) / 1000.0D) / 0.00097);
                        double speed1 = speed.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

                        show.setText(
                                "第" + count + "次  " + "------>ok  "
                                        + "\n"
                                        + "本次写入速度为：" + msg.obj.toString() + "M/s"
                                        + "\n"
                                        + "平均写入速度为：" + speed1 + "M/s"
                                        + "\n"
                                        + "总耗时：" + secToTime((int) ((endtiem - countTime) / 1000))
                                        + "\n"
                                        + "总写入：" + countszie1 + "G");


                        if (countszie1 > 2048) {//2048

                            String showStr = "测试完成！  " + "总写入大小：" + countszie1 + "G" + "  总时间：" + countTime2 + "分钟" + "  平均写入速度为：" + speed1 + "M/s";
                            show.setText(showStr);
                            saveLog(showStr, null);
                            countTime = 0;
                            countThread = 0;
                            countsize = COUNTSIZE;
                            countthread = COUNTTHREAD;
                            count = 0;
                            cuntmax = 0;
                            firstsize=0;
                            isStop = true;
                            start.setEnabled(true);
                            thread_less.setEnabled(true);
                            thread_add.setEnabled(true);
                            size_add.setEnabled(true);
                            size_less.setEnabled(true);
                        }

                        if (cuntmax - firstsize > 0.5) {
                            firstsize=cuntmax;
                            final String message = "\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(new Date())
                                    + "\n"
                                    + show.getText().toString();
                            saveLog(message, null);
                        }

                    }
                    break;
                case 1:
                    break;

            }

        }
    };

    private long getAvailableBlocks() {
        File root = Environment.getDataDirectory();
        StatFs sf = new StatFs(root.getPath());
        long mBlockSize = sf.getBlockSize();
        long mAvailCount = sf.getAvailableBlocks();
        long mAvail = mAvailCount * mBlockSize / 1024 / 1024;
        return mAvail;
    }

    class deltefileThread extends Thread {


        public deltefileThread(String deletethread) {
            super(deletethread);
        }

        @Override
        public void run() {

            while (!isStop) {

                if (getAvailableBlocks() < 500 && (detleFirleName.size() > countthread + 3)) {
                    try {
                        sleep(1000);
                        for (int i = 0; i < detleFirleName.size() - countthread - 3; i++) {

                            deleteMyFile(detleFirleName.get(i));
                            detleFirleName.remove(i);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    }

    private void deleteMyFile(String filename) {
        File file = new File(filename);
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            }
        }

    }

    public void saveLog(String msg, String type) {
        try {
            RandomAccessFile raf = new RandomAccessFile(logFile, "rwd");
            raf.seek(logFile.length());
            raf.write(msg.getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
//                if (hour > 99)
//                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + String.valueOf(i);
        else
            retStr = "" + i;
        return retStr;
    }


    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
//            ActivityCompatExt.requestPermissions(activity, PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}