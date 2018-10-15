package com.tea.weathernews;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tea.weathernews.adapter.WeatherForecastAdapter;
import com.tea.weathernews.common.Common;
import com.tea.weathernews.model.WeatherForecastResult;
import com.tea.weathernews.retrofit.IOpenWeatherMap;
import com.tea.weathernews.retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView tv_city_name;
    TextView tv_geo_coords;
    RecyclerView rcv_forecast;

    static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if (instance == null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        tv_city_name = view.findViewById(R.id.tv_city_name);
        tv_geo_coords = view.findViewById(R.id.tv_geo_coords);

        rcv_forecast = view.findViewById(R.id.rcv_forecast);
        rcv_forecast.setHasFixedSize(true);
        rcv_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        getForecastInformation();

        return view;
    }

    // final, clear composite after stop fragment


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void getForecastInformation() {
        compositeDisposable.add(mService.getForecastWeatherByLatLong(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_API,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForcastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", "" + throwable.getMessage());
                    }
                })
        );
    }

    private void displayForcastWeather(WeatherForecastResult weatherForecastResult) {
        tv_city_name.setText(new StringBuilder(weatherForecastResult.city.name));
        tv_geo_coords.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(), weatherForecastResult);
        rcv_forecast.setAdapter(adapter);
    }

}
