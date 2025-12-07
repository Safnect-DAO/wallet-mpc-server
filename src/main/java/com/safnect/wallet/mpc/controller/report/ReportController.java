package com.safnect.wallet.mpc.controller.report;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.report.ReportPsbtMapper;
import com.safnect.wallet.mpc.model.report.ReportPsbt;
import com.safnect.wallet.mpc.service.FetchHistoryService;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@RestController
@RequestMapping("report")
public class ReportController {

	@Autowired
	ReportPsbtMapper reportPsbtMapper;
	
	@Autowired
	FetchHistoryService fetchHistoryService;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@PostMapping("psbt")
	public ResponseModel psbt(ReportPsbt psbt) {
		psbt.setId(TextUtil.generateId());
		psbt.setCreateDatetime(new Date());
		this.reportPsbtMapper.insertSelective(psbt);
		return ResponseModel.success();
	}
	
	@PostMapping("address")
	public ResponseModel address(String network, String walletId, Integer accountIndex, String addressJson) {
		if (StringUtils.isAnyBlank(network, walletId, addressJson) && accountIndex != null) {
			return ResponseModel.fail601();
		}
		if (StringUtils.equals(network, Constants.NETWORK_TESTNET)) { // testnet no fetch transactions
			return ResponseModel.success();
		}
		String key = String.format("fetch_history_%s_%s_%s", network, walletId, accountIndex);
		if (!this.redisTemplate.hasKey(key)) {
			Map<String, Object> map = JsonUtil.fromJson2Map(addressJson);
			this.fetchHistoryService.asyncFetchHistory(network, map);
			this.redisTemplate.opsForValue().set(key, "0", 2, TimeUnit.MINUTES);
		}
		return ResponseModel.success();
	}
}
