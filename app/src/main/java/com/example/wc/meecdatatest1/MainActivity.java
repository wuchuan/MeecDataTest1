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
         countthread=1;
        countsize=20;
        show_thread_count.setText("countthread:"+countthread);
        show_size_count.setText("size:"+countsize+"M");
        thread_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countthread>1&&countthread<17)
                countthread--;
                show_thread_count.setText("countthread:"+countthread);
            }
        });
        thread_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countthread>0&&countthread<17)
                countthread++;
                show_thread_count.setText("countthread:"+countthread);
            }
        });
        size_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countsize>1&&countsize<205)
                countsize=countsize+5;
                show_size_count.setText("size:"+countsize+"M");
            }
        });
        size_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countsize>1&&countsize<205)
                countsize=countsize-5;
                show_size_count.setText("size:"+countsize+"M");

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText("请测试");
                countsize=10;
                countthread=4;
                isStop = true;
                start.setEnabled(true);


            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText("正在写入数据...");
                long timetamp = countTime = System.currentTimeMillis();
                String logFileName = "MeecDataTest" + "-" + timetamp + "_log.txt";
                logFile = new File(Environment.getExternalStorageDirectory().getPath(), logFileName);
                isStop = false;

                new Thread("4") {
                    @Override
                    public void run() {
                        gotest();
                    }
                }.start();
                deltefileThread mythreath = new deltefileThread();
                mythreath.start();
                start.setEnabled(false);
            }
        });
    }

    private class writerThread extends Thread {
        String size;
        String file_name1;

        public writerThread(String size, String file_name1) {
            setCountThread(true);
            this.size = size;
            this.file_name1 = file_name1;
        }

        @Override
        public void run() {
            try {
                fileWrite(size, file_name1);
                setCountThread(false);
//                sendmesge();
//                this=null;
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
                    new writerThread(countsize+"", file_name1).start();
                }
            }
        } catch (Exception e) {
        }


    }

    private synchronized void sendmesge() {
        Message msg = new Message();
        msg.what = 0;
        handler.sendMessage(msg);

    }

    private int fileWrite(String size, String file_name1) throws IOException {
        int l = 9;
        try {
            Log.d("wuchuan", "-----------------------------" + size + "         " + file_name1+"    "+"dd if=/dev/zero of=" + file_name1 + " bs=1m  count=" + size);
            Process process = Runtime.getRuntime().exec("dd if=/dev/zero of=" + file_name1 + " bs=1m  count=" + size);
            l = process.waitFor();
        } catch (IOException e) {
            Log.e("runtime", e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("wuchuan", "-----------------------------" + l);
        sendmesge();
        return l;

    }

    private double cuntmax;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    count++;
                    cuntmax=cuntmax+( countsize*0.00097);
                    long endtiem = System.currentTimeMillis();
                    BigDecimal countTime1 = new BigDecimal((endtiem - countTime) / 60000.0D);
                    BigDecimal countszie = new BigDecimal(cuntmax);
                    double countTime2 = countTime1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double countszie1 = countszie.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

                    show.setText("第" + count + "次  "
                            //  + "输入10.24M用时" //+ copytime2 + "秒"
                            + "------>ok  "
                            + "用时总计：" + countTime2 + "分"
                            + " 总写入：" +countszie1 + "G");
                    final String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date())
                            + "\n"
                            + show.getText().toString()
                            + "\n";
                    if (countszie1 > 2048) {//2048
                        show.setText("测试完成！  " + "总写入大小：" + countszie1 + "G" + "  总时间：" + countTime2 / 60 + "天");
                        stop.performClick();
                        saveLog(message, null);
                    }


                    if (count % 100 == 0)
                        saveLog(message, null);


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


        @Override
        public void run() {

            while (!isStop) {

                if (getAvailableBlocks() < 500 && (detleFirleName.size() > 4)) {
                    try {
                        sleep(10000);
                        for (int i = 0; i < detleFirleName.size() - 4; i++) {

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
            raf.write(msg.getBytes("gb2312"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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