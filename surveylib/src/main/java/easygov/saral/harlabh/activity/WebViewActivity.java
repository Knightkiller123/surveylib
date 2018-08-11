package easygov.saral.harlabh.activity;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import easygov.saral.harlabh.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView wbStat;
    private ImageView ivWebBack;
    String urls,heading;
    private TextView tvTit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        wbStat= findViewById(R.id.wbStat);
        tvTit= findViewById(R.id.tvTit);
        ivWebBack= findViewById(R.id.ivWebBack);
        ivWebBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        wbStat.getSettings().setJavaScriptEnabled(true);
        wbStat.setWebViewClient(new MyBrowser());
        //webview.getSettings().setJavaScriptEnabled(true);
        //wbStat.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        wbStat.getSettings().setLoadWithOverviewMode(true);

        wbStat.getSettings().setUseWideViewPort(true);
        wbStat.getSettings().setDomStorageEnabled(true);
        Bundle bundle=new Bundle();
        bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            //heading=bundle.getString("heading");
            urls=bundle.getString("url");
            heading=bundle.getString("heading");
            tvTit.setText(heading);
        }

        wbStat.loadUrl(urls);

    }

    private class MyBrowser extends WebViewClient
    {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {

            Log.d("onReceivedSslError", "onReceivedSslError");
            super.onReceivedSslError(view,handler,error);


        }
    }

}
