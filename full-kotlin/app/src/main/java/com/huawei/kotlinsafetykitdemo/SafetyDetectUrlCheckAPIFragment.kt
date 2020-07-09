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
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.safetydetect.UrlCheckThreat
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.fg_urlcheck.*

/**
 * An example of how to use UrlCheck Service API.
 *
 * Note that you should call initUrlCheck method before you call urlCheck method.
 */
class SafetyDetectUrlCheckAPIFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        private const val TAG = "SafetyDetectUrlCheckAPI"
        private const val APP_ID = "102377513"
    }
    private lateinit var client: SafetyDetectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = SafetyDetect.getClient(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fg_urlcheck, container, false)
    }

    override fun onResume() {
        super.onResume()
        client.initUrlCheck()
    }

    override fun onPause() {
        super.onPause()
        client.shutdownUrlCheck()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fg_call_url_btn.setOnClickListener {callUrlCheckApi()}
        fg_url_spinner.onItemSelectedListener = this
        val adapter = requireContext().let {
            ArrayAdapter.createFromResource(
                it,
                R.array.url_array,
                R.layout.support_simple_spinner_dropdown_item
            )
        }
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        fg_url_spinner.adapter = adapter
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, pos: Int, id: Long ) {
        val url = adapterView.getItemAtPosition(pos) as String
        fg_call_urlCheck_text.text = Editable.Factory.getInstance().newEditable(url)
        fg_call_urlResult.text = Editable.Factory.getInstance().newEditable("")
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}

    private fun callUrlCheckApi() {
        Log.i(TAG, "Start call URL check api")
        val realUrl = fg_call_urlCheck_text.text.toString().trim()
        // Specify url threat type
        client.urlCheck(realUrl, APP_ID, UrlCheckThreat.MALWARE, UrlCheckThreat.PHISHING)
            .addOnSuccessListener { urlCheckResponse ->
                /**
                 * Called after successfully communicating with the SafetyDetect API.
                 * The #onSuccess callback receives an
                 * [com.huawei.hms.support.api.entity.safetydetect.UrlCheckResponse] that contains a
                 * list of UrlCheckThreat that contains the threat type of the Url.
                 */
                // Indicates communication with the service was successful.
                // Identify any detected threats.
                // Call getUrlCheckResponse method of UrlCheckResponse then you can get List<UrlCheckThreat> .
                // If List<UrlCheckThreat> is empty , that means no threats found , else that means threats found.
                val list = urlCheckResponse.urlCheckResponse
                if (list.isEmpty()) {
                    // No threats found.
                   fg_call_urlResult.text = Editable.Factory.getInstance().newEditable(getString(R.string.threat_not_found))
                } else {
                    // Threats found!
                    fg_call_urlResult.text = Editable.Factory.getInstance().newEditable(getString(R.string.threats_found))
                }
            }
            .addOnFailureListener { apiException ->
                /**
                 * Called when an error occurred when communicating with the SafetyDetect API.
                 */
                // An error with the Huawei Mobile Service API contains some additional details and You can use the apiException.getStatusCode() method to get the status code.
                val errorMsg: String? = if (apiException is ApiException) {
                    "Error : ${SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode)} : ${apiException.message}"
                    // Note: If the status code is SafetyDetectStatusCodes.CHECK_WITHOUT_INIT, you need to call initUrlCheck().
                } else {
                    // Unknown type of error has occurred.
                    apiException.message
                }
                Log.d(TAG, getString(R.string.url_check_failed_error, errorMsg))
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT ).show()
            }
    }

}