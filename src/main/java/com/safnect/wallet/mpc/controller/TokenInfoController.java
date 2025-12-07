package com.safnect.wallet.mpc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.TokenInfoMapper;
import com.safnect.wallet.mpc.model.TokenInfo;
import com.safnect.wallet.mpc.service.OklinkService;
import com.safnect.wallet.mpc.util.EmojiUtil;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@RestController
@RequestMapping("token-info")
public class TokenInfoController {

	@Autowired
	TokenInfoMapper tokenInfoMapper;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	OklinkService oklinkService;
	
	@GetMapping("all")
	public ResponseModel all(String network, String chainName) {
		if (StringUtils.isAnyBlank(network)) {
			return ResponseModel.fail601();
		}
		String redisKey = "tokenlistv1_" + network + StringUtils.defaultString(chainName, "");
		String value = this.redisTemplate.opsForValue().get(redisKey);
		Map<String, Map<String, TokenInfo>> map = null;
		if (StringUtils.isBlank(value)) {
			List<TokenInfo> tokenList = this.tokenInfoMapper.select(new TokenInfo(network, chainName));
			map = new HashMap<>();
			for (TokenInfo token : tokenList) {
				String chainNameKey = token.getChainName();
				String contractAddress = token.getContractAddress();
				Map<String, TokenInfo> tokenMap = map.get(chainNameKey);
				boolean isNew = false;
				if (tokenMap == null) {
					tokenMap = new HashMap<>();
					isNew = true;
				}
				tokenMap.put(contractAddress, token);
				if (isNew) {
					map.put(chainNameKey, tokenMap);
				}
			}
			this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(map), 2, TimeUnit.HOURS);
		} else {
			map = JsonUtil.fromJson(value, new TypeReference<Map<String, Map<String, TokenInfo>>>() {});
		}
		return ResponseModel.successData(map);
	}
	
	@GetMapping("fetch-token")
	public ResponseModel fetchToken(String chainName, Integer chainId) {
		int sno = 1;
		for (int i=1; i<201; i++) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("chainShortName", chainName);
			paramMap.put("page", i);
			paramMap.put("limit", 50);
			paramMap.put("orderBy", "transactionAmount24h");
			ResponseModel rm = this.oklinkService.executeGet("token/token-list", paramMap);
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) rm.getData();
			if (CollectionUtils.isNotEmpty(mapList)) {
				Map<String, Object> map = mapList.get(0);
				List<Map<String, Object>> tokenList = (List<Map<String, Object>>) map.get("tokenList");
				if (CollectionUtils.isNotEmpty(tokenList)) {
					List<TokenInfo> tokenInfoList = new ArrayList<>();
					for (Map<String, Object> tokenMap : tokenList) {
						TokenInfo token = new TokenInfo();
						token.setChain(chainId);
						token.setChainName(chainName);
						token.setContractAddress(MapUtils.getString(tokenMap, "tokenContractAddress"));
						token.setDecimals(MapUtils.getInteger(tokenMap, "precision"));
						String tokenFullName = MapUtils.getString(tokenMap, "tokenFullName");
						String tokenFullName2 = EmojiUtil.emojiConverterToAlias(tokenFullName);
						if (StringUtils.contains(tokenFullName2, "𝕏")) {
							tokenFullName2 = TextUtil.handleNickName(tokenFullName2);
						}
						token.setFullName(tokenFullName2);
						token.setImg(MapUtils.getString(tokenMap, "logoUrl"));
						token.setInvisable(false);
						token.setNetwork("mainnet");
						token.setSno(sno);
						String Symbol = MapUtils.getString(tokenMap, "token");
						String Symbol2 = EmojiUtil.emojiConverterToAlias(Symbol);
						Symbol2 = TextUtil.handleNickName(Symbol2);
						token.setSymbol(Symbol2);
						token.setTotalSupply(MapUtils.getString(tokenMap, "totalSupply"));
						tokenInfoList.add(token);
						System.out.println(tokenFullName + "---" + tokenFullName2 + "--" + Symbol + "---" + Symbol2 + "---------------- " + sno);
						sno ++;
					}
					if (CollectionUtils.isNotEmpty(tokenInfoList)) {
						this.tokenInfoMapper.insertList(tokenInfoList);
						System.out.println("--------i " + i);
					}
				}
			}
		}
		return ResponseModel.success();
	}
	
	@GetMapping("fetch-tron-token")
	public ResponseModel fetchTronToken() {
		String url = "https://apilist.tronscanapi.com/api/tokens/overview?start=0&limit=200&verifier=all&order=desc&filter=trc20&sort=&showAll=1&field=";
		String json = HttpClientUtil.httpGet(url, null);
		Map<String, Object> map = JsonUtil.fromJson2Map(json);
		List<Map<String, Object>> tokenMapList = (List<Map<String, Object>>) map.get("tokens");
		int sno = 1;
		List<TokenInfo> tokenList = new ArrayList<>();
		for (Map<String, Object> tokenMap : tokenMapList) {
			TokenInfo to = new TokenInfo();
			to.setChain(195);
			to.setChainName("TRON");
			to.setContractAddress(MapUtils.getString(tokenMap, "contractAddress"));
			to.setDecimals(MapUtils.getInteger(tokenMap, "decimal"));
			to.setFullName(MapUtils.getString(tokenMap, "name"));
			to.setImg(MapUtils.getString(tokenMap, "imgUrl"));
			to.setInvisable(false);
			to.setNetwork("mainnet");
			to.setSno(sno);
			to.setSymbol(MapUtils.getString(tokenMap, "abbr"));
			to.setTotalSupply(MapUtils.getString(tokenMap, "supply"));
			tokenList.add(to);
			sno ++;
		}
		this.tokenInfoMapper.insertList(tokenList);
		return ResponseModel.success();
	}
}
