package com.safnect.wallet.mpc.controller.activity;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.activity.MarketingActivity2412Mapper;
import com.safnect.wallet.mpc.mapper.activity.MarketingActivityConfigMapper;
import com.safnect.wallet.mpc.mapper.activity.TwitterApiKeyMapper;
import com.safnect.wallet.mpc.model.activity.MarketingActivity2412;
import com.safnect.wallet.mpc.model.activity.MarketingActivityConfig;
import com.safnect.wallet.mpc.util.TextUtil;

@RestController
@RequestMapping("marketing-activity")
public class MarketingActivityController {
	
	@Autowired
	MarketingActivity2412Mapper marketingActivity2412Mapper;
	
	@Autowired
	TwitterApiKeyMapper twitterApiKeyMapper;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	MarketingActivityConfigMapper marketingActivityConfigMapper;
	
	public static final String VERIFY_KEY = "MarketingActivity2412_toverify_list";
	
	public static void main(String[] args) throws DecoderException {
		for (int i=0; i<1600; i++) {
			String str = String.valueOf(System.nanoTime());
//			int year = 241210;
			int year = 250816;
			String str1 = str.substring(4, 11);
			String str2 = year + str1;
			long l = Long.parseLong(str2);
			long n = l % 11;
			if (n == 10) {
				n = 0;
			}
			String yearHex = Integer.toString(year, 36).toUpperCase();
			String no = "S" + yearHex +str1;
			String hex = Hex.encodeHexString(no.getBytes());
			System.out.println(no + " " + hex);
			
//			String str = String.valueOf(System.nanoTime());
//			String str1 = str.substring(6, 13);
//			String str2 = 11 + str1;
//			long l = Long.parseLong(str2);
//			long n = l % 11;
//			if (n == 10) {
//				n = 0;
//			}
//			System.out.println("S"  + 07 + str1 + n);
		}
	}
	
	
	@GetMapping("2412")
	public ResponseModel get2412(String address) {
		final String activityId = "2412";
		MarketingActivityConfig mac = this.marketingActivityConfigMapper.selectByPrimaryKey(activityId);
		mac.setStarted(new Date());
		if (StringUtils.isNotBlank(address)) {
			List<MarketingActivity2412> list = this.marketingActivity2412Mapper.select(new MarketingActivity2412(null, address));
			if (CollectionUtils.isNotEmpty(list)) {
				mac.setWinner(list.get(0).getWinner());
			}
		}
		return ResponseModel.successData(mac);
	}
	
	@PostMapping("address-check")
	public ResponseModel checkAddress(String address) {
		if (StringUtils.isAnyBlank(address)) {
			return ResponseModel.fail601();
		}
		int count = this.marketingActivity2412Mapper.selectCount(new MarketingActivity2412(null, address));
		if (count > 0) {
			return ResponseModel.successData(false);
		}
		return ResponseModel.successData(true);
	}
	
	@PostMapping("post-check")
	public ResponseModel check(String postLink) {
		ResponseModel rm = get2412(null);
		MarketingActivityConfig mac = (MarketingActivityConfig) rm.getData();
		if (!mac.getStarted()) {
			return ResponseModel.fail(613, "The event has ended");
		}
		if (StringUtils.isAnyBlank(postLink)) {
			return ResponseModel.fail601();
		}
		String postId = getPostId(postLink);
		if (!StringUtils.startsWith(postLink, "https://x.com/") || StringUtils.isBlank(postId) || postId.length() != 19) {
			return ResponseModel.fail(610, "The post URL is incorrect");
		}
		
		
		int count = this.marketingActivity2412Mapper.selectCount(new MarketingActivity2412(postId, null));
		if (count > 0) {
			return ResponseModel.fail(612, "Your post has been submitted, You cannot submit again.");
		}
		
		String twUserId = postLink.substring(postLink.indexOf("x.com/") + 6, postLink.indexOf("/status"));
		MarketingActivity2412 ma = new MarketingActivity2412();
		ma.setTwUserId(twUserId);
		count = this.marketingActivity2412Mapper.selectCount(ma);
		if (count > 0) {
			return ResponseModel.fail(612, "Your post has been submitted, You cannot submit again.");
		}
		return ResponseModel.success();
	}

	@PostMapping("post-submit")
	public ResponseModel submitPost(String walletId, String address, String postLink) {
		if (StringUtils.isAnyBlank(walletId, address, postLink)) {
			return ResponseModel.fail601();
		}
		String postId = getPostId(postLink);
		if (!StringUtils.startsWith(postLink, "https://x.com/") || StringUtils.isBlank(postId) || postId.length() != 19) {
			return ResponseModel.fail(610, "The post URL is incorrect");
		}
		
		int count = this.marketingActivity2412Mapper.selectCount(new MarketingActivity2412(null, address));
		if (count > 0) {
			return ResponseModel.fail(611, "You have already submitted, You cannot submit again.");
		}
		
		count = this.marketingActivity2412Mapper.selectCount(new MarketingActivity2412(postId, null));
		if (count > 0) {
			return ResponseModel.fail(612, "Your post has been submitted, You cannot submit again.");
		}
		
		String twUserId = postLink.substring(postLink.indexOf("x.com/") + 6, postLink.indexOf("/status"));
		MarketingActivity2412 maco = new MarketingActivity2412();
		maco.setTwUserId(twUserId);
		count = this.marketingActivity2412Mapper.selectCount(maco);
		if (count > 0) {
			return ResponseModel.fail(612, "Your post has been submitted, You cannot submit again.");
		}
		
		MarketingActivity2412 ma = new MarketingActivity2412(postId, address);
		ma.setId(TextUtil.generateId());
		ma.setCreateDatetime(new Date());
		ma.setPostId(postId);
		ma.setWalletId(walletId);
		ma.setPostLink(postLink);
		ma.setTwUserId(twUserId);
		ma.setVerified(false);
		ma.setPassed(false);
		this.marketingActivity2412Mapper.insertSelective(ma);
		this.redisTemplate.opsForList().leftPush(VERIFY_KEY, ma.getId());
		
		return ResponseModel.success();
	}
	
	public static String getPostId(String postLink) {
		postLink = postLink.trim();
		String postId = null;
		try {
			int beginIndex = postLink.lastIndexOf("/") + 1;
			if (!StringUtils.contains(postLink, "?")) {
				postId = postLink.substring(beginIndex);
			} else {
				postId = postLink.substring(beginIndex, postLink.lastIndexOf("?"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return postId;
	}
}
