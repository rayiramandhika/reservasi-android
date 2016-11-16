package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import id.or.rspmibogor.rspmibogor.Models.User;

public class CompleteRegisterActivity extends AppCompatActivity {


    private EditText edtConfirm;
    private TextView txtSendEmail, txtLogin;
    private ProgressDialog progressDialog;
    private String TAG = "CompleteRegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);

        edtConfirm = (EditText) findViewById(R.id.edtCodeConfirmation);
        txtSendEmail = (TextView)  findViewById(R.id.txtKirimUlang);
        txtLogin =  (TextView) findViewById(R.id.txtLogin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Konfirmasi Registrasi");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        edtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i == 3)
                {
                    SendConfirmationCode();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();

            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompleteRegisterActivity.this, LoginActivity.class));
                finish();
            }
        });


    }

    private void sendEmail() {


        Bundle b = getIntent().getExtras();

        String email = b.getString("email");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/register/confirm/sendemail?email="+email;

        progressDialog = new ProgressDialog(CompleteRegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang mengirim ulang kode verifikasi...");
        progressDialog.show();

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();

                        Toast.makeText(getBaseContext(), "Email telah dikirim ulang", Toast.LENGTH_SHORT).show();

                        //Log.d("SendConfirmationCode - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Email gagal dikirim ulang", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        int socketTimeOut = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CompleteRegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }

    private void SendConfirmationCode() {

        Bundle b = getIntent().getExtras();

        String email = b.getString("email");
        String confirmationCode = edtConfirm.getText().toString();

        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("confirmationCode", confirmationCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog = new ProgressDialog(CompleteRegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang konfirmasi kode verifikasi...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/register/confirm";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();

                        try {
                            String token = response.getString("token");
                            onSuccess(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Log.d("SendConfirmationCode - Response", response.toString());
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

    private void onSuccess(String token) {
        setResult(RESULT_OK, null);
        final String jwtToken = token;

        //Log.d(TAG, "onLoginSuccess jwtToken: " + jwtToken);

        User user = new User();
        user.getDataFromToken(jwtToken, this);

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        Bundle b = new Bundle();
        b.putBoolean("loginSuccess", true);

        intent.putExtras(b);

        Toast.makeText(getBaseContext(), "Konfirmasi Berhasil", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                },
                3000);

    }

    private void onFailed(String message) {

        String msg = null;
        if(message == null) msg = "Konfirmasi Gagal";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        edtConfirm.setText("");

    }

}
