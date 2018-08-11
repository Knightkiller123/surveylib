package easygov.saral.harlabh.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
        
        private static SmsListener mListener;

        @Override
        public void onReceive(Context context, Intent intent) {

            try {


                Bundle data = intent.getExtras();
                Pattern p = Pattern.compile("(|^)\\d{6}");
                Object[] pdus = (Object[]) data.get("pdus");

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    String sender = smsMessage.getDisplayOriginatingAddress();
                    //You must check here if the sender is your provider and not another one with same text.
                    String otp="";
                    String messageBody = smsMessage.getMessageBody();

                    if(messageBody!=null)
                    {
                        Matcher m = p.matcher(messageBody);
                        if(m.find()) {
                            Toast.makeText(context, ""+m.group(0), Toast.LENGTH_SHORT).show();
                            otp=m.group(0);
                        }
                        else
                        {
                            //something went wrong
                        }
                    }


                    if ( (messageBody.toLowerCase().contains("easygov")
                            || messageBody.toLowerCase().contains("harlabh"))) {
                        //Pass on the text to our listener.
                        if (otp.length() == 6)
                            mListener.messageReceived(otp);
                    }

                    else if (messageBody.contains("OTP") && (messageBody.toLowerCase().contains("for aadhaar"))) {
                            if(otp.length()==6)
                            mListener.messageReceived(otp);
                       // Toast.makeText(context, "" + messageBody.substring(30, 36), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (Exception e)
            {

            }
        }

        public static void bindListener(SmsListener listener) {
            mListener = listener;
        }
    }
