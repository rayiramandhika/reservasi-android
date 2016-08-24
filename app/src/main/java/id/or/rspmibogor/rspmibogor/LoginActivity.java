package id.or.rspmibogor.rspmibogor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.or.rspmibogor.rspmibogor.Models.User;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText  _emailText;
    EditText _passwordText;
    Button  _loginButton;
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //ButterKnife.inject(this);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);



        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Login Failed");
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
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
        String url = "http://103.43.44.211:1337/v1/login";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        progressDialog.dismiss();
                        try {
                            onLoginSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("login - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressDialog.dismiss();


                        String message = null;

                        //JSONObject data = new JSONObject(error.networkResponse.data);

                        try {
                            String body = new String(error.networkResponse.data,"UTF-8");
                            JSONObject data = new JSONObject(body);
                            message = data.getString("message");
                            Log.d("login - Error.Response", data.getString("message"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        onLoginFailed(message);


                    }
                }
        );
        queue.add(putRequest);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(JSONObject data) throws JSONException {
        _loginButton.setEnabled(true);

        final String jwtToken = data.getString("token");

        Log.d(TAG, "onLoginSuccess data: " + data.toString());
        Log.d(TAG, "onLoginSuccess jwtToken: " + jwtToken);

        /**SharedPreferences sharedpreferences = this.getSharedPreferences("RS PMI BOGOR MOBILE APPS" ,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("jwtToken", jwtToken);**/

        User user = new User();
        user.getDataFromToken(jwtToken, this);

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        Bundle b = new Bundle();
        b.putBoolean("loginSuccess", true);

        intent.putExtras(b);

        Toast.makeText(getBaseContext(), "Login Success", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                },
                1000);

    }

    public void onLoginFailed(String message) {
        String msg = null;
        if(message == null) msg = "Login Failed";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("enter a password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

