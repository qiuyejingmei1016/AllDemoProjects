package com.bairuitech.anychat.f2fvideo.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.lang.ref.WeakReference;

/**
 * @describe: 高德定位工具类
 * @author: yyh
 * @createTime: 2019/9/19 13:53
 * @className: LocationUtil
 */
public class LocationUtil {

    private static volatile LocationUtil mInstance = null;
    private Context mContext;
    private AMapLocation mLocation;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private WeakReference<LocationChangeCallBack> mWeakReference;

    private void setLocation(AMapLocation location) {
        this.mLocation = location;
    }

    //获取经纬度
    public AMapLocation showLocation() {
        return mLocation;
    }

    private LocationUtil(Context context) {
        this.mContext = context;
        initLocation();
    }

    public static LocationUtil getInstance(Context context) {
        if (null == mInstance) {
            synchronized (LocationUtil.class) {
                if (mInstance == null) {
                    mInstance = new LocationUtil(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(mContext);
        locationOption = new AMapLocationClientOption();
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        locationOption.setGpsFirst(false);
        //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        locationOption.setHttpTimeOut(30000);
        //可选，设置定位时间间隔。默认为2秒 (可根据具体要求设置时间间隔定时检测位置信息)
        locationOption.setInterval(60000 * 5);
        //可选，设置是否返回逆地理地址信息。默认是true
        locationOption.setNeedAddress(true);
        //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        //开始定位
        startLocation();

        //获取上一个定位数据
        AMapLocation location = locationClient.getLastKnownLocation();
        if (location != null) {
            setLocation(location);
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (null != locationClient) {
            // 设置定位参数
            locationClient.setLocationOption(locationOption);
            // 启动定位
            locationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        if (null != locationClient) {
            locationClient.stopLocation();
        }
    }

    /**
     * 销毁定位
     */
    public void destroyLocation() {
        if (null != locationClient) {
            stopLocation();
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        if (null != mInstance) {
            mInstance = null;
        }
    }

    /**
     * 定位监听
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            setLocation(location);
            if (mWeakReference != null) {
                mWeakReference.get().onLocationChanged(location);
            }
        }
    };

    public interface LocationChangeCallBack {
        void onLocationChanged(AMapLocation location);
    }

    public LocationChangeCallBack setCallBack(LocationChangeCallBack callBack) {
        this.mWeakReference = new WeakReference<>(callBack);
        return mWeakReference.get();
    }
}