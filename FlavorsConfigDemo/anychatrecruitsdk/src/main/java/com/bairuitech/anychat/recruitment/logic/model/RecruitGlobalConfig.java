package com.bairuitech.anychat.recruitment.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bairuitech.anychat.recruitment.logic.model.trans.RecruitBusinessModel;

/**
 * @describe: 用于保存全局配置信息
 * @author: yyh
 * @createTime: 2018/10/31 13:54
 * @className: RecruitGlobalConfig
 */
public class RecruitGlobalConfig implements Parcelable {

    private static volatile RecruitGlobalConfig mInstance;

    private String reservationNo;//预约编码

    private RecruitBusinessModel recruitBusinessModel;//业务信息模型实体类

    public void release() {
        reservationNo = "";
        if (mInstance != null) {
            mInstance = null;
        }
    }

    private RecruitGlobalConfig() {
    }

    private RecruitGlobalConfig(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static RecruitGlobalConfig getInstance() {
        if (mInstance == null) {
            synchronized (RecruitGlobalConfig.class) {
                if (mInstance == null) {
                    mInstance = new RecruitGlobalConfig();
                }
            }
        }
        return mInstance;
    }

    public String getReservationNo() {
        return reservationNo;
    }

    public void setReservationNo(String reservationNo) {
        this.reservationNo = reservationNo;
    }

    public RecruitBusinessModel getRecruitBusinessModel() {
        return recruitBusinessModel;
    }

    public void setRecruitBusinessModel(RecruitBusinessModel recruitBusinessModel) {
        this.recruitBusinessModel = recruitBusinessModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(reservationNo);
    }

    private void readFromParcel(Parcel parcel) {
        this.reservationNo = parcel.readString();
    }

    public static final Parcelable.Creator<RecruitGlobalConfig> CREATOR = new Parcelable.Creator<RecruitGlobalConfig>() {
        @Override
        public RecruitGlobalConfig createFromParcel(Parcel parcel) {
            return new RecruitGlobalConfig(parcel);
        }

        @Override
        public RecruitGlobalConfig[] newArray(int size) {
            return new RecruitGlobalConfig[size];
        }
    };
}