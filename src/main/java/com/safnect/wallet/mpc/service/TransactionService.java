package com.safnect.wallet.mpc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safnect.wallet.mpc.mapper.TransactionFailureMapper;
import com.safnect.wallet.mpc.mapper.TransactionMapper;
import com.safnect.wallet.mpc.mapper.TransactionSuccessMapper;
import com.safnect.wallet.mpc.model.Transaction;
import com.safnect.wallet.mpc.model.TransactionFailure;
import com.safnect.wallet.mpc.model.TransactionSuccess;

@Service
public class TransactionService {

	@Autowired
	TransactionMapper transactionMapper;
	
	@Autowired
	TransactionSuccessMapper transactionSuccessMapper;
	
	@Autowired
	TransactionFailureMapper transactionFailureMapper;
	
	public void addTransactionSuccess(Transaction trans, TransactionSuccess ts) {
		this.transactionMapper.insertSelective(trans);
		this.transactionSuccessMapper.insertSelective(ts);
	}
	
	public void addTransactionFailure(Transaction trans, TransactionFailure tf) {
		this.transactionMapper.insertSelective(trans);
		this.transactionFailureMapper.insertSelective(tf);
	}
}
