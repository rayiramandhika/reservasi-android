package id.or.rspmibogor.rspmibogor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    Button _signupButton;
    TextView _loginLink;

    Integer passwordView = 1;
    private Drawable getCompoundDrawables;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

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
                    if(passwordView.equals(1))
                    {
                        _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, iconPasswordView, null);
                        _passwordText.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        _passwordText.setSelection(_passwordText.getText().length());
                    }else{
                        _passwordText.setCompoundDrawablesWithIntrinsicBounds(null, null, iconTextView, null);
                        _passwordText.setInputType(InputType.TYPE_CLASS_TEXT);
                        _passwordText.setSelection(_passwordText.getText().length());
                    }
                    return false;
                }
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void signup() {
        //Log.d(TAG, "Signup");

        _emailText.clearFocus();
        _passwordText.clearFocus();

        if (!validate()) {
            onSignupFailed("Sign Up");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
         progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
         progressDialog.setMessage("Loading...");
         progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        JSONObject object = new JSONObject();
        try {
            object.put("nama", name);
            object.put("email", email);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://103.23.22.46:1337/v1/register";

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        progressDialog.dismiss();
                        onSignupSuccess();
                        //Log.d("login - Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressDialog.dismiss();


                        String message = null;

                        if(error.networkResponse != null && error.networkResponse.data != null){
                            try {
                                String body = new String(error.networkResponse.data,"UTF-8");
                                JSONObject data = new JSONObject(body);
                                //message = data.getString("message");

                                String errorMsg = data.getString("error");
                                //Log.d("login - Error.Response", errorMsg);
                                if(errorMsg.equals("E_VALIDATION")) {
                                    message = "Email sudah terdaftar";
                                    _emailText.setError("Email sudah terdaftar");
                                }

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        onSignupFailed(message);

                    }
                }
        );
        queue.add(putRequest);
    }


    public void onSignupSuccess() {
        //_signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Toast.makeText(getBaseContext(), "Sign Up Berhasil", Toast.LENGTH_LONG).show();
        final Intent intent = new Intent(this, CompleteRegisterActivity.class);

        String email = _emailText.getText().toString();

        Bundle b = new Bundle();
        b.putString("email", email);

        intent.putExtras(b);

        startActivity(intent);
        finish();

    }

    public void onSignupFailed(String message) {
        String msg = null;
        if(message == null) msg = "Sign Up Gagal";
        else msg = message;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("At least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Enter a password ");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
