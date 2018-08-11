package easygov.saral.harlabh.fragments;

/**
 * Created by apoorv on 27/09/17.
 */


import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;


import easygov.saral.harlabh.R;


/**
 * Created by apoorv on 20-02-2017.
 */

public class StaticWebFragment extends Fragment {
    private WebView wbWebView;
    private String heading;
    private String urls;
    private TextView tvWebHeading;
    private ImageView ivStaticWebBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_staticweb,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        tvWebHeading= view.findViewById(R.id.tvWebHeading);
        ivStaticWebBack= view.findViewById(R.id.ivStaticWebBack);

        ivStaticWebBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        wbWebView= view.findViewById(R.id.wbStaticView);
        wbWebView.getSettings().setJavaScriptEnabled(true);
        wbWebView.getSettings().setLoadWithOverviewMode(true);

        wbWebView.getSettings().setUseWideViewPort(true);
        wbWebView.setWebViewClient(new MyBrowser());
        wbWebView.getSettings().setDomStorageEnabled(true);
        Bundle bundle=new Bundle();
        bundle=getArguments();
        if(bundle!=null)
        {
            //heading=bundle.getString("heading");
            urls=bundle.getString("url");
            heading=bundle.getString("heading");
            tvWebHeading.setText(heading);
        }

        wbWebView.loadUrl(urls);
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
