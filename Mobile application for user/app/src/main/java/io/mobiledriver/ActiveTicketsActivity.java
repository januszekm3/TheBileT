package io.mobiledriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ActiveTicketsActivity extends ActionBarActivity {

    private String array_spinner[];
    public JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_tickets);

        SharedPreferences prefs = this.getSharedPreferences("com.sharedPreferences", Context.MODE_PRIVATE);
        String token = prefs.getString("Token", null);

        new activeTicketsTask().execute("http://thebilet.usetitan.com/web.ashx/listTickets",
                token);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_active_tickets, menu);
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

    public void onBack(View view){
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /*public void onChosen(View view)
    {
        Spinner dropbox = (Spinner)findViewById(R.id.spinner);
        String current = String.valueOf(dropbox.getBaseline());
    }*/

    private class activeTicketsTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String string_url = params[0];
            String token = params[1];
            try {

                List<NameValuePair> qparams = new ArrayList<NameValuePair>();
                qparams.add(new BasicNameValuePair("token", token));
                URI uri = URIUtils.createURI("https", "thebilet.usetitan.com", -1,
                        "/web.ashx/listTickets", URLEncodedUtils.format(qparams, "UTF-8"), null);
                URL url = new URL(string_url + "?authToken=" + token);
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
            }
            else
            {
                try {
                    JSONArray array = new JSONArray(result);
                    array_spinner = new String[array.length()];
                    for(int i=0;i<array.length();i++)
                    {
                        array_spinner[i] = "Ticket " +  String.valueOf(i + 1);
                    }
                    Spinner s = (Spinner) findViewById(R.id.spinner);
                    s.setPrompt("Choose");
                    ArrayAdapter<String> adapter;
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, array_spinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                    TextView licensePlate = (TextView)findViewById(R.id.activeTickets_licenceplate);
                    TextView startDate = (TextView)findViewById(R.id.activeTickets_startdate);
                    TextView endDate = (TextView)findViewById(R.id.activeTickets_enddate);
                    TextView area = (TextView)findViewById(R.id.activeTickets_area);
                    s.setOnItemSelectedListener(new SpinnerListener(array, licensePlate, startDate, endDate, area));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
