package com.example.pk1.rianews.view;


import com.example.pk1.rianews.model.POJO.Category;
import com.example.pk1.rianews.model.POJO.News;

import java.util.List;

public interface NewsView {
    void showNews(List<News> newsObjectList);
    void createDrawerMenu(List<Category> categoryList);
    void showError(String error);
}
