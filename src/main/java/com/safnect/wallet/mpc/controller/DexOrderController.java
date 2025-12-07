package com.safnect.wallet.mpc.controller;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.model.dex.DexCancelOrder;
import com.safnect.wallet.mpc.model.dex.DexMakeOrder;
import com.safnect.wallet.mpc.model.dex.DexOrderResult;
import com.safnect.wallet.mpc.model.dex.DexTakeOrder;
import com.safnect.wallet.mpc.service.DexOrderService;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@RestController
@RequestMapping("dex-order")
@SuppressWarnings("unchecked")
public class DexOrderController {
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	DexOrderService dexOrderService;
	
	@GetMapping("token-list")
	public Object tokenList(Integer start, Integer limit) {
		String url = String.format("https://fractal-api.unisat.io/query-v4/cat20/info-list?name=&sort=transactions&start=%s&limit=%s", start, limit);
		String json = HttpClientUtil.httpGet(url, null);
		return JsonUtil.fromJson2Map(json);
	}
	
	@PostMapping("make")
	public ResponseModel make(DexMakeOrder makeOrder, String resultInfo) {
		
		String value = this.redisTemplate.opsForValue().get(FetchDataCointroller.priceRedisKey);
		Map<String, Object> map = JsonUtil.fromJson2Map(value);
		Map<String, Object> fbMap = (Map<String, Object>) map.get("fb");
		Double fbPrice = MapUtils.getDouble(fbMap, "usd");
		
		makeOrder.setCreateDatetime(new Date());
		makeOrder.setId(TextUtil.generateId());
		makeOrder.setCanceled(false);
		makeOrder.setTaken(false);
		makeOrder.setUsdFb(fbPrice.toString());
		
		DexOrderResult result = new DexOrderResult(makeOrder.getId(), resultInfo); 
		this.dexOrderService.saveMakeOrder(makeOrder, result);
		return ResponseModel.success();
	}
	
	@PostMapping("take")
	public ResponseModel make(DexTakeOrder takeOrder, String resultInfo) {
		
		String value = this.redisTemplate.opsForValue().get(FetchDataCointroller.priceRedisKey);
		Map<String, Object> map = JsonUtil.fromJson2Map(value);
		Map<String, Object> fbMap = (Map<String, Object>) map.get("fb");
		Double fbPrice = MapUtils.getDouble(fbMap, "usd");
		
		takeOrder.setCreateDatetime(new Date());
		takeOrder.setId(TextUtil.generateId());
		takeOrder.setUsdFb(fbPrice.toString());
		
		DexOrderResult result = new DexOrderResult(takeOrder.getId(), resultInfo); 
		this.dexOrderService.saveTakeOrder(takeOrder, result);
		return ResponseModel.success();
	} 
	
	@PostMapping("cancel")
	public ResponseModel cancel(DexCancelOrder cancelOrder, String resultInfo) {
		
		cancelOrder.setCreateDatetime(new Date());
		cancelOrder.setId(TextUtil.generateId());
		
		DexOrderResult result = new DexOrderResult(cancelOrder.getId(), resultInfo); 
		this.dexOrderService.saveCancelOrder(cancelOrder, result);
		return ResponseModel.success();
	} 
}
