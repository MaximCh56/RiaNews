package com.example.pk1.rianews.model.dataBase;

import android.content.Context;

import com.example.pk1.rianews.model.POJO.Category;
import com.example.pk1.rianews.model.POJO.News;

import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.realm.Realm;



public class NewsDAO {

    private static NewsDAO singletonInstance;

    private NewsDAO(Context context) {
        Realm.init(context);
    }

    public static NewsDAO getInstance(Context context) {
        if (null == singletonInstance) {
            singletonInstance = new NewsDAO(context);
        }
        return singletonInstance;
    }


    public Single<List<News>> getAllNews(final String category){
        Realm realm=Realm.getDefaultInstance();
        List<News> news=realm.copyFromRealm(realm.where(News.class).equalTo("category",category).findAll());
        realm.close();
        return Single.just(news);
    }

    public Completable updateNews(final List<News> newsObjectList,final String category){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.where(News.class).equalTo("category",category).findAll().deleteAllFromRealm();
                realm.insertOrUpdate(newsObjectList);
                realm.commitTransaction();
                realm.close();
            }
        });
    }

    public Completable updateCategory(final List<Category> categoryList){
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.where(Category.class).findAll().deleteAllFromRealm();
                realm.insertOrUpdate(categoryList);
                realm.commitTransaction();
                realm.close();
            }
        });
    }


    public Single<List<Category>> getAllCategory() {
        Realm realm=Realm.getDefaultInstance();
        List<Category> categoryList=realm.copyFromRealm(realm.where(Category.class).findAll());
        realm.close();
        return Single.just(categoryList);
    }

    public Completable updateSingleNews(final News news) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.insertOrUpdate(news);
                realm.commitTransaction();
                realm.close();
            }
        });
    }
}
