package com.example.workmanagersample.network;


import com.example.workmanagersample.models.Picture;

import java.util.ArrayList;

import io.reactivex.Observable;

public class Repository {
    private static Repository instance = null;

    private Repository() {
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public Observable<ArrayList<Picture>> getPictures() {
        return ServiceGen.getRequestApi().getPhoto();
    }
}
