package com.safnect.wallet.mpc.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.dto.TxToConfirm;
import com.safnect.wallet.mpc.mapper.TransactionMapper;
import com.safnect.wallet.mpc.model.Transaction;
import com.safnect.wallet.mpc.model.TransactionFailure;
import com.safnect.wallet.mpc.service.TransactionService;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

/**
 * 交易
 * @author shiwe
 *
 */
@RestController
@RequestMapping("trans")
public class TransactionController {
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	TransactionMapper transactionMapper;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;

	/**
	 * 交易成功
	 * @param trans
	 * @param txid
	 * @return
	 */
	@PostMapping("success")
	public ResponseModel success(Transaction trans) {
		if (trans.isEmpty() || StringUtils.isBlank(trans.getTxid())) {
			return ResponseModel.fail601();
		}
		String transId = TextUtil.generateId();
		trans.setId(transId);
		trans.setSuccessed(true);
		trans.setSendTime(new Date());
		trans.setConfirmed(false);
		trans.setInternal(true);
		
		if (StringUtils.equalsAnyIgnoreCase(trans.getChain(), "Bitcoin", "Fractal Bitcoin", "Bell", "Litecoin", "Bitcoin Cash", "Bitcoin SV", "Dogecoin")) { // 这些链需要检测确认
			this.redisTemplate.opsForList().leftPush(Constants.UNCONFIRMED_TXS_LIST, JsonUtil.toJson(new TxToConfirm(transId, trans.getTxid(), trans.getNetwork(), trans.getChain())));
		} else {
			trans.setConfirmed(true);
		}
		this.transactionService.addTransactionSuccess(trans);
		return ResponseModel.success();
	}
	
	@GetMapping("test")
	public ResponseModel test(String transId, String txid, String network, String chain) {
		this.redisTemplate.opsForList().leftPush(Constants.UNCONFIRMED_TXS_LIST, JsonUtil.toJson(new TxToConfirm(transId, txid, network, chain)));
		return ResponseModel.success();
	}
	
	/**
	 * 交易失败
	 * @param trans
	 * @param exInfo
	 * @return
	 */
	@PostMapping("failure")
	public ResponseModel failure(Transaction trans, String exInfo) {
		if (trans.isEmpty() || StringUtils.isBlank(exInfo)) {
			return ResponseModel.fail601();
		}
		String transId = TextUtil.generateId();
		trans.setId(transId);
		trans.setSuccessed(false);
		trans.setSendTime(new Date());
		trans.setConfirmed(false);
		trans.setInternal(true);
		if (StringUtils.isBlank(trans.getTxid())) {
			trans.setTxid("#");
		}
		
		TransactionFailure tf = new TransactionFailure(transId, exInfo);
		this.transactionService.addTransactionFailure(trans, tf);
		return ResponseModel.success();
	}
	
	@GetMapping("txids")
	public ResponseModel txids(String address) {
		if (StringUtils.isBlank(address)) {
			return ResponseModel.fail601();
		}
		List<String> txidList = this.transactionMapper.getTxids(address);
		return ResponseModel.successData(txidList);
	}
	
	@GetMapping("get")
	public ResponseModel get(String network, String addresses, String chain, String contractAddress, Integer start, Integer limit) {
		if (StringUtils.isAnyBlank(network, addresses)) {
			return ResponseModel.fail601();
		}
		if (start == null) {
			start = 0;
		}
		if (limit == null) {
			limit = 10;
		}
		List<String> addrList = Arrays.asList(StringUtils.split(addresses, ","));
		List<Transaction> transList = this.transactionMapper.get(network, chain, contractAddress, addrList, start, limit);
		return ResponseModel.successData(transList);
	}
	
}
