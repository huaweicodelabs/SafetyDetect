/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.huawei.kotlinsafetykitdemo

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.fg_sysintegrity.*
import org.json.JSONException
import org.json.JSONObject

/**
 * An example of how to use SysIntegrity Service API.
 * Note that you have to configure an AppId for SafetyDetect Service first.
 */
class SafetyDetectSysIntegrityAPIFragment : Fragment() {

    companion object {
        val TAG = SafetyDetectSysIntegrityAPIFragment::class.java.simpleName
        private const val APP_ID = "102297821"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_sysintegrity, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fg_button_sys_integrity_go.setOnClickListener {
            processView()
            invokeSysIntegrity()
        }
    }

    private fun processView() {
        fg_payloadBasicIntegrity.text = ""
        fg_button_sys_integrity_go.text = getString(R.string.processing)
        fg_button_sys_integrity_go.setBackgroundResource(R.drawable.btn_round_processing)
    }

    private fun invokeSysIntegrity() {
        // TODO(developer): Change the nonce generation to include your own value.
        val nonce = getString(R.string.nounce_sample_string, System.currentTimeMillis()).toByteArray()
        Log.d(TAG,"nonce value : $nonce")
        SafetyDetect.getClient(activity)
            .sysIntegrity(nonce, APP_ID)
            .addOnSuccessListener { response ->
                // On success of API call, we get result from response.getResult()
                val jwsSplitResponse = response.result.split(".").toTypedArray()[1]
                val payloadDetail = String(Base64.decode( jwsSplitResponse.toByteArray(), Base64.URL_SAFE))
                try {
                    val basicIntegrity = JSONObject(payloadDetail).getBoolean("basicIntegrity")
                    Log.d(TAG,"payload String : $basicIntegrity")
                    fg_button_sys_integrity_go.setBackgroundResource(if (basicIntegrity) R.drawable.btn_round_green else R.drawable.btn_round_red)
                    fg_button_sys_integrity_go.setText(R.string.rerun)
                    fg_payloadBasicIntegrity.text = getString(R.string.basic_integrity_txt,basicIntegrity.toString())
                    if (!basicIntegrity) {
                        val advice = JSONObject(payloadDetail).getString("advice")
                        fg_payloadAdvice.text = getString(R.string.basic_integrity_advice_txt, advice)
                    }
                } catch (e: JSONException) {
                    e.message?.let { errorMessageToast(it) }
                }
            }
            .addOnFailureListener { exception ->
                val errorMsg = if (exception is ApiException) {
                    // An error with the HMS API contains some additional details.
                    "${(SafetyDetectStatusCodes.getStatusCodeString(exception.statusCode))} : ${exception.message}"
                    // You can use the apiException.getStatusCode() method to get the status code.
                } else {
                    // Unknown type of error has occurred.
                    exception.message.toString()
                }
                errorMessageToast(errorMsg)
            }
    }

    private fun errorMessageToast(errorMsg : String) {
        Log.e(TAG, errorMsg)
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        fg_button_sys_integrity_go.setBackgroundResource(R.drawable.btn_round_yellow)
        fg_button_sys_integrity_go.setText(R.string.rerun)
    }
}