package com.blucore.chalochale.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    private static Dialog alert;

    //DB db;


    public static void setGradient(int Color1, int Color2, Button vu) {
        Shader textShader = new LinearGradient(0, 0, 0, 20, Color1, Color2,
                TileMode.CLAMP);
        vu.getPaint().setShader(textShader);
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    // ============================================================

    public static void log(String tag, String msg) {
        Log.d(tag, msg);
    }

    // ==================================================================

    public static void exception(String tag, Exception exp, String msg) {
        Log.d(tag, exp.getMessage() + " at " + msg);
    }

    // ===================================================================



    //====================================================
    public static boolean isNumberValid(String mobile) {
        boolean isValid = false;
        String expression = "^[0-9]{10}$";
        CharSequence inputStr = mobile;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

//	public static String GetCountryZipCode(Context ctx){
//	    String CountryID="";
//	    String CountryZipCode="";
//
//	    TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
//	    //getNetworkCountryIso
//	    CountryID= manager.getSimCountryIso().toUpperCase();
//	    String[] rl=ctx.getResources().getStringArray(R.array.CountryCodes);
//	    for(int i=0;i<rl.length;i++){
//	        String[] g=rl[i].split(",");
//	        if(g[1].trim().equals(CountryID.trim())){
//	            CountryZipCode=g[0];
//	            break;  
//	        }
//	    }
//	    return CountryZipCode;
//	}


    // ======================================================================
    public static void onError(Context ctx, Exception e) {
        showToast(ctx, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG);
        Log.d("", "Error =====> " + e.getMessage());
    }


    // ======================================================================
    public static void showToast(Context ctx, String text, int duration) {
        Toast.makeText(ctx, text, duration).show();
    }

// ===================================================================

    public static void goAnotherActivity(Activity a, Class c) {
        a.startActivity(new Intent(a, c));
        //a.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        a.finish();
    }
    // ======================================================================


    public static void showCustomToast(Context ctx, String msg, TextView tv) {

        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        tv = (TextView) toast.getView().findViewById(android.R.id.message);
        tv.setTextColor(Color.parseColor("#37BFEA"));
        toast.show();

    }


    // ======================================================================
    public static void showToastOnMainThread(final Context ctx,
                                             final String text, final int duration) {

        ((Activity) ctx).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                showToast(ctx, text, duration);
            }
        });

    }

    // ======================================================================
    public static String getDeviceID(Context ctx) {
        // return "123456";
        return Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
    }


    // ======================================================================
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity
                    .getCurrentFocus().getWindowToken(), 0);
    }

    // ======================================================================
    public static void SoftKeyBoard(Context c, boolean state) {

        try {
            if (!state) {
                InputMethodManager imm = (InputMethodManager) c
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (((Activity) c).getCurrentFocus() != null)
                    imm.hideSoftInputFromWindow(((Activity) c)
                            .getCurrentFocus().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

        }

    }

    // ======================================================================
    public static void SoftKeyBoard(Context c, View v, boolean state) {
        try {
            if (!state) {
                InputMethodManager imm = (InputMethodManager) c
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    // ================================================================
    public static void hideKeyboard(Activity activity) {

        try {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // ================================================================
    public static void ShowKeyboard(Activity activity) {

        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Service.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

	/*// ================================================================
    public static void alert(Context c, int titleRes, String message) {

		if (alert != null && alert.isShowing()) {
			// ======Do nothing
		} else {
			
		
			final Dialog dialog = new Dialog(c);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        dialog.setContentView(R.layout.alert_dialog);
	       TextView title_msg=(TextView)dialog.findViewById(R.id.title);
	       TextView msg=(TextView)dialog.findViewById(R.id.message);
			Button ok_btn=(Button)dialog.findViewById(R.id.ok_btn);
			
			
			ok_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			
			title_msg.setText("Message");
			msg.setText(message);
			//title_msg.setText("Confirmation..");
			//String str="Are you sure want to send gift to "+pref.get(com.propliance.megainfomatix.happyhiring.megainfomatix.com.happyhiring.Adapters.Common.Constants.OtherUserName)+" ?";
			//msg.setText(str);
	        dialog.show();
			

		}
	}*/


    // ======================================================================

    public static boolean isNetworkConnectedMainThred(Context ctx) {

        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null)
            return false;
        else
            return true;
    }

    // ================================================================
    public static String decodeToBase64(String str) {
        String retStr = null;
        try {
            retStr = new String(Base64.decode(str, Base64.DEFAULT));

        } catch (Exception e) {
            // TODO: handle exception
        }
        return (retStr == null) ? "" : retStr;
    }

    // ================================================================
    public static String encodeToBase64(String str) {
        String retStr = null;
        try {
            retStr = new String(Base64.encode(str.getBytes(), Base64.DEFAULT));

        } catch (Exception e) {
            // TODO: handle exception
        }
        return (retStr == null) ? "" : retStr.replaceAll("\n", "");
    }

    // ================================================================

    // =================================================================

    public static Boolean CNet() {

        final String mWalledGardenUrl = "http://m.google.com";
        final int WALLED_GARDEN_SOCKET_TIMEOUT_MS = 15000;

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mWalledGardenUrl); // "http://clients3.google.com/generate_204"
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setConnectTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setReadTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setUseCaches(false);
            urlConnection.getInputStream();
            // We got a valid response, but not from the real google
            return urlConnection.getResponseCode() != 204;
        } catch (IOException e) {

            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

    }

    // =================================================================

    // ================================================================

    public static String getDeviceType(Context context) {
        String device;

        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            device = "Android-Tablet";
        } else {
            device = "Android-Phone";
        }

        return device;
    }

    // ================================================================

    public static int getDeviceOrientation(Activity ctx) {

        int getOrient = ctx.getResources().getConfiguration().orientation;

        return getOrient; // return value 1 is portrait and 2 is Landscape Mode

    }

    //==============================================================

    public static boolean isEmailValid(String email) {

        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    // ================================================================

    public static void ChangeStausBarColour(Activity a, Integer i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = a.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(a, i));
        }
    }
    // ================================================================

    public static int getDevicewidth(Activity ctx) {

        Display mDisplay = ctx.getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();

        return width;

    }

    // ================================================================

    public static int getDeviceheight(Activity ctx) {

        Display mDisplay = ctx.getWindowManager().getDefaultDisplay();
        int height = mDisplay.getHeight();

        return height;

    }


    // ================================================================

    @SuppressLint("SimpleDateFormat")
    public static String DateToString(String format, Date date) {
        String temp = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            temp = dateFormat.format(date);
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
        return temp;
    }

    // ================================================================

    public static void clearPrefrenceOnLogout(Context ctx) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        Editor edit = pref.edit();

        edit.putLong("id", -1).commit();
        edit.putString("auth_token", "").commit();
        edit.putString("fb_id", "").commit();
        edit.putLong("apiUserID", -1).commit();
        edit.putString("client_id", "").commit();
        edit.clear().commit();

    }


    // ================================================================

    public static String getCurrentFBID(Context ctx) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        String fb_id = pref.getString("fb_id", "");
        return fb_id;
    }

    // ================================================================

    public static String getCurrentClientId(Context ctx) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        String user_id = pref.getString("client_id", null);
        return user_id;
    }

    // ================================================================
    @SuppressLint("SimpleDateFormat")
    public static Date StringToDate(String format, String date) {
        Date d = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            d = dateFormat.parse(date);
            // Log.d("Date === "+d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
        return d;
    }

    // ================================================================
    public static String convertDate(String fromFormat, String toFormat,
                                     String date) {

        String retDate = null;
        try {

            Date d = StringToDate(fromFormat, date);
            retDate = DateToString(toFormat, d);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return retDate;

    }

    // ================================================================
    public static int compareDate(Date d1, Date d2) {

        d1.setHours(0);
        d1.setMinutes(0);
        d1.setSeconds(0);

        d2.setHours(0);
        d2.setMinutes(0);
        d2.setSeconds(0);

        return d1.compareTo(d2);

    }


    // ================================================================
    public static void quitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // ================================================================
    public static String getCurrentLocale(Context ctx) {
        return Locale.getDefault().getDisplayLanguage();

    }

    // ================================================================
    public static ProgressDialog getProgress(Context ctx) {

        return ProgressDialog.show(ctx, "Loading", "Please Wait....", true);

    }

    // ================================================================
//	public static com.propliance.megainfomatix.happyhiring.megainfomatix.com.happyhiring.Adapters.Common.CustomLoader getCustomProgress(Context ctx) {
//
//		com.propliance.megainfomatix.happyhiring.megainfomatix.com.happyhiring.Adapters.Common.CustomLoader loader = new com.propliance.megainfomatix.happyhiring.megainfomatix.com.happyhiring.Adapters.Common.CustomLoader(ctx,
//				android.R.style.Theme_Translucent_NoTitleBar);
//		loader.show();
//		return loader;
//
//	}

    // ================================================================


    // ================================================================
    @SuppressWarnings("unused")
    public static String getmiliTimeStamp() {

        long LIMIT = 10000000000L;

        long t = Calendar.getInstance().getTimeInMillis();

        return String.valueOf(t).substring(0, 10);
    }

    // ================================================================
    public static String getmilisecTimeStamp() {

        long t = Calendar.getInstance().getTimeInMillis();

        return String.valueOf(t);
    }

    // ===================================================================

/*	public static String getMediaUrl(Context ctx, int api) {

		return "http://" + ctx.getString(R.string.server) + "/"
				+ ctx.getString(api);
	}*/


    // ================================================================
    public static int getAgeFromDOB(Date dobDate) {

        int age = 0;

        try {

            if (dobDate != null) {

                Date currDate = Calendar.getInstance().getTime();

                // Log.d("Curr year === "+currDate.getYear()+" DOB Date == "+dobDate.getYear());
                age = currDate.getYear() - dobDate.getYear();
                // Log.d("Calculated Age == "+age);

            }

        } catch (Exception e) {
            //Log.d(Constants.kApiExpTag, e.getMessage()+ "at Get Age From DOB mehtod.");
        }

        return age;

    }

    // ================================================================
    public static Date getDOBFromAge(int year) {

        Date date = Calendar.getInstance().getTime();
        date.setYear(1900 + date.getYear() - year);

        return date;

    }

    // ================================================================


    // ================================================================


    // ================================================================
    public static String getLocaleValue(Cursor c, String defColName,
                                        String locColName) {
        String value = c.getString(c.getColumnIndex(locColName));
        if (value == null) {
            value = c.getString(c.getColumnIndex(defColName));
        }
        return value;
    }


    // =================================================================

    @SuppressLint("SimpleDateFormat")
    public static String getDateFromMilisecond(long milliSeconds,
                                               String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    // =================================================================

    @SuppressLint("SimpleDateFormat")
    public static String getDateFormatMonthYear(String date,
                                                String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        Date d = null;

        try {
            d = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.

        String strdate = DateFormat.getDateInstance().format(d);

        return strdate;
    }

    // =================================================================

    @SuppressLint("SimpleDateFormat")
    public static String getDateFromSecond(long Seconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Seconds * 1000);
        return formatter.format(calendar.getTime());
    }

    // ================================================================
    public static Date getFirstDayOfWeek() {

        Calendar cal = Calendar.getInstance();

        int dow = cal.get(Calendar.DAY_OF_WEEK);
        dow = (dow > 1) ? dow - 2 : 6;

        cal.add(Calendar.DAY_OF_WEEK, -dow);

        Date d = cal.getTime();

        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);

        return d;

    }

    // ================================================================
    public static Date getLastDayOfWeek() {

        Calendar cal = Calendar.getInstance();

        int dow = cal.get(Calendar.DAY_OF_WEEK);
        dow = (dow > 1) ? 7 - dow : -1;

        cal.add(Calendar.DAY_OF_WEEK, dow);

        Date d = cal.getTime();

        d.setHours(23);
        d.setMinutes(59);
        d.setSeconds(59);

        return d;

    }


    // ================================================================

    public static Bitmap getImageFromLocal(Context ctx, String text) {
        InputStream imageStream = null;
        try {
            imageStream = ctx.getContentResolver().openInputStream(
                    Uri.parse(text));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap preview_bitmap = BitmapFactory.decodeStream(imageStream, null,
                options);
        return preview_bitmap;
    }

    // ================================================================
    // public static String getStaffSiteQuery(String loginStaffID){
    //
    // String query
    // ="SELECT "+Table.StaffStudySiteElement.site_element_master_id+" FROM "+Table.Name.StaffStudySiteElement+" WHERE "+Table.StaffStudySiteElement.staff_master_id+" = "+loginStaffID+" AND "+Table.StaffStudySiteElement.status+" = 1";
    // Log.d("Staff Site Query ===>>> "+query);
    // return query;
    //
    // }

    // ================================================================
    public static Bitmap getPortraitViewBitmap(String filePath)
            throws IOException {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap resizedBitmap = BitmapFactory.decodeFile(filePath, options);
            if (resizedBitmap != null) {
                ExifInterface exif = new ExifInterface(filePath);
                String orientString = exif
                        .getAttribute(ExifInterface.TAG_ORIENTATION);
                int orientation = orientString != null ? Integer
                        .parseInt(orientString)
                        : ExifInterface.ORIENTATION_NORMAL;
                int rotationAngle = 0;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                    rotationAngle = 90;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                    rotationAngle = 180;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                    rotationAngle = 270;

                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle,
                        (float) resizedBitmap.getWidth() / 2,
                        (float) resizedBitmap.getHeight() / 2);
                Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0,
                        resizedBitmap.getWidth(), resizedBitmap.getHeight(),
                        matrix, true);

                return rotatedBitmap;
            } else {
                return null;
            }
        } catch (Exception e) {

        }
        return null;
    }

    // ========================================================================

    @SuppressWarnings("unused")
    public static int getScaleedImage(Bitmap bitmap, Context context) {

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int i = display.getHeight();
        int j = display.getWidth();

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        float scale = (float) j / originalWidth;
        int newHeight = (int) Math.round(originalHeight * scale);

        return newHeight;

    }

    // ==================================================================

    public static String getpath(Uri imageUri, Context context) {
        String res = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(imageUri, proj,
                    null, null, null);
            if (cursor.moveToFirst()) {
                ;
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        } catch (Exception e) {

        }

        return res;
    }

    // ===================================================

    public static Bitmap getThumbImage(String rowImage, Context context)
            throws IOException {

        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(Uri.parse(rowImage),
                proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 32;
        Bitmap resizedBitmap = BitmapFactory.decodeFile(res, options);

        ExifInterface exif = new ExifInterface(res);
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString)
                : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
            rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
            rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
            rotationAngle = 270;

        Log.d("Rotation Angle == ", "" + rotationAngle);
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) resizedBitmap.getWidth() / 2,
                (float) resizedBitmap.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0,
                resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix,
                true);

        return rotatedBitmap;

    }


    // ================

    public static void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    // =======set listview height dinamicly=======

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
                MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // =============date & time diff========
    public static void TimeDiff() {
        Calendar calendar = Calendar.getInstance();
        TimeZone fromTimeZone = TimeZone.getTimeZone("CST+2");
        TimeZone toTimeZone = TimeZone.getTimeZone("CST+5.30");

        calendar.setTimeZone(fromTimeZone);
        calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
        if (fromTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone()
                    .getDSTSavings() * -1);
        }

        calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
        if (toTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
        }

        Log.v("time log", calendar.getTime() + "");
    }

    // =============================================

    public static String datedifrence(long d1) {

        String timedif = "";

        try {

            // in seconds

            long diff = Long.parseLong(Utils.getmiliTimeStamp()) - d1;

            long diffSeconds = diff;
            long diffMinutes = diff / 60;
            long diffHours = diff / 3600;
            long diffDays = diff / 86400;
            long diffMonth = diff / 2592000;

            if (diffMonth > 0) {
                if (diffMonth > 1) {
                    timedif = String.valueOf(diffMonth) + " months ago";
                } else {
                    timedif = String.valueOf(diffMonth) + " month ago";
                }
            } else {
                if (diffDays > 0) {
                    if (diffDays > 1) {
                        timedif = String.valueOf(diffDays) + " days ago";
                    } else {
                        timedif = String.valueOf(diffDays) + " day ago";
                    }

                } else {
                    if (diffHours > 0) {
                        if (diffHours > 1) {
                            timedif = String.valueOf(diffHours) + " hours ago";
                        } else {
                            timedif = String.valueOf(diffHours) + " hour ago";
                        }

                    } else {
                        if (diffMinutes > 0) {
                            if (diffMinutes > 1) {
                                timedif = String.valueOf(diffMinutes)
                                        + " minutes ago";
                            } else {
                                timedif = String.valueOf(diffMinutes)
                                        + " minute ago";
                            }

                        } else {
                            if (diffSeconds > 1) {
                                timedif = String.valueOf(diffSeconds)
                                        + " seconds ago";
                            } else {
                                if (diffSeconds > 0) {
                                    timedif = String.valueOf(diffSeconds)
                                            + " second ago";
                                } else {
                                    timedif = "0 second ago";
                                }

                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
        return timedif;
    }

    public static class ImageHelper {
        public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }

    public static Bitmap roundCornerImage(Bitmap raw, float round) {
        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


  /*  public static String getSpinnerItemId(String table_name, String column_name, String condition, String condition_name, Context context) {
        DB db = new DB(context);
        Cursor cur = null;
        String id = "";

        String item_query = " SELECT " + column_name + " FROM " + table_name + " WHERE " + condition + " = " + '"' + condition_name + '"';

        cur = db.findCursor(item_query, null);

        //Toast.makeText(this, "length = " + cur.getCount(), Toast.LENGTH_LONG).show();

        if (cur != null && cur.moveToNext()) {


            id = cur.getString(cur.getColumnIndex(column_name));

        }


        cur.close();
        return id;

        //Log.v("Locality= ",Location.toString());
        //Toast.makeText(this, "location_id : " + Location.toString(), Toast.LENGTH_LONG).show();
    }
*/
    public static String getSpinnerItemPos(String table_name, String column_name, String condition, String condition_name, Context context) {
        // DB db=new DB(context);
        Cursor cur = null;
        String id = "";

        int i = 1;

        String item_query = " SELECT " + column_name + " FROM " + table_name + " WHERE " + condition + " = " + '"' + condition_name + '"';

        // cur = db.findCursor(item_query, null);

        //Toast.makeText(this, "length = " + cur.getCount(), Toast.LENGTH_LONG).show();

        if (cur != null && cur.moveToNext()) {


            //id= cur.getString(cur.getColumnIndex(column_name));
            id = String.valueOf(i);

            //Toast.makeText(context, id, Toast.LENGTH_SHORT).show();

            i = i + 1;


        }


        cur.close();
        return id;

        //Log.v("Locality= ",Location.toString());
        //Toast.makeText(this, "location_id : " + Location.toString(), Toast.LENGTH_LONG).show();
    }

}
