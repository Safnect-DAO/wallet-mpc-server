package com.safnect.wallet.mpc.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.CollectionInfoMapper;
import com.safnect.wallet.mpc.model.CollectionInfo;
import com.safnect.wallet.mpc.util.TextUtil;

@RestController
@RequestMapping("coll-info")
public class CollectionInfoController {
	
	@Autowired
	CollectionInfoMapper collectionInfoMapper;
	
	@PostMapping("add") 
	public ResponseModel add(String walletId, String chain, String network, String name, String symbol, String description, String address) {
		if (StringUtils.isAnyBlank(address, name, symbol, chain, network)) {
			return ResponseModel.fail601();
		}
		
		if (name.length() > 100) {
			return ResponseModel.fail602();
		}
		
		if (symbol.length() > 10) {
			return ResponseModel.fail602();
		}
		
		if (description.length() > 1000) {
			return ResponseModel.fail602();
		}
		
		CollectionInfo co = new CollectionInfo();
		co.setWalletId(walletId);
		co.setChain(chain);
		co.setNetwork(network);
		co.setName(name);
		co.setSymbol(symbol);
		co.setDescription(description);
		co.setCreateDatetime(new Date());
		co.setBcCollId("#");
		co.setId(TextUtil.generateId());
		co.setIsmint(false);
		co.setAddress(address);
		this.collectionInfoMapper.insertSelective(co);
		return ResponseModel.successData(co.getId());
	}
	
	@GetMapping("get") 
	public ResponseModel get(String address, String chain, String network) {
		if (StringUtils.isAnyBlank(address, chain, network)) {
			return ResponseModel.fail601();
		}
		CollectionInfo co = new CollectionInfo();
		co.setAddress(address);
		co.setChain(chain);
		co.setNetwork(network);
		List<CollectionInfo> collList = this.collectionInfoMapper.select(co);
		return ResponseModel.successData(collList);
	}
	
	@PostMapping("mint-update") 
	public ResponseModel mintUpdate(String id, String bcCollId) {
		if (StringUtils.isAnyBlank(id, bcCollId)) {
			return ResponseModel.fail601();
		}
		CollectionInfo co = new CollectionInfo();
		co.setId(id);
		co.setIsmint(true);
		co.setBcCollId(bcCollId);
		this.collectionInfoMapper.updateByPrimaryKeySelective(co);
		return ResponseModel.success();
	}
}
