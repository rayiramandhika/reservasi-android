package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ForgotPasswordActivity extends AppCompatActivity {


    EditText _emailText;
    Button _btnKirim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lupa Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        _emailText = (EditText) findViewById(R.id.input_email);
        _btnKirim = (Button) findViewById(R.id.btnNext);

        _btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendForgotPassword();
            }
        });
    }

    private void sendForgotPassword()
    {

        if (!validate()) {
            onFailed("Gagal Reset Password");
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang mengirim kode reset password...");
        progressDialog.show();

        final String email = _emailText.getText().toString();

        _btnKirim.setEnabled(false);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.rspmibogor.or.id/v1" + "/forgotpassword?email="+email;

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        onSuccess(response);
                        //Log.d("sendForgotPassword - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Log.d("sendForgotPassword - Response", error.toString());

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

                        onFailed(message);

                    }
                }
        );
        int socketTimeOut = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }

    public boolean validate() {
        boolean valid = true;

        String password = _emailText.getText().toString();

        if (password.isEmpty()) {
            _emailText.setError("Email harus diisi");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        return valid;
    }

    private void onSuccess(JSONObject data)
    {
        Intent intent = new Intent(ForgotPasswordActivity.this, ConfirmResetPasswordActivity.class);

        Bundle b = new Bundle();
        b.putString("email", _emailText.getText().toString());

        intent.putExtras(b);
        startActivity(intent);
    }

    private void onFailed(String message)
    {
        String msg = null;
        if(message == null) msg = "Gagal mengirim email";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        _btnKirim.setEnabled(true);
    }
}
