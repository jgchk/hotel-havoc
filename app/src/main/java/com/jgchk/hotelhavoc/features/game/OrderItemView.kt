package com.jgchk.hotelhavoc.features.game

import android.os.Parcel
import com.jgchk.hotelhavoc.core.platform.KParcelable
import com.jgchk.hotelhavoc.core.platform.parcelableCreator

data class OrderItemView(val ingredientStrings: Array<String>, val ingredientDrawables: IntArray)
    : KParcelable {

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::OrderItemView)
    }

    constructor(parcel: Parcel) : this(parcel.createStringArray(), parcel.createIntArray())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeStringArray(ingredientStrings)
            writeIntArray(ingredientDrawables)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderItemView

        if (!ingredientStrings.contentEquals(other.ingredientStrings)) return false
        if (!ingredientDrawables.contentEquals(other.ingredientDrawables)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ingredientStrings.contentHashCode()
        result = 31 * result + ingredientDrawables.contentHashCode()
        return result
    }
}