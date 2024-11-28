# Safnect钱包服务端

  服务端使用Java语言编写，为Safnect私钥分片存储和获取提供外部API接口（服务端不保存用户密码和完整私钥）。

  ![恢复钱包](https://github.com/Safnect-DAO/web3-js-sdk/blob/main/restore.jpg)

## Endpoint

  地址：https://server.safnect.com  http://35.240.161.157（备用）


## 协议

  ```
  Request Content-type: x-www-form-urlencoded
  header: 
    key: token
    value: sha1(base64(param1.value+param2.value+param3.value+KEY))
  ```
  KEY参数不需要设置，在Safnect js会自动添加，参数拼接顺序是参数名称的ascii排列正序，如下示例：
  
  `name=Aresei, id=33, age=18`
  
  拼接后为:1833Aresei
  
  最后通过Safnect.js中的base64和Sha1加密函数计算得出Token
  
  `var token = Safnect.sha1(Safnect.base64Encode('1833Aresei'));`
  
  ```
  Response Content-type: application-json

  {"code":200,"msg":null,"data":null}

  {"code":601,"msg":"Missing parameters","data":null}
  ```
  code 200 代表请求成功，500未知异常，601缺少必填参数，602无效的参数。

  msg 提示信息

  data 业务数据

  
## API清单

### 1、钱包登记

  ```
  path: /extensions/signup
  Method: POST
  parameters:
    walletId  
    publicKey
    pkSharding
  ```

  入参：
  
    walletId 钱包Id
    
    publicKey 用户密码生成的公钥 （见Safnect.js库)
    
    pkSharding 私钥（区块链的私钥）分片
    

  响应：
  
    成功 `{"code":200,"msg":null,"data":null}`

  
### 2、验证分片（获取私钥分片）

  ```
  path: /extensions/sharding-verify
  Method: POST
  parameters:
    walletId  
    randomStr
    signatureHex
  ```

  入参：
  
    walletId 钱包Id
    
    randomStr 用户密码签名的随机字符串 （见Safnect.js库)
    
    signatureHex 用户密码签名得到的16进制摘要 （见Safnect.js库)

  响应：
  
    成功 `{"code":200,"msg":null,"data":"1111111111111111111111111111111111111111111111"}`
    data中的数据为钱包私钥分片。
    
### 3、摘要验证（此接口与2、验证分片实现逻辑相同，不返回私钥分片）

  ```
  path: /extensions/signature-verify
  Method: POST
  parameters:
    walletId  
    randomStr
    signatureHex
  ```

  入参：
  
    walletId 钱包Id
    
    randomStr 用户密码签名的随机字符串 （见Safnect.js库)
    
    signatureHex 用户密码签名得到的16进制摘要 （见Safnect.js库)

  响应：
  
    成功 `{"code":200,"msg":null,"data":null}`


### 4、更新公钥

  用户修改个人密码后，需要重新派生公钥，并将公钥上传至服务端更新  

  ```
  path: /extensions/pk-update
  Method: POST
  parameters:
    walletId  
    publicKey
  ```

  入参：
  
    walletId 钱包Id
    
    publicKey 用户密码生成的公钥 （见Safnect.js库)

  响应：
  
    成功 `{"code":200,"msg":null,"data":null}`

### 5、交易成功

  交易发送成功后记录交易信息，向服务端发送交易信息数据。  

  ```
  path: /trans/success
  Method: POST
  parameters:
    chain  链名称，如：Bitcoin, Runes
    network 网络取值：testnet | mainnet
    sendAddress 发送地址
    toAddress 接收地址
    walletId  钱包id
    tokenName Token，取值：BTC|ETH|SOL|符文ID
    transHex 交易16进制数据，00001EADCD313832...
    amount 转账额度
    gasFee GasFee
    totalAmount 总额度


    txid 交易id（交易发送成功取得）
  ```

  响应：
  
    成功 `{"code":200,"msg":null,"data":null}`


### 6、交易失败

  交易发送失败后记录交易信息，向服务端发送交易信息数据。  

  ```
  path: /trans/failure
  Method: POST
  parameters:
    chain  链名称，如：Bitcoin, Runes
    network 网络取值：testnet | mainnet
    sendAddress 发送地址
    toAddress 接收地址
    walletId  钱包id
    tokenName Token，取值：BTC|ETH|SOL|符文ID
    transHex 交易16进制数据，00001EADCD313832...
    amount 转账额度
    gasFee GasFee
    totalAmount 总额度


    exInfo 异常信息（交易发送失败时返回的信息）
  ```

  响应：
  
    成功 `{"code":200,"msg":null,"data":null}`

### 7、查询实时币价

  当前支持这些ethereum,bitcoin,solana,litecoin,dogecoin,conflux,arbitrum,Filecoin币的市场实时价格返回，该接口缓存5秒数据。

  ```
  path: /fetch-data/price
  Method: GET
  parameters:
    chainNames 可选，token名称，多个使用“,”（英文逗号）拼接，示例值：bitcoin,solana,litecoin
  ```

  响应：
  
    成功 
    ```
    {
        "code": 200,
        "msg": null,
        "data": {
            "arbitrum": {  // ARB
                "usd": 0.789265,
                "usd_24h_change": 1.5101157941386123
            },
            "bitcoin": { // 比特币
                "usd": 68064,
                "usd_24h_change": 2.150894883462686
            },
            "dogecoin": { // 狗狗币
                "usd": 0.140385,
                "usd_24h_change": 7.084696337373682
            },
            "ethereum": {  // 这是以太的实时价格
                "usd": 3500.92,
                "usd_24h_change": 0.28830383613273364
            },
            "filecoin": { // FIL
                "usd": 4.66,
                "usd_24h_change": 1.2911342229613854
            }, 
            "litecoin": { // 莱特
                "usd": 73.46,
                "usd_24h_change": 0.39121018732592916
            },
            "solana": { // 索拉娜
                "usd": 181.67,
                "usd_24h_change": 5.627780654708391
            },
            "cat": { // cat
                "usd": 1.32122323,
                "usd_24h_change": 15.2838911212
            }
        }
    }
    ```

### 7-1、查询实时币价V2

    查询速度更快

    ```
    path: /fetch-data/v2/price
    Method: GET
    parameters:
      无
    ```
  
    响应：
    
    成功 
    ```
    {
      "code": 200,
      "msg": null,
      "data": {
          "ethereum": {
              "usd": 2688.15,
              "usd_24h_change": 2.54
          },
          "fb": {
              "usd": 14.04,
              "usd_24h_change": 3.46
          },
          "bitcoin": {
              "usd": 66320.3,
              "usd_24h_change": 2.66
          },
          "c468e99ac3b533e503eac5ccf4f0e3362772f80cead8b7f71d802305d02f73d0_0": { // 代表token20协议的代币，对应tokenId
              "usd": 0.5200174739671446,
              "usd_24h_change": 0.0587369086164037
          },
          "45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0": {
              "usd": 1.7137035005831767,
              "usd_24h_change": 0.1614705216208915
          },
          "cat": {
              "usd": 1.7137035005831767,
              "usd_24h_change": 0.1614705216208915
          },
          "cc1b4c7e844c8a7163e0fccb79a9ade20b0793a2e86647825b7c05e8002b9f6a_0": {
              "usd": 0.04933882838896821,
              "usd_24h_change": 0.0183191979924502
          },
          "a004b19b6e52aba25546360acc11e8b650c2387a975c9f48ff74a8bd5f6c32e7_0": {
              "usd": 0.0,
              "usd_24h_change": 0.0
          },
          "34475c0c600acf665737ef4c8d97bade02e9c5472bcfc0be141184e244d7daaf_0": {
              "usd": 0.0,
              "usd_24h_change": 1.062895868977297
          },
          "59d566844f434e419bf5b21b5c601745fcaaa24482b8d68f32b2582c61a95af2_0": {
              "usd": 0.043899617972850524,
              "usd_24h_change": 0.2239769987338117
          },
          "usdt": {
              "usd": 1.0,
              "usd_24h_change": 0.0
          }
      }
    }
    ```
### 8、查询ETH币余额

  查询ETH币余额，接口数据缓存30秒有效期

  ```
  path: /fetch-data/eth-balance
  Method: GET
  parameters:
    network 网络取值：testnet | mainnet
    address 钱包地址
  ```

  响应：
    {"code":200,"msg":null,"data": "4.838577283"}


### 9、获取FB-Cat20协议资产列表

  获取FB链的Cat20协议下资产列表，仅返回地址中余额大于0的代币和余额的列表

  ```
  path: /fetch-data/cat20-balances
  Method: GET
  parameters:
    network 网络取值：testnet | mainnet
    address 钱包地址
  ```

  响应：
  
  ```
  {
    "code": 200,
    "msg": null,
    "data": [
      {
        "tokenId": "0005dd65d6378004c2c9b7b12206e67ec56721c5e1d1413a9dc596d1faf566f8_0", // tokenId 转账时需要的参数
        "confirmed": "2100000", // 余额
        "minterAddr": "bc1pe23u8zdymhk85w5apd2gv86yjelpc9h3rmll5m9n0gjlxurz2d7s0ken7z", // 转账时需要的参数
        "tokenAddr": "bc1pataut94dx0ecdcy3cxf9k9r8v47p627jr7e5drt9z47rus7w8q6sasxvrm",  // 转账时需要的参数
        "name": "PizzaSwap", // 代币名称
        "symbol": "PizzaSwap", // 代币符号 
        "decimals": 0 // 最小精度单位，可使用Math.pow函数转换余额
      },
      {
        "tokenId": "59d566844f434e419bf5b21b5c601745fcaaa24482b8d68f32b2582c61a95af2_0",
        "confirmed": "725000",
        "minterAddr": "bc1pt6e0vse643qwzg6x06r8c42rm425avz4ymlap738530hwtj8pc0snkk0lx",
        "tokenAddr": "bc1pesk2868wvfrq4pjwwxumjrg2x0ddauu3vs996vqkz9lpmvvt0mzqcz6yln",
        "name": "cat20_pizza",
        "symbol": "CAT20_PIZZA",
        "decimals": 2
      },
      {
        "tokenId": "577a614cd2c9f376c68959c594e17d6c9159116df3377c2c52abe23bc30755ae_0",
        "confirmed": "2900000",
        "minterAddr": "bc1ptugpm5nnm4wxgeu4ndlcvuuy5jj5wc3xch6hlqtxjy7xr6y2wjcs8espcv",
        "tokenAddr": "bc1p7yfc8c6ruxwsplc9pg7jng9egfkam6kuufj4d0dxfjnrgvxq8wmskhlz7y",
        "name": "BTC",
        "symbol": "BTC",
        "decimals": 2
      }
    ]
  }
  ```

### 10、获取钱包地址交易txid

  获取钱包地址最近50笔交易的txid列表

  ```
  path: /trans/txids?address={address}
  Method: GET
  parameters:
    address 钱包地址
  ```

  响应：

  ```
  {
    "code": 200,
    "msg": null,
    "data": [
      "72e823c25d2b9993ba1c2166320c889c86d52c2a2923d351d41938cbcfdc62eb",
      "05d02e0836a914590d5d84ba3c0bfcbd210670829afb8e0f1a2b4ad45edbeb59",
      "f9249682916997772daf41b496041bd8048f3e59d8f6dd41c6e2554afb653e4f",
      "3fc9f5a150bbce5ff0c3e2f7b43af4bc6f697a0cb51613f6ca51df683ac74c3b",
      ...
    ]
  }
  ```

### 11、上报Psbt签名记录

  Dapp通过Provider服务发起signPsbt请求时，插件将数据发送至服务器记录，用于统计Dapp使用Safnect钱包的次数。

  ```
  path: /report/psbt
  Method: POST
  parameters:
    address - String 钱包地址
    chain - String 公链名称
    psbtHex - String psbt数据（16进制字符串）
    sourceDomain - String 来源域名，如（ordbit.com，unisat.io）
  ```

  Response：

  ```
  {
    "code": 200,
    "msg": null,
    "data": null
  }
  ```
### 12、推特活动

  #### 12.1 检查是否可参与

  检查钱包地址是否已参考活动（已经提交了贴子链接）防止一个钱包地址多次参与活动。

  ```
  path: /marketing-activity/address-check
  Method: POST
  parameters:
    address - String 钱包地址
  ```

  Response：

  ```
  {
    "code": 200,
    "msg": null,
    "data": true // true表示可以参加（还未提交过），false表示已经参加过
  }
  ```

  #### 12.2 检查贴子地址

  检查x贴子的地址格式是否正确，贴子是否已经被使用过（已提交到系统）。

  ```
  path: /marketing-activity/post-check
  Method: POST
  parameters:
    postLink - String 贴子链接
  ```

  Response：

  ```
  {
    "code": 200, // 200表示检查通过（有效的贴子链接），610 表示链接格式错误（不是推特的贴子链接），612 表示贴子链接已经提交到系统了（重复提交）
    "msg": null,
    "data": null
  }
  ```

  #### 12.3 提交贴子链接

  提交贴子链接，接口也会对钱包地址与贴子链接再次进行检查，但不对贴子内容检查，提交后

  ```
  path: /marketing-activity/post-submit
  Method: POST
  parameters:
    postLink - String 贴子链接
    walletId - String 钱包id
    address - String 钱包地址
  ```

  Response：

  ```
  {
    "code": 200, // 200表示检查通过（有效的贴子链接），610 表示链接格式错误（不是推特的贴子链接），611 表示钱包地址已经参考过活动 612 表示贴子链接已经提交到系统了（重复提交）
    "msg": null,
    "data": null
  }
  ```
