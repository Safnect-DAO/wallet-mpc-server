package com.safnect.wallet.mpc.mapper.activity;

import org.apache.ibatis.annotations.Select;

import com.safnect.wallet.mpc.model.activity.TwitterApiKey;

import tk.mybatis.mapper.common.Mapper;

public interface TwitterApiKeyMapper extends Mapper<TwitterApiKey> {

	@Select("select t.* from twitter_api_key t where t.valid = 1 and TIMESTAMPDIFF(MINUTE, t.last_use_datetime, NOW()) > 16 order by t.last_use_datetime asc limit 1")
	TwitterApiKey getTwitterApiKey();
}
