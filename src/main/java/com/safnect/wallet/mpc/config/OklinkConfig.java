package com.safnect.wallet.mpc.config;

public class OklinkConfig {
	
	public static final String ENDPOINT = "https://www.oklink.com/api/v5/explorer/";

	static final String[] KEY_ARR = new String[] {
		"07015457-9aac-4f22-bf09-83c92af62a75",
		"e3d4c8e4-7be1-4656-bb56-be5e07a1bb01",
		"84471fec-db53-420a-8771-182fff3dfbb7",
		"a783397c-92ad-4c05-b725-316733ccf152",
		"b5af6f83-4f1c-4abb-be42-075ccdad371a",
		"b34d30f4-2ac5-4223-bd7b-ec3cab6278ba",
		"a30b45c4-b35f-49ec-ad1f-47dc0870795b"
	};
	
	static int index = 0;
	
	public static String getKey() {
		if (index >= KEY_ARR.length) {
			index = 0;
		}
		return KEY_ARR[index ++];
	}
}
/*
[
    {
        "chainFullName": "Goerli Testnet",
        "chainShortName": "GOERLI_TESTNET",
        "symbol": "ETH",
        "lastHeight": "10666686",
        "lastBlockTime": "1710786960000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": ""
    },
    {
        "chainFullName": "EthereumPoW",
        "chainShortName": "ETHW",
        "symbol": "ETHW",
        "lastHeight": "20871703",
        "lastBlockTime": "1734236976000",
        "circulatingSupply": "107818999.05",
        "circulatingSupplyProportion": "1",
        "transactions": "1764106907"
    },
    {
        "chainFullName": "Sui",
        "chainShortName": "SUI",
        "symbol": "SUI",
        "lastHeight": "25716918",
        "lastBlockTime": "1707408042031",
        "circulatingSupply": "2927660018.56",
        "circulatingSupplyProportion": "0.292766001856",
        "transactions": "2802601539"
    },
    {
        "chainFullName": "Kaia",
        "chainShortName": "KAIA",
        "symbol": "KLAY",
        "lastHeight": "171910798",
        "lastBlockTime": "1733925805000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "DIS CHAIN",
        "chainShortName": "DIS",
        "symbol": "DIS",
        "lastHeight": "21009641",
        "lastBlockTime": "1734236953000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": "1661950767"
    },
    {
        "chainFullName": "zkSync Era",
        "chainShortName": "ZKSYNC",
        "symbol": "ETH",
        "lastHeight": "51326494",
        "lastBlockTime": "1734236979000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "435114236"
    },
    {
        "chainFullName": "Gravity Alpha Mainnet",
        "chainShortName": "GRAVITY",
        "symbol": "G",
        "lastHeight": "24709461",
        "lastBlockTime": "1734236984000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Dogecoin",
        "chainShortName": "DOGE",
        "symbol": "DOGE",
        "lastHeight": "5503330",
        "lastBlockTime": "1734236912000",
        "circulatingSupply": "147205286383.71",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Ethereum",
        "chainShortName": "ETH",
        "symbol": "ETH",
        "lastHeight": "21405540",
        "lastBlockTime": "1734236975000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "2618499873"
    },
    {
        "chainFullName": "Gnosis",
        "chainShortName": "GNOSIS",
        "symbol": "xDAI",
        "lastHeight": "37533051",
        "lastBlockTime": "1734236980000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Litecoin",
        "chainShortName": "LTC",
        "symbol": "LTC",
        "lastHeight": "2809513",
        "lastBlockTime": "1734236912000",
        "circulatingSupply": "75307255.8",
        "circulatingSupplyProportion": "0.8965",
        "transactions": "295515256"
    },
    {
        "chainFullName": "Fantom",
        "chainShortName": "FTM",
        "symbol": "FTM",
        "lastHeight": "100071204",
        "lastBlockTime": "1734236979000",
        "circulatingSupply": "2803634835.53",
        "circulatingSupplyProportion": "",
        "transactions": "575487476"
    },
    {
        "chainFullName": "BNB Chain",
        "chainShortName": "BSC",
        "symbol": "BNB",
        "lastHeight": "44885334",
        "lastBlockTime": "1734236981000",
        "circulatingSupply": "144008361.19",
        "circulatingSupplyProportion": "1",
        "transactions": "3584346166"
    },
    {
        "chainFullName": "Fractal Bitcoin Mainnet",
        "chainShortName": "FRACTAL",
        "symbol": "FB",
        "lastHeight": "281093",
        "lastBlockTime": "1734236967000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Polygon",
        "chainShortName": "POLYGON",
        "symbol": "POL",
        "lastHeight": "65498878",
        "lastBlockTime": "1734236981000",
        "circulatingSupply": "8362808808.56",
        "circulatingSupplyProportion": "",
        "transactions": "4705944491"
    },
    {
        "chainFullName": "Sepolia Testnet",
        "chainShortName": "SEPOLIA_TESTNET",
        "symbol": "ETH",
        "lastHeight": "7281732",
        "lastBlockTime": "1734236952000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": ""
    },
    {
        "chainFullName": "Ronin",
        "chainShortName": "RONIN",
        "symbol": "RON",
        "lastHeight": "40795958",
        "lastBlockTime": "1734236983000",
        "circulatingSupply": "370186524.43",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Bsquared Mainnet",
        "chainShortName": "B2",
        "symbol": "BTC",
        "lastHeight": "10536598",
        "lastBlockTime": "1734236982000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Aptos",
        "chainShortName": "APT",
        "symbol": "APT",
        "lastHeight": "264971591",
        "lastBlockTime": "1734236979407",
        "circulatingSupply": "554962166.87",
        "circulatingSupplyProportion": "0",
        "transactions": "1752009128"
    },
    {
        "chainFullName": "OKT Chain",
        "chainShortName": "OKTC",
        "symbol": "OKT",
        "lastHeight": "32619282",
        "lastBlockTime": "1734236975000",
        "circulatingSupply": "20701246.34",
        "circulatingSupplyProportion": "0.9857736352380952",
        "transactions": "221761133"
    },
    {
        "chainFullName": "Bitcoin Cash",
        "chainShortName": "BCH",
        "symbol": "BCH",
        "lastHeight": "876650",
        "lastBlockTime": "1734236241000",
        "circulatingSupply": "19802009.38",
        "circulatingSupplyProportion": "0.9429",
        "transactions": "404563337"
    },
    {
        "chainFullName": "Avalanche-C",
        "chainShortName": "AVAXC",
        "symbol": "AVAX",
        "lastHeight": "54364504",
        "lastBlockTime": "1734236982000",
        "circulatingSupply": "409695771.64",
        "circulatingSupplyProportion": "",
        "transactions": "481274665"
    },
    {
        "chainFullName": "Bitcoin",
        "chainShortName": "BTC",
        "symbol": "BTC",
        "lastHeight": "874819",
        "lastBlockTime": "1734235480000",
        "circulatingSupply": "19796178",
        "circulatingSupplyProportion": "0.9427",
        "transactions": "1132018994"
    },
    {
        "chainFullName": "DASH",
        "chainShortName": "DASH",
        "symbol": "DASH",
        "lastHeight": "2188807",
        "lastBlockTime": "1734236618000",
        "circulatingSupply": "12066754.55",
        "circulatingSupplyProportion": "0.6405",
        "transactions": "55722352"
    },
    {
        "chainFullName": "Ethereum Classic",
        "chainShortName": "ETC",
        "symbol": "ETC",
        "lastHeight": "21277152",
        "lastBlockTime": "1734236955000",
        "circulatingSupply": "149948828.44",
        "circulatingSupplyProportion": "1",
        "transactions": "132745290"
    },
    {
        "chainFullName": "POLYGON_ZKEVM Testnet",
        "chainShortName": "POLYGON_ZKEVM_TESTNET",
        "symbol": "ETH",
        "lastHeight": "4308422",
        "lastBlockTime": "1712226114000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": "4307974.0"
    },
    {
        "chainFullName": "X Layer",
        "chainShortName": "XLAYER",
        "symbol": "OKB",
        "lastHeight": "7430433",
        "lastBlockTime": "1734236978000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": "7080369"
    },
    {
        "chainFullName": "Arbitrum One",
        "chainShortName": "ARBITRUM",
        "symbol": "ETH",
        "lastHeight": "284896519",
        "lastBlockTime": "1734236983000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "1098375824"
    },
    {
        "chainFullName": "Bitlayer Mainnet",
        "chainShortName": "BITLAYER",
        "symbol": "BTC",
        "lastHeight": "7320116",
        "lastBlockTime": "1734236981000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "BOB Mainnet",
        "chainShortName": "BOB",
        "symbol": "ETH",
        "lastHeight": "10687498",
        "lastBlockTime": "1734236983000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Mumbai Testnet",
        "chainShortName": "MUMBAI_TESTNET",
        "symbol": "MATIC",
        "lastHeight": "47002303",
        "lastBlockTime": "1710344198000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": ""
    },
    {
        "chainFullName": "BLAST",
        "chainShortName": "BLAST",
        "symbol": "ETH",
        "lastHeight": "12713580",
        "lastBlockTime": "1734236975000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Manta Pacific",
        "chainShortName": "MANTA",
        "symbol": "ETH",
        "lastHeight": "4001302",
        "lastBlockTime": "1734236979000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "82671080"
    },
    {
        "chainFullName": "OP Mainnet",
        "chainShortName": "OP",
        "symbol": "ETH",
        "lastHeight": "129319098",
        "lastBlockTime": "1734236973000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "415736962"
    },
    {
        "chainFullName": "Duckchain Mainnet",
        "chainShortName": "DUCKCHAIN",
        "symbol": "TON",
        "lastHeight": "3819409",
        "lastBlockTime": "1734236984000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "opBNB Mainnet",
        "chainShortName": "OPBNB",
        "symbol": "BNB",
        "lastHeight": "42483259",
        "lastBlockTime": "1734236982000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": "2345548113"
    },
    {
        "chainFullName": "ApeChain",
        "chainShortName": "APE",
        "symbol": "APE",
        "lastHeight": "6689789",
        "lastBlockTime": "1734236977000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "X Layer Testnet",
        "chainShortName": "XLAYER_TESTNET",
        "symbol": "OKB",
        "lastHeight": "20300166",
        "lastBlockTime": "1734236979000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": "37332098"
    },
    {
        "chainFullName": "POLYGON_ZKEVM",
        "chainShortName": "POLYGON_ZKEVM",
        "symbol": "ETH",
        "lastHeight": "18362459",
        "lastBlockTime": "1734236973000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "14390774"
    },
    {
        "chainFullName": "Amoy Testnet",
        "chainShortName": "AMOY_TESTNET",
        "symbol": "POL",
        "lastHeight": "15619754",
        "lastBlockTime": "1734236981000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": ""
    },
    {
        "chainFullName": "Omega",
        "chainShortName": "OMEGA",
        "symbol": "OMN",
        "lastHeight": "16013729",
        "lastBlockTime": "1734236983000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "SCROLL",
        "chainShortName": "SCROLL",
        "symbol": "ETH",
        "lastHeight": "11922474",
        "lastBlockTime": "1734236978000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "106912838"
    },
    {
        "chainFullName": "CANTO",
        "chainShortName": "CANTO",
        "symbol": "CANTO",
        "lastHeight": "12727798",
        "lastBlockTime": "1734236974000",
        "circulatingSupply": "608465729.9",
        "circulatingSupplyProportion": "0.6084657299",
        "transactions": "11044087"
    },
    {
        "chainFullName": "BEVM Mainnet",
        "chainShortName": "BEVM",
        "symbol": "BTC",
        "lastHeight": "4018896",
        "lastBlockTime": "1734236982000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "BASE",
        "chainShortName": "BASE",
        "symbol": "ETH",
        "lastHeight": "23723814",
        "lastBlockTime": "1734236975000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "1273264274"
    },
    {
        "chainFullName": "LINEA",
        "chainShortName": "LINEA",
        "symbol": "ETH",
        "lastHeight": "13328933",
        "lastBlockTime": "1734236979000",
        "circulatingSupply": "120448515.53",
        "circulatingSupplyProportion": "1",
        "transactions": "2618503349"
    },
    {
        "chainFullName": "KAVA",
        "chainShortName": "KAVA",
        "symbol": "KAVA",
        "lastHeight": "13090907",
        "lastBlockTime": "1734236960000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": "55359569"
    },
    {
        "chainFullName": "COSMOS HUB",
        "chainShortName": "COSMOS",
        "symbol": "ATOM",
        "lastHeight": "23517345",
        "lastBlockTime": "1734236959000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": "75417641"
    },
    {
        "chainFullName": "EVMOS",
        "chainShortName": "EVMOS",
        "symbol": "EVMOS",
        "lastHeight": "22763499",
        "lastBlockTime": "1723475557000",
        "circulatingSupply": "",
        "circulatingSupplyProportion": "",
        "transactions": ""
    },
    {
        "chainFullName": "Solana",
        "chainShortName": "SOLANA",
        "symbol": "SOL",
        "lastHeight": "285932623",
        "lastBlockTime": "1734236979000",
        "circulatingSupply": "479040735.02",
        "circulatingSupplyProportion": "0.81",
        "transactions": "348369045565"
    }
]
*/
