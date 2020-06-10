package com.example.workmanagersample.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.workmanagersample.IWorker;
import com.example.workmanagersample.R;
import com.example.workmanagersample.db.DbHelper;
import com.example.workmanagersample.models.Picture;
import com.example.workmanagersample.network.Repository;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private int oneTimeTaskType = 1;
    private int periodicTimeTaskType = 2;
    private CompositeDisposable disposable = new CompositeDisposable();
    private AppCompatButton oneTimeRequestBtn, periodicRequestBtn;
    private Repository repository = Repository.getInstance();
    private WorkManager workManager;
    private DbHelper dbHelper;
    private RecyclerView onTimeRecyclerView;
    private RecyclerView periodicRecyclerView;
    private PicturesAdapter onTimeAdapter;
    private PicturesAdapter periodicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        workManager = WorkManager.getInstance(this);
        dbHelper = new DbHelper(this);

        onTimeRecyclerView = findViewById(R.id.on_time_listView);
        periodicRecyclerView = findViewById(R.id.periodic_listView);
        oneTimeRequestBtn = findViewById(R.id.btn_one_time);
        periodicRequestBtn = findViewById(R.id.btn_periodic_time);
        initListView();
    }

    private void initListView() {
        onTimeAdapter = new PicturesAdapter();
        onTimeRecyclerView.setAdapter(onTimeAdapter);
        ArrayList<Bitmap> images = dbHelper.getImages(oneTimeTaskType);
        onTimeAdapter.setItems(images);
        onTimeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        periodicAdapter = new PicturesAdapter();
        periodicRecyclerView.setAdapter(periodicAdapter);
        images = dbHelper.getImages(periodicTimeTaskType);
        periodicAdapter.setItems(images);
        periodicRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one_time: {
                repository.getPictures()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ArrayList<Picture>>() {

                            @Override
                            public void onSubscribe(Disposable d) {
                                disposable.add(d);
                            }

                            @Override
                            public void onNext(@NonNull ArrayList<Picture> pictures) {
                                createOnTimeTask(pictures);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
                break;
            }
            case R.id.btn_periodic_time: {

                repository.getPictures()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ArrayList<Picture>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                disposable.add(d);
                            }

                            @Override
                            public void onNext(ArrayList<Picture> pictures) {
                                createPeriodicTask(pictures);
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                break;
            }
        }
    }

    private void createPeriodicTask(ArrayList<Picture> pictures) {
        Constraints constraints = new Constraints.Builder()
                /*.setRequiresCharging(true)*/
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        String[] pictures_urls = new String[pictures.size()];
        for (int i = 0; i < pictures.size(); i++) {
            pictures_urls[i] = pictures.get(i).getUrls().getSmall();
        }
        Data.Builder builder = new Data.Builder();
        builder.putStringArray("pictures", pictures_urls);
        builder.putInt("type", periodicTimeTaskType);
        Data data = builder.build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                .Builder(IWorker.class, 15, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.getId()).observe(MainActivity.this,
                new androidx.lifecycle.Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                            Bitmap bitmap = dbHelper.getLastImage(1, periodicTimeTaskType);
                            periodicAdapter.addItem(bitmap);
                        }
                    }
                }
        );
        workManager.enqueue(periodicWorkRequest);
    }

    private void createOnTimeTask(@NonNull ArrayList<Picture> pictures) {
        Constraints constraints = new Constraints.Builder()
                /*.setRequiresCharging(true)*/
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        String[] pictures_urls = new String[pictures.size()];
        for (int i = 0; i < pictures.size(); i++) {
            pictures_urls[i] = pictures.get(i).getUrls().getSmall();
        }
        Data.Builder builder = new Data.Builder();
        builder.putStringArray("pictures", pictures_urls);
        builder.putInt("type", oneTimeTaskType);
        Data data = builder.build();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(IWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(MainActivity.this,
                new androidx.lifecycle.Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            String result = workInfo.getOutputData().getString("result");
                            if (result == null || !result.equals("ok")) {
                                return;
                            }
                            Bitmap bitmap = dbHelper.getLastImage(1, oneTimeTaskType);
                            onTimeAdapter.addItem(bitmap);
                        }
                    }
                }
        );

        workManager.enqueue(oneTimeWorkRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
