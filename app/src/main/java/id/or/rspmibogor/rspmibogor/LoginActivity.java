package id.or.rspmibogor.rspmibogor;

;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import id.or.rspmibogor.rspmibogor.Models.User;



public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText  _emailText;
    EditText _passwordText;
    Button  _loginButton;
    TextView _signupLink;
    TextView _forgotPassword;

    Integer passwordView = 1;
    private Drawable iconPasswordView;
    private Drawable iconTextView;
    private Drawable getCompoundDrawables;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _forgotPassword = (TextView) findViewById(R.id.forgotPassword);


        iconPasswordView = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_remove_red_eye_black_24dp);
        iconTextView = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_remove_red_eye_red_24dp);

        _passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                getCompoundDrawables = _passwordText.getCompoundDrawables()[DRAWABLE_RIGHT];
                //Log.d(TAG, "getCompoundDrawables: " + getCompoundDrawables);

                if(getCompoundDrawables != null)
                {
                    if(event.getAction() == MotionEvent.ACTION_UP) {

                        final int width = _passwordText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                        final int right = _passwordText.getRight();

                        if(event.getRawX() >= (right - width)) {
                            _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            if(passwordView.equals(1))
                            {
                                _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, iconTextView, null);
                                _passwordText.setInputType(InputType.TYPE_CLASS_TEXT);
                                _passwordText.setSelection(_passwordText.getText().length());
                                passwordView = 0;
                            }else{
                                _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, iconPasswordView, null);
                                _passwordText.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                _passwordText.setSelection(_passwordText.getText().length());
                                passwordView = 1;
                            }
                            return true;
                        }
                    }
                    return false;
                }else{
                    _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    if(passwordView.equals(1))
                    {
                        _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, iconPasswordView, null);
                        _passwordText.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        _passwordText.setSelection(_passwordText.getText().length());
                        passwordView = 1;
                    }else{
                        _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, iconTextView, null);
                        _passwordText.setInputType(InputType.TYPE_CLASS_TEXT);
                        _passwordText.setSelection(_passwordText.getText().length());
                        passwordView = 0;
                    }
                    return true;
                }
            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _forgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login() {
        //Log.d(TAG, "Login");

        _emailText.clearFocus();
        _passwordText.clearFocus();

        if (!validate()) {
            onLoginFailed("Sign In Gagal");
            return;
        }

        _loginButton.setEnabled(false);


        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang memeriksa akun...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = R.string.ip_api + "/login";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        try {
                            onLoginSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.d("login - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();

                        String message = null;

                        if(error.networkResponse != null && error.networkResponse.data != null){
                            String body = null;
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                                try {

                                    //Log.d("login - Error.body", body.toString());
                                    JSONObject data = new JSONObject(body);
                                    message = data.getString("message");

                                }  catch (JSONException e) {
                                    message = body;
                                    e.printStackTrace();
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        onLoginFailed(message);

                    }
                }
        );
        int socketTimeOut = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(JSONObject data) throws JSONException {
        //_loginButton.setEnabled(true);

        final String jwtToken = data.getString("token");

        //Log.d(TAG, "onLoginSuccess data: " + data.toString());
        //Log.d(TAG, "onLoginSuccess jwtToken: " + jwtToken);

        User user = new User();
        user.getDataFromToken(jwtToken, this);

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        Bundle b = new Bundle();
        b.putBoolean("loginSuccess", true);
        intent.putExtras(b);
        Toast.makeText(getBaseContext(), "Sign In Berhasil", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                },
                2000);

    }

    public void onLoginFailed(String message) {

        String msg = null;
        if(message == null) msg = "Sign In Gagal";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        _loginButton.setEnabled(true);
                    }
                },
                2000);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("Email harus diisi");
            valid = false;
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Mohon masukan email yang benar");
            valid = false;
        }else{
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Password harus diisi");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

