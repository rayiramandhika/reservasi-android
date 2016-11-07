package id.or.rspmibogor.rspmibogor.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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


    public void updateFCMToken(final String token, final Integer idUser, final String jwtToken, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://103.23.22.46:1337/v1/user/" + idUser;

        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("updateFCMToken-Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof AuthFailureError)
                        {
                            if(token != null){
                                User user = new User();
                                user.refreshToken(token, context);
                            }
                        }
                        // error
                        //Log.d("updateFCMToken - Error.Response", String.valueOf(error));
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

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + jwtToken);
                return params;
            }

        };

        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);

    }

    public void getDataFromToken(final String token, final Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://103.23.22.46:1337/v1/me";

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

                        Log.d("getDataFromToken-Res", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof AuthFailureError)
                        {
                            if(token != null){
                                User user = new User();
                                user.refreshToken(token, context);
                            }
                        }
                        // error
                        //Log.d("getDataFromToken - Error.Response", String.valueOf(error));
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);

    }

    public void refreshToken(final String token, final Context context)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://103.23.22.46:1337/v1/refreshtoken";

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
                            String newToken = response.getString("token");
                            getDataFromToken(newToken, context);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("refreshToken-Response", response.toString());
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        /*if(error instanceof AuthFailureError)
                        {
                            if(token != null){
                                User user = new User();
                                user.refreshToken(token, context);
                            }
                        }*/
                        // error
                        //Log.d("getDataFromToken - Error.Response", String.valueOf(error));
                    }
                }
        );

        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }
}
