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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.safetydetect.UserDetectResponse
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.fg_userdetect.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * An example of how to use the UserDetect Service API.
 *
 */
class SafetyDetectUserDetectAPIFragment : Fragment(){

    private lateinit var mSafetyDetectClient: SafetyDetectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSafetyDetectClient = SafetyDetect.getClient(activity)
    }

    override fun onResume() {
        super.onResume()
        mSafetyDetectClient.initUserDetect()
    }

    override fun onPause() {
        super.onPause()
        mSafetyDetectClient.shutdownUserDetect()
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_userdetect, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fg_login_btn.setOnClickListener { detect() }
    }

    private fun detect() {
        Log.i(TAG,"User detection start.")
        mSafetyDetectClient.userDetection(APP_ID.toString())
            .addOnSuccessListener { userDetectResponse ->
                /**
                 * Called after successfully communicating with the SafetyDetect API.
                 * The #onSuccess callback receives an
                 * [UserDetectResponse] that contains a
                 * responseToken that can be used to get user detect result.
                 */
                // Indicates communication with the service was successful.
                Log.i(TAG,"User detection succeed, response = $userDetectResponse")
                val verifySucceed= verify(userDetectResponse.responseToken)
                if (verifySucceed) {
                    Toast.makeText(requireContext(),getString(R.string.user_detect_success_str),Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(),getString(R.string.user_detect_failed_str),Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { apiException -> // There was an error communicating with the service.
                val errorMsg : String? = if (apiException is ApiException) {
                    // An error with the HMS API contains some additional details and You can use the apiException.getStatusCode() method to get the status code.
                    "${SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode)} : ${apiException.message}"
                } else {
                    // Unknown type of error has occurred.
                    apiException.message
                }
                Log.i(TAG,getString(R.string.user_detect_failed_info_error, errorMsg))
                Toast.makeText(requireContext(),errorMsg,Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        val TAG = SafetyDetectUserDetectAPIFragment::class.java.simpleName
        private const val APP_ID = 102377513

        /**
         * Send responseToken to your server to get the result of user detect.
         */
        private fun verify(responseToken: String): Boolean {
            var isTokenVerified = false
            val inputResponseToken: String = responseToken
            val isTokenResponseVerified = GlobalScope.async {
                val jsonObject = JSONObject()
                try {
                    // TODO(developer): Replace the baseUrl with your own server address,better not hard code.
                    val baseUrl = "https://hirms.cloud.huawei.com/rms/v1/userRisks/verify?appId=$APP_ID"
                    val put = jsonObject.put("response", inputResponseToken)
                    val result: String? = sendPost(baseUrl, put)
                    result?.let {
                        val resultJson = JSONObject(result)
                        isTokenVerified = resultJson.getBoolean("success")
                        // if success is true that means the user is real human instead of a robot.
                        Log.i(TAG, "verify: result = $isTokenVerified")
                    }
                    return@async isTokenVerified
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@async false
                }
            }
            GlobalScope.launch {
                isTokenVerified = isTokenResponseVerified.await()
            }
            return isTokenVerified
        }

        /**
         * post the response token to yur own server.
         */
        private fun sendPost(baseUrl: String, postDataParams: JSONObject): String? {
            try {
                val httpURLConnection = URL(baseUrl).openConnection() as HttpURLConnection
                httpURLConnection.apply {
                    readTimeout = 20000
                    connectTimeout = 20000
                    requestMethod = "POST"
                    doInput = true
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    outputStream.use {outputStream ->
                        BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
                            .use { writer ->
                                writer.write(postDataParams.toString())
                                writer.flush()
                            }
                    }
                }
                // To Check for 200
                if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                    val stringBuffer = StringBuffer()
                    lateinit var line: String
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuffer.append(line)
                        break
                    }
                    bufferedReader.close()
                    return stringBuffer.toString()
                }
            }catch (ioException : IOException) {
                Log.e("Request", "Error ", ioException)
                return null
            }
            return null
        }
    }
}