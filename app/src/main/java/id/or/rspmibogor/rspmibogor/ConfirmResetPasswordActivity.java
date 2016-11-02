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

public class ConfirmResetPasswordActivity extends AppCompatActivity {

    String email;

    TextView informasi;
    TextView sendEmail;
    EditText resetCode;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reset_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Konfirmasi Kode");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        email = b.getString("email");

        informasi = (TextView) findViewById(R.id.informasi);
        sendEmail = (TextView) findViewById(R.id.sendEmail);
        resetCode = (EditText) findViewById(R.id.resetCode);
        btnSend = (Button) findViewById(R.id.btnSend);

        informasi.setText("Kami telah mengirikan kode reset password ke \n"+email+", silahkan cek.");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConfirmation();
            }
        });
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResetCode();
            }
        });
    }

    private void sendConfirmation()
    {
        btnSend.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ConfirmResetPasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        final String passwordResetCode = resetCode.getText().toString();

        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("passwordResetCode", passwordResetCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/forgotpassword/confirm";

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
                        //Log.d("sendConfirmation - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();

                        String message = null;

                        if(error.networkResponse != null && error.networkResponse.data != null){
                            try {

                                String body = new String(error.networkResponse.data,"UTF-8");

                                JSONObject data = new JSONObject(body);
                                message = data.getString("message");
                                //Log.d("login - Error.Response", data.getString("message"));

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
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

    private void onSuccess(JSONObject data) throws JSONException {
        Intent intent = new Intent(ConfirmResetPasswordActivity.this, ResetPasswordActivity.class);

        Bundle b = new Bundle();
        b.putString("token", data.getString("token"));

        intent.putExtras(b);
        startActivity(intent);
    }

    private void onFailed(String message)
    {
        String msg = null;
        if(message == null) msg = "Gagal Konfirmasi Kode Reset Password";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        btnSend.setEnabled(true);
    }

    private void sendResetCode()
    {

        final ProgressDialog progressDialog = new ProgressDialog(ConfirmResetPasswordActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/forgotpassword/sendemail?email="+email;

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Email berhasil dikirim.", Toast.LENGTH_LONG).show();
                        //Log.d("sendResetCode - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Email gagal dikirim.", Toast.LENGTH_LONG).show();
                        //Log.d("sendResetCode - Response", error.toString());
                    }
                }
        );
        int socketTimeOut = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        putRequest.setRetryPolicy(policy);
        queue.add(putRequest);
    }
}
