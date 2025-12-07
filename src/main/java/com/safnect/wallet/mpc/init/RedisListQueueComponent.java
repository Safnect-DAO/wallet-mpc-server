package com.safnect.wallet.mpc.init;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.safnect.wallet.mpc.controller.activity.MarketingActivityController;
import com.safnect.wallet.mpc.mapper.activity.MarketingActivity2412Mapper;
import com.safnect.wallet.mpc.mapper.activity.TwitterApiKeyMapper;
import com.safnect.wallet.mpc.model.activity.MarketingActivity2412;
import com.safnect.wallet.mpc.model.activity.TwitterApiKey;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

//@Component
public class RedisListQueueComponent {

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	MarketingActivity2412Mapper marketingActivity2412Mapper;
	
	@Autowired
	TwitterApiKeyMapper twitterApiKeyMapper;
	
	static final String[] ACTIVITY_POST_TEXT_ARR = new String[] { "Safnect Wallet", "Wonder Unicorn NFT" };
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void start() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						String id = redisTemplate.opsForList().rightPop(MarketingActivityController.VERIFY_KEY, 20, TimeUnit.SECONDS);
						if (StringUtils.isNotBlank(id)) {
							MarketingActivity2412 ma = marketingActivity2412Mapper.selectByPrimaryKey(id);
							if (ma.getVerified()) {
								continue;
							}
							String postId = ma.getPostId();
							boolean verified = false;
							try {
								Boolean passed = false;
								String result = HttpClientUtil.httpGet("http://tx801.29cp.cn:3001/tweet/" + postId, null);
								Map<String, Object> map1 = JsonUtil.fromJson2Map(result);
								Map<String, Object> quoted_tweetMap = MapUtils.getMap(map1, "quoted_tweet");
								String text = MapUtils.getString(quoted_tweetMap, "text");
								if (StringUtils.isNotBlank(text)) {
									if (StringUtils.contains(text, ACTIVITY_POST_TEXT_ARR[0]) && StringUtils.contains(text, ACTIVITY_POST_TEXT_ARR[1])) {
										passed = true;
									}
									verified = true;
									MarketingActivity2412 upma = new MarketingActivity2412();
									upma.setId(ma.getId());
									upma.setPassed(passed);
									upma.setVerified(verified);
									marketingActivity2412Mapper.updateByPrimaryKeySelective(upma);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							if (verified) {
								continue;
							}
							
							TwitterApiKey tak = twitterApiKeyMapper.getTwitterApiKey();
							if (tak != null) {
								Header[] headerArr = new Header[] {new BasicHeader("Authorization", "Bearer " + tak.getBearerToken())};
								String json = HttpClientUtil.httpGet("https://api.x.com/2/tweets/" + postId, null, headerArr, 7000);
								Map<String, Object> map = JsonUtil.fromJson2Map(json);
								Integer status = MapUtils.getInteger(map, "sataus");
								if (status == null) {
									boolean passed = false;
									verified = true;
									Map<String, Object> dataMap = MapUtils.getMap(map, "data");
									String text = MapUtils.getString(dataMap, "text");
									if (StringUtils.contains(text, ACTIVITY_POST_TEXT_ARR[0]) && StringUtils.contains(text, ACTIVITY_POST_TEXT_ARR[1])) {
										passed = true;
									}
									MarketingActivity2412 upma = new MarketingActivity2412();
									upma.setId(ma.getId());
									upma.setPassed(passed);
									upma.setVerified(verified);
									twitterApiKeyMapper.updateByPrimaryKeySelective(new TwitterApiKey(tak.getId(), new Date()));
									marketingActivity2412Mapper.updateByPrimaryKeySelective(upma);
								} else if (status == 401) {
									// 401可能是Api key失效或被封
									twitterApiKeyMapper.updateByPrimaryKeySelective(new TwitterApiKey(tak.getId(), false));
								} else {
									System.out.println("x api status enter else -> " + json);
								}
							}
							if (!verified) {
								redisTemplate.opsForList().leftPush(MarketingActivityController.VERIFY_KEY, id);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}
