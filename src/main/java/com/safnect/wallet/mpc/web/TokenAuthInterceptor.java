package com.safnect.wallet.mpc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@Component
public class TokenAuthInterceptor implements HandlerInterceptor {
	
	static final String KEY = "s2fnect";
	
	static final String ALLOW_TOKEN = "8bdf70515e98bd98e4532aa55778b88791cff4f08a61a2998930619aaeef70a8";

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader("token");
		if (StringUtils.isAnyBlank(token)) {
			this.writeMessage(response, ResponseModel.fail601());
			return false;
		}
		
		if (StringUtils.equals(token, ALLOW_TOKEN)) { // allow this token
			return true;
		}
		
		String encryptText = this.getEncryptText(request);
		if (!StringUtils.equals(token, encryptText)) {
			try{
				this.writeMessage(response, ResponseModel.fail(603, "Invalid token"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
    }
	
	private String getEncryptText(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		List<String> list = new ArrayList<>();
		while (names.hasMoreElements()) {
			list.add(names.nextElement());
		}
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		list.forEach((item) -> {
			String value = request.getParameter(item);
			sb.append(value);
		});
		sb.append(KEY);
		String text = sb.toString();
		String encryptText = TextUtil.sha1(TextUtil.base64(text));
		return encryptText;
	}

	private void writeMessage(HttpServletResponse response, ResponseModel rm) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		PrintWriter out = response.getWriter();
		out.write(JsonUtil.toJson(rm));
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) {
		System.out.println(TextUtil.sha1(TextUtil.base64("{\"FLOW\":{\"address\":\"0xfa9cce2fe5275fc6169ac432bf884b12d65e51f4\",\"publicKey\":\"0x5d2917e700734113c2bc094561f78ca1cffb8a0d23cb3e33b166541364448555c44caf2542a8ae76f8d50676dffbd9f187753751c6c2cb637c2e8738a77de18c\"},\"EOS\":{\"address\":\"\",\"publicKey\":\"EOS7DiAgie3YvhPH7u3kSPFs1s61YgAvmMePBYdpxFf3MTWgda7Lw\"},\"BCH\":{\"address\":\"qqvs38aqh8nh5twswas5c9s459gfh3ywpctaucqemw\",\"publicKey\":\"02683cf0df2cb0d4bd13b6d1a71717b3d3b2fb32ee542b038b08c44637eddb1f53\"},\"COSMOS\":{\"address\":\"cosmos1tgahajnh2kx0e8s95q9v0jc620jfm55cq30wty\",\"publicKey\":\"035e1ae5a2d9c19bf3b4aa6b1854ede13642d18075b739c33c21972a8fe02d5fd0\"},\"NOSTR\":{\"address\":\"npub1a0smw7th03eqthwrdy52nufdw0p2fvwz2qpeyxlcuna3u8w9th3qwa9f27\",\"publicKey\":\"ebe1b779777c7205ddc36928a9f12d73c2a4b1c25003921bf8e4fb1e1dc55de2\"},\"STARKNET\":{\"address\":\"0x5c20751cc7456a2bcbc48bc252c1e27c096e75d1d71fa92b4afe456153ffee5\",\"publicKey\":\"0x12c76ef8c2f184b2d32e90efdfdb9bf51bb200f1951aa7c5240d049532eae23\"},\"tron\":{\"publicKey\":\"04c3ee1d6f5205091b1d6583039e21e2fc4b2c0289194fe1f07f2bf1cf7c7b4a13dc43ad117c6470b61c0949af136ea5b36843bc251b5a4ed2c01cb6d668b47eca\",\"address\":\"TRaeWHqtKAQduZnUsxz1moJMYdmEAFszBC\"},\"XRP\":{\"publicKey\":\"0298685ec7830d7a4c6a07daa0b3e8135b4b3c445b1a792f0cdf52ccf9f3201773\",\"address\":\"rNzyXPGyCW2udyy4gEi67Vt8hA3ihkTEkZ\"},\"bitcoinTestnet\":[{\"compressedPublicKey\":\"03e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\",\"type\":\"Taproot\",\"publicKey\":\"e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\",\"address\":\"tb1pkrxjzvy5ev86qq57yw4pyf9zzztgmgw92lje4f37hkn063ed7frskz5495\"},{\"compressedPublicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\",\"address\":\"mkLD8Q3qXgWXt3k8PzqUXePpeDo8jkSsLf\",\"type\":\"Legacy\",\"publicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\"},{\"address\":\"2NFBMWtGtfVzVquGvBnFBgYTsgwakTycs8b\",\"publicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\",\"compressedPublicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\",\"type\":\"Nested Segwit\"},{\"address\":\"tb1qf4zp2cenu32k4ffdt0k3y60rcn2fl4d2pz203g\",\"compressedPublicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\",\"publicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\",\"type\":\"Native Segwit\"}],\"BSV\":{\"address\":\"1Dp4bhNqZXyg2YAJUJjgGaq5hmrV2ftqUY\",\"publicKey\":\"0377c220ceeba8a6948a0d9ce53003023390bbb4be0dd18a56ea6556aac08a405c\"},\"TON\":{\"address\":\"UQDCZvG8WmNq5ALJUZLD0CUxepC5iPt2ulqg9oXIy4o2-5EI\",\"publicKey\":\"ed91c83d6c76e026c265c54c076fa449978512e8ef24f133f8d9e2d49d1b5d88\"},\"DOGE\":{\"address\":\"DUK1NvKTenT49R7f5J1ojs7rGeTKZ1NLjo\",\"publicKey\":\"03f83a31ccd9a4de963e9e71a9f39015b592dcaf02e74dc9029b173fdfa19f31bd\"},\"CARDANO\":{\"address\":\"addr1qxr8fhhaz3a76vjuvjvvknulp4sjfrq65enedv4gm84zd4nzh26597q4mm4vxnfu866v36f62nlulwmq97en39gpqycqs3yx76\",\"publicKey\":\"8c67e65b0f35dd866535b9c288f2a785bad06cc14b2ed66cb485b4af04d2c98376fdd1903abbf32f7c72ee9d83dea2b7b6b013c10d6f516f0be68b8166305148\"},\"WAX\":{\"publicKey\":\"EOS8NdSnxrPrbnkeLiVyNERXgwZnJAYbDYmF5uVoB1r72dp2wQa3d\",\"address\":\"\"},\"STACKS\":{\"address\":\"SP37KB0Y8JY56F4XYABP8V0YRMVBKYMZ6F9YQJHRA\",\"publicKey\":\"0377934274d2580a7c82f66acd3f283c1d98e51c3ab7a33c1d0448d244e43c954e\"},\"POLKADOT\":{\"address\":\"153PC6gbMrPmAMURqtWnyGd6D5guX1ZZaSqumbJpc6jL47ET\",\"publicKey\":\"b2c22099af13b0e85a731b0e69c33f193219fcde7a81c309f478721095949b49\"},\"STELLAR\":{\"publicKey\":\"GCQHE3BCQT7HERFWXLRTYYEWRBPWMOHDQNHDQNFSJHVHBWVZIJBLRWQI\",\"address\":\"GCQHE3BCQT7HERFWXLRTYYEWRBPWMOHDQNHDQNFSJHVHBWVZIJBLRWQI\"},\"BSC\":{\"publicKey\":\"0x5d2917e700734113c2bc094561f78ca1cffb8a0d23cb3e33b166541364448555c44caf2542a8ae76f8d50676dffbd9f187753751c6c2cb637c2e8738a77de18c\",\"address\":\"0xfa9cce2fe5275fc6169ac432bf884b12d65e51f4\"},\"ETC\":{\"address\":\"0x50c01c9aa833111fffaf82ad5306ef37b6f99c35\",\"publicKey\":\"0xc5960470278f7bca5a32bb04062e276bb551e7deaa745a81b1bef9668b7359bd92a9568d0f65466c5e39c036e95fc8b2268ce5ede8e08cfa6019868ba61cbf70\"},\"BELL\":{\"publicKey\":\"d91be291ede43d9f6a66365f170685cd604ebbcc180c924beff3dceac2fe4090\",\"address\":\"bel1pex8t74ffpcgcvun5389cgks9ny263xwxncy29qekqn0kpycncx0sh592uh\"},\"bitcoin\":[{\"address\":\"bc1pkrxjzvy5ev86qq57yw4pyf9zzztgmgw92lje4f37hkn063ed7frsp2z6lm\",\"publicKey\":\"e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\",\"compressedPublicKey\":\"03e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\",\"type\":\"Taproot\"},{\"publicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\",\"compressedPublicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\",\"address\":\"15pFqLxrif5H6wGWgRs6hjBVnECRrJmgg6\",\"type\":\"Legacy\"},{\"type\":\"Nested Segwit\",\"compressedPublicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\",\"address\":\"3Pd9T9Ls43V9e7eNWedK4bUcUbNafXC9Wv\",\"publicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\"},{\"type\":\"Native Segwit\",\"compressedPublicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\",\"address\":\"bc1qf4zp2cenu32k4ffdt0k3y60rcn2fl4d2ty3u2m\",\"publicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\"}],\"LTC\":{\"publicKey\":\"0252c2720d692db725d8de2d340c9a1b800d26aedd81bb3329f71d2f67cbddf695\",\"address\":\"MGbeht93KfqQzhwwuXk4R3PuNzc7PKvg37\"},\"NEAR\":{\"address\":\"2c3d7b1ed0e45de7b0144f7205739d321805a7b5df018a7f4bc1c366dc1463d8\",\"publicKey\":\"ed25519:3yhK97NLFBZg8vmhTrmMMcpJLCWc1hsrzDDByZpXGCy5\"},\"APTOS\":{\"publicKey\":\"0x3ca57fab63b09aff2c7ef3cf813ed24b10ac2dfc2d680d07782b486d02e81fb2\",\"address\":\"0x7621f9bd8ceb3ed4216f4f8810a44ba9f025e88fe527a199e3835ede31216265\"},\"EVM\":{\"address\":\"0xfa9cce2fe5275fc6169ac432bf884b12d65e51f4\",\"publicKey\":\"0x5d2917e700734113c2bc094561f78ca1cffb8a0d23cb3e33b166541364448555c44caf2542a8ae76f8d50676dffbd9f187753751c6c2cb637c2e8738a77de18c\"},\"SUI\":{\"publicKey\":\"OKcKZi4z1e8uQ9352HANcsW0Wcv1HuBASYxsx3fCSWs=\",\"address\":\"0x8a5b99c2f4d8f89cd91ccf672f260398c964b9262001d8246b5accc5fa3a1fcc\"},\"fractalBitcoinTestnet\":[{\"type\":\"Taproot\",\"address\":\"bc1pkrxjzvy5ev86qq57yw4pyf9zzztgmgw92lje4f37hkn063ed7frsp2z6lm\",\"publicKey\":\"e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\",\"compressedPublicKey\":\"03e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\"},{\"address\":\"15pFqLxrif5H6wGWgRs6hjBVnECRrJmgg6\",\"publicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\",\"compressedPublicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\",\"type\":\"Legacy\"},{\"address\":\"3Pd9T9Ls43V9e7eNWedK4bUcUbNafXC9Wv\",\"publicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\",\"type\":\"Nested Segwit\",\"compressedPublicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\"},{\"publicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\",\"address\":\"bc1qf4zp2cenu32k4ffdt0k3y60rcn2fl4d2ty3u2m\",\"compressedPublicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\",\"type\":\"Native Segwit\"}],\"SOLANA\":{\"address\":\"GBsUNATVVNQYxtJ1rVaEu9WmedA92Ud2bZxPc4zcQPXE\",\"publicKey\":\"e1a82e3e870a015f7eac5b22670100928c23f6e77c97259b9b36455d55f8a999\"},\"ethereum\":{\"publicKey\":\"0x5d2917e700734113c2bc094561f78ca1cffb8a0d23cb3e33b166541364448555c44caf2542a8ae76f8d50676dffbd9f187753751c6c2cb637c2e8738a77de18c\",\"address\":\"0xfa9cce2fe5275fc6169ac432bf884b12d65e51f4\"},\"KASPA\":{\"address\":\"kaspa:qp8wxl8q2t2qzqpr9xt9umlajzxa8r4teuwwnnvaathf2vcy2q477tzr8lrfs\",\"publicKey\":\"4ee37ce052d401002329965e6ffd908dd38eabcf1ce9cd9deaee953304502bef\"},\"fractalBitcoin\":[{\"type\":\"Taproot\",\"compressedPublicKey\":\"03e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\",\"address\":\"bc1pkrxjzvy5ev86qq57yw4pyf9zzztgmgw92lje4f37hkn063ed7frsp2z6lm\",\"publicKey\":\"e7fcd8c88fa52c0be6a1d7375661465a0ccf2f998fbf2640d41e6c04cc2d5317\"},{\"compressedPublicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\",\"address\":\"15pFqLxrif5H6wGWgRs6hjBVnECRrJmgg6\",\"type\":\"Legacy\",\"publicKey\":\"03b5458d94134c72d8744261c95e4146c8a554c335b94bd57e81cc77606391d27f\"},{\"compressedPublicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\",\"type\":\"Nested Segwit\",\"publicKey\":\"02a0c2643af33b7edb780fb472c4001fb05cdf62d4aaa70be16dd62d8e445ea6a7\",\"address\":\"3Pd9T9Ls43V9e7eNWedK4bUcUbNafXC9Wv\"},{\"publicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\",\"type\":\"Native Segwit\",\"address\":\"bc1qf4zp2cenu32k4ffdt0k3y60rcn2fl4d2ty3u2m\",\"compressedPublicKey\":\"035427b4c047a7064628728b2a035adb820935c060ecfc0a56cc26a333fd8c2d3e\"}]}533536344133363634333532533536344133363634353336SHD1970579e2e412413786s2fnect")));
	}
}
