package com.huawei.kotlinsafetykitdemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.fg_wifidetect.*


class SafetyDetectWifiDetectAPIFragment : Fragment(){

    companion object {
        private  val TAG = SafetyDetectWifiDetectAPIFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fg_wifidetect, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fg_get_wifidetect_status.setOnClickListener { getWifiDetectStatus() }
    }

    /**
     * getWifiDetectStatus()  Get Wifi Status.
     */
    private fun getWifiDetectStatus() {
        SafetyDetect.getClient(activity).wifiDetectStatus
            .addOnSuccessListener {
                Log.d(TAG, it.wifiDetectStatus.toString())
                fg_wifidetecttextView.text = getString(R.string.wifi_status_txt, it.wifiDetectStatus.toString())
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception is ApiException) {
                    // An error with the HMS API contains some additional details.
                    "${SafetyDetectStatusCodes.getStatusCodeString(exception.statusCode)} : ${exception.message}"
                    // You can use the apiException.getStatusCode() method to get the status code.
                } else {
                    // Unknown type of error has occurred.
                    exception.message.toString()
                }
                Log.e(TAG, errorMsg)
                fg_wifidetecttextView.text = getString(R.string.wifi_status_error_txt, errorMsg)
             }
     }

}