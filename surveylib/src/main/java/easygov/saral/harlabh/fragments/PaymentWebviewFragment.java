package easygov.saral.harlabh.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.net.URLEncoder;

import easygov.saral.harlabh.activity.EsignDoneActivity;
import easygov.saral.harlabh.activity.ManagementActivity;
import easygov.saral.harlabh.activity.PaymentFailedActivity;
import easygov.saral.harlabh.activity.SuccessFulPaymentActivity;
import easygov.saral.harlabh.utils.AndroidBug5497Workaround;
import easygov.saral.harlabh.utils.GeneralFunctions;
import easygov.saral.harlabh.utils.Prefs;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.utils.Constants;

/**
 * Created by apoorv on 26/09/17.
 */

public class PaymentWebviewFragment extends Fragment {
    private WebView wbPaymentWebview;
    private Prefs mPrefs;
    private ImageView ivCrossGoto;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_paymentwebview,container,false);
        init(view);
        webviewSettings();
        setPaymentView();
        return view;
    }








    private void setPaymentView() {
        String postData = null;
        GeneralFunctions.showDialog(getActivity());

        try {
            postData = "merchantTxnId=" + URLEncoder.encode(mPrefs.getString(Constants.MerchantId,""), "UTF-8") +
                    "&orderAmount=" + URLEncoder.encode(mPrefs.getString(Constants.OrderAmount,""), "UTF-8")
                    + "&currency=" + URLEncoder.encode(mPrefs.getString(Constants.Currency,""), "UTF-8") +
                    "&email=" + URLEncoder.encode(mPrefs.getString(Constants.StaticEmail,""), "UTF-8")
                    + "&phoneNumber=" + URLEncoder.encode(mPrefs.getString(Constants.BillContact,""), "UTF-8")
                    + "&returnUrl=" + URLEncoder.encode(mPrefs.getString(Constants.ReturnUrl,""), "UTF-8")
                    + "&notifyUrl=" + URLEncoder.encode(mPrefs.getString(Constants.NotifyUrl,""), "UTF-8")
                    + "&secSignature=" + URLEncoder.encode(mPrefs.getString(Constants.Signature,""), "UTF-8")
                    + "&contactNo=" + URLEncoder.encode(mPrefs.getString(Constants.BillContact,""), "UTF-8")
                    + "&firstName=" + URLEncoder.encode(mPrefs.getString(Constants.BillFirst,""), "UTF-8")
                    + "&lastName=" + URLEncoder.encode(mPrefs.getString(Constants.BillLastName,""), "UTF-8")
                    + "&addressStreet1=" + URLEncoder.encode(mPrefs.getString(Constants.BillStreet1,""), "UTF-8")
                    + "&addressStreet2=" + URLEncoder.encode(mPrefs.getString(Constants.BillStreet2,""), "UTF-8")
                    + "&addressCity=" + URLEncoder.encode(mPrefs.getString(Constants.BillCity,""), "UTF-8")
                    + "&addressState=" + URLEncoder.encode(mPrefs.getString(Constants.BillState,""), "UTF-8")
                    + "&addressCountry=" + URLEncoder.encode(mPrefs.getString(Constants.BillCountry,""), "UTF-8")
                    + "&addressZip=" + URLEncoder.encode(mPrefs.getString(Constants.BillZip,""), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            GeneralFunctions.dismissDialog();

        }
        wbPaymentWebview.postUrl(mPrefs.getString(Constants.PaymentUrl,""), postData.getBytes());
    }

    private void init(View view) {
        wbPaymentWebview= view.findViewById(R.id.wbPaymentWebview);
        ivCrossGoto= view.findViewById(R.id.ivCrossGoto);
        mPrefs=Prefs.with(getActivity());
        //GeneralFunctions.showDialog(getActivity());
        ivCrossGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), ManagementActivity.class);
                intent.putExtra("frompayment","yes");
                startActivity(intent);
                getActivity().finish();
            }
        });
        AndroidBug5497Workaround.assistActivity(getActivity());

        wbPaymentWebview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GeneralFunctions.hideSoftKeyboard(getActivity());
                return false;
            }
        });
    }

    private void webviewSettings() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);





        wbPaymentWebview.getSettings().setJavaScriptEnabled(true);
        wbPaymentWebview.getSettings().setAllowFileAccess(true);
        wbPaymentWebview.getSettings().setDomStorageEnabled(true);
        wbPaymentWebview.getSettings().setDatabaseEnabled(true);
        wbPaymentWebview.getSettings().setSupportMultipleWindows(true);
        wbPaymentWebview.addJavascriptInterface(new WebInterface(getContext()), "app");
        WebView.setWebContentsDebuggingEnabled(true);
        wbPaymentWebview.getSettings().setPluginState(WebSettings.PluginState.ON);


        wbPaymentWebview.setWebViewClient(new MyBrowser());

    }


    @SuppressWarnings("deprecation")
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
            super.shouldOverrideUrlLoading(view,url);
            GeneralFunctions.dismissDialog();
            if(url.contains(mPrefs.getString(Constants.ReturnUrl,"")))
            {

                Intent intent =new Intent(getActivity(), ManagementActivity.class);
                intent.putExtra("frompayment","yes");
                startActivity(intent);
                /*Intent intent = new Intent(getActivity(),
                        HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               // mPrefs.removeAll();
                startActivity(intent);*/
            }
            //view.loadUrl(url);

            //  wbWebView.loadUrl(url);

            return false;

        }




        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {

            Log.d("onReceivedSslError", "onReceivedSslError");
            //  mbErrorOccured = true;
            //tvError.setText("Unable to connect to Server");
            //showErrorLayout();
            handler.proceed();
            super.onReceivedSslError(view,handler,error);


        }

       /* @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }*/

        @Override
        public void onPageFinished(android.webkit.WebView view, String url) {

            String host = String.valueOf(Uri.parse(url));
            GeneralFunctions.dismissDialog();
            super.onPageFinished(view, url);
        }


    }


    //todo: need to pass next link here
    public class WebInterface {
        Context context;

        WebInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void notifyStatus(String asd) {
           // Intent intent = new Intent(getActivity(), GpsCheck.class);

            //startActivity(intent);


            //Todo: convert checks to next link
            if(asd.equals("1")) {
                if(mPrefs.getString(Constants.FromAadhar,"").equals("YES")) {



                    Intent intent = new Intent(getActivity(), SuccessFulPaymentActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                else {

                    Intent intent = new Intent(getActivity(), EsignDoneActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            else {

                Intent intent = new Intent(getActivity(), PaymentFailedActivity.class);
                startActivity(intent);
                getActivity().finish();
               // getActivity().finish();
            }
        }
    }
}
