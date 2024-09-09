package com.safnect.wallet.mpc.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.model.Transaction;
import com.safnect.wallet.mpc.model.TransactionFailure;
import com.safnect.wallet.mpc.model.TransactionSuccess;
import com.safnect.wallet.mpc.service.TransactionService;
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
	

	/**
	 * 交易成功
	 * @param trans
	 * @param txid
	 * @return
	 */
	@PostMapping("success")
	public ResponseModel success(Transaction trans, String txid) {
		if (trans.isEmpty() || StringUtils.isBlank(txid)) {
			return ResponseModel.fail601();
		}
		String transId = TextUtil.generateId();
		trans.setId(transId);
		trans.setSuccessed(true);
		trans.setSendTime(new Date());
		
		TransactionSuccess ts = new TransactionSuccess(transId, txid);
		this.transactionService.addTransactionSuccess(trans, ts);
		return ResponseModel.sucess();
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
		
		TransactionFailure tf = new TransactionFailure(transId, exInfo);
		this.transactionService.addTransactionFailure(trans, tf);
		return ResponseModel.sucess();
	}
}
