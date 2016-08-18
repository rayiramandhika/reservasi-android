package id.or.rspmibogor.rspmibogor.Models;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iqbalprabu on 18/08/16.
 */
public class User {


    public void updateFCMToken(final String token, Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://103.43.44.211:1337/v1/user/1";

        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("updateFCMToken - Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("updateFCMToken - Error.Response", String.valueOf(error));
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("fcmToken", token);

                return params;
            }

        };

        queue.add(putRequest);

    }
}
