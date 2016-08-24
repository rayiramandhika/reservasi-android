package id.or.rspmibogor.rspmibogor.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iqbalprabu on 18/08/16.
 */
public class User {


    public void updateFCMToken(final String token, final Integer idUser, Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://103.43.44.211:1337/v1/user/" + idUser;

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

    public void getDataFromToken(final String token, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://103.43.44.211:1337/v1/me";

        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response

                        try {
                            JSONObject data = response.getJSONObject("data");


                            SharedPreferences sharedpreferences = context.getSharedPreferences("RS PMI BOGOR MOBILE APPS" ,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("jwtToken", token);
                            editor.putString("nama", data.getString("nama"));
                            editor.putInt("id", data.getInt("id"));
                            editor.putString("email", data.getString("email"));
                            editor.putString("profilePicture", data.getString("profilePicture"));
                            editor.commit();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("getDataFromToken - Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("getDataFromToken - Error.Response", String.valueOf(error));
                    }
                }
        );

        queue.add(putRequest);

    }
}
