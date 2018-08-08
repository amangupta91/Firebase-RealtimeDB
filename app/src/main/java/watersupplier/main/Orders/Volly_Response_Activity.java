package watersupplier.main.Orders;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.R;

/**
 * Created by aman on 05/10/17.
 */

public class Volly_Response_Activity extends Common_ActionBar_Abstract {

    ImageView image;
    TextView text1,text22;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volly_response);

         image = (ImageView) findViewById(R.id.image);
         text1 = (TextView) findViewById(R.id.volly_text);
        text22 = (TextView) findViewById(R.id.text22);

        image_response();
        json_response();
        


    }

    private void image_response() {

        String url = "http://i.imgur.com/Nwk25LA.jpg";
//        image = (ImageView) findViewById(R.id.image);

        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        image.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                image.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(imgRequest);
    }

    private void json_response() {

        String url = "http://httpbin.org/get?site=code&network=tutsplus";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("args");
                            String site = response.getString("network"),
                                    network = response.getString("site");

                            System.out.println("Site: "+site+"\nNetwork: "+network);

                            text1.setText("Site: "+site+"\nNetwork: "+network);

                            text22.setText("Site: "+site+"\nNetwork: "+network);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLY","Error:"+error);
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(jsonRequest);
    }
}
