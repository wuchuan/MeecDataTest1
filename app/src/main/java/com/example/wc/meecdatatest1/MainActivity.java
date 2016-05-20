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

    private Button start;
    private Button stop;
    private TextView show;

    private long copytime;
    private long countTime;
    private boolean isStop;
    private int count;

    public static synchronized int getCountThread() {
        return countThread;
    }

    public static synchronized void setCountThread(Boolean isChange) {
        if (isChange) countThread++;
        else countThread--;
    }

    private static int countThread;
    private final static int CountThread = 2;
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
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        show = (TextView) findViewById(R.id.show);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText("请测试");
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
        private Boolean open;
        String localFile1;
        String file_name1;

        public writerThread(String localFile1, String file_name1) {
            setCountThread(true);
            this.open = true;
            this.localFile1 = localFile1;
            this.file_name1 = file_name1;
        }

        @Override
        public void run() {
            try {
                fileWrite(localFile1, file_name1);
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
//            int l =1;
            // Log.d("wuchuan","------------------------666666666666666-----");
            while (!isStop) {


                if (countThread < CountThread) {
//                if (l == 1) {
                    nuber++;
                    String file_name1 = Environment.getExternalStorageDirectory().getPath() + "/t_w_data_" + nuber;
                    File cunta = new File(file_name1);
                    detleFirleName.add(file_name1);
                    if (cunta.exists()) {
                        // Log.d("wuchuan","------"+file_name1+"------------------存在");
                        continue;
                    }

                    // Log.d("wuchuan","-----------------------------"+countThread);

                    // Log.d("wuchuan","-----------------------------"+nuber);
//                    l = fileWrite(null, file_name1);
//                    new writerThread("test", file_name1).start();
//                   new writerThread("test_100M", file_name1).start();
                   new writerThread("test_50M", file_name1).start();

//                }
                }
            }
        } catch (Exception e) {
        }


    }

    private synchronized void sendmesge() {
        Message msg = new Message();
        msg.what = 0;
        // Log.d("wuchuan","-------------------4444444444444444444444----------sendmesge");
        handler.sendMessage(msg);

    }

    private int fileWrite(String localFile1, String file_name1) throws IOException {
        int l = 9;

        try {
            Log.d("wuchuan", "-----------------------------" + localFile1 + "         " + file_name1);
            Process process = Runtime.getRuntime().exec("cp -a " + "/storage/emulated/0/3" + " " + file_name1);
//            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
//            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
//            errorGobbler.start();
//            outputGobbler.start();
            l = process.waitFor();
        } catch (IOException e) {
            Log.e("runtime", e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Log.d("wuchuan", "-----------------------------" + l);
//        long begin = System.currentTimeMillis();
//        File localFile2 = new File(file_name1);
//        FileOutputStream fos = new FileOutputStream(localFile2);
//        InputStream fsin = getAssets().open(localFile1);
////        java.io.FileInputStream fis = new java.io.FileInputStream(srcPath);
////        java.io.FileOutputStream fos = new java.io.FileOutputStream(destPath);
//        java.io.BufferedInputStream bis = new java.io.BufferedInputStream(fsin);
//        java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(fos);
//        int rd = bis.read();
//        while (rd!=-1){
//            bos.write(rd);
//            rd = bis.read();
//        }
//        fos.flush();
//        fos.close();
//        fsin.close();
//        long end = System.currentTimeMillis();
//        Log.d("wuchuan","-----------------------------1");
        sendmesge();
        return l;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    count++;
                    long endtiem = System.currentTimeMillis();
                    BigDecimal copytime1 = new BigDecimal((copytime) / 1000.0D);
                    BigDecimal countTime1 = new BigDecimal((endtiem - countTime) / 60000.0D);
                    BigDecimal countszie = new BigDecimal(count * 0.0100);
                    double copytime2 = copytime1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double countTime2 = countTime1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double countszie1 = countszie.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

                    show.setText("第" + count + "次  "
                            //  + "输入10.24M用时" //+ copytime2 + "秒"
                            + "------>ok  "
                            + "用时总计：" + countTime2 + "分"
                            + " 总写入：" + countszie1 + "G");
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

                if (getAvailableBlocks() < 500 && (detleFirleName.size() > CountThread)) {
                    try {
                        sleep(10000);
                        for (int i = 0; i < detleFirleName.size() - CountThread; i++) {

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