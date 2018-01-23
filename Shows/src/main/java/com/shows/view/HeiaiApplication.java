package com.shows.view;

import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.app.Application;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 
 * @Description : 全局Application
 * @author xyz
 * @version : 1.0 Create Date : 2016-1-27
 */
public class HeiaiApplication extends Application
{
	private static final String TAG = "HeiaiApplication";
	/** Activity Stack */
	private Stack<Activity> mActivityStack = new Stack<Activity>();
	private Context mContext;

	@Override
	public void onCreate()
	{
		super.onCreate();
		mContext = getApplicationContext();

		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);     		// 初始化 JPush
	}














	/**
	 * 添加Activity至堆栈
	 * 
	 * @param activity
	 */
	public synchronized void addActivity(Activity activity)
	{
		mActivityStack.add(activity);
	}

	/**
	 * 删除堆栈中Activity
	 * 
	 * @param activity
	 */
	public synchronized void removeActivty(Activity activity)
	{
		mActivityStack.remove(activity);
	}

	/**
	 * 清空堆栈
	 */
	public synchronized void cleanStack()
	{
		mActivityStack.clear();
	}

	/**
	 * 清除堆栈，忽略ignoreList中对应的Activity
	 * 
	 * @param ignoreList
	 */
	public synchronized void cleanStack(List<Class<?>> ignoreList)
	{
		final int size = mActivityStack.size();
		for (int i = size - 1; i >= 0; i--)
		{
			Activity activity = mActivityStack.get(i);
			for (Class<?> classz : ignoreList)
			{
				if (!classz.isInstance(activity))
				{
					Log.d(TAG, "Finish Activity = "
							+ activity.getClass().getName());
					mActivityStack.remove(activity);
					activity.finish();
				}
			}
		}
	}

	/**
	 * 清除堆栈，忽略ignoreList中对应的Activity
	 * 
	 * @param
	 */
	public synchronized void cleanStack(Class<?> ignored)
	{
		final int size = mActivityStack.size();
		for (int i = size - 1; i >= 0; i--)
		{
			Activity activity = mActivityStack.get(i);
			if (!ignored.isInstance(activity))
			{
				Log.e(TAG, "Finish Activity = " + activity.getClass().getName());
				mActivityStack.remove(activity);
				activity.finish();
			}
		}
	}

	/**
	 * 清除堆栈，关闭所有activity
	 * 
	 * @param
	 */
	public synchronized void finishStack()
	{
		final int size = mActivityStack.size();
		for (int i = size - 1; i >= 0; i--)
		{
			Activity activity = mActivityStack.get(i);
			Log.e(TAG, "Finish Activity = " + activity.getClass().getName());
			mActivityStack.remove(activity);
			activity.finish();
		}
	}

	/**
	 * 获取指定的activity
	 * 
	 * @param activityClass
	 * @return
	 */
	public Activity getActivity(Class<?> activityClass)
	{
		for (int i = 0; i < mActivityStack.size(); i++)
		{
			if (activityClass.isInstance(mActivityStack.get(i)))
			{
				return mActivityStack.get(i);
			}
		}
		return null;
	}
	/**
	 * 获取当前activity
	 * 
	 * @param
	 * @return
	 */
	public Activity getCurrentActivity()
	{
		return mActivityStack.lastElement();
	}
	
	/**
	 * 获取上级activity
	 * @return
	 */
	public Activity getSuperActivity()
	{
		if(mActivityStack.size() >= 2)
		{
			return mActivityStack.get(mActivityStack.size() - 2);
		}
		return null;
	}

}
