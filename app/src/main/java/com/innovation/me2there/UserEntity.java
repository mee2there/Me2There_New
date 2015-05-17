package com.innovation.me2there;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public  class UserEntity implements Parcelable{

    private String _userID;
    private String _userName;
    private String _locationDetails;
    private String _preference;

    public String getUserID() {
        return _userID;
    }

    public String getUserName() {
        return _userName;
    }

    public String getLocationDetails() {
        return _locationDetails;
    }


    public String getPreference() {
        return _preference;
    }


    public UserEntity(String userId,String userName, String locationDetails, String preference) {
        _userID = userId;
        _userName = userName;
        _locationDetails = locationDetails;
        _preference = preference;

    }

    public UserEntity(Parcel in){
        String[] data = new String[4];


        in.readStringArray(data);
        this._userID = data[0];
        this._userName = data[1];
        this._locationDetails = data[2];
        this._preference = data[3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeStringArray(new String[] {this._userID,
                this._userName,this._locationDetails,this._preference});



    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };
}