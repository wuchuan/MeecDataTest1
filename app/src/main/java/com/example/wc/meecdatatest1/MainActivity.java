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
    private InputStream localFile1;
    private long copytime;
    private long countTime;
    private boolean isStop;
    private int count;
    private int nuber;
    private ArrayList<String> detleFirleName = new ArrayList<>();
    private static File logFile;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

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
                isStop = true;
                start.setEnabled(true);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long timetamp = countTime=System.currentTimeMillis();
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


    private void gotest() {
        try {
            while (!isStop) {
                String file_name1 = Environment.getExternalStorageDirectory().getPath() + "/t_w_data_" + nuber;
                File localFile2 = new File(file_name1);
                detleFirleName.add(file_name1);
                if (!localFile2.exists()) {
                    localFile2.createNewFile();
                }else{
                    nuber++;
                    continue;
                }
                localFile1 = getAssets().open("test");
                copytime =fileWrite(localFile1, localFile2);

                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);

                nuber++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


    }

    private long fileWrite(InputStream localFile1, File localFile2) throws IOException {

        long begin = System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(localFile2);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = localFile1.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        localFile1.close();
        long end = System.currentTimeMillis();
        return end - begin;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    count++;
                    long endtiem = System.currentTimeMillis();
                    BigDecimal copytime1 = new BigDecimal((copytime) / 1000.0D);
                    BigDecimal countTime1 = new BigDecimal((endtiem-countTime )/ 60000.0D);
                    BigDecimal countszie = new BigDecimal(count * 0.01024);
                    double copytime2 = copytime1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double countTime2 = countTime1.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double countszie1 = countszie.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

                    show.setText("第" + count + "次  "
                            + "输入10.24M用时" + copytime2 + "秒"
                            + "------>ok  "
                            + "用时总计：" + countTime2 + "分"
                            + " 总写入：" + countszie1 + "G");
                    if (countszie1 > 2048) {//2048
                        show.setText("测试完成！  " + "总写入大小："+countszie1+"G"+"  总时间："+countTime2/60+"天");
                        stop.performClick();
                    }

                   final String message = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date())
                            + "\n"
                            +show.getText().toString()
                            + "\n";

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

                if (getAvailableBlocks() < 500) {
                    try {
                        sleep(10000);
                        for (int i = 0; i < detleFirleName.size() - 2; i++) {

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
     *
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