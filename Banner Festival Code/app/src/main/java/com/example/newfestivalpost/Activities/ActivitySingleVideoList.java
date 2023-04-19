package com.example.newfestivalpost.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newfestivalpost.Adapters.AdapterCategoryList;
import com.example.newfestivalpost.Adapters.AdapterFrames;
import com.example.newfestivalpost.Adapters.AdapterSingleCatVideoList;
import com.example.newfestivalpost.AnalyticsApplication;
import com.example.newfestivalpost.BuildConfig;
import com.example.newfestivalpost.Model.SubCategoryModel;
import com.example.newfestivalpost.ModelRetrofit.VideoCategoriesData;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Utills.Constance;
import com.example.newfestivalpost.Utills.SharedPrefrenceConfig;
import com.example.newfestivalpost.payment.Config;
import com.example.newfestivalpost.payment.Network.RetrofitClient;
import com.example.newfestivalpost.payment.Network.apis.UserDataApi;
import com.example.newfestivalpost.payment.Network.models.User;
import com.example.newfestivalpost.payment.Utils.PreferenceUtils;
import com.example.newfestivalpost.payment.activity.LoginActivity;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.xm.weidongjian.popuphelper.PopupWindowHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActivitySingleVideoList extends AppCompatActivity {

    Context context;
    AdView mAdView;
    LinearLayout facbook_ad_banner;
    RecyclerView rv_singlevideoitems;
    ArrayList<SubCategoryModel> cat1_list = new ArrayList<>();
    TextView tv_singlevideocatname;
    String childitemtittle, catnameid, videoUrl;
    ImageView iv_backarrow, iv_al_language;
    VideoView vv_playvideo;
    LinearLayout ll_selectedvideo, ll_next_singlevideo;
    SharedPrefrenceConfig sharedPrefrenceConfig;
    MediaController mediacontroller;

    String selectedvideopath, language;
    ArrayList<VideoCategoriesData> videoCategoriesDataArrayList;
    public AdapterSingleCatVideoList adapterSingleCatVideoList;
    boolean df = false;
    RelativeLayout rl_frame;
    int frame;
    ProgressBar pb_video;
    public AdapterFrames adapterFrame;
    Tracker mTracker;
    public PopupWindowHelper popupWindowHelper;
    View popupview_down;
    ProgressDialog mProgressDialog;
    String id;
    String frameName[] = {"All", "English", "Hindi", "Gujarati"};
    String location, folder;


    public static ActivitySingleVideoList instance = null;

    public ActivitySingleVideoList() {
        instance = ActivitySingleVideoList.this;
    }

    public static synchronized ActivitySingleVideoList getInstance() {
        if (instance == null) {
            instance = new ActivitySingleVideoList();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_video_list);

        context = ActivitySingleVideoList.this;
        bindView();
        sharedPrefrenceConfig = new SharedPrefrenceConfig(context);
        language = sharedPrefrenceConfig.getPrefString(context, Constance.language, "");

        id = PreferenceUtils.getUserId(ActivitySingleVideoList.this);

        childitemtittle =  getIntent().getExtras().getString("childitemtittle");
        catnameid = getIntent().getExtras().getString("catnameid");
        videoUrl = getIntent().getExtras().getString("video");

        selectedvideopath = videoUrl;

        location = Environment.getExternalStorageDirectory().toString() + "/Android/media/" + getPackageName() + "/" + getString(R.string.app_name) + "/Media/video/" + childitemtittle +".mp4";
        Log.e("60060", "onCreate: "+ Constance.SaveVideoDirectory);
        if (!Constance.SaveVideoDirectory.exists()) {

            Constance.SaveVideoDirectory.mkdirs();

        } else {
            File[] listFiles = Constance.SaveVideoDirectory.listFiles();

            for(File listFile : listFiles) {
                long diff = new Date().getTime() - new File(location).lastModified();
                long cutoff = (7 * (24 * 60 * 60 * 1000));

                if (diff > cutoff) {
                    listFile.delete();
                }

            }
        }


        if(new File(location).exists()) {
            Toast.makeText(context, "Already Downloaded", Toast.LENGTH_SHORT).show();

            playVideo();
//            Intent i = new Intent(context, ActivityCreatePost.class);
//            i.putExtra("videoName", childitemtittle);
//            startActivity(i);
        } else {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.gredient_dialog));
            mProgressDialog.setMessage("Video Downloading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            ActivitySingleVideoList.DownloadTask downloadFile = new ActivitySingleVideoList.DownloadTask(context);
            downloadFile.execute(selectedvideopath);

            TextView tv1 = (TextView) mProgressDialog.findViewById(android.R.id.message);
            tv1.setTextSize(16);
            tv1.setTextColor(context.getResources().getColor(R.color.colorBlack));
        }


        Log.e("555555", "onCreate: "+ videoUrl + "----> " + childitemtittle);

        calculationForHeight();

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        tv_singlevideocatname.setText(childitemtittle);
        rv_singlevideoitems.setLayoutManager(new GridLayoutManager(context, 3));
        videoCategoriesDataArrayList = new ArrayList<>();

        iv_al_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                popupview_down = LayoutInflater.from(context).inflate(R.layout.category_dropdown_raw, null);
                popupWindowHelper = new PopupWindowHelper(popupview_down);
                popupWindowHelper.showAsDropDown(v, 0, 0);
                RecyclerView rv_cat_dd_raw = popupview_down.findViewById(R.id.rv_cat_dd_raw);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                rv_cat_dd_raw.setLayoutManager(gridLayoutManager);
                rv_cat_dd_raw.setHasFixedSize(true);

                AdapterCategoryList adapterCategory = new AdapterCategoryList(context, frameName, "video");
                rv_cat_dd_raw.setAdapter(adapterCategory);
            }
        });

        iv_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ll_next_singlevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constance.ComeFrom = "video";
                Constance.FromSinglecatActivity = selectedvideopath;
                if (PreferenceUtils.isLoggedIn(ActivitySingleVideoList.this)) {

                    Intent i = new Intent(context, ActivityCreatePost.class);
                    i.putExtra("videoName", childitemtittle);
                    startActivity(i);

              /*  i.putExtra("FromSinglecatActivity", selectedvideopath);
                i.putExtra("ComeFrom", "video");*/

                } else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("you need a account to continue ... ")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent i = new Intent(context, LoginActivity.class);
                                    startActivity(i);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }


    public void bindView() {
        rv_singlevideoitems = findViewById(R.id.rv_singlevideoitems);
        tv_singlevideocatname = findViewById(R.id.tv_singlevideocatname);
        iv_backarrow = findViewById(R.id.iv_backarrow);
        ll_selectedvideo = findViewById(R.id.ll_selectedvideo);
        vv_playvideo = findViewById(R.id.iv_videoshow);
        ll_next_singlevideo = findViewById(R.id.ll_next_singlevideo);
        rl_frame = findViewById(R.id.rl_frame);
        pb_video = findViewById(R.id.pb_video);
        iv_al_language = findViewById(R.id.iv_al_language);


    }

  public void playVideo() {

        vv_playvideo.start();

        if (selectedvideopath != null) {
            if(new File(location).exists()) {
                vv_playvideo.setVideoPath(Constance.SaveVideoDirectory + "/" + childitemtittle + ".mp4");
            } else {
                vv_playvideo.setVideoPath(selectedvideopath);
            }
        } else {
            Toast.makeText(context, "Not get the video path", Toast.LENGTH_LONG).show();
        }
        mediacontroller = new MediaController(context);
        mediacontroller.setMediaPlayer(vv_playvideo);
        vv_playvideo.setMediaController(mediacontroller);
        vv_playvideo.requestFocus();
        vv_playvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                vv_playvideo.start();
            }
        });
        vv_playvideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                vv_playvideo.pause();
            }
        });
    }



    public void calculationForHeight() {
        ViewTreeObserver vto = ll_selectedvideo.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    ll_selectedvideo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ll_selectedvideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int heightOfImage;
                int widthOfImage;
                widthOfImage = ll_selectedvideo.getMeasuredWidth();//1080 horizontalview
                heightOfImage = ll_selectedvideo.getMeasuredHeight();//236

                ViewGroup.LayoutParams params = ll_selectedvideo.getLayoutParams();
                params.height = widthOfImage;
                params.width = widthOfImage;
                ll_selectedvideo.setLayoutParams(params);
            }
        });


    }

    Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        float scalex = wantedWidth / originalWidth;
        float scaley = wantedHeight / originalHeight;
        float xTranslation = 0.0f;
        float yTranslation = (wantedHeight - originalHeight * scaley) / 2.0f;
        m.postTranslate(xTranslation, yTranslation);
        m.preScale(scalex, scaley);
        // m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, m, paint);
        return output;
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Image~" + "Google Analytics Testing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void setselectedvideo(String video_url) {
        selectedvideopath = video_url;
        playVideo();
    }

    public void setFrame(int frame1) {
        rl_frame.setBackgroundResource(frame1);
        Log.d("frsrsgh", "" + frame);
        frame = frame1;
    }


    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {

                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();


                Log.e("888888", "doInBackground: " + location );


                File file = new File(location);
                if (!file.exists())
                    file.createNewFile();

                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    ignored.getStackTrace();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();

            if (mProgressDialog != null && !mProgressDialog.isShowing())
                mProgressDialog.show();


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();

            }


            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(context, new String[]{new File(location).getAbsolutePath()},
                            null, (path, uri) -> {
                            });
                } else {

                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(location));

                    context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", uri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            scanMedia(location);


            if (result != null){
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
                Log.e("888888", "onPostExecute: " + result );}
            else
                Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show();
            playVideo();
        }


    }
    private void scanMedia(String path) {
        File file = new File(path);

        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);

        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
    }
}