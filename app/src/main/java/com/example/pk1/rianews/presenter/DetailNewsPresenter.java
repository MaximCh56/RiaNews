package com.example.pk1.rianews.presenter;

import com.example.pk1.rianews.model.POJO.News;
import com.example.pk1.rianews.view.DetailNewsView;
import com.example.pk1.rianews.view.NewsView;

import java.util.List;

public interface DetailNewsPresenter {
    void requestJsonNewsText(News news);
    void saveNewsInDB(News news);
    void attach(DetailNewsView view);
    void detach();
    boolean isAttached();
}
