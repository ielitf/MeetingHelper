package com.ceiv.meetinghelper.control;

import android.app.Application;

import com.ceiv.meetinghelper.utils.BaseConfig;
import com.ceiv.meetinghelper.utils.CrashHandler;
import com.ceiv.meetinghelper.utils.SSLSocketClient;
import com.ceiv.meetinghelper.utils.SharePreferenceManager;
import com.ceiv.meetinghelper.utils.SharedPreferenceTools;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {
	private static final String SHARED_PREFERENCE_NAME = "MeetingSystem_sp";

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		SharePreferenceManager.init(getApplicationContext(), SHARED_PREFERENCE_NAME);
		if (SharePreferenceManager.getIsFirstUse()) {
//			SDCardUtils.writeTxt("001",CodeConstants.ROOM_NUMBER);
//			SDCardUtils.writeTxt("192.168.10.2:1883",CodeConstants.IP_HOST_NEWS);
//			SDCardUtils.writeTxt("192.168.10.120:8080",CodeConstants.IP_HOST_APP);
			//首次初始化
			SharedPreferenceTools.putValuetoSP(getApplicationContext(), "mqttIp", "aids.zdhs.com.cn:1883");
			SharedPreferenceTools.putValuetoSP(getApplicationContext(), "ServiceIp", "https://aids.zdhs.com.cn");
			SharedPreferenceTools.putValuetoSP(getApplicationContext(), "DeviceNum", "999");
			SharePreferenceManager.setIsFirstUse(false);
		}

		String ServiceIp = (String) SharedPreferenceTools.getValueofSP(getApplicationContext(), "ServiceIp", "https://c.huihuinet.cn");
		BaseConfig.ServiceIp = ServiceIp;
		String mqttIp = (String) SharedPreferenceTools.getValueofSP(getApplicationContext(), "mqttIp", "https://c.huihuinet.cn");
		BaseConfig.MqttIp = mqttIp;

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
		//log打印级别，决定了log显示的详细程度
		loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
		//log颜色级别，决定了log在控制台显示的颜色
		loggingInterceptor.setColorLevel(Level.INFO);
		builder.addInterceptor(loggingInterceptor);

		//全局的读取超时时间  基于前面的通道建立完成后，客户端终于可以向服务端发送数据了
		builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		//全局的写入超时时间  服务器发回消息，可是客户端出问题接受不到了
		builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		//全局的连接超时时间  http建立通道的时间
		builder.connectTimeout(3000L, TimeUnit.MILLISECONDS);
//        HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("ase.cer"));
//        builder.sslSocketFactory(sslParams3.sSLSocketFactory, sslParams3.trustManager);
//        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
//        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
//        builder.sslSocketFactory(SSLSocketClient.getSocketFactory(this), sslParams3.trustManager);
		HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
		builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
		builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
		//使用sp保持cookie，如果cookie不过期，则一直有效
		builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
		OkGo.getInstance().init(this)                       //必须调用初始化
				.setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
				.setCacheMode(CacheMode.NO_CACHE)
				//全局统一缓存模式，默认不使用缓存，可以不传
				.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
				.setRetryCount(3);

	}
}