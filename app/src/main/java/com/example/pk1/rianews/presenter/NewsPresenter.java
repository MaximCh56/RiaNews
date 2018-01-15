package com.example.pk1.rianews.presenter;


import com.example.pk1.rianews.model.POJO.Category;
import com.example.pk1.rianews.model.POJO.News;
import com.example.pk1.rianews.view.NewsView;

import java.util.List;

public interface NewsPresenter {
    void requestJsonNews(String categoryUrl,String categoryName);
    void saveCategoryInDB(List<Category> categoryList);
    void requestJsonCategory();
    void saveNewsInDB(List<News> newsObjectList,String category);
    void attach(NewsView view);
    void detach();
    boolean isAttached();
}
