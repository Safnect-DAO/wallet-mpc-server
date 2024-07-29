# Safnect钱包服务端

  服务端使用Java语言编写，为Safnect私钥分片存储和获取提供外部API接口（服务端不保存用户密码和完整私钥）。

  ![恢复钱包](https://github.com/Safnect-DAO/web3-js-sdk/blob/main/restore.jpg)

## Endpoint

  测试环境：http://35.240.161.157/


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


    ex_info 异常信息（交易发送失败时返回的信息）
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
    
    
