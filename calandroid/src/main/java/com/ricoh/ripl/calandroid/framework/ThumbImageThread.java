package com.ricoh.ripl.calandroid.framework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ricoh.ripl.calandroid.fragments.CalMainFragment;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ThumbImageThread implements Runnable {

    private int threadNo;
    private Handler handler;
    private String upc;
    private String rspMsg = null;
    private int rspCode = 0;
    private Date date;
    private List<Bitmap> thumbList;
    private String pimhost = null;
    private Context mContext;
    public static HashMap<String, Bitmap> thumbHash = new HashMap<String, Bitmap>();

    public static final String TAG = "ThumbImageThread";

    public ThumbImageThread() {
    }

    public ThumbImageThread(int threadNo, Context context, String upc, String pimhost, Handler handler) {
        this.threadNo = threadNo;
        this.handler = handler;
        this.upc = upc;
        this.pimhost = pimhost;
    }

    @Override
    public void run() {
        Log.i(TAG, "Starting Thread : " + threadNo);
        getBitmap(upc);
        //sendMessage(CalMainFragment.THREAD_INFO, threadNo);
        Log.i(TAG, "Thread Completed " + threadNo);
    }

    private void sendMessage(int what, String msg) {
        Message message = handler.obtainMessage(what, msg);
        message.sendToTarget();
    }

    private Bitmap getBitmap(String upcno) {
        Bitmap thumbnail = null;
        String upc = upcno;

        date = new Date();

        thumbList = new ArrayList<Bitmap>();
        thumbList.toArray();
        Log.d(TAG, " inside thumb ThreadPoolExecutor---> upc is: " + upc);

        URL urlthumb = null;
        try {
            if (!pimhost.equals("") && !TextUtils.isEmpty(pimhost)) {
                String query = URLEncoder.encode(pimhost, "utf-8");
                urlthumb = new URL("https://" + query + "/v1/productmaster/products/" + upc + "?thumbnail=medium");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            // Get Authorization headers
            String authString = null;
            OcutagAuthScheme.Builder builder = new OcutagAuthScheme.Builder("GET", String.valueOf(urlthumb), sendDateImage(date), CalMainFragment.authHeaders);
            OcutagAuthScheme authScheme = null;

            Log.d(TAG, "Auth Headers: " + Arrays.toString(CalMainFragment.authHeaders));
            try {
                authScheme = new OcutagAuthScheme(builder);
                authString = authScheme.getAuthorizedHeader();
                Log.d(TAG, "Thumbnail auth string: " + authString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // make API call for cal_thumbnails
            URL urls = new URL(String.valueOf(urlthumb));
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) urls.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Date", sendDateImage(date));
            con.setRequestProperty("Authorization", authString);
            con.setRequestProperty("thumbnail", "medium");
            con.setConnectTimeout(60000); // 1 minute
            Map<String, List<String>> headerResp = con.getHeaderFields();
            rspCode = con.getResponseCode();
            rspMsg = con.getResponseMessage();

            Log.d(TAG, "Thumbnail Server response: " + headerResp);
            Log.d(TAG, "Thumbnail response: -----------------> " + rspMsg + " : " + rspCode);

            if (rspCode == 200) {
                InputStream in = new BufferedInputStream(con.getInputStream());
                thumbnail = BitmapFactory.decodeStream(in);
                synchronized (thumbHash) {
                    thumbHash.put(upc, thumbnail);
                }
                Log.d(TAG, "thumbhash size: " + thumbHash.size() + "  productlist size:" + CalMainFragment.upcFilteredList.size());
                Log.d(TAG, "Thumb Image retrieved successfully " + thumbnail);
            } else {
                Log.d(TAG, "Thumbnail error:: Error connecting to server!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (thumbHash.size() > 0) {
                Log.d(TAG, "thumb and product list size equals ");
                //CalMainFragment.updateBitmapInProductList();
                sendMessage(CalMainFragment.UPDATE_ADAPTER, "update list");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return thumbnail;
    }

    private String sendDateImage(java.util.Date date) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String finalDate = dateFormatGmt.format(date).toString();
        finalDate = finalDate.replace("+00:00", "");
        Log.d(TAG, "sendDate: Date :" + finalDate);
        return String.valueOf(finalDate);
    }
}
