package com.example.pk1.rianews.presenter;


import android.util.Log;

import com.example.pk1.rianews.model.POJO.News;
import com.example.pk1.rianews.model.dataBase.NewsDAO;
import com.example.pk1.rianews.model.network.JSOUPClient;
import com.example.pk1.rianews.view.DetailNewsView;
import java.io.IOException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailNewsPresenterImpl implements DetailNewsPresenter {
    private DetailNewsView detailNewsView;
    private NewsDAO newsDAO;
    private JSOUPClient jsoupClient;

    public DetailNewsPresenterImpl(NewsDAO newsDAO, JSOUPClient jsoupClient) {
        this.jsoupClient=jsoupClient;
        this.newsDAO=newsDAO;
    }

    @Override
    public void requestJsonNewsText(final News news) {
        if (isAttached()) {
            try {
                jsoupClient.getTextNews(news)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<News>() {
                            @Override
                            public void onSuccess(News newsLocal) {
                                detailNewsView.showText(newsLocal.getText());
                                detailNewsView.showImage(newsLocal.getFullSizeImageUrl());
                                saveNewsInDB(news);
                            }
                            @Override
                            public void onError(Throwable e) {
                                detailNewsView.showError("internet not working");
                                detailNewsView.showText(news.getText());
                                detailNewsView.showImage(news.getFullSizeImageUrl());
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveNewsInDB(News news) {
        newsDAO.updateSingleNews(news).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("debug ","save news in db");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d("debug ",e.getMessage());
                    }
                });
    }

    @Override
    public void attach(DetailNewsView view) {
        detailNewsView=view;
    }

    @Override
    public void detach() {
        detailNewsView=null;
    }

    @Override
    public boolean isAttached() {
        return detailNewsView != null;
    }
}
