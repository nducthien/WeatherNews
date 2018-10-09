package com.tea.weathernews.retrofit;

import com.tea.weathernews.model.WeatherForecastResult;
import com.tea.weathernews.model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLong(@Query("lat") String lat,
                                                  @Query("lon") String lng,
                                                  @Query("appid") String appid,
                                                  @Query("units") String unit);

    @GET("forecast")
    Observable<WeatherForecastResult> getForecastWeatherByLatLong(@Query("lat") String lat,
                                                                  @Query("lon") String lng,
                                                                  @Query("appid") String appid,
                                                                  @Query("units") String unit);

}
