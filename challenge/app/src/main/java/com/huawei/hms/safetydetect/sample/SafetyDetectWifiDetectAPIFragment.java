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
import android.widget.Button;
import android.widget.TextView;

/**
 * An example of how to use WifiDetect Service API.
 * Note that you have to configure an AppId for SafetyDetect Service first.
 *
 * @since 4.0.0.300
 */
public class SafetyDetectWifiDetectAPIFragment extends Fragment implements View.OnClickListener {
    private Button mButton1;

    private TextView wifiDetectStatusView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fg_wifidetect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mButton1 = getActivity().findViewById(R.id.fg_get_wifidetect_status);
        mButton1.setOnClickListener(this);
        wifiDetectStatusView = getActivity().findViewById(R.id.fg_wifidetecttextView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fg_get_wifidetect_status) {
            getWifiDetectStatus();
        }
    }

    /**
     * getWifiDetectStatus() Get Wifi Status.
     */

    private void getWifiDetectStatus() {
        // TODO 使用恶意Wi-Fi检测能力，检测手机连接Wi-Fi是否为恶意Wi-Fi
        // TODO: 要求：1.使用恶意Wi-Fi检测能力，检测手机连接Wi-Fi是否为恶意Wi-Fi。
        // TODO:      2.从结果中解析检测结果，并展示在界面中。
        // TODO: 完成效果图参照 output文件夹中的wifidetect.png
        wifiDetectStatusView.setText("请完成wifidetect接口调用");
    }
}
