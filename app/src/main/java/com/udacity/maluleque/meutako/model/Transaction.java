package com.udacity.maluleque.meutako.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Transaction implements Parcelable {
    private String uid;
    private String type;
    private double amount;
    private String category;
    private String description;
    private long date;
    private String image;

    public Transaction() {
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    protected Transaction(Parcel in) {
        uid = in.readString();
        type = in.readString();
        amount = in.readDouble();
        category = in.readString();
        description = in.readString();
        date = in.readLong();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(type);
        dest.writeDouble(amount);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeLong(date);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
