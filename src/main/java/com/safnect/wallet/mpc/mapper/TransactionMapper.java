package com.safnect.wallet.mpc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.safnect.wallet.mpc.model.Transaction;

import tk.mybatis.mapper.common.Mapper;

public interface TransactionMapper extends Mapper<Transaction> {
	
	@Select("select t.* "
			+ "from transaction t "
			+ "where t.successed = 1 and t.network = #{network} and t.chain = #{chain} and (t.send_address = #{addr} or t.to_address = #{addr}) order by send_time desc limit #{start}, #{limit}")
	List<Transaction> getTrans(@Param("network") String network, @Param("chain") String chain, @Param("addr") String addr, @Param("start") Integer start, @Param("limit") Integer limit);
	
	@Select("select t.* "
			+ "from transaction t "
			+ "where t.successed = 1 and t.network = #{network} and token_name like 'CAT-721%' and (t.send_address = #{addr} or t.to_address = #{addr}) order by send_time desc limit #{start}, #{limit}")
	List<Transaction> getCat721Trans(@Param("network") String network, @Param("addr") String addr, @Param("start") Integer start, @Param("limit") Integer limit);
	
	@Select("select distinct t.txid from (select t.txid from transaction t where (send_address = #{address} or to_address = #{address}) and successed = 1 order by t.send_time desc limit 50) t")
	List<String> getTxids(@Param("address") String address);
	
	List<Transaction> get(@Param("network") String network, @Param("chain") String chain, @Param("contractAddress") String contractAddress, 
			@Param("addressList") List<String> addressList, @Param("start") Integer start, @Param("limit") Integer limit);
}
