package io.controllerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void onLogin(View view)
    {
        new loginTask().execute();
    }

    private class loginTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            EditText eLogin = (EditText)findViewById(R.id.login);
            EditText ePassword = (EditText)findViewById(R.id.password);

            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            try {
                List<NameValuePair> qparams = new ArrayList<NameValuePair>();
                qparams.add(new BasicNameValuePair("username", eLogin.getText().toString()));
                qparams.add(new BasicNameValuePair("password", ePassword.getText().toString()));
                qparams.add(new BasicNameValuePair("deviceId", tm.getDeviceId()));
                URI uri = URIUtils.createURI("https", "thebilet.usetitan.com", -1,
                        "/web.ashx/loginController", URLEncodedUtils.format(qparams, "UTF-8"), null);
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
