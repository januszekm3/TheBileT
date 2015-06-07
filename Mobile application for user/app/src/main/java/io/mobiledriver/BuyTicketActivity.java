package io.mobiledriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BuyTicketActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buy_ticket, menu);
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

    public void onBuy(View view)
    {
        SharedPreferences tokenDetail = getSharedPreferences("com.sharedPreferences", Context.MODE_PRIVATE);
        String token = tokenDetail.getString("Token", null);
        new buyingTask().execute("https://thebilet.usetitan.com/web.ashx/buyTicket", token);
    }

    private class buyingTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String string_url = params[0];
            String token = params[1];
            String licensePlate = ((EditText)findViewById(R.id.licenceplate)).getText().toString();
            String startingDate = ((EditText)findViewById(R.id.startdate)).getText().toString();
            String startingTime = ((EditText)findViewById(R.id.startTime)).getText().toString();
            String length = ((EditText)findViewById(R.id.enddate)).getText().toString();
            String area = ((EditText)findViewById(R.id.area)).getText().toString();


            String[] splited = startingTime.split(":");
            if(splited[0].length() == 1)
            {
                startingTime = "0" + startingTime;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = null;
            try {
                date = dateFormat.parse(startingDate + "T" + startingTime);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                splited = length.split(":");
                c.add(Calendar.HOUR, Integer.parseInt(splited[0]));
                c.add(Calendar.MINUTE, Integer.parseInt(splited[1]));
                String newDate = Integer.toString(c.get(Calendar.YEAR));
                newDate += "-" + Integer.toString(c.get(Calendar.MONTH) + 1);
                newDate += "-" + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "T";
                int temp = c.get(Calendar.HOUR_OF_DAY);
                String temp_string = "";
                if(temp < 10)
                {
                    temp_string = "0" + temp;
                }
                else
                {
                    temp_string = Integer.toString(temp);
                }
                newDate += temp_string;
                temp = c.get(Calendar.MINUTE);
                if(temp < 10)
                {
                    temp_string = "0" + temp;
                }
                else
                {
                    temp_string = Integer.toString(temp);
                }
                newDate += ":" + temp_string;
                temp = c.get(Calendar.SECOND);
                if(temp < 10)
                {
                    temp_string = "0" + temp;
                }
                else
                {
                    temp_string = Integer.toString(temp);
                }
                newDate += ":" + temp_string;

                List<NameValuePair> qparams = new ArrayList<NameValuePair>();
                qparams.add(new BasicNameValuePair("registrationNumber", licensePlate));
                qparams.add(new BasicNameValuePair("startDate", startingDate + "T" + startingTime));
                qparams.add(new BasicNameValuePair("endDate", newDate));
                qparams.add(new BasicNameValuePair("authToken", token));
                qparams.add(new BasicNameValuePair("areaId", area));
                URI uri = URIUtils.createURI("https", "thebilet.usetitan.com", -1,
                        "/web.ashx/buyTicket", URLEncodedUtils.format(qparams, "UTF-8"), null);
                //URL url = new URL(string_url);
                HttpURLConnection con = (HttpURLConnection)uri.toURL().openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String input;
                String result = "";
                while((input = br.readLine()) != null)
                {
                    result += input;
                }
                return result;
            } catch (ParseException e) {
                e.printStackTrace();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(BuyTicketActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Wrong input format");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    };
                });
                builder.show();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(BuyTicketActivity.this);
                builder.setTitle("Succes");
                builder.setMessage("Ticket bought");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    };
                });
                builder.show();
            }

        }
    }


}
