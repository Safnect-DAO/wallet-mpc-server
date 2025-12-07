package com.safnect.wallet.mpc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.safnect.wallet.mpc.model.Coin;

import tk.mybatis.mapper.common.Mapper;

public interface CoinMapper extends Mapper<Coin> {

	@Select("select * from coin where visible = 1 and network = #{network} order by sno")
	List<Coin> get(@Param("network") String network);
}
