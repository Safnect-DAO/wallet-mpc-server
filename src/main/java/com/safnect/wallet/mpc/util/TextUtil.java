package com.safnect.wallet.mpc.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 说明：文本工具类
 * 
 * @author shiw 2014年7月19日
 */
public class TextUtil {

	static final String CHARTS = "abcdefghijklmnopqrstuvwxyz012346789";
	static final String LETTER_CHARTS = "abcdefghijklmnopqrstuvwxyz";
	static final int CHARTS_LENGTH = CHARTS.length();
	
	static final Random r = new Random();

	public static String generateCode() {
		String randomChar = String.valueOf(CHARTS.charAt(new Random().nextInt(CHARTS_LENGTH)));
		String prefix = Long.toString(System.currentTimeMillis(), 36);
		return String.format("%s%s", prefix, randomChar);
	}

	/**
	 * 当前纳秒转36进制（长度11位，适用于中型并发id）
	 * 
	 * @return
	 */
	public static String generateId() {
		String nano = String.valueOf(System.nanoTime());
		String id = new BigDecimal(System.currentTimeMillis() + nano.substring(nano.length() - 9, nano.length())).toBigInteger().toString(36);
		return id.toUpperCase();
	}
	
	/**
	 * 32位uuid
	 * @return
	 */
	public static String UUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println(TextUtil.sha1(TextUtil.base64("tb1qddnqymwlngtmgq3qntqxjwylgj5sfmz00hapa2testnet" + "s2fnect")));
	}

	/**
	 * 说明：Md5加密
	 * 
	 * @author shiw 2014年7月19日
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String md5(String text) {
		MessageDigest alg = null;
		try {
			alg = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		alg.update(text.getBytes());
		byte[] digesta = alg.digest();
		return byte2Hex(digesta);
	}
	
	/**
	 * 说明：SHA1加密
	 * @param text
	 * @return
	 */
	public static String sha1(String text) {
		MessageDigest alg = null;
		try {
			alg = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		alg.update(text.getBytes());
		byte[] digesta = alg.digest();
		return byte2Hex(digesta);
	}

	/**
	 * 说明：Md5加密
	 * 
	 * @author shiw 2014年7月19日
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static String md5(String text, int count) {
		for (int i = 0; i < count; i++) {
			text = TextUtil.md5(text);
		}
		return text;
	}

	/**
	 * 生成length位长度的的字母和数字组成的字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String generateRandom(int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(35);
			builder.append(String.valueOf(CHARTS.charAt(index)));
		}
		return builder.toString();
	}

	public static String generateLetter(int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(LETTER_CHARTS.length());
			builder.append(String.valueOf(CHARTS.charAt(index)));
		}
		return builder.toString();
	}

	public static String generateVerifyCode() {
		String str = String.valueOf(System.nanoTime());
		return str.substring(str.length() - 4);
	}
	
	public static String generateVerifyCode2() {
		String str = String.valueOf(System.nanoTime());
		return str.substring(str.length() - 6);
	}

	public static String getFlowNo() {
		String nano = String.valueOf(System.nanoTime());
		return System.currentTimeMillis() + nano.substring(nano.length() - 9, nano.length());
	}

	/**
	 * 说明：获取AES密码KEY
	 * 
	 * @author shiw 2014年7月19日
	 * @return
	 * @throws Exception
	 */
	private static SecretKeySpec getKey(String password) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(password.getBytes()));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		return key;
	}

	/**
	 * 说明：AES加密
	 * 
	 * @author shiw 2014年7月19日
	 * @param text
	 * @return
	 */
	public static String encrypt(String text, String password) {
		String result = "";
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, getKey(password));
			byte[] textByte = cipher.doFinal(text.getBytes("UTF-8"));
			result = byte2Hex(textByte);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 说明：AES解密
	 * 
	 * @author shiw 2014年7月19日
	 * @param text
	 * @return
	 */
	public static String decrypt(String text, String password) {
		String result = "";
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, getKey(password));
			byte[] textByte = cipher.doFinal(hex2Byte(text));
			result = new String(textByte);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 说明：获取UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static String getUUID2() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 将序号补0至length参数的长度
	 * 
	 * @param serialNo
	 *            序号
	 * @param length
	 *            总长度
	 * @return
	 */
	public static String appendZero(Long serialNo, int length) {
		String pattern = "%0" + length + "d";
		return String.format(pattern, serialNo);
	}
	
	public static String appendZeroHex(Integer num, int length) {
		String pattern = "%0" + length + "x";
		return String.format(pattern, num);
	}
	
	/**
	 * MD5后再base64编码
	 * 
	 * @param str
	 * @return
	 */
	public static String md5Base64(String str) {
		String encodeStr = "";
		try {
			byte[] utfBytes = str.getBytes("UTF-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(utfBytes);
			byte[] md5Bytes = mdTemp.digest();
			Base64 base64 = new Base64();
			byte[] base64Bytes = base64.encode(md5Bytes);
			encodeStr = new String(base64Bytes);
			return encodeStr;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String base64(String text) {
		Base64 base64 = new Base64();
		byte[] base64Bytes = base64.encode(text.getBytes());
		return new String(base64Bytes);
	}
	
	public static byte[] base64Decode(String text) {
		return Base64.decodeBase64(text);
	}

	public static String randomChar() {
		int start = new Random().nextInt(25);
		char c = (char) (start + 65);
		return String.valueOf(c);
	}

	/**
	 * 
	 * @param nickname
	 * @return
	 */
	public static String encodingCharset(String nickname) {
		if (StringUtils.isNoneBlank(nickname)) {
			try {
				if (nickname.equals(new String(nickname.getBytes("ISO8859-1"), "ISO8859-1"))) {
					nickname = new String(nickname.getBytes("ISO8859-1"), "UTF-8");
				}
			} catch (UnsupportedEncodingException ignore) {
			}
		}
		return nickname;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x010000) && (codePoint <= 0x10FFFF));
	}

	public static String handleNickName(String nickName) {
		if (StringUtils.isBlank(nickName)) {
			return nickName;
		}
		StringBuilder _sb = new StringBuilder();
		int _l = nickName.length();
		for (int i = 0; i < _l; i++) {
			char _s = nickName.charAt(i);
			if (isEmojiCharacter(_s)) {
				_sb.append(_s);
			}
		}
		return _sb.toString();
	}

	/**
	 * 字符自增
	 * 
	 * @param str
	 * @return
	 */
	public static String increment(String str) {
		if (StringUtils.isBlank(str)) {
			return "A1";
		}

		if (str.length() != 2) {
			throw new IllegalArgumentException("参数长度必须是两位");
		}

		char[] arr = str.toCharArray();
		int i = Integer.parseInt(String.valueOf(arr[1]));
		if (i + 1 < 10) {
			return String.valueOf(arr[0]) + (i + 1);
		} else {
			char next = (char) (arr[0] + 1);
			return String.valueOf(next) + 1;
		}
	}

	public static byte[] hex2Byte(String hexStr) {
		if (StringUtils.isBlank(hexStr)) {
			return null;
		}
		try {
			char[] cs = hexStr.toCharArray();
			return Hex.decodeHex(cs);
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String byte2Hex(byte[] bytes) {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		return new String(Hex.encodeHex(bytes));
	}

	public static String hex2Ascii(String hex) {
		StringBuilder sb = new StringBuilder();
		String[] split = hex.split(",");
		for (String str : split) {
			int i = Integer.parseInt(str, 16);
			sb.append((char) i);
		}
		return sb.toString();
	}
	
	public static String getBillNo() {
		long nano = System.nanoTime();
		return Long.toString(nano, 16).toUpperCase();
	}

	public static String sha256(String text, String salt) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(salt.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(text.getBytes());
			return byte2Hex(bytes);
		} catch (Exception e) {
			System.out.println("Error HmacSHA256 ===========" + e.getMessage());
		}
		return null;
	}
	
	public static String sha2562(String text) {
		return DigestUtils.sha256Hex(text);
	}

	/**
	 * 3位数代码
	 * 
	 * @return
	 */
	public static String getCode() {
		Random r = new Random();
		int i = r.ints(1, 10001, 46555).findFirst().getAsInt();
		return Integer.toString(i, 36).toUpperCase();
	}

	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		return htmlStr.trim(); // 返回文本字符串
	}

	/**
	 * 获取随机字母数字组合
	 * 
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String getRandomCharAndNumr(Integer length) {
		String str = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			boolean b = random.nextBoolean();
			if (b) { // 字符串
				int choice = random.nextBoolean() ? 65 : 97; // 取得65大写字母还是97小写字母
				str += (char) (choice + random.nextInt(26));// 取得大写字母
			} else { // 数字
				str += String.valueOf(random.nextInt(10));
			}
		}
		return str;

	}

	public static byte[] intToByteArray(int a) {
		return new byte[] { (byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF),
				(byte) (a & 0xFF) };
	}
	
	/**
	 * 判断字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}


}
