package watersupplier.main.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;
import watersupplier.main.R;

/**
 * Created by Aman Gupta on 27/1/17.
 */

public class AppUtills {

    //date formats
    public static final String DATE_FORMAT_DDMMYYYY = "dd-MM-yyyy";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DDMMMYYYY = "dd-MMM-yyyy";
    public static final String Date_Format="yyyy-MM-dd hh:mm:ss";
    private static SharedPreferences sharedPreferences;

    public static String Email="",ShopName="",ShopAddress="",ShopAddress1="",FullShopAddress="",htmlCode="";


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static  String getUserName(Context context){
        String Username="";
        SharedPreferences sharedPreferences=context.getSharedPreferences("Login",Context.MODE_PRIVATE);
        Username=sharedPreferences.getString(context.getResources().getString(R.string.UserId_Spelling),"");
        return Username;
    }

    //intent effect
    public static void giveIntentEffect(Context context){

        Activity activity = (Activity) context;
//        activity.overridePendingTransition(R.anim.intent_out_slide_out_left, R.anim.intent_out_slide_out_left);
    }//intentInEffect closes here....

    public static void givefinishEffect(Context context){
        Activity activity = (Activity) context;

//        activity.overridePendingTransition(R.anim.slideout, R.anim.slideout);
    }//givefinishEffect closes here....

    /**
     * below method is used convert date format
     * */
    public static String dateFormatConverter(String date, String fromFormat, String toFormat){
        try {
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat(fromFormat);
            SimpleDateFormat dateFormat= new SimpleDateFormat(toFormat);
            java.util.Date  parse = simpleDateFormat.parse(date);
            date=dateFormat.format(parse);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

    /**
     * set String Preference value
     *
     * @param context        Context
     * @param preferenceName Name of your Preference
     * @param value          String value
     * @param preferenceKey  Key name of the Preference
     */
    public static void setStringPrefrences(Context context, String preferenceName, String value, String preferenceKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, value);
        editor.apply();
    }

    /**
     * get String Preference value
     *
     * @param context        Context
     * @param preferenceName Name of your Preference
     * @param preferenceKey  Key name of the Preference
     * @return value of saved preference. If value is not set, return null
     */
    public static String getStringPrefrences(Context context, String preferenceName, String preferenceKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, null);
    }

    /**
     * AMAN 25-4-2017
     * method used to add prefixes to date
     * */
    public static String addPrefixBeforeDateNumber(int value){
        return value<10?"0"+value:String.valueOf(value);
    }
    /***
     * below method will capatalize the first letter String
     *
     * */
    public static String capitalizeFirstLetter(String original){

        if(original.length() == 0)
        {
            return original;
        }
        else
        {
            return original.substring(0, 1).toUpperCase() + original.substring(1);
        }
    }//String capitalizeFirstLetter(String original) Close Here......


    /**
     * this method is used to create Csv file
     * file will be stored in folder name which you have sent as parameter below with filename
     *
     * **/
    public static void csvFileCreate(ArrayList<String[]> arrayList, String folderName, String fileName){
        File exportFile=new File(Environment.getExternalStorageDirectory(),"/"+folderName);
        if (!exportFile.exists()){
            exportFile.mkdirs();
        }

        try {
            File file = new File(exportFile, fileName+".csv");
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            for (int i = 0; i < arrayList.size(); i++) {
                csvWrite.writeNext(arrayList.get(i));
            }
            csvWrite.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }//csvConverter() close here...

  /*
  *      * this method is used to send mail
	 * pass here the filename context of class and to whom to send the mail
  *
  * **/
    public static void sendMail(String FileName,Context context,String To,String Subject){
        String filename="WiseBill/reportexport/"+FileName;

        File filelocation = new File(Environment.getExternalStorageDirectory(), filename);
        Uri path = Uri.fromFile(filelocation);
        Log.d("AMAN", "AMAN CHECKING MAIL "+path);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {To};//mail id here...
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, Subject);
        context.startActivity(Intent.createChooser(emailIntent , "Send email..."));

    }



}
