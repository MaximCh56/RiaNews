package com.example.pk1.rianews.presenter;


import android.content.Context;
import android.util.Log;

import com.example.pk1.rianews.model.POJO.Category;
import com.example.pk1.rianews.model.POJO.News;
import com.example.pk1.rianews.model.dataBase.NewsDAO;
import com.example.pk1.rianews.model.network.JSOUPClient;
import com.example.pk1.rianews.view.NewsView;

import java.io.IOException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NewsPresenterImpl implements NewsPresenter {


    private NewsView newsView;
    private NewsDAO newsDAO;
    private JSOUPClient jsoupClient;

    public NewsPresenterImpl(NewsDAO newsDAO, JSOUPClient jsoupClient) {
        this.jsoupClient=jsoupClient;
        this.newsDAO=newsDAO;
    }


    @Override
    public void requestJsonNews(final String categoryUrl, final String categoryName) {
        if (isAttached()) {
            try {
                jsoupClient.getNewsForCategory(categoryUrl,categoryName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<List<News>>() {
                            @Override
                            public void onSuccess(List<News> news) {
                                newsView.showNews(news);
                                saveNewsInDB(news,categoryName);
                            }
                            @Override
                            public void onError(Throwable e) {
                                newsView.showError("internet not working");
                                newsDAO.getAllNews(categoryName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableSingleObserver<List<News>>() {
                                        @Override
                                        public void onSuccess(List<News> newsObjectList) {
                                            newsView.showNews(newsObjectList);
                                            Log.d("debug ",newsObjectList.size()+" "+categoryName);

                                        }
                                        @Override
                                        public void onError(Throwable e) {
                                            Log.d("debug ",e.getMessage());
                                        }
                                    });
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestJsonCategory() {
        if (isAttached()) {
            try {
                jsoupClient.getCategory()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<List<Category>>() {
                            @Override
                            public void onSuccess(List<Category> categoryList) {
                                newsView.createDrawerMenu(categoryList);
                                saveCategoryInDB(categoryList);
                            }
                            @Override
                            public void onError(Throwable e) {
                                newsDAO.getAllCategory()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new DisposableSingleObserver<List<Category>>() {
                                            @Override
                                            public void onSuccess(List<Category> categoryList) {
                                                newsView.createDrawerMenu(categoryList);
                                            }
                                            @Override
                                            public void onError(Throwable e) {
                                                Log.d("debug ",e.getMessage());
                                            }
                                        });
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveNewsInDB(List<News> newsObjectList,String category) {
        newsDAO.updateNews(newsObjectList,category)
                .subscribeOn(Schedulers.io())
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
    public void saveCategoryInDB(List<Category> categoryList) {
        newsDAO.updateCategory(categoryList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d("debug ","save Category in db");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d("debug ",e.getMessage());
                    }
                });
    }




    @Override
    public void attach(NewsView view) {
        newsView=view;
    }

    @Override
    public void detach() {
        newsView=null;
    }

    @Override
    public boolean isAttached() {
        return newsView != null;
    }
}
