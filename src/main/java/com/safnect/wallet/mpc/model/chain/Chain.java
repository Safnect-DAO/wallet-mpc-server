package com.safnect.wallet.mpc.model.chain;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safnect.wallet.mpc.util.JsonUtil;

public class Chain {
	
	public Chain() {
		super();
	}

	public Chain(String chainId, String name, String rpcUrl, String symbol, String explorerUrl, String icon,
			Integer decimals, Integer sno, Boolean visible, Boolean enabled) {
		super();
		this.chainId = chainId;
		this.name = name;
		this.rpcUrl = rpcUrl;
		this.symbol = symbol;
		this.explorerUrl = explorerUrl;
		this.icon = icon;
		this.decimals = decimals;
		this.sno = sno;
		this.visible = visible;
		this.enabled = enabled;
	}
	
	public static void main(String[] args) {
		ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 读取 JSON 文件并将其映射为 User 对象
            File jsonFile = new File("D:\\mcohilncbfahbmgdjkbpemcciiolgcge.JSON"); // 替换为你的 JSON 文件路径
            Map map = objectMapper.readValue(jsonFile, Map.class);
            Map<String, Object> logMap = (Map<String, Object>) map.get("log");
            List<Map<String, Object>> entriesMapList = (List<Map<String, Object>>) logMap.get("entries");
            for (Map<String, Object> entriesMap : entriesMapList) {
            	Map<String, Object> responseMap = (Map<String, Object>) entriesMap.get("response");
            	Map<String, Object> contentMap = (Map<String, Object>) responseMap.get("content");
            	String text = (String) contentMap.get("text");
            	Map<String, Object> textMap = JsonUtil.fromJson2Map(text);
            	List<Map<String, Object>> dataList = (List<Map<String, Object>>) textMap.get("data");
            	for (Map<String, Object> dataMap : dataList) {
            		System.out.println(dataMap);
            	}
            	break;
            }
            // 输出结果
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Id
	String chainId;
	
	String name, rpcUrl, symbol, explorerUrl, icon, restApi, hostedBy;
	
	Integer decimals, sno;
	
	Boolean visible, enabled;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRpcUrl() {
		return rpcUrl;
	}

	public void setRpcUrl(String rpcUrl) {
		this.rpcUrl = rpcUrl;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getExplorerUrl() {
		return explorerUrl;
	}

	public void setExplorerUrl(String explorerUrl) {
		this.explorerUrl = explorerUrl;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public String getChainId() {
		return chainId;
	}

	public void setChainId(String chainId) {
		this.chainId = chainId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getRestApi() {
		return restApi;
	}

	public void setRestApi(String restApi) {
		this.restApi = restApi;
	}

	public String getHostedBy() {
		return hostedBy;
	}

	public void setHostedBy(String hostedBy) {
		this.hostedBy = hostedBy;
	}
}
