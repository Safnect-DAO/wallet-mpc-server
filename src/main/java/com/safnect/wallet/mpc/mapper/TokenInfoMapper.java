package com.safnect.wallet.mpc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.safnect.wallet.mpc.model.TokenInfo;

import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface TokenInfoMapper extends Mapper<TokenInfo>, InsertListMapper<TokenInfo> {
	
	@Select("select * from token_info where decimals is null")
	List<TokenInfo> get();
}
