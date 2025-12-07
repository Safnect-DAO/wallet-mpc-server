package com.safnect.wallet.mpc.mapper.chain;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.safnect.wallet.mpc.model.chain.Chain;

import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface ChainMapper extends Mapper<Chain>, InsertListMapper<Chain> {

	List<Chain> getChain(@Param("walletId") String walletId);
	
	List<Chain> getUnadded(@Param("walletId") String walletId, @Param("keyword") String keyword);
}
