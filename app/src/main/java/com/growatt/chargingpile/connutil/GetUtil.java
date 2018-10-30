package com.growatt.chargingpile.connutil;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.T;

import org.xutils.common.Callback.CacheCallback;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

//全部不做缓存
public class GetUtil {

	public static void get(final String url, final GetListener httpListener){
		final Handler handler=new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Mydialog.Dismiss();
				String a=(String) msg.obj;
				switch (msg.what) {
				case 0:
					httpListener.success(a);
					break;
				case 1:
					httpListener.error(a);
					break;
				case 2:	//超时重新登录
					LoginUtil.serverTimeOutLogin();
					break;
			
				}
			}
		};
		try {
			Cancelable cancle = XUtil.GetCache(url, null, new CacheCallback<String>() {
				
		            private String result = null;

		            @Override
		            public boolean onCache(String result) {
		                // 得到缓存数据, 缓存过期后不会进入这个方法.
		                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
		                //
		                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
		                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
		                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
		                //
		                // * 如果信任该缓存返回 true, 将不再请求网络;
		                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
		                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
		                //
		                this.result = result;
//						LogUtil.i("getcache_result_load:"+result);
//		                Log.i("TAG", "getcache_url:"+url);
//						Log.i("TAG", "getcache_result:"+result);
					
		                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
		            }

		            @Override
		            public void onSuccess(String result) {
		                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
						LogUtil.i("get_result_load:"+result);
						LogUtil.i("get_result_url:"+url);
		                if (result != null) {
		                    this.result = result;
		                }
		                if(TextUtils.isEmpty(this.result)){
		                	Message msg=new Message();
		                	msg.what=1;
		                	msg.obj=url;
		                	handler.sendMessage(msg);
		                }else if(this.result.contains("<!DOCTYPE")){
		                	//重新做登陆操作
		                	Message.obtain(handler, 2, url).sendToTarget();
		                }else{
		                	Message msg=new Message();
		                	msg.what=0;     
		                	msg.obj=this.result;
		                	handler.sendMessage(msg);
		                }
		            }

		            @Override
		            public void onError(Throwable ex, boolean isOnCallback) {
//		            	 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":1"+ex.getMessage(), Toast.LENGTH_LONG).show();
//						Log.i("err", "onError: "+ex.getMessage());
						if(ex instanceof HttpException){
							T.make(R.string.m网络错误, MyApplication.context);
						}else if(ex instanceof SocketTimeoutException){
							T.make(R.string.m网络超时, MyApplication.context);
						}else if(ex instanceof UnknownHostException){
							T.make(R.string.m服务器连接失败, MyApplication.context);
						}else{
							T.make(R.string.m服务器连接失败, MyApplication.context);
						}
		            	Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
//		            	if (ex instanceof HttpException) { //网络错误
//		                    HttpException httpEx = (HttpException) ex;
//		                    int responseCode = httpEx.getCode();
//		                    String responseMsg = httpEx.getMessage();
//		                    String errorResult = httpEx.getResult();
//		                    //...
//		                } else { //其他错误
//		                    //...
//		                }
		            }

		            @Override
		            public void onCancelled(CancelledException cex) {
//		                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
		            }

		            @Override
		            public void onFinished() {
		         
		            }
			});
			if(cancle==null){
				Message.obtain(handler, 1,url).sendToTarget();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg=new Message();
			msg.what=2;
			msg.obj=e.toString();
			handler.sendMessage(msg);
		}

	}
	//get 带参数
	public static void getParams(final String url, final PostUtil.postListener httpListener){
		final Map<String, String> params=new HashMap<String, String>();
		httpListener.Params(params);
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String a=(String) msg.obj;
				switch (msg.what) {
				case 0:
					Mydialog.Dismiss();
					httpListener.success(a);
					break;
				case 1:
					Mydialog.Dismiss();
					httpListener.LoginError(a);
					break;
				case 2:
					//登陆超时
//					SqliteUtil.url("");
//					SqliteUtil.plant("");
//					Cons.guestyrl="";
//					Cons.isflag=false;
//					Cons.plants.clear();
//					SqliteUtil.time("0");
					LoginUtil.serverTimeOutLogin();
//					Intent intent=new Intent(ShineApplication.context,LoginActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//					ShineApplication.context.startActivity(intent);
				}
			}
		};
		try {
			Cancelable cancle = XUtil.GetCache(url, params, new CacheCallback<String>() {
				
	            private String result = null;

	            @Override
	            public boolean onCache(String result) {
	                // 得到缓存数据, 缓存过期后不会进入这个方法.
	                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
	                //
	                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
	                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
	                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
	                //
	                // * 如果信任该缓存返回 true, 将不再请求网络;
	                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
	                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
	                //
	                this.result = result;
//					LogUtil.i("getcache_url_load:"+url);
//					LogUtil.i("getcache_result_load:"+result);
//	                Log.i("TAG", "getcache_url:"+url);
//					Log.i("TAG", "getcache_result:"+result);
				
	                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
	            }

	            @Override
	            public void onSuccess(String result) {
	                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
//	            	Log.i("TAG", "get_url:"+url);
//					Log.i("TAG", "get_result:"+result);
					LogUtil.i("get_url_load:"+url);
					LogUtil.i("get_result_load:"+result);
	                if (result != null) {
	                    this.result = result;
	                }
	                if(TextUtils.isEmpty(this.result)){
	                	Message msg=new Message();
	                	msg.what=1;
	                	msg.obj=url;
	                	handler.sendMessage(msg);
	                }else if(this.result.contains("<!DOCTYPE")){
	                	//重新做登陆操作
	                	Message.obtain(handler, 2, url).sendToTarget();
	                }else{
	                	Message msg=new Message();
	                	msg.what=0;     
	                	msg.obj=this.result;
	                	handler.sendMessage(msg);
	                }
	            }

	            @Override
	            public void onError(Throwable ex, boolean isOnCallback) {
//	            	 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":1"+ex.getMessage(), Toast.LENGTH_LONG).show();
					if(ex instanceof HttpException){
						T.make(R.string.m网络错误, MyApplication.context);
					}else if(ex instanceof SocketTimeoutException){
						T.make(R.string.m网络超时, MyApplication.context);
					}else if(ex instanceof UnknownHostException){
						T.make(R.string.m服务器连接失败, MyApplication.context);
					}else{
						T.make(R.string.m服务器连接失败, MyApplication.context);
					}
	            	Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
//	            	if (ex instanceof HttpException) { //网络错误
//	                    HttpException httpEx = (HttpException) ex;
//	                    int responseCode = httpEx.getCode();
//	                    String responseMsg = httpEx.getMessage();
//	                    String errorResult = httpEx.getResult();
//	                    //...
//	                } else { //其他错误
//	                    //...
//	                }
	            }

	            @Override
	            public void onCancelled(CancelledException cex) {
//	                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
	            }

	            @Override
	            public void onFinished() {
	         
	            }
		});
			if(cancle==null){
				Message.obtain(handler, 1,url).sendToTarget();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg=new Message();
			msg.what=2;
			msg.obj=e.toString();
			handler.sendMessage(msg);
		}
	}
	//get 带参数获取服务器：设置超时为5s-10s
	public static void getParamsServerUrl(final String url, final PostUtil.postListener httpListener){
		final Map<String, String> params=new HashMap<String, String>();
		httpListener.Params(params);
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String a=(String) msg.obj;
				switch (msg.what) {
					case 0:
						httpListener.success(a);
						break;
					case 1:
						Mydialog.Dismiss();
						httpListener.LoginError(a);
						break;
					case 2:
						//登陆超时
//					SqliteUtil.url("");
//					SqliteUtil.plant("");
//					Cons.guestyrl="";
//					Cons.isflag=false;
//					Cons.plants.clear();
//					SqliteUtil.time("0");
						LoginUtil.serverTimeOutLogin();
//						Intent intent=new Intent(ShineApplication.context,LoginActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//						ShineApplication.context.startActivity(intent);
				}
			}
		};
		try {
			Cancelable cancle = XUtil.GetServerUrl(url, params, new CacheCallback<String>() {

				private String result = null;

				@Override
				public boolean onCache(String result) {
					// 得到缓存数据, 缓存过期后不会进入这个方法.
					// 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
					//
					// * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
					//   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
					//   逻辑, 那么xUtils将请求新数据, 来覆盖它.
					//
					// * 如果信任该缓存返回 true, 将不再请求网络;
					//   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
					//   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
					//
					this.result = result;
//					LogUtil.i("getcache_url_load:"+url);
//					LogUtil.i("getcache_result_load:"+result);
//	                Log.i("TAG", "getcache_url:"+url);
//					Log.i("TAG", "getcache_result:"+result);

					return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
				}

				@Override
				public void onSuccess(String result) {
					// 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
//	            	Log.i("TAG", "get_url:"+url);
//					Log.i("TAG", "get_result:"+result);
					LogUtil.i("get_url_load:"+url);
					LogUtil.i("get_result_load:"+result);
					if (result != null) {
						this.result = result;
					}
					if(TextUtils.isEmpty(this.result)){
						Message msg=new Message();
						msg.what=1;
						msg.obj=url;
						handler.sendMessage(msg);
					}else if(this.result.contains("<!DOCTYPE")){
						//重新做登陆操作
						Message.obtain(handler, 2, url).sendToTarget();
					}else{
						Message msg=new Message();
						msg.what=0;
						msg.obj=this.result;
						handler.sendMessage(msg);
					}
				}

				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
//	            	 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":1"+ex.getMessage(), Toast.LENGTH_LONG).show();
					if(ex instanceof HttpException){
						T.make(R.string.m网络错误, MyApplication.context);
					}else if(ex instanceof SocketTimeoutException){
						T.make(R.string.m网络超时, MyApplication.context);
					}else if(ex instanceof UnknownHostException){
						T.make(R.string.m服务器连接失败, MyApplication.context);
					}else{
						T.make(R.string.m服务器连接失败, MyApplication.context);
					}
					Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
//	            	if (ex instanceof HttpException) { //网络错误
//	                    HttpException httpEx = (HttpException) ex;
//	                    int responseCode = httpEx.getCode();
//	                    String responseMsg = httpEx.getMessage();
//	                    String errorResult = httpEx.getResult();
//	                    //...
//	                } else { //其他错误
//	                    //...
//	                }
				}

				@Override
				public void onCancelled(CancelledException cex) {
//	                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFinished() {

				}
			});
			if(cancle==null){
				Message.obtain(handler, 1,url).sendToTarget();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg=new Message();
			msg.what=2;
			msg.obj=e.toString();
			handler.sendMessage(msg);
		}
	}
	//不缓存
		public static void get1(final String url, final GetListener httpListener){
			final Handler handler=new Handler(Looper.getMainLooper()){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					Mydialog.Dismiss();
					String a=(String) msg.obj;
					switch (msg.what) {
					case 0:
						httpListener.success(a);
						break;
					case 1:
						httpListener.error(a);
						break;
					case 2:
						LoginUtil.serverTimeOutLogin();
//						Intent intent=new Intent(ShineApplication.context,LoginActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//						ShineApplication.context.startActivity(intent);
						break;
				
					}
				}
			};
			try {
				Cancelable cancle = XUtil.GetCache(url, null, new CacheCallback<String>() {
					
			            private String result = null;

			            @Override
			            public boolean onCache(String result) {
			                // 得到缓存数据, 缓存过期后不会进入这个方法.
			                // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
			                //
			                // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
			                //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
			                //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
			                //
			                // * 如果信任该缓存返回 true, 将不再请求网络;
			                //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
			                //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
			                //
			                this.result = result;
//							LogUtil.i("getcache_url_load:"+url);
//							LogUtil.i("getcache_result_load:"+result);
						
			                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
			            }

			            @Override
			            public void onSuccess(String result) {
			                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
							LogUtil.i("get_url_load:"+url);
							LogUtil.i("get_result_load:"+result);
			                if (result != null) {
			                    this.result = result;
			                }
			                if(TextUtils.isEmpty(this.result)){
			                	Message msg=new Message();
			                	msg.what=1;
			                	msg.obj=url;
			                	handler.sendMessage(msg);
			                }else if(this.result.contains("<!DOCTYPE")){
			                	//重新做登陆操作
			                	Message.obtain(handler, 2, url).sendToTarget();
			                }else{
			                	Message msg=new Message();
			                	msg.what=0;     
			                	msg.obj=this.result;
			                	handler.sendMessage(msg);
			                }
			            }

			            @Override
			            public void onError(Throwable ex, boolean isOnCallback) {
//			            	 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err), Toast.LENGTH_LONG).show();
							if(ex instanceof HttpException){
								T.make(R.string.m网络错误, MyApplication.context);
							}else if(ex instanceof SocketTimeoutException){
								T.make(R.string.m网络超时, MyApplication.context);
							}else if(ex instanceof UnknownHostException){
								T.make(R.string.m服务器连接失败, MyApplication.context);
							}else{
								T.make(R.string.m服务器连接失败, MyApplication.context);
							}
			                Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
//			            	if (ex instanceof HttpException) { //网络错误
//			                    HttpException httpEx = (HttpException) ex;
//			                    int responseCode = httpEx.getCode();
//			                    String responseMsg = httpEx.getMessage();
//			                    String errorResult = httpEx.getResult();
//			                    //...
//			                } else { //其他错误
//			                    //...
//			                }
			            }

			            @Override
			            public void onCancelled(CancelledException cex) {
//			                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
			            }

			            @Override
			            public void onFinished() {
			         
			            }
				});
				if(cancle==null){
					Message.obtain(handler, 1,url).sendToTarget();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg=new Message();
				msg.what=2;
				msg.obj=e.toString();
				handler.sendMessage(msg);
			}

		}
	public  interface GetListener {
		//������ȷ
		void error(String json);
		//���ش���
		void success(String json);
	}
}
