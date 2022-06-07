package com.example.arch_practices.model

import android.os.Parcel
import android.os.Parcelable

data class Coin(
    val changePercent24Hr: Double,
    val name: String,
    val priceUsd: Double,
    val symbol: String,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(changePercent24Hr)
        parcel.writeString(name)
        parcel.writeDouble(priceUsd)
        parcel.writeString(symbol)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coin> {
        override fun createFromParcel(parcel: Parcel): Coin {
            return Coin(parcel)
        }

        override fun newArray(size: Int): Array<Coin?> {
            return arrayOfNulls(size)
        }
    }
}