package com.example.newfestivalpost.NewData.NewActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newfestivalpost.Model.home_content.Video;
import com.example.newfestivalpost.NewData.Adapter.CommonGridAdapter;
import com.example.newfestivalpost.NewData.Adapter.HomePageAdapter;
import com.example.newfestivalpost.NewData.Util.SpacingItemDecoration;
import com.example.newfestivalpost.NewData.Util.Tools;
import com.example.newfestivalpost.NewData.model.CommonModels;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Retrofit.Api;
import com.example.newfestivalpost.payment.Config;
import com.example.newfestivalpost.payment.Network.RetrofitClient;
import com.example.newfestivalpost.payment.Utils.NetworkInst;
import com.example.newfestivalpost.payment.Utils.RtlUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.example.newfestivalpost.Activities.ActivityHome.iv_createposter;
import static com.example.newfestivalpost.Activities.ActivityHome.iv_home;
import static com.example.newfestivalpost.Activities.ActivityHome.iv_image;
import static com.example.newfestivalpost.Activities.ActivityHome.iv_setting;
import static com.example.newfestivalpost.Activities.ActivityHome.iv_subscript;
import static com.example.newfestivalpost.Activities.ActivityHome.iv_video;

public class ItemPosterActivity extends AppCompatActivity {

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private CommonGridAdapter mAdapter;
    private List<CommonModels> list = new ArrayList<>();

    private boolean isLoading = false;
    private ProgressBar progressBar;
    private int pageCount = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String id = "", type = "";
    private CoordinatorLayout coordinatorLayout;
    private TextView tvNoItem;
    private RelativeLayout adView;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        setTheme(R.style.AppThemeLight);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "movie_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adView = findViewById(R.id.adView);
        coordinatorLayout = findViewById(R.id.coordinator_lyt);
        progressBar = findViewById(R.id.item_progress_bar);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        tvNoItem = findViewById(R.id.tv_noitem);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        if(getIntent().getStringExtra("title").equalsIgnoreCase("festival")){
            mAdapter = new CommonGridAdapter(this, list, "festival", ItemPosterActivity.this);
        } else {
            mAdapter = new CommonGridAdapter(this, list, "imovie", ItemPosterActivity.this);
        }

//        mAdapter = new CommonGridAdapter(this, list, "imovie", ItemPosterActivity.this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    pageCount = pageCount + 1;
                    isLoading = true;
                    progressBar.setVisibility(View.VISIBLE);

                    getPosterByGenreId(id, pageCount);

                }
            }
        });


        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        if (new NetworkInst(this).isNetworkAvailable()) {
            initData();
        } else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                coordinatorLayout.setVisibility(View.GONE);

                pageCount = 1;

                list.clear();
                recyclerView.removeAllViews();
                mAdapter.notifyDataSetChanged();

                if (new NetworkInst(ItemPosterActivity.this).isNetworkAvailable()) {
                    initData();
                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    private void initData() {
        if (id == null) {
            getPoster(pageCount);
        } else {
            getPosterByGenreId(id, pageCount);
        }

    }

    private void getPosterByGenreId(String id, int pageNum) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        Call<List<Video>> call = api.getPosterByGenreId(Config.API_KEY, id, pageNum);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, retrofit2.Response<List<Video>> response) {
                if (response.code() == 200) {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.body().size() == 0 && pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    } else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        Video video = response.body().get(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(video.getThumbnailUrl());
                        models.setTitle(video.getTitle());
                        models.setQuality(video.getVideoQuality());
                        models.setReleaseDate(video.getRelease());
                        models.setIsPaid(video.getIsPaid());
                        if (video.getIsTvseries().equals("1")) {
                            models.setVideoType("tvseries");
                        } else {
                            models.setVideoType("movie");
                        }


                        models.setId(video.getVideosId());
                        list.add(models);
                    }

                    mAdapter.notifyDataSetChanged();
                } else {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (pageCount == 1) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void getPoster(int pageNum) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        Api api = retrofit.create(Api.class);
        Call<List<Video>> call = api.getPoster(Config.API_KEY, pageNum);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, retrofit2.Response<List<Video>> response) {
                if (response.code() == 200) {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    if (response.body().size() == 0 && pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    } else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        Video video = response.body().get(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(video.getThumbnailUrl());
                        models.setTitle(video.getTitle());
                        models.setQuality(video.getVideoQuality());
                        models.setReleaseDate(video.getRelease());
                        models.setIsPaid(video.getIsPaid());
                        if (video.getIsTvseries().equals("1")) {
                            models.setVideoType("tvseries");
                        } else {
                            models.setVideoType("movie");
                        }


                        models.setId(video.getVideosId());
                        list.add(models);
                    }

                    mAdapter.notifyDataSetChanged();
                } else {
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (pageCount == 1) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (pageCount == 1) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        iv_home.setImageResource(R.drawable.nav_home);
        iv_video.setImageResource(R.drawable.nav_video);
        iv_image.setImageResource(R.drawable.nav_image);
        iv_subscript.setImageResource(R.drawable.nav_crown1);
        iv_createposter.setImageResource(R.drawable.nav_createpost);
        iv_setting.setImageResource(R.drawable.nav_setting);
    }


}
