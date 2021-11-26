package pl.adambartkowiak.rectanglegame

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ModelPathName(val path: String, val fileName: String) : Parcelable