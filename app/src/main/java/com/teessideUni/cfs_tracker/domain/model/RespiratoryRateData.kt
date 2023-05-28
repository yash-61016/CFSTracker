package com.teessideUni.cfs_tracker.domain.model

data class RespiratoryRateData(val rateValue: Float, val timestamp: Long, val detailedValue: ArrayList<RespiratoryAccelerometerData>){
    fun getRate(): Float{
        return rateValue;
    }
}