package com.safnect.wallet.mpc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.safnect.wallet.mpc.model.Wallet;
import com.safnect.wallet.mpc.model.WalletAccount;

import tk.mybatis.mapper.common.Mapper;

public interface WalletMapper extends Mapper<Wallet> {

	List<Wallet> getWallets(@Param("idList") List<String> idList);
	
	List<WalletAccount> getWalletAccounts(@Param("idList") List<String> idList);
}
