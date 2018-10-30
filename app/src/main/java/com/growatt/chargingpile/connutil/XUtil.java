package com.growatt.chargingpile.connutil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.application.MyApplication;

import org.xutils.common.Callback;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.Map;

public class XUtil {
	    private static Context context;
		private static ConnectivityManager connectivityManager;
		/**
	     * 发送get请求
	     * @param <T>
	     */
	    public static <T> Cancelable Get(String url, Map<String,String> map, CommonCallback<T> callback){
	    	//检查网络
	    	if(!isNetworkAvailable()){
	    		if(context!=null){
	    			Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
	    		}
	    		return null;
	    	}
	        RequestParams params=new RequestParams(url);
	        params.setExecutor(new PriorityExecutor(2, true));
			params.setConnectTimeout(30000);
	        if(null!=map){
	            for(Map.Entry<String, String> entry : map.entrySet()){
	                params.addQueryStringParameter(entry.getKey(), entry.getValue());
	            }
	        }
	        Cancelable cancelable = x.http().get(params, callback);
	        return cancelable;
	    }

	    /**
	     * 发送post请求
	     * @param <T>
	     */
	    public static <T> Cancelable Post(String url, Map<String, String> params2, CommonCallback<String> commonCallback){
	    	//检查网络
	    	if(!isNetworkAvailable()){
	    		if(context!=null){
	    			Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
	    		}
	    		return null;
	    	}
	        RequestParams params=new RequestParams(url);
	        params.setExecutor(new PriorityExecutor(2, true));
            params.setConnectTimeout(30000);
	        if(null!=params2){
	            for(Map.Entry<String, String> entry : params2.entrySet()){
	                params.addParameter(entry.getKey(), entry.getValue());
	            }
	        }
	        Cancelable cancelable = x.http().post(params, commonCallback);
	        return cancelable;
	    }
	/**
	 * 发送post json请求
	 * @param <T>
	 */
	public static <T> Cancelable postJson(String url, String json, CommonCallback<String> commonCallback){
		//检查网络
		if(!isNetworkAvailable()){
			if(context!=null){
				Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
			}
			return null;
		}
		RequestParams params=new RequestParams(url);
		params.setAsJsonContent(true);
		params.setBodyContent(json);
		params.setExecutor(new PriorityExecutor(2, true));
		params.setConnectTimeout(30000);
		Cancelable cancelable = x.http().post(params, commonCallback);
		return cancelable;
	}
	/**
	 * 发送post请求
	 * @param <T>
	 */
	public static <T> Cancelable PostObj(String url, Map<String, Object> params2, CommonCallback<String> commonCallback){
		//检查网络
		if(!isNetworkAvailable()){
			if(context!=null){
				Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
			}
			return null;
		}
		RequestParams params=new RequestParams(url);
		params.setExecutor(new PriorityExecutor(2, true));
		params.setConnectTimeout(30000);
		if(null!=params2){
			for(Map.Entry<String, Object> entry : params2.entrySet()){
				params.addParameter(entry.getKey(), entry.getValue());
			}
		}
		Cancelable cancelable = x.http().post(params, commonCallback);
		return cancelable;
	}
	/**
	 * 发送post请求:oss登录设置超时5s-10s
	 * @param <T>
	 */
	public static <T> Cancelable postOssLoginTimeOut(String url, Map<String, String> params2, CommonCallback<String> commonCallback){
		//检查网络
		if(!isNetworkAvailable()){
			if(context!=null){
				Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
			}
			return null;
		}
		RequestParams params=new RequestParams(url);
		params.setExecutor(new PriorityExecutor(2, true));
		params.setConnectTimeout(10000);
		if(null!=params2){
			for(Map.Entry<String, String> entry : params2.entrySet()){
				params.addParameter(entry.getKey(), entry.getValue());
			}
		}
		Cancelable cancelable = x.http().post(params, commonCallback);
		return cancelable;
	}
	/**
	 * 发送post请求:oss登录设置超时5s-10s
	 * @param <T>
	 */
	public static <T> Cancelable postTimeOut(String url, Map<String, String> params2, int time, CommonCallback<String> commonCallback){
		//检查网络
		if(!isNetworkAvailable()){
			if(context!=null){
				Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
			}
			return null;
		}
		RequestParams params=new RequestParams(url);
		params.setExecutor(new PriorityExecutor(2, true));
		params.setConnectTimeout(time);
		if(null!=params2){
			for(Map.Entry<String, String> entry : params2.entrySet()){
				params.addParameter(entry.getKey(), entry.getValue());
			}
		}
		Cancelable cancelable = x.http().post(params, commonCallback);
		return cancelable;
	}

	    /**
	     * 上传文件对象
	     * @param <T>
	     */
	    public static <T> Cancelable UpLoadFile(String url, Map<String,Object> map, CommonCallback<T> callback){
	    	//检查网络
	    	if(!isNetworkAvailable()){
	    		if(context!=null){
	    			Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
	    		}
	    		return null;
	    	}
	        RequestParams params=new RequestParams(url);
	        params.setExecutor(new PriorityExecutor(2, true));
			params.setConnectTimeout(30000);
	        if(null!=map){
	            for(Map.Entry<String, Object> entry : map.entrySet()){
	            	String key=entry.getKey();
	            	Object value=entry.getValue();
	            	if(TextUtils.isEmpty(String.valueOf(value)) && (key.contains("image")||key.contains("plantImg")||key.contains("plantMap")||key.contains("file"))){
	            		params.addBodyParameter(key, "");
	            	}else if(key.contains("image")||key.contains("plantImg")||key.contains("plantMap")||key.contains("file")){
	            	  params.addBodyParameter(key, new File(String.valueOf(value)));
	            	}else{
	                  params.addParameter(key, value);
	            	}
	            }
			}
			params.setMultipart(true);
			Cancelable cancelable = x.http().post(params, callback);
	        return cancelable;
	    }

	    /**
	     * 下载文件
	     * @param <T>
	     */
	    public static <T> Cancelable DownLoadFile(String url, String filepath, CommonCallback<T> callback){
	    	//检查网络
	    	if(!isNetworkAvailable()){
	    		if(context!=null){
	    			Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
	    		}
	    		return null;
	    	}
	        RequestParams params=new RequestParams(url);
	        params.setExecutor(new PriorityExecutor(2, true));
	        //设置断点续传
	        params.setAutoResume(true);
	        params.setSaveFilePath(filepath);
	        Cancelable cancelable = x.http().get(params, callback);
	        return cancelable;
	    }
	    
	    /**
	     * 发送get请求:缓存
	     * @param <T>
	     */
	    public static <T> Cancelable GetCache(String url, Map<String,String> map, Callback.CacheCallback<String> callback){
	    	//检查网络
	    	if(!isNetworkAvailable()){
	    		if(context!=null){
	    			Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
	    		}
	    		return null;
	    	}
	        RequestParams params=new RequestParams(url);
	        params.setExecutor(new PriorityExecutor(2, true));
			params.setConnectTimeout(30000);
	        if(null!=map){
	            for(Map.Entry<String, String> entry : map.entrySet()){
	                params.addQueryStringParameter(entry.getKey(), entry.getValue());
	            }
	        }
	        // 默认缓存存活时间, 单位:毫秒.(如果服务没有返回有效的max-age或Expires)
	        params.setCacheMaxAge(1000*20);
	     
	        Cancelable cancelable = x.http().get(params, callback);
	        return cancelable;
	    }
	/**
	 * 发送get请求:单独为获取服务器地址方法
	 * @param <T>
	 */
	public static <T> Cancelable GetServerUrl(String url, Map<String,String> map, Callback.CacheCallback<String> callback){
		//检查网络
		if(!isNetworkAvailable()){
			if(context!=null){
				Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
			}
			return null;
		}
		RequestParams params=new RequestParams(url);
		params.setExecutor(new PriorityExecutor(2, true));
		params.setConnectTimeout(10000);
		if(null!=map){
			for(Map.Entry<String, String> entry : map.entrySet()){
				params.addQueryStringParameter(entry.getKey(), entry.getValue());
			}
		}
		// 默认缓存存活时间, 单位:毫秒.(如果服务没有返回有效的max-age或Expires)
		params.setCacheMaxAge(1000*20);

		Cancelable cancelable = x.http().get(params, callback);
		return cancelable;
	}
	    /**
	     * 发送post请求：缓存
	     * @param <T>
	     */
	    public static <T> Cancelable PostCache(String url, Map<String, String> params2, Callback.CacheCallback<String> commonCallback){
	    	//检查网络
	    	if(!isNetworkAvailable()){
	    		if(context!=null){
	    			Toast.makeText(context, context.getString(R.string.m请开启网络), Toast.LENGTH_SHORT).show();
	    		}
	    		return null;
	    	}
	        RequestParams params=new RequestParams(url);
	        params.setExecutor(new PriorityExecutor(2, true));
	        if(null!=params2){
	            for(Map.Entry<String, String> entry : params2.entrySet()){
	                params.addParameter(entry.getKey(), entry.getValue());
	            }
	        }
	        // 默认缓存存活时间, 单位:毫秒.(如果服务没有返回有效的max-age或Expires)
	        params.setCacheMaxAge(1000 * 10);
	        Cancelable cancelable = x.http().post(params, commonCallback);
	        return cancelable;
	    }

	    /**
	     * 检查当前网络是否可用
	     * 
	     * @return
	     */
	    
	    public static boolean isNetworkAvailable()
	    { 
	    	if(context==null){
	        context = MyApplication.context;
	    	}
	        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
	    	if(connectivityManager==null){
	        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    	}
	        
	        if (connectivityManager == null)
	        {
	            return false;
	        }
	        else
	        {
	            // 获取NetworkInfo对象
	            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
	            
	            if (networkInfo != null && networkInfo.length > 0)
	            {
	                for (int i = 0; i < networkInfo.length; i++)
	                {
	                	
	                    // 判断当前网络状态是否为连接状态
	                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
	                    {
	                        return true;
	                    }
	                }
	            }
	        }
	        return false;
	    }
	}
