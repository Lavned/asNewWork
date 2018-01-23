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
          Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();  
          // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）  
          ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);  
        }  
      }  
     
      //Android6.0申请权限的回调方法  
      @Override  
      public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {  
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);  
        switch (requestCode) {  
          // requestCode即所声明的权限获取码，在checkSelfPermission时传入  
          case BAIDU_READ_PHONE_STATE:  
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  
              // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）  
            } else {  
              // 没有获取到权限，做特殊处理  
              Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();  
            }  
            break;  
          default:  
            break;  
        }  
      }  
}
