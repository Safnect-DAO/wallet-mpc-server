package com.safnect.wallet.mpc.mapper;

import com.safnect.wallet.mpc.model.WalletAddress;

import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface WalletAddressMapper extends Mapper<WalletAddress>, InsertListMapper<WalletAddress> {

}
