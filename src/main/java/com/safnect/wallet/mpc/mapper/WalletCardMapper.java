package com.safnect.wallet.mpc.mapper;

import com.safnect.wallet.mpc.model.WalletCard;

import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface WalletCardMapper extends Mapper<WalletCard>, InsertListMapper<WalletCard> {

}
