package com.example.android.sunshine.utilities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.R;

/**
 * Created by psw10 on 2017-01-04.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    private String[] mWeatherData;

    public ForecastAdapter(){

    }
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView mWeatherTextView;
        ForecastAdapterViewHolder(View view){
            super(view);
            mWeatherTextView = (TextView)view.findViewById(R.id.tv_weather_data);
        }
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.forecast_list_item, parent, false);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        String weatherForThisDay = mWeatherData[position];
        holder.mWeatherTextView.setText(weatherForThisDay);
    }
    @Override
    public int getItemCount(){
        if(mWeatherData == null) return 0;
        return mWeatherData.length;
    }

    public void setWeatherData(String[] weatherData){
        mWeatherData=weatherData;
        notifyDataSetChanged();
    }
}
