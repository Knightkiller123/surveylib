package easygov.saral.harlabh.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import easygov.saral.harlabh.BuildConfig;
import easygov.saral.harlabh.R;
import easygov.saral.harlabh.activity.SplashActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by apoorv on 10/08/17.
 */

public class GeneralFunctions {

    public static Dialog dialog;
    public static PopupWindow pw;
    public static Snackbar bar;
    public static void showDialog(Context context)
    {
        try {
            dialog= new Dialog(context, R.style.CustomDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_progress);
            dialog.show();
        }
        catch (Exception e)
        {}}

    public static void dismissDialog()
    {
        try {
            if(dialog!=null&&dialog.isShowing())
                dialog.dismiss();
        }
        catch (Exception e)
        {}

    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
            //inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /*public static String getErrorMessage(String error)
    {
        JSONObject jObjError = null;
        try {
            jObjError = new JSONObject(error);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s="";
        try {
            s=jObjError.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }*/

    public static File saveImageToExternalStorage(Bitmap finalBitmap,int n2) {
        File file;
        long length=0;
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/Harlabh_Docs");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpeg";
        file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            length = file.length();
            length = length/1024000;
            Log.d("Size",""+length);
              //Toast.makeText(context, ""+length, Toast.LENGTH_SHORT).show();
            // return null;
        }
        catch (Exception e)
        {
            Log.d("Size","error");
        }

        return file;
    }

    public static File saveFileExternalStorage(Bitmap finalBitmap) {
        File file;
        long length=0;
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/Harlabh_Docs");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".png";
        file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            length = file.length();
            length = length/1024000;
            //  Toast.makeText(context, ""+length, Toast.LENGTH_SHORT).show();
            // return null;
        }
        catch (Exception e)
        {
            //   Toast.makeText(context, "err", Toast.LENGTH_SHORT).show();
        }
        return file;
    }


    public static File savePdf(Uri uri,Context context) throws IOException
    {
        long length=0;
        File file;
        byte[] buffer=null;
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/Harlabh_Docs");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "file-" + n + ".pdf";
        file = new File(myDir, fname);

        if (file.exists())
            file.delete();

        InputStream inputfile;
        try {
            inputfile=context.getContentResolver().openInputStream(uri);
             buffer = new byte[inputfile.available()];

            inputfile.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            OutputStream outStream = new FileOutputStream(file);
            outStream.write(buffer);
        }
        catch (Exception e)
        {

        }

        try {
          length = file.length();
            length = length/1024000;
          // Toast.makeText(context, ""+length, Toast.LENGTH_SHORT).show();
           // return null;
        }
        catch (Exception e)
        {
           //Toast.makeText(context, "err", Toast.LENGTH_SHORT).show();
        }
        if(length<4)
        return file;

        else return null;
    }





    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected();
    }

    public static String getDeviceId(Context context)
    {
        if(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)!=null)
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        else return "";
    }

    public static String getIMEINumber(Context context)
    {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager.getDeviceId()!=null)
        return telephonyManager.getDeviceId();

        else return "";
    }


   /* public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }*/

    /*public static String getPath(Context context, Uri uri)  {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }*/


    /*public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }*/

   /* public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }*/


   /* public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }*/


    /*public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }*/


   public static ArrayList<String> createAuthXMLRegistered(String piddataxml) {
        ArrayList<String> asd=new ArrayList<>();


        try {
            InputStream is = new ByteArrayInputStream(piddataxml.getBytes());

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setIgnoringComments(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(is);




String key=doc.getElementsByTagName("DeviceInfo").item(0).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem("name").getTextContent();

String value=doc.getElementsByTagName("DeviceInfo").item(0).getChildNodes().item(1).getChildNodes().item(1).getAttributes().getNamedItem("value").getTextContent();

        asd.add(key);
            asd.add(value);
        }
         catch (Exception e) {
            e.printStackTrace();
          asd=new ArrayList<>();
             asd.add("error");
        }
        return asd;
    }



    public static void backPopup(View view, final Activity activity , final int decider) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater)activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.back_popup,
                    (ViewGroup)view.findViewById(R.id.rlBackPopup));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvnavigateCancel= layout.findViewById(R.id.tvnavigateCancel);
            TextView tvnavigateYes= layout.findViewById(R.id.tvnavigateYes);

            tvnavigateCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });

            tvnavigateYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(decider==0)
                    {
                        activity.finish();
                    }
                    else if(decider==1)
                    {
                        activity.getFragmentManager().popBackStack();
                    }
                    else if(decider==2)
                    {
                        //Todo : in case fragment doesnt pops back (getSupportFragment  vs getfragment issue)
                    }
                }
            });
            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void installmorpho(View view, final Activity activity) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.nomorphopopup,
                    (ViewGroup) view.findViewById(R.id.rlNoMorpho));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvnavigateCancel = layout.findViewById(R.id.tvnavigateCancel);
            TextView tvnavigateYes = layout.findViewById(R.id.tvnavigateYes);

            tvnavigateCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //    activity.onBackPressed();
                    pw.dismiss();
                }
            });


            tvnavigateYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   //activity.onBackPressed();
                    pw.dismiss();
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            ("market://details?id=com.scl.rdservice")));
                }
            });

        }
        catch (Exception e)
        {

        }
    }


    public static void otherMethodsSign(View view, final Context activity, String desc, final FragmentManager fragmentManager) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.nomorphopopup,
                    (ViewGroup) view.findViewById(R.id.rlNoMorpho));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvnavigateCancel = layout.findViewById(R.id.tvnavigateCancel);
            TextView tvnavigateYes = layout.findViewById(R.id.tvnavigateYes);
            TextView tvMethodDesc = layout.findViewById(R.id.tvMethodDesc);

            tvMethodDesc.setText(desc);
            tvnavigateCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });


            tvnavigateYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                  fragmentManager.popBackStack();
                }
            });

        }
        catch (Exception e)
        {

        }
    }
        public static void makeSnackbar(View view,String text)
    {
        try {
            bar= Snackbar.make(view,text,Snackbar.LENGTH_INDEFINITE).setAction(MyApplication.dismiss, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bar.dismiss();
                }
            });
            bar.show();
        }
        catch (Exception e)
        {

        }


    }


    public static void PleaseGrantPermission(View view, final Activity activity) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.nopermission,
                    (ViewGroup) view.findViewById(R.id.rlNoMorpho));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            TextView tvKill = layout.findViewById(R.id.tvKill);



            tvKill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                    activity.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                    pw.dismiss();

                }
            });

            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    pw.dismiss();
                }
            });




        }
        catch (Exception e)
        {

        }
    }

   /* public static void firstCheck(Prefs mPrefs, String toMatch)
    {
        try {
            List<String> user =new ArrayList<>();

            user=new Gson().fromJson(mPrefs.getString(Constants.    USER_LISTING,""),new TypeToken<List<String>>() {}.getType());
            if(user==null)
            {
                user=new ArrayList<>();
            }
            if(user!=null&&user.size()>0&&user.contains(toMatch))
            {
                mPrefs.save(Constants.IS_USER_FIRST_TIME,"no");
            }
            else
            {
                user.add(toMatch);
                mPrefs.save(Constants.USER_LISTING,new Gson().toJson(user));
                mPrefs.save(Constants.IS_USER_FIRST_TIME,"yes");
            }

        }
        catch (Exception e)
        {
            int a =0;
        }
       }*/

       public  static  void clearPrefs(Prefs mPrefs)
       {
           try {
               List<String> user =new ArrayList<>();
                try {
                    user=new Gson().fromJson(mPrefs.getString(Constants.USER_LISTING,""),new TypeToken<List<String>>() {}.getType());

                }
                catch (Exception e)
                {
                    int s=0;
                }


               mPrefs.removeAll();
               if(user!=null)
               mPrefs.save(Constants.USER_LISTING,new Gson().toJson(user));


           }
           catch (Exception e)
           {
int a =0;
           }


       }

       public static String removeStr(String original)
       {
           try {
               String toReturn="";

               for (int i=0;i<original.length();i++)
               {
                   if(original.substring(i,i+2).equals("?>")) {
                       toReturn = original.substring(i + 2, original.length());
                       break;
                   }


               }
               int from=-1;
               int to=-1;
               for(int j=0;j<toReturn.length();j++)
               {
                   if(toReturn.substring(j,j+5).equals("<Resp"))
                   {
                       from =j;
                       break;
                   }

               }
               for(int j=0;j<toReturn.length();j++)
               {

                   if(toReturn.substring(j,j+6).equals("</PidD"))
                   {
                       to=j;
                       break;
                   }
               }

               String remove=toReturn.substring(from,to);
               toReturn= toReturn.replace("<PidData>","");
               toReturn= toReturn.replace("</PidData>","");

               toReturn=toReturn.replace(remove,"");

               return toReturn;
           }
           catch (Exception e)
           {
               return  "";
           }


       }


    public static void tokenExpireAction(final Context context)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.session_expired)
                .setCancelable(false)
                .setPositiveButton(R.string.login,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context,
                                        SplashActivity.class);

                                Prefs.with(context).removeAll();
                                context.startActivity(intent);
                                ((AppCompatActivity)context).finishAffinity();
                            }
                        }).show();
    }



}
