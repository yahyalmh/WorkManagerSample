package com.example.workmanagersample;

import android.content.Context;
import android.util.Log;

import com.example.workmanagersample.db.DbHelper;
import com.example.workmanagersample.network.ServiceGen;
import com.example.workmanagersample.ui.NotificationCenter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class IWorker extends Worker {
    private String TAG = IWorker.class.getName();
    DbHelper dbHelper;

    public IWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        try {
            Data inputData = getInputData();
            int taskType = inputData.getInt("type", 1);
            NotificationCenter.sendNotification(context, taskType);
            String[] pictures = inputData.getStringArray("pictures");

            for (String url : pictures) {

                Response<ResponseBody> response = ServiceGen.getRequestApi().downloadImage(url).execute();
                if (response.isSuccessful()) {
                    try {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        InputStream inputStream = response.body().byteStream();

                        int nRead;
                        byte[] data = new byte[16384];

                        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }
                        boolean isInserted = dbHelper.insertImage(buffer.toByteArray(), taskType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    finish(context);
                }
            }

            finish(context);
            return Result.success(new Data.Builder().putString("result", "ok").build());
        } catch (Throwable throwable) {
            finish(context);
            Log.d(TAG, "Error Sending Notification" + throwable.getMessage());
            return Result.failure();
        }
    }

    private void finish(Context context) {
        NotificationCenter.finishNotification(context);
    }
}
