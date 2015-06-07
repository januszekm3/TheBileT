package io.mobiledriver;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Toshiba on 2015-05-25.
 */
public class SpinnerListener implements AdapterView.OnItemSelectedListener {

    private JSONArray array;
    private TextView licensePlate;
    private TextView startDate;
    private TextView endDate;
    private TextView area;

    public SpinnerListener(JSONArray array, TextView licensePlate, TextView startDate,
                           TextView endDate, TextView area)
    {
        this.array = array;
        this.licensePlate = licensePlate;
        this. startDate = startDate;
        this.endDate = endDate;
        this.area = area;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        try {
            this.licensePlate.setText(this.array.getJSONObject(position).getString("registrationNumber"));
            this.startDate.setText(this.array.getJSONObject(position).getString("startDate").substring(0,16).replace("T", " "));
            this.endDate.setText(this.array.getJSONObject(position).getString("endDate").substring(0,16).replace("T", " "));
            this.area.setText(this.array.getJSONObject(position).getString("areaId"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
