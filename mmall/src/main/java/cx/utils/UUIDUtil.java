package cx.utils;

import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UUIDUtil {

	public static final String TOKEN_PREFIX = "token_";
	private static Logger log = LoggerFactory.getLogger(UUIDUtil.class);
	//	初始化容量和缓存的最大容量,有效时间12小时.
//	private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
//			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
//				//默认加载,如果没有get对应值时调用的方法
//				@Override
//				public String load(String s) throws Exception {
//					return "null";
//				}
//			});


	private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
				@Override
				public String load(String s) throws Exception {
					return "null";
				}
			});

	public static void setKey(String key, String value) {
		loadingCache.put(key, value);
	}

	public static String getKey(String key) {
		String value;
		try {
			value = loadingCache.get(key);
			if (value.equals("null")) {
				return null;
			}
			return value;
		} catch (ExecutionException e) {
			log.error("localCache get error", e);
		}
		return null;
	}

	public static String getUUID() {

		Random random = new Random();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hmmsss");
		String id = simpleDateFormat.format(new Date()) + random.nextInt(10);
		return id;

	}


	public static void main(String[] args) {
		System.out.println(UUIDUtil.getUUID());
	}

}
