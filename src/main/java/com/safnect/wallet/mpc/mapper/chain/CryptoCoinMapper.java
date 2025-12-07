package com.safnect.wallet.mpc.mapper.chain;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.safnect.wallet.mpc.model.chain.CryptoCoin;
import com.safnect.wallet.mpc.model.chain.CryptoCoinPopular;

import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface CryptoCoinMapper extends Mapper<CryptoCoin>, InsertListMapper<CryptoCoin> {

	List<CryptoCoin> getCoinList(@Param("chainId") String chainId);
	
	List<CryptoCoinPopular> getCryptoCoinPopular();
}
