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
 * An example of how to use SysIntegrity Service API.
 * Note that you have to configure an AppId for SafetyDetect Service first.
 *
 * @since 4.0.0.300
 */
public class SafetyDetectSysIntegrityAPIFragment extends Fragment implements View.OnClickListener {
    private Button mButton1;

    private TextView basicIntegrityTextView;

    private TextView adviceTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fg_sysintegrity, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButton1 = getActivity().findViewById(R.id.fg_button_sys_integrity_go);
        mButton1.setOnClickListener(this);

        basicIntegrityTextView = getActivity().findViewById(R.id.fg_payloadBasicIntegrity);
        adviceTextView = getActivity().findViewById(R.id.fg_payloadAdvice);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fg_button_sys_integrity_go) {
            processView();
            invokeSysIntegrity();
        }
    }

    private void processView() {
        basicIntegrityTextView.setText("");
        adviceTextView.setText("");
        ((TextView) getActivity().findViewById(R.id.fg_textView_title)).setText("");
        mButton1.setText(R.string.processing);
        mButton1.setBackgroundResource(R.drawable.btn_round_processing);
    }

    private void invokeSysIntegrity() {
        // TODO: 使用系统完整性检测能力，检测手机系统完整性是否被破坏。
        // TODO: 要求：1.调用系统完整性检测接口，获取结果。
        // TODO:      2.从结果中解析检测结果 basicIntegrity 并展示在界面中。
        // TODO: 完成效果图参照 output文件夹中的sysintegrity.png
        basicIntegrityTextView.setText("使用系统完整性检测能力，检测手机系统完整性是否被破坏。");
    }
}
