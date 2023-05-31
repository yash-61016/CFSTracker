package com.teessideUni.cfs_tracker.data.model

import java.util.Date

data class RespiratoryRateData(val rateValue: Float, val timestamp: Date, val detailedValue: ArrayList<RespiratoryAccelerometerData>){
    fun getRate(): Float{
        return rateValue;
    }
}