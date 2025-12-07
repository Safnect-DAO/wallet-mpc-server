package com.safnect.wallet.mpc.controller.chain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.chain.ChainMapper;
import com.safnect.wallet.mpc.mapper.chain.NetworkMapper;
import com.safnect.wallet.mpc.model.chain.Chain;
import com.safnect.wallet.mpc.model.chain.Network;

@RestController
@RequestMapping("network")
public class NetworkController {

	@Autowired
	NetworkMapper networkMapper;
	
	@Autowired
	ChainMapper chainMapper;
	
	@GetMapping("get")
	public ResponseModel get(String walletId) {
		Network network = new Network();
		network.setVisible(true);
		List<Network> networkList = this.networkMapper.select(network);
		
		List<Chain> chainList = this.chainMapper.getChain(null);
		chainList.forEach(item -> {
			networkList.add(new Network("EVM", item.getIcon(), item.getName(), item.getSymbol(), true, true, true, false, 
				true, false, 0.00000001, 0.00000001, 99, item.getDecimals(), item.getRpcUrl(), item.getChainId(), item.getRestApi(), 
				false, 0.000001, item.getExplorerUrl() + "/tx/${txid}", ""));
		});
		
		return ResponseModel.successData(networkList);
	}
}
