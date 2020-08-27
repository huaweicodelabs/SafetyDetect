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
package com.huawei.hms.safetydetect.sample

import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

/**
 * An example of how to use SysIntegrity Service API.
 * Note that you have to configure an AppId for SafetyDetect Service first.
 *
 * @since 4.0.0.300
 */
class SafetyDetectSysIntegrityAPIFragment : Fragment(), View.OnClickListener {
    private var mButton1: Button? = null
    private var basicIntegrityTextView: TextView? = null
    private var adviceTextView: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_sysintegrity, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mButton1 = activity?.findViewById(R.id.fg_button_sys_integrity_go)
        mButton1?.setOnClickListener(this)
        basicIntegrityTextView = activity?.findViewById(R.id.fg_payloadBasicIntegrity)
        adviceTextView = activity?.findViewById(R.id.fg_payloadAdvice)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.fg_button_sys_integrity_go) {
            processView()
            invokeSysIntegrity()
        }
    }

    private fun invokeSysIntegrity() {
        // TODO(developer): Change the nonce generation to include your own value.
        val nonce = ByteArray(24)
        try {
            val random: SecureRandom
            random = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SecureRandom.getInstanceStrong()
            } else {
                SecureRandom.getInstance("SHA1PRNG")
            }
            random.nextBytes(nonce)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, e.message!!)
        }
        SafetyDetect.getClient(activity)
                .sysIntegrity(nonce, APP_ID)
                .addOnSuccessListener { response -> // Indicates communication with the service was successful.
                    // Use response.getResult() to get the result data.
                    val jwsStr = response.result

                    // Process the result data here
                    val jwsSplit = jwsStr.split(".").toTypedArray()
                    Log.e(TAG, "-----jwsSplit-----$jwsSplit")
                    val jwsPayloadStr = jwsSplit[1]
                    val payloadDetail = String(Base64.decode(jwsPayloadStr.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE), StandardCharsets.UTF_8)
                    try {
                        val jsonObject = JSONObject(payloadDetail)
                        val basicIntegrity = jsonObject.getBoolean("basicIntegrity")
                        mButton1!!.setBackgroundResource(if (basicIntegrity) R.drawable.btn_round_green else R.drawable.btn_round_red)
                        mButton1!!.setText(R.string.rerun)
                        val isBasicIntegrity = basicIntegrity.toString()
                        val basicIntegrityResult = "Basic Integrity: $isBasicIntegrity"
                        basicIntegrityTextView!!.text = basicIntegrityResult
                        if (!basicIntegrity) {
                            val advice = "Advice: " + jsonObject.getString("advice")
                            adviceTextView!!.text = advice
                        }
                    } catch (e: JSONException) {
                        val errorMsg = e.message
                        Log.e(TAG, errorMsg ?: "unknown error")
                    }


                }
                .addOnFailureListener { e -> // There was an error communicating with the service.
                    val errorMsg: String?
                    errorMsg = if (e is ApiException) {
                        // An error with the HMS API contains some additional details.
                        val apiException = e as ApiException
                        SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode) +
                                ": " + apiException.message
                        // You can use the apiException.getStatusCode() method to get the status code.
                    } else {
                        // unknown type of error has occurred.
                        e.message
                    }
                    Log.e(TAG, errorMsg)
                    Toast.makeText(activity!!.applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                    mButton1!!.setBackgroundResource(R.drawable.btn_round_yellow)
                    mButton1!!.setText(R.string.rerun)
                }
    }

    private fun processView() {
        basicIntegrityTextView!!.text = ""
        adviceTextView!!.text = ""
        (activity?.findViewById<View>(R.id.fg_textView_title) as TextView).text = ""
        mButton1!!.setText(R.string.processing)
        mButton1!!.setBackgroundResource(R.drawable.btn_round_processing)
    }

    companion object {
        val TAG = SafetyDetectSysIntegrityAPIFragment::class.java.simpleName
        private const val APP_ID = "101324691"
    }
}