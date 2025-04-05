package in.kpis.afoozo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;


/**
 * Created by hemant on 13-04-2018.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = SmsReceiver.class.getSimpleName();


    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;


        if (message.length() != -1) {
            int start = message.length()-4;
            int length = 4;
            code = message.substring(start, start + length);
            return code;
        }
        return code;
    }

            private OTPReceiveListener otpListener;

            /**
             * @param otpListener
             */
            public void setOTPListener(OTPReceiveListener otpListener) {
                this.otpListener = otpListener;
            }


            /**
             * @param context
             * @param intent
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                    Bundle extras = intent.getExtras();
                    Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:

                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            String lines[] = message.split("\\r?\\n");

                            //Extract the OTP code and send to the listener

                            if (otpListener != null) {
                                otpListener.onOTPReceived(getVerificationCode(lines[0]));
                            }
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            if (otpListener != null) {
                                otpListener.onOTPTimeOut();
                            }
                            break;

                        case CommonStatusCodes.API_NOT_CONNECTED:

                            if (otpListener != null) {
                                otpListener.onOTPReceivedError("API NOT CONNECTED");
                            }

                            break;

                        case CommonStatusCodes.NETWORK_ERROR:

                            if (otpListener != null) {
                                otpListener.onOTPReceivedError("NETWORK ERROR");
                            }

                            break;

                        case CommonStatusCodes.ERROR:

                            if (otpListener != null) {
                                otpListener.onOTPReceivedError("SOME THING WENT WRONG");
                            }

                            break;

                    }
                }
            }

            /**
             *
             */
            public interface OTPReceiveListener {

                void onOTPReceived(String otp);

                void onOTPTimeOut();

                void onOTPReceivedError(String error);
            }
}
