package com.safnect.wallet.mpc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safnect.wallet.mpc.mapper.dex.DexCancelOrderMapper;
import com.safnect.wallet.mpc.mapper.dex.DexMakeOrderMapper;
import com.safnect.wallet.mpc.mapper.dex.DexOrderResultMapper;
import com.safnect.wallet.mpc.mapper.dex.DexTakeOrderMapper;
import com.safnect.wallet.mpc.model.dex.DexCancelOrder;
import com.safnect.wallet.mpc.model.dex.DexMakeOrder;
import com.safnect.wallet.mpc.model.dex.DexOrderResult;
import com.safnect.wallet.mpc.model.dex.DexTakeOrder;

@Service
public class DexOrderService {

	@Autowired
	DexMakeOrderMapper dexMakeOrderMapper;
	
	@Autowired
	DexTakeOrderMapper dexTakeOrderMapper;
	
	@Autowired
	DexCancelOrderMapper dexCancelOrderMapper;
	
	@Autowired
	DexOrderResultMapper dexOrderResultMapper;
	
	@Transactional
	public void saveMakeOrder(DexMakeOrder makeOrder, DexOrderResult result) {
		this.dexMakeOrderMapper.insertSelective(makeOrder);
		this.dexOrderResultMapper.insertSelective(result);
	}
	
	@Transactional
	public void saveTakeOrder(DexTakeOrder takeOrder, DexOrderResult result) {
		if (takeOrder.getSuccessed()) {
			DexMakeOrder existsOrder = this.dexMakeOrderMapper.selectOne(new DexMakeOrder(takeOrder.getOrderId()));
			if (existsOrder != null) {
				DexMakeOrder updateOrder = new DexMakeOrder();
				updateOrder.setId(existsOrder.getId());
				updateOrder.setTaken(true);
				this.dexMakeOrderMapper.updateByPrimaryKeySelective(updateOrder);
			}
		}
		
		this.dexTakeOrderMapper.insertSelective(takeOrder);
		this.dexOrderResultMapper.insertSelective(result);
	}
	
	@Transactional
	public void saveCancelOrder(DexCancelOrder cancelOrder, DexOrderResult result) {
		if (cancelOrder.getSuccessed()) {
			DexMakeOrder existsOrder = this.dexMakeOrderMapper.selectOne(new DexMakeOrder(cancelOrder.getOrderId()));
			if (existsOrder != null) {
				DexMakeOrder updateOrder = new DexMakeOrder();
				updateOrder.setId(existsOrder.getId());
				updateOrder.setCanceled(true);
				this.dexMakeOrderMapper.updateByPrimaryKeySelective(updateOrder);
			}
		}
		this.dexCancelOrderMapper.insertSelective(cancelOrder);
		this.dexOrderResultMapper.insertSelective(result);
	}
}
