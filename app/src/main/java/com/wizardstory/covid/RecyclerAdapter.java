package com.wizardstory.covid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context context;
    private JSONArray mDataset;
    private DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    private DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textCountry, textDate, textNewConfirmed, textTotalConfirmed, textNewDeath, textTotalDeath, textNewRecovered, textTotalRecovered;

        public MyViewHolder(View v) {
            super(v);
            textCountry = v.findViewById(R.id.textCountry);
            textDate = v.findViewById(R.id.textDate);
            textNewConfirmed = v.findViewById(R.id.textNewConfirmed);
            textTotalConfirmed = v.findViewById(R.id.textTotalConfirmed);
            textNewDeath = v.findViewById(R.id.textNewDeath);
            textTotalDeath = v.findViewById(R.id.textTotalDeath);
            textNewRecovered = v.findViewById(R.id.textNewRecovered);
            textTotalRecovered = v.findViewById(R.id.textTotalRecovered);
        }
    }

    public RecyclerAdapter(Context context, JSONArray mDataset) {
        this.mDataset = mDataset;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        try {
            JSONObject data = (JSONObject) mDataset.get(position);
            DecimalFormat decimalFormat = getDecimalFormat();
            holder.textCountry.setText(data.getString("Country"));
            holder.textDate.setText(outputFormatter.format(LocalDateTime.parse(data.getString("Date"), inputFormatter)));
            holder.textNewConfirmed.setText("New Confirmed: " + decimalFormat.format(data.getLong("NewConfirmed")));
            holder.textTotalConfirmed.setText("Total Confirmed: " + decimalFormat.format(data.getLong("TotalConfirmed")));
            holder.textNewDeath.setText("New Deaths: " + decimalFormat.format(data.getLong("NewDeaths")));
            holder.textTotalDeath.setText("Total Deaths: " + decimalFormat.format(data.getLong("TotalDeaths")));
            holder.textNewRecovered.setText("New Recovered: " + decimalFormat.format(data.getLong("NewRecovered")));
            holder.textTotalRecovered.setText("Total Recovered: " + decimalFormat.format(data.getLong("TotalRecovered")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length();
    }

    private DecimalFormat getDecimalFormat(){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat;
    }
}
