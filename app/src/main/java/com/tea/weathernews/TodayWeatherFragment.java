package com.tea.weathernews;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.squareup.picasso.Picasso;
import com.tea.weathernews.common.Common;
import com.tea.weathernews.model.WeatherResult;
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
public class TodayWeatherFragment extends Fragment {

    ImageView img_weather;
    TextView tv_city_name;
    TextView tv_temperature;
    TextView tv_description;
    TextView tv_date_time;
    TextView tvWind;
    TextView tv_pressure;
    TextView tv_humidity;
    TextView tv_sunrise;
    TextView tv_sunset;
    TextView tv_geo_coords;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mServicer;

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if (instance == null)
            instance = new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServicer = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);

        img_weather = view.findViewById(R.id.img_weather);
        tv_city_name = view.findViewById(R.id.tv_city_name);
        tv_temperature = view.findViewById(R.id.tv_temperature);
        tv_description = view.findViewById(R.id.tv_description);
        tv_date_time = view.findViewById(R.id.tv_date_time);
        tvWind = view.findViewById(R.id.tvWind);
        tv_pressure = view.findViewById(R.id.tv_pressure);
        tv_humidity = view.findViewById(R.id.tv_humidity);
        tv_sunrise = view.findViewById(R.id.tv_sunrise);
        tv_sunset = view.findViewById(R.id.tv_sunset);
        tv_geo_coords = view.findViewById(R.id.tv_geo_coords);

        weather_panel = view.findViewById(R.id.weather_panel);
        loading = view.findViewById(R.id.loading);

        //Add targetView
        TapTargetView.showFor((Activity) getContext(),
                TapTarget.forView(view.findViewById(R.id.weather_panel), "This is Target", "Show weather today")
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorAccent));

        getWeatherInformation();

        return view;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mServicer.getWeatherByLatLong(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_API,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        // Load image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);

                        //Load information
                        tv_city_name.setText(weatherResult.getName());
                        tv_description.setText(new StringBuilder("Weather in ")
                                .append(weatherResult.getName().toString()));
                        tv_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                                .append("°C").toString());
                        tv_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        tv_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure()))
                                .append("hpa").toString());
                        tv_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity()))
                                .append("%").toString());
                        tv_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        tv_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        tv_geo_coords.setText(new StringBuilder(weatherResult.getCoord().toString()).toString());

                        //Display Panel
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
