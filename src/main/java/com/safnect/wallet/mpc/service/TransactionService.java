package com.safnect.wallet.mpc.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safnect.wallet.mpc.mapper.TransactionFailureMapper;
import com.safnect.wallet.mpc.mapper.TransactionMapper;
import com.safnect.wallet.mpc.model.Transaction;
import com.safnect.wallet.mpc.model.TransactionFailure;

@Service
public class TransactionService {

	@Autowired
	TransactionMapper transactionMapper;
	
	@Autowired
	TransactionFailureMapper transactionFailureMapper;
	
	@Transactional
	public void addTransactionSuccess(Transaction trans) {
		Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
		List<Transaction> list = this.transactionMapper.select(cond);
		if (CollectionUtils.isEmpty(list)) {
			this.transactionMapper.insertSelective(trans);
		} else {
			Transaction exists = list.get(0);
			Transaction update = new Transaction();
			update.setId(exists.getId());
			update.setInternal(true);
			this.transactionMapper.updateByPrimaryKeySelective(update);
		}
	}
	
	@Transactional
	public void addTransactionFailure(Transaction trans, TransactionFailure tf) {
		this.transactionMapper.insertSelective(trans);
		this.transactionFailureMapper.insertSelective(tf);
	}
}
