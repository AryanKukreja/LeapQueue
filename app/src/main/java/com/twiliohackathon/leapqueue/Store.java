/*
 * Author     - Aryan Kukreja | Student #: 100651838
 * Date Made  - Tuesday, April 21, 2019 @ 11:13 PM
 * Course     -  |
 *
 * Purpose:
 *      - Assignment # | Assignment Description
 *
 * About:
 *      -
 *
 * Input      - {List of Inputs}
 * Output     - {List of Outputs}
 *
 * Extra Info - {}
 */

package com.twiliohackathon.leapqueue;

import android.os.Parcel;
import android.os.Parcelable;


@SuppressWarnings({"unused", "WeakerAccess"})
public class Store implements Parcelable {

    String storeName, postalCode, address, website, type;

    public Store(Parcel in) {
        this.storeName = in.readString();
        this.postalCode = in.readString();
        this.address = in.readString();
        this.website = in.readString();
        this.type = in.readString();
    }

    public Store(String name, String type, String website, String address, String postalCode) {
        this.storeName = name;
        this.type = type;
        this.website = website;
        this.address = address;
        this.postalCode = postalCode;
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.storeName);
        dest.writeString(this.postalCode);
        dest.writeString(this.address);
        dest.writeString(this.website);
        dest.writeString(this.type);
    }

    void setStoreName(String name) {
        this.storeName = name;
    }

    protected void setWebsite(String website) {
        this.website = website;
    }

    void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    void setAddress(String formattedAddress) {
        this.address = formattedAddress;
    }

    void setType(String entityType) { this.type = entityType; }

    String getStoreName() {
        return this.storeName;
    }

    String getType() { return this.type; }

    String getWebsite() {
        return this.website;
    }

    String getPostalCode() {
        return this.postalCode;
    }

    String getAddress() {
        return this.address;
    }

    public void printStore() {
        System.out.println("Store name: " + this.getStoreName());
        System.out.println("Store type: " + this.getType());
        System.out.println("Store address: " + this.getAddress());
        System.out.println("Postal Code: " + this.getPostalCode());
        System.out.println("Website: " + this.getWebsite());
        System.out.println("--------------------------------------------------");
    }
}
