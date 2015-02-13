package org.aku.sm.smclient.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Medication  implements Parcelable {

    private long id;
    private String name;
    private String description;

    public Medication() {
    }

     public Medication(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public Medication(Exception ex) {

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //
    // Implementation of Parcelable
    //
    public Medication(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<Medication> CREATOR = new Parcelable.Creator<Medication>() {
        public Medication createFromParcel(Parcel in) {
            return new Medication(in);
        }

        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };


}
