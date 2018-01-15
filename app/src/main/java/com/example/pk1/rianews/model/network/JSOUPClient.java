package com.example.pk1.rianews.model.network;


import android.util.Log;

import com.example.pk1.rianews.model.POJO.Category;
import com.example.pk1.rianews.model.POJO.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

public class JSOUPClient {

    private static JSOUPClient singletonInstance;

    private JSOUPClient() {
    }

    public static JSOUPClient getInstance() {
        if (null == singletonInstance) {
            singletonInstance = new JSOUPClient();
        }
        return singletonInstance;
    }

    public Single<News> getTextNews(final News news)throws IOException {
        return Single.fromCallable(new Callable<News>() {
            @Override
            public News call() throws Exception {
                Document politic = Jsoup.connect(news.getUrl()).userAgent("Mozilla").get();
                Element divPolitic = politic.select("div.b-article__body.js-mediator-article.mia-analytics").first();
                news.setText(divPolitic.select("p").toString());
                news.setFullSizeImageUrl(politic.select("div.l-photoview__open").first().child(3).attr("src"));
                return news;
            }
        });
    }

    public Single<List<Category>> getCategory() throws IOException {
        return Single.fromCallable(new Callable<List<Category>>() {
            @Override
            public List<Category> call() throws Exception {
                List<Category> categoryList = new ArrayList<>();
                Document html = Jsoup.connect("https://ria.ru/").userAgent("Mozilla").get();
                Element div = html.select("div.b-footer__nav-content.m-active").first();
                Element elementUl = div.child(0);
                Elements elements = elementUl.children();
                for (Element result : elements) {
                    if (!result.child(0).attr("href").equals("/")) {
                        if (!result.child(0).attr("href").equals("/culture/")) {
                            categoryList.add(new Category("https://ria.ru" + result.child(0).attr("href"), result.child(0).child(0).text()));
                        }
                    }
                }
                return categoryList;
            }
        });

    }

    public Single<List<News>> getNewsForCategory(final String category,final String name) throws IOException {
        return Single.fromCallable(new Callable<List<News>>() {
            @Override
            public List<News> call() throws Exception {
                List<News> newsObjectList = new ArrayList<>();
                Document politic = Jsoup.connect(category).userAgent("Mozilla").get();
                Element divPolitic = politic.select("div.b-list").first();
                Elements elementsPolitic = divPolitic.children();
                for (Element result : elementsPolitic) {
                    if (!result.child(0).attr("href").equals("/")) {
                        newsObjectList.add(new News("https://ria.ru"+result.child(0).attr("href"), result.child(0).child(1).child(0).text(), result.select("div.b-list__item-time").first().child(0).text() + " " + result.select("div.b-list__item-date").first().child(0).text(), result.child(0).child(0).child(0).child(0).attr("src"),name));
                    }
                }
                return newsObjectList;
            }
        });
    }
}
