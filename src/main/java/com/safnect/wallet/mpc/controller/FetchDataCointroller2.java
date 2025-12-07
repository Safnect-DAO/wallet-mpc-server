package com.safnect.wallet.mpc.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.TokenInfoMapper;
import com.safnect.wallet.mpc.model.TokenInfo;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

/**
 * 抓抓抓
 * 
 * @author shiwe
 *
 */
public class FetchDataCointroller2 {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	TokenInfoMapper tokenInfoMapper;
	

	// @GetMapping("tokens")
	public ResponseModel getEthTokens() throws IOException {
		String url = "https://etherscan.io/tokens?ps=100&p=%s";
		int sno = 1;
		for (int i = 1; i < 10000; i++) {
			Document doc = null;
			try {
				doc = Jsoup.connect(String.format(url, i)).get();
			} catch (Exception e) {
				try {
					doc = Jsoup.connect(String.format(url, i)).get();
				} catch (Exception e1) {
					try {
						doc = Jsoup.connect(String.format(url, i)).get();
					} catch (Exception e2) {
						doc = Jsoup.connect(String.format(url, i)).get();
					}
				}
			}
			Elements els = doc.select("#ContentPlaceHolder1_tblErc20Tokens table tbody tr");
			List<TokenInfo> tokenList = new ArrayList<>();
			for (Element el : els) {
				Elements tds = el.select("td");
				String href = tds.get(1).select("a").attr("href");
				String contractAddress = href.replace("/token/", "");
				String imgUrl = tds.get(1).select("a>img").attr("src");
				String fullName = tds.get(1).select("a div.gap-1 div").text();
				String symbol = tds.get(1).select("a div.gap-1 span").text().replace("(", "").replace(")", "");
				TokenInfo ti = new TokenInfo();
				ti.setChain(1);
				ti.setContractAddress(contractAddress);
				ti.setFullName(fullName);
				ti.setImg(imgUrl);
				ti.setInvisable(false);
				ti.setNetwork("mainnet");
				ti.setSno(sno);
				ti.setSymbol(symbol);
				tokenList.add(ti);
				System.out.println("contractAddress: " + contractAddress + ", imgUrl: " + imgUrl + ", fullName: "
						+ fullName + ", symbol: " + symbol);
				sno++;
			}
			try {
				this.tokenInfoMapper.insertList(tokenList);
			} catch (Exception e) {
				e.printStackTrace();
				this.tokenInfoMapper.insertList(tokenList);
			}

		}
		return ResponseModel.success();

	}

//	@GetMapping("download")
	public ResponseModel downloadImg() throws Exception {
		List<TokenInfo> list = this.tokenInfoMapper.selectAll();
		for (int i = 0; i < list.size(); i++) {
			TokenInfo ti = list.get(i);
			String url = "https://etherscan.io" + ti.getImg();
			try {
				downloadFile(url);
			} catch (Exception e) {
				e.printStackTrace();
				i--;
			}
		}
		return ResponseModel.success();
	}

	private static void downloadFile(String url) throws FileNotFoundException, IOException {
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		fileName = fileName.split("[?]")[0];
		if (!StringUtils.containsIgnoreCase(fileName, ".png")) {
			return ;
		}
		File file = new File("D:\\token\\" + fileName);
		if (file.exists()) {
			return ;
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		// 设置请求方法为 GET
		conn.setRequestMethod("GET");

		// 设置 HTTP 头部
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
		conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
//		conn.setRequestProperty(":path", "/token/images/trueusd_32.png");
//		conn.setRequestProperty(":authority", "etherscan.io");
		conn.setRequestProperty("Cookie", "ASP.NET_SessionId=3klinx3skwryx2pj1b0omflr; etherscan_offset_datetime=+8; __stripe_mid=f02151f0-7e24-4c9c-a63f-8e9cd4bdb6fc87ee43; etherscan_autologin=True; etherscan_pwd=4792:Qdxb:52yUxhbPs0iQ5zqNi/+UoRI9hBP5Ry7f2YRe03PZwjM=; etherscan_userid=shiw1962; etherscan_switch_token_amount_value=value; __cflb=02DiuFnsSsHWYH8WqVXbZzkeTrZ6gtmGUs26HBXnJpMvk; _gid=GA1.2.740466803.1721694540; cf_clearance=tEj2tz1HGSgKBw8ZraZs0DNmUEUXZ2TYgJn7aCJHcGE-1721707296-1.0.1.1-_CFcXl1NINY6pbsFxhm68IQ.EevvkGent5yFQMs5iv4_izo8a5.sZ8.5aP622ZPfac0tB1EeHdJwnySOF6LKew; _ga_T1JC9RNQXV=GS1.1.1721727706.11.1.1721728636.58.0.0; _ga=GA1.2.2146578518.1721399032");

		// 其他头部可以这样设置
		// conn.setRequestProperty("Other-Header", "header-value");

		// 发送请求并接收响应
		InputStream in = conn.getInputStream();

		FileOutputStream out = new FileOutputStream(file);

		byte[] buffer = new byte[4096];
		int n = -1;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		out.flush();
		out.close();
		in.close();
		conn.disconnect();
		System.out.println("File downloaded successfully.");
	}

	private String getUrlNetworkPrefix(String network) {
		String prefix = null;
		if (StringUtils.equals(network, "mainnet")) {
			prefix = "";
		} else {
			prefix = "sepolia.";
		}
		return prefix;
	}

//	@GetMapping("set")
	public ResponseModel set() throws Exception {
		String url = "https://etherscan.io/token/%s";
		List<TokenInfo> list = this.tokenInfoMapper.selectAll();
		for (TokenInfo ti : list) {
			if (StringUtils.isNotBlank(ti.getTotalSupply()) && ti.getDecimals() != null) {
				continue;
			}
			TokenInfo u = new TokenInfo();
			u.setContractAddress(ti.getContractAddress());
			Document doc = null;
			try {
				doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
			} catch (Exception e) {
				try {
					Thread.sleep(3000l);
					doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
				} catch (Exception e1) {
					try {
						Thread.sleep(3000l);
						doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
					} catch (Exception e2) {
						Thread.sleep(3000l);
						doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
					}
				}
			}
			try {
				String decimals = doc.select("#ContentPlaceHolder1_divSummary .g-3>div:eq(2) .card-body h4 b").text();
				u.setDecimals(Integer.parseInt(decimals));
				String totalSupply = doc
						.select("#ContentPlaceHolder1_divSummary .g-3>div:eq(0) .card-body div.align-items-center>span")
						.text();
				u.setTotalSupply(totalSupply);
				try {
					this.tokenInfoMapper.updateByPrimaryKeySelective(u);
				} catch (Exception e) {
					e.printStackTrace();
					this.tokenInfoMapper.updateByPrimaryKeySelective(u);
				}
			} catch (Exception e) {
				System.out.println(JsonUtil.toJson(ti));
			}
		}
		return ResponseModel.success();

	}

	private String getCoingeckoIds() {
		String redisKey = "coingecko_token_ids";
		String ids;
		String value = this.redisTemplate.opsForValue().get(redisKey);
		if (StringUtils.isBlank(value)) {
			String result = HttpClientUtil.httpGet("https://api.coingecko.com/api/v3/coins/list", null);
			List<Map> mapList = JsonUtil.fromJsonList(result, Map.class);
			List<String> idList = new ArrayList<>();
			for (int i = 0; i < mapList.size(); i++) {
				String id = MapUtils.getString(mapList.get(i), "id");
				if (!StringUtils.containsAny(id, "-", " ")) {
					idList.add(id);
				}
			}
			ids = StringUtils.join(idList, ",");
			this.redisTemplate.opsForValue().set(redisKey, ids, 6, TimeUnit.HOURS);
		} else {
			ids = value;
		}
		return ids;
	}
}
