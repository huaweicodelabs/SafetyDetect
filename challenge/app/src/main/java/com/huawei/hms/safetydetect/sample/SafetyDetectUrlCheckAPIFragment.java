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

package com.huawei.hms.safetydetect.sample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;

/**
 * An example of how to use UrlCheck Service API.
 * Note that you should call initUrlCheck method before you call urlCheck method.
 *
 * @since 4.0.0.300
 */
public class SafetyDetectUrlCheckAPIFragment extends Fragment
    implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private TextView urlCheckStatusView;

    private SafetyDetectClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = SafetyDetect.getClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fg_urlcheck, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        client.initUrlCheck();
    }

    @Override
    public void onPause() {
        super.onPause();
        client.shutdownUrlCheck();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.fg_call_url_btn).setOnClickListener(this);
        Spinner spinner = getActivity().findViewById(R.id.fg_url_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
            R.array.url_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        urlCheckStatusView = getActivity().findViewById(R.id.fg_urlchecktextView);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fg_call_url_btn) {
            callUrlCheckApi();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String url = (String) adapterView.getItemAtPosition(pos);
        EditText textView = getActivity().findViewById(R.id.fg_call_urlCheck_text);
        textView.setText(url);
        final EditText testRes = getActivity().findViewById(R.id.fg_call_urlResult);
        testRes.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void callUrlCheckApi() {

        // TODO: 使用恶意URL检测能力，检测URL是否为恶意网址
        // TODO: 要求：1.使用恶意URL检测能力，检测URL是否为恶意网址
        // TODO:      2.从结果中解析检测结果，并展示在界面中。
        // TODO: 完成效果图参照 output文件夹中的urlcheck.PNG
        urlCheckStatusView.setText("使用恶意URL检测能力，检测URL是否为恶意网址");
    }
}
