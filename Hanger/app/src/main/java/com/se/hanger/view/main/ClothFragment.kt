package com.se.hanger.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.se.hanger.R
import com.se.hanger.data.model.Weather
import com.se.hanger.data.retrofit.RetrofitClient
import com.se.hanger.data.retrofit.api.WeatherService
import com.se.hanger.databinding.FragmentClothBinding
import com.se.hanger.view.weather.WeatherActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ClothFragment : Fragment(), View.OnClickListener {

    private var retrofit = RetrofitClient.getRetrofit()
    private var job = Job()
    private val _weatherItems = MutableLiveData<List<Weather.Response.Body.Items.Item>>()
    private val weatherItems: LiveData<List<Weather.Response.Body.Items.Item>> = _weatherItems
    lateinit var binding: FragmentClothBinding
    private var temperature: String? = null // 온도

    companion object {
        // 실수 입력 불가 e.g. 37.651234
        const val sangmyeongNx = "38"
        const val sangmyeongNy = "127"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClothBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 비동기적으로 날씨 데이터를 불러온다.
        loadWeatherData()
        // 데이터를 가져올 경우 갱신할 UI를 위한 LiveData
        registerLiveData()
        setClickListener() // 클릭 리스너 설정
    }

    private fun setClickListener() {
        with(binding) {
            weatherBtn.setOnClickListener(this@ClothFragment)
        }
    }


    private fun registerLiveData() {
        weatherItems.observe(viewLifecycleOwner) { items ->
            items.forEach { item ->
                if (item.category == "PTY") { // 현재 날씨 상태 : PTY
                    val state = item.obsrValue.toInt()
                    Log.d("TAG", ": $state")
                    updateWeather(state) // 날씨에 따라 UI 갱신

                } else if (item.category == "T1H") { // 온도
                    temperature = item.obsrValue // 날씨 변수 초기화
                    Log.d("TAG", "온도: $temperature")
                    updateTemperature(temperature!!) // 온도 수치 갱신
                }
            }
        }
    }

    private fun updateTemperature(temperature: String) {
        binding.weatherTv.text = binding.weatherTv.text.toString() + " $temperature°C"
    }

    private fun updateWeather(state: Int) {
        // PTY : 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
        when (state) {
            0 -> {
                binding.weatherIv.setImageResource(R.drawable.ic_weather_sunny)
                binding.weatherTv.text = "맑음"
            }
            1, 2, 4, 5, 6 -> {
                binding.weatherIv.setImageResource(R.drawable.ic_weather_rain)
                binding.weatherTv.text = "비내림"
            }
            3, 7 -> {
                binding.weatherIv.setImageResource(R.drawable.ic_weather_snow)
                binding.weatherTv.text = "눈내림"
            }
        }
    }

    private fun loadWeatherData() {
        CoroutineScope(job + Dispatchers.IO).async {
            val weatherService = retrofit?.create(WeatherService::class.java)
            weatherService?.getWeather(
                RetrofitClient.serviceKey,
                "1",
                "1000",
                "JSON",
                getNowDate(),
                getNowHour(),
                sangmyeongNx,
                sangmyeongNy
            )?.enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    Log.d("Weather", "onResponse: $response")

                    val weather = response.body() as Weather?
                    //Log.d("Weather", "loaded Data: $weather")

                    // category 종류
                    // T1H(기온 ℃), RN1(1시간 강수량 mm), UUU(동서바람성분 m/s), VVV(남북바람성분 m/s)
                    // REH(습도 %), PTY(강수형태), VEC(풍향 deg), WSD(풍속 m/s)
                    // PTY : 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
                    // obsrValue : 수치
                    weather?.response?.body?.items?.item?.forEach { item ->
                        Log.d("TAG", "onResponse: $item")
                    }
                    // 가져온 데이터 할당
                    _weatherItems.value = weather?.response?.body?.items?.item


                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    Log.d("Weather", "onFailure: $t")
                }

            })
        }
    }

    private fun getNowHour(): String {
        var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
        if (hour.length == 1) {
            hour = "0$hour"
        }
        hour += "00"
        Log.d("TAG", "getNowHour: $hour")
        return hour
    }

    @SuppressLint("SimpleDateFormat")
    private fun getNowDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd")
        return dateFormat.format(Date())
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.weather_btn -> { // 날씨 버튼 클릭시 Weather Fragment로 전환
                val intent = Intent(requireContext(), WeatherActivity::class.java)
                intent.putExtra("temp", temperature)
                startActivity(intent)
            }
        }
    }

}