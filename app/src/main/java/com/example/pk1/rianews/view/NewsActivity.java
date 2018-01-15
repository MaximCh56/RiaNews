package com.example.pk1.rianews.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pk1.rianews.R;
import com.example.pk1.rianews.model.POJO.Category;
import com.example.pk1.rianews.model.POJO.News;
import com.example.pk1.rianews.model.dataBase.NewsDAO;
import com.example.pk1.rianews.model.network.JSOUPClient;
import com.example.pk1.rianews.model.network.NetworkAvailable;
import com.example.pk1.rianews.presenter.NewsPresenter;
import com.example.pk1.rianews.presenter.NewsPresenterImpl;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,NewsView {
    @BindView(R.id.recyclerViewNews) RecyclerView recyclerViewNews;
    private List<News> posts;
    private List<Category> categoryList;
    private NewsPresenter newsPresenter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewNews.setLayoutManager(layoutManager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        newsPresenter=new NewsPresenterImpl(NewsDAO.getInstance(this), JSOUPClient.getInstance());
        newsPresenter.attach(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            Category category= (Category) bundle.getSerializable("category");
            newsPresenter.requestJsonNews(category.getUrl(),category.getName());
            newsPresenter.requestJsonCategory();
        }else {
            newsPresenter.requestJsonNews("https://ria.ru/politics/","politics");
            newsPresenter.requestJsonCategory();
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.detach();
    }

    @Override
    public void showNews(List<News> newsObjectList) {
        posts=newsObjectList;
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(posts);
        recyclerViewNews.setAdapter(adapter);
    }

    @Override
    public void createDrawerMenu(List<Category> categoryList) {
        this.categoryList=categoryList;
        for (int i = 0; i < categoryList.size(); i++) {
            menu.add(0, Menu.FIRST+i, Menu.FIRST, categoryList.get(i).getName());
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    class RecyclerViewHolders extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.subtitle)
        TextView subtitle;
        @BindView(R.id.imageViewMin)
        ImageView imageViewMin;
        RecyclerViewHolders(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
        private List<News> itemList;

        RecyclerViewAdapter(List<News> itemList) {
            this.itemList = itemList;
        }

        @Override
        public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
            return new RecyclerViewHolders(layoutView);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolders holder, final int position) {
            holder.title.setText(itemList.get(position).getTitle());
            holder.subtitle.setText(itemList.get(position).getSubtitle());
            Picasso.with(getApplicationContext())
                    .load(itemList.get(position).getImageUrl())
                    .into(holder.imageViewMin);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetworkAvailable.isNetworkAvailable(NewsActivity.this)){
                        Intent intent = new Intent(getApplicationContext(), DetailNewsActivity.class);
                        intent.putExtra("news",posts.get(position));
                        intent.putExtra("categoryList",(ArrayList<Category>)categoryList);
                        startActivity(intent);
                    }else {
                        if(posts.get(position).getText()!=null){
                            Intent intent = new Intent(getApplicationContext(), DetailNewsActivity.class);
                            intent.putExtra("news",posts.get(position));
                            intent.putExtra("categoryList",(ArrayList<Category>)categoryList);
                            startActivity(intent);
                        }
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return this.itemList.size();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        newsPresenter.requestJsonNews(categoryList.get(id-1).getUrl(),categoryList.get(id-1).getName());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
