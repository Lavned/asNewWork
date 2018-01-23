/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.jy.drivershelper.project;

import android.os.Bundle;
import org.apache.cordova.*;

public class MainActivity extends CordovaActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }
    public void showContacts(){  
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)  
          != PackageManager.PERMISSION_GRANTED  
          || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  
          != PackageManager.PERMISSION_GRANTED  
          || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)  
          != PackageManager.PERMISSION_GRANTED) {  
          Toast.makeText(getApplicationContext(),"û��Ȩ��,���ֶ�������λȨ��",Toast.LENGTH_SHORT).show();  
          // ����һ����������Ȩ�ޣ����ṩ���ڻص����صĻ�ȡ�루�û����壩  
          ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);  
        }  
      }  
     
      //Android6.0����Ȩ�޵Ļص�����  
      @Override  
      public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {  
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);  
        switch (requestCode) {  
          // requestCode����������Ȩ�޻�ȡ�룬��checkSelfPermissionʱ����  
          case BAIDU_READ_PHONE_STATE:  
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  
              // ��ȡ��Ȩ�ޣ�����Ӧ�������ö�λSDKӦ��ȷ�����Ȩ�޾�����Ȩ�������������λʧ�ܣ�  
            } else {  
              // û�л�ȡ��Ȩ�ޣ������⴦��  
              Toast.makeText(getApplicationContext(), "��ȡλ��Ȩ��ʧ�ܣ����ֶ�����", Toast.LENGTH_SHORT).show();  
            }  
            break;  
          default:  
            break;  
        }  
      }  
}
