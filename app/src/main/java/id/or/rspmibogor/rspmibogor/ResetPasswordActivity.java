package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    String token;

    EditText _passwordText;
    Button _btnSend;

    Integer passwordView = 1;
    private Drawable getCompoundDrawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reset Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        token = b.getString("token");

        _passwordText = (EditText) findViewById(R.id.input_password);
        _btnSend = (Button) findViewById(R.id.btnSend);

        final Drawable iconPasswordView = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_remove_red_eye_black_24dp);
        final Drawable iconTextView = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_remove_red_eye_red_24dp);

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


        _btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword()
    {
        
        if (!validate()) {
            onFailed("Gagal Reset Password");
            return;
        }

        _btnSend.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang proses reset password...");
        progressDialog.show();

        final String password = _passwordText.getText().toString();

        JSONObject object = new JSONObject();
        try {
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.20.160:1337/v1/resetpassword";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        try {
                            onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.d("resetPassword - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();

                        String message = "Gagal reset password, Silahkan coba lagi.";
                        onFailed(message);

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        int socketTimeOut = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }

    private void onSuccess(JSONObject data) throws JSONException {
        final Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);

        Toast.makeText(getBaseContext(), "Password berhasil di reset", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(
        new Runnable() {
            public void run() {
                startActivity(intent);
                finish();
            }
        },
        2000);

    }

    public boolean validate() {
        boolean valid = true;

        String password = _passwordText.getText().toString();

        if (password.isEmpty()) {
            _passwordText.setError("Password harus diisi");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void onFailed(String message)
    {
        String msg = null;
        if(message == null) msg = "Gagal Konfirmasi Kode Reset Password";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        _btnSend.setEnabled(true);
    }
}
