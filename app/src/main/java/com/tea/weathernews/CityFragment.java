package com.tea.weathernews;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.tea.weathernews.common.Common;
import com.tea.weathernews.model.WeatherResult;
import com.tea.weathernews.retrofit.IOpenWeatherMap;
import com.tea.weathernews.retrofit.RetrofitClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {

    private List<String> lstCities;
    private MaterialSearchBar searchBar;

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


    static CityFragment instance;

    public static CityFragment getInstance() {
        if (instance == null)
            instance = new CityFragment();
        return instance;
    }

    public CityFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServicer = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_city, container, false);

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

        searchBar = view.findViewById(R.id.serchBar);
        searchBar.setEnabled(false);

        new LoadCities().execute(); // Asynctask class to load Cities List


        return view;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {
            lstCities = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream inputStream = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

                InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(inputStreamReader);

                String readed;
                while ((readed = in.readLine()) != null)
                    builder.append(readed);

                lstCities = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {}.getType());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return lstCities;
        }

        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : listCity) {
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInfomation(text.toString());
                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);

            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInfomation(String cityName) {
        compositeDisposable.add(mServicer.getWeatherByCityName(cityName,
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
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
