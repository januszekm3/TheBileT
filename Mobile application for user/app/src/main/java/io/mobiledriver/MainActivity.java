package io.mobiledriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private boolean stayHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = this.getSharedPreferences("com.sharedPreferences", Context.MODE_PRIVATE);
        String key = "Token";
        String l = prefs.getString(key, "0");
        if(l != "0")
        {
            //TODO
            /*
                Add async task to login
                Save token onPostExecute
             */
        }
        else
        {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void register(View view)
    {
        //Button registerButton = (Button)findViewById(R.id.registerButton);
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);

    }

    public void onLogin(View view) throws IOException {
        new loginTask().execute("https://thebilet.usetitan.com/web.ashx/login");
    }

    public void forgotPassword(View view)
    {
        TextView forgotLoginView = (TextView)findViewById(R.id.main_forgotLogin);
        TextView forgotEmailView = (TextView)findViewById(R.id.main_forgotEmail);
        EditText forgotLoginEntry = (EditText)findViewById(R.id.main_forgotLoginEntry);
        EditText forgotEmailEntry = (EditText)findViewById(R.id.main_forgotEmailEntry);
        Button sendEmailButton = (Button)findViewById(R.id.main_sendMailButton);


        if(stayHidden)
        {
            forgotLoginView.setVisibility(View.VISIBLE);
            forgotEmailView.setVisibility(View.VISIBLE);
            forgotLoginEntry.setVisibility(View.VISIBLE);
            forgotEmailEntry.setVisibility(View.VISIBLE);
            sendEmailButton.setVisibility(View.VISIBLE);
            stayHidden = false;
        }
        else
        {
            forgotLoginView.setVisibility(View.INVISIBLE);
            forgotEmailView.setVisibility(View.INVISIBLE);
            forgotLoginEntry.setVisibility(View.INVISIBLE);
            forgotEmailEntry.setVisibility(View.INVISIBLE);
            sendEmailButton.setVisibility(View.INVISIBLE);
            stayHidden = true;
        }
    }

    public void sendEmail(View view)
    {
        new sendEmailTask().execute("https://thebilet.usetitan.com/web.ashx/resetPassword");
    }

    private class sendEmailTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            String string_url = params[0];
            String login = ((EditText)findViewById(R.id.main_forgotLoginEntry)).getText().toString();
            String email = ((EditText)findViewById(R.id.main_forgotEmailEntry)).getText().toString();
            try {
                List<NameValuePair> qparams = new ArrayList<NameValuePair>();
                qparams.add(new BasicNameValuePair("username", login));
                qparams.add(new BasicNameValuePair("email", email));
                URI uri = URIUtils.createURI("https", "thebilet.usetitan.com", -1,
                        "/web.ashx/resetPassword", URLEncodedUtils.format(qparams, "UTF-8"), null);
                URL url = uri.toURL();
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String input;
                String result = "";
                while((input = br.readLine()) != null)
                {
                    result += input;
                }
                return result;
            } catch (MalformedURLException e) {
                return "ERROR";
            } catch (IOException e) {
                return "ERROR";
            } catch (URISyntaxException e) {
                return "ERROR";
            }
        }

        protected void onPostExecute(String result)
        {
            if(result.startsWith("ERROR"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Wrong credentials");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    };
                });
                builder.show();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Succes");
                builder.setMessage("Email send");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    };
                });
                builder.show();
            }

        }
    }

    private class loginTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            EditText eLogin = (EditText)findViewById(R.id.login);
            EditText ePassword = (EditText)findViewById(R.id.password);

            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);


            String string_url = params[0];
            try {
                List<NameValuePair> qparams = new ArrayList<NameValuePair>();
                qparams.add(new BasicNameValuePair("userName", eLogin.getText().toString()));
                qparams.add(new BasicNameValuePair("password", ePassword.getText().toString()));
                qparams.add(new BasicNameValuePair("deviceId", tm.getDeviceId()));
                URI uri = URIUtils.createURI("https", "thebilet.usetitan.com", -1,
                        "/web.ashx/login", URLEncodedUtils.format(qparams, "UTF-8"), null);
                URL url = uri.toURL();
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String input;
                String result = "";
                while((input = br.readLine()) != null)
                {
                    result += input;
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return "ERROR";
        }

        protected void onPostExecute(String result)
        {
            if(result.startsWith("ERROR"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Login error");
                builder.setMessage("Bad credentials");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    };
                });
                builder.show();
            }
            else
            {
                SharedPreferences sharedPref = getSharedPreferences("com.sharedPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.clear();
                edit.putString("Token", result);
                edit.commit();
                startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
                finish();
            }
        }
    }

}
