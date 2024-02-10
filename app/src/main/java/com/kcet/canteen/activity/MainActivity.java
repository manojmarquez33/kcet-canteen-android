package com.kcet.canteen.activity;





import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.kcet.canteen.R;
import com.google.android.material.navigation.NavigationView;
import com.kcet.canteen.model.WebPage;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private static final int RC_APP_UPDATE = 100;
    private AdvancedWebView webView;
    private ContentLoadingProgressBar loading;
    private SwipeRefreshLayout refresh;
    private String currentUrl;
    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private RelativeLayout connectionError;
    private ActionBar bar;
    private TextView errorText;
    private View headerView;
    private ProgressBar circleLoading;
    private AppUpdateManager mAppUpdateManager;
    public static final String Roboto = "roboto.ttf";
    public static final boolean Yes = true;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUpdateManager mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, MainActivity.this
                                , RC_APP_UPDATE);

                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        toolbar = findViewById(R.id.appBar);
        if (Yes) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }

        webView = findViewById(R.id.web);
        loading = findViewById(R.id.loading);
        circleLoading = findViewById(R.id.circular_loading);
        refresh = findViewById(R.id.refresh);

        connectionError = findViewById(R.id.connection);
        ImageView errorChar = connectionError.findViewById(R.id.character);
        errorChar.setImageDrawable(getResources().getDrawable(R.drawable.character_nine));

        TextView errorMessage = connectionError.findViewById(R.id.message);
        errorMessage.setText("Make sure there is wifi or cellular data is turned on, then try again later");

        errorText = connectionError.findViewById(R.id.error);
        errorText.setText("No internet connection");

        drawerLayout = findViewById(R.id.drawer);
        navigation = findViewById(R.id.navigation);


        headerView = navigation.inflateHeaderView(R.layout.drawer_header);

        TextView drawerTitle = headerView.findViewById(R.id.drawer_title);
        TextView drawerDesc = headerView.findViewById(R.id.drawer_description);
        ImageView drawerIcon = headerView.findViewById(R.id.drawer_icon);
        drawerTitle.setText("KCET Canteen");
        drawerTitle.setTextColor(getResources().getColor(R.color.white));
        drawerDesc.setText("E canteen system managment");
        drawerDesc.setTextColor(getResources().getColor(R.color.white));
        drawerIcon.setImageResource(R.mipmap.ic_launcher_round);

        setupToolbar();
        setupNavigationDrawer();

        refresh.setOnRefreshListener(() -> {
            webView.reload();
            refresh.setRefreshing(false);
        });

        loadWebPage(getWebPages().get(0).getPageUrlAddress());


    }

    public List<WebPage> getWebPages() {
        List<WebPage> pages = new ArrayList<>();

        pages.add(new WebPage(0, R.drawable.ic_home, "Home", "https://prasanakumar19ucse002.000webhostapp.com/"));


        return pages;
    }

    private void setupNavigationDrawer() {


        Menu menu = navigation.getMenu();


        SubMenu info = menu.addSubMenu("Info");

        MenuItem aboutus = menu.add("About Us");
        aboutus.setIcon(R.drawable.ic_about);

        MenuItem share = menu.add("Share");
        share.setIcon(R.drawable.ic_share);
        MenuItem rate = menu.add("Rate us");
        rate.setIcon(R.drawable.ic_star);

        navigation.setNavigationItemSelectedListener(item -> {
            navigation.setCheckedItem(item.getItemId());


            if (item.getTitle().equals("Share")) {
                share();
                Log.d("TAG", "onNavigationItemSelected: " + item.getItemId());
            } else if (item.getTitle().equals("Rate us")) {
                rateUs();
                Log.d("TAG", "onNavigationItemSelected: " + item.getItemId());
            }  else   if (isDrawerOpen()) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            return true;
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerOpened(view);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        };

        mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebPage(String currentUrl) {
        webView.setListener(this, this);
        webView.setMixedContentAllowed(false);
        webView.loadUrl(currentUrl);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setTitle(getWebPages().get(0).getTitle());
        }


       changeToolbarFont(this, toolbar,Roboto );
        toolbar.setTitleTextColor(getResources().getColor(R.color.merun));
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void changeToolbarFont(MainActivity mainActivity, Toolbar toolbar, String roboto) {
    }

    private boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /*@SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                share();
                break;
            case R.id.rate:
                rateUs();
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void share() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, R.string.sharetext + "https://play.google.com/store/apps/details?id=com.viyaga.shoppe ");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            e.printStackTrace();
        }
    }

    private void rateUs() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.rate_us_view);

        RatingBar ratingBar = dialog.findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (rating >= 3) {
                try {
                    Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    dialog.dismiss();
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }



    private void showConnectionError() {
        webView.setVisibility(View.INVISIBLE);
        connectionError.setVisibility(View.VISIBLE);
    }

    private void hideConnectionError() {
        webView.setVisibility(View.VISIBLE);
        connectionError.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void handleError() {
        //nada
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (!url.contains("file:///android_asset")) {
            if (isNetworkAvailable()) {

                hideConnectionError();
                if (url != null) {
                    currentUrl = url;
                }
            } else {
                showConnectionError();
            }
        } else {
            hideConnectionError();
        }
    }

    @Override
    public void onPageFinished(String url) {
        webView.loadUrl("javascript:(function() { " +
                "var head = document.getElementsByTagName('header')[0];"
                + "head.parentNode.removeChild(head);" +
                "})()");
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        handleError();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }


    @Override
    public void onExternalPageRequest(String url) {
        //nada
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop()
    {
        if(mAppUpdateManager!=null) mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private InstallStateUpdatedListener installStateUpdatedListener =new InstallStateUpdatedListener()
    {
        @Override
        public void onStateUpdate(InstallState state)
        {
            if(state.installStatus() == InstallStatus.DOWNLOADED)
            {
                showCompletedUpdate();
            }
        }
    };

    private void showCompletedUpdate()
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"New app is ready!",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mAppUpdateManager.completeUpdate();
            }
        });
        snackbar.show();

    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    /* we can check without requestCode == RC_APP_UPDATE because
    we known exactly there is only requestCode from  startUpdateFlowForResult() */
        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK) {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}