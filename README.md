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
    
