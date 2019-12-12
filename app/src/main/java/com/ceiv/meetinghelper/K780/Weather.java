package com.ceiv.meetinghelper.K780;

/**
 * K780天气数据实体类
 */
public class Weather {

	private String citynm; // 城市名字
	private String temperature; // 温度范围
	private String temperature_curr; // 当前温度
	private String weather; // 今日天气
	private String weather_curr; // 当前天气
	private String weather_icon; // 天气图片

	public Weather() {
	}

	public Weather(String citynm, String temperature, String temperature_curr, String weather_curr,
			String weather_icon) {
		this.citynm = citynm;
		this.temperature = temperature;
		this.temperature_curr = temperature_curr;
		this.weather_curr = weather_curr;
		this.weather_icon = weather_icon;
	}

	@Override
	public String toString() {
		return "Weather{" +
				"citynm='" + citynm + '\'' +
				", temperature='" + temperature + '\'' +
				", temperature_curr='" + temperature_curr + '\'' +
				", weather='" + weather + '\'' +
				", weather_curr='" + weather_curr + '\'' +
				", weather_icon='" + weather_icon + '\'' +
				'}';
	}
}
