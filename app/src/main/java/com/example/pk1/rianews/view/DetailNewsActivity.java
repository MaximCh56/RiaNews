package com.example.pk1.rianews.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pk1.rianews.R;
import com.example.pk1.rianews.model.POJO.Category;
import com.example.pk1.rianews.model.POJO.News;
import com.example.pk1.rianews.model.dataBase.NewsDAO;
import com.example.pk1.rianews.model.network.JSOUPClient;
import com.example.pk1.rianews.presenter.DetailNewsPresenter;
import com.example.pk1.rianews.presenter.DetailNewsPresenterImpl;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailNewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,DetailNewsView {
    @BindView(R.id.imageViewNews) ImageView imageViewNews;
    @BindView(R.id.textViewNews) TextView textViewNews;
    @BindView(R.id.textViewTitle) TextView textViewTitle;
    @BindView(R.id.textViewSubtitle) TextView textViewSubtitle;
    private List<Category> categoryList;
    private DetailNewsPresenter detailNewsPresenter;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        detailNewsPresenter=new DetailNewsPresenterImpl(NewsDAO.getInstance(this), JSOUPClient.getInstance());
        detailNewsPresenter.attach(this);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            News news = (News) bundle.getSerializable("news");
            categoryList= (List<Category>) bundle.getSerializable("categoryList");
            textViewTitle.setText(news.getTitle());
            textViewSubtitle.setText(news.getSubtitle());
            detailNewsPresenter.requestJsonNewsText(news);
            createDrawerMenu(categoryList);
        }

    }

    public void createDrawerMenu(List<Category> categoryList) {
        this.categoryList=categoryList;
        for (int i = 0; i < categoryList.size(); i++) {
            menu.add(0, Menu.FIRST+i, Menu.FIRST, categoryList.get(i).getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detailNewsPresenter.detach();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(DetailNewsActivity.this, NewsActivity.class);
        intent.putExtra("category",categoryList.get(id-1));
        startActivity(intent);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showText(String text) {
        textViewNews.setText(Html.fromHtml(text));
        textViewNews.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void showImage(String url) {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        imageViewNews.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels/3));
        Picasso.with(getApplicationContext())
                .load(url)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageViewNews.setBackground(new BitmapDrawable(DetailNewsActivity.this.getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
