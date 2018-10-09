package com.tea.weathernews.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tea.weathernews.R;
import com.tea.weathernews.common.Common;
import com.tea.weathernews.model.WeatherForecastResult;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Load icon
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_weather);

        holder.tv_date.setText(new StringBuilder(Common.convertUnixToDate(weatherForecastResult
                .list.get(position).dt)));

        holder.tv_description.setText(new StringBuilder(weatherForecastResult.list.get(position)
                .weather.get(0).getDescription()));

        holder.tv_tempature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position)
                .main.getTemp())).append("°C"));


    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date;
        TextView tv_description;
        TextView tv_tempature;
        ImageView img_weather;

        public MyViewHolder(View itemView) {
            super(itemView);

            img_weather = itemView.findViewById(R.id.img_weather);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_tempature = itemView.findViewById(R.id.tv_temperature);
        }
    }
}
