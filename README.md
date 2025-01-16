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

### 1、钱包登记（废弃）

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

### 账户管理

#### 钱包登记V2 （插件）

```
path: /extensions/v2/signup
Method: POST
parameters:
  walletId - String 钱包ID
  publicKey - String 公钥
  pkSharding - String 分片
  addressJson - String 所有的钱包地址Json格式字符串（JS库SFKey.getAllAddr(mnemonic)函数返回）
  shardType - Number 分片类型，1：旧的，2：新版助记词，3：新版私钥
  alias - String 钱包别名（没有传此参数截取walletId后X位）
```

响应：

  成功 `{"code":200,"msg":null,"data":null}`

#### 钱包登记 （App）

用户创建钱包走完一系列流程（刷卡片，设置密码）操作完成后调用本接口记录钱包属性状态的数据。

```
path: /mobile-app/signup
Method: POST
parameters:
  walletId - String 钱包ID（通过Safnect.min.js生成）
  publicKey - String 用户密码的PublicKey[Option]
  walletAlias - String 钱包名称[Option]
  cardSn1 - String 卡片1序号
  cardSn2 - String 卡片2序号
  cardSn3 - String 卡片3序号 [Option]
  accountAlias - String 子账户名称[Option]
  addressJson - String 所有的钱包地址Json格式字符串（JS库SFKey.getAllAddr(mnemonic)函数返回）
```

Response：

```
{
  "code": 200, 
  "msg": null,
  "data": null
}
```

#### 添加子账户（创建账户）

用户在钱包中创建新的子账户时调用该接口记录用户的子账户编号，在恢复钱包时可以从服务器取子账户编号恢复所有的子账户。

```
path: /mobile-app/account-add
Method: POST
parameters:
  walletId - String 钱包ID
  accountIndex - Number 子账户索引编号（从0开始增长，每创建一个子账户增长步长为1，需在本地缓存，子账户编号是获取子账户多链钱包地址、私钥的关键参数）
  alias - String 子账户名称[Option]
```

Response：

```
成功：
{
  "code": 200,
  "msg": null,
  "data": null
}

失败1：
{
  "code": 601, // 必填参数为空
  "msg": "Missing parameters",
  "data": null
}

失败2：
{
  "code": 603, // 子账户已经存在
  "msg": "Already exists",
  "data": null
}
```

#### 更新钱包

```
path: /wallet/update
Method: POST
parameters:
  walletId - String 钱包ID
  alias - String 钱包名称
```

响应：

  成功 `{"code":200,"msg":null,"data":null}`

#### 更新子账户

```
path: /wallet/account-update
Method: POST
parameters:
  walletId - String 钱包ID
  alias - String 子账户名称
  accountIndex - Number 子账户索引
```

响应：

  成功 `{"code":200,"msg":null,"data":null}`

#### 获取钱包

```
path: /wallet/get
Method: GET
parameters:
  walletId - String 钱包ID
```

响应：

```
{
    "code": 200,
    "msg": null,
    "data": {
        "walletId": "d11",
        "publicKey": null,
        "pkSharding": null,
        "sourceApp": "E",
        "alias": "Areseiqq", // 钱包名称
        "accountIndex": 0,
        "shardType": 2,
        "type": 1,
        "createDatetime": 1736565190000,
        "waList": [
            {
                "walletId": "d11",
                "accountIndex": 0, // 账户索引
                "alias": "Aresei Account", // 子账户名称
                "createDatetime": 1736565190000
            }
        ]
    }
}
```

### 批量获取钱包

```
path: /wallet/get-all
Method: GET
parameters:
  walletIds - String 钱包ID
```

响应：

```
{
  "code": 200,
  "msg": null,
  "data": [
    {
      "walletId": "S1945ee344b2-5181499",
      "publicKey": null,
      "pkSharding": null,
      "sourceApp": "E",
      "alias": "5181499",
      "accountIndex": 1,
      "shardType": 2,
      "type": 1,
      "createDatetime": 1736729941000,
      "waList": [
        {
          "walletId": "S1945ee344b2-5181499",
          "accountIndex": 0,
          "alias": "Account 01",
          "createDatetime": 1736729941000
        },
        {
          "walletId": "S1945ee344b2-5181499",
          "accountIndex": 1,
          "alias": "Account 2",
          "createDatetime": 1736729952000
        }
      ]
    },
    {
      "walletId": "S1945ee87bd9-87420",
      "publicKey": null,
      "pkSharding": null,
      "sourceApp": "E",
      "alias": "87420",
      "accountIndex": 1,
      "shardType": 2,
      "type": 1,
      "createDatetime": 1736730283000,
      "waList": [
        {
          "walletId": "S1945ee87bd9-87420",
          "accountIndex": 0,
          "alias": "Account 01",
          "createDatetime": 1736730283000
        },
        {
          "walletId": "S1945ee87bd9-87420",
          "accountIndex": 1,
          "alias": "Account 2",
          "createDatetime": 1736730323000
        }
      ]
    }
  ]
}
```
  
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
    chain  链名称，如：Bitcoin | Fractal Bitcoin | Ethereum | BSC | BELL | TRON | TON | SOLANA | LITECOIN | DOGECOIN | BCH | BSV ...
    network 网络取值：testnet | mainnet
    sendAddress 发送地址
    toAddress 接收地址
    walletId  钱包id
    tokenName Token名称
    transHex 交易16进制数据，00001EADCD313832...
    amount 转账额度
    gasFee GasFee
    totalAmount 总额度
    contractAddress token合约地址（代币转账时传入）[Option]
    txid 交易id（交易发送成功取得）
    symbol 代币符号 取值：BTC | ETH | FB | BNB | TRX | BEL | WETH | USDT | USDC | ONE | HBTC | OKT | DAI | PEPE ...
  ```

  响应：
  
    成功 `{"code":200,"msg":null,"data":null}`


### 6、交易失败

  交易发送失败后记录交易信息，向服务端发送交易信息数据。  

  ```
  path: /trans/failure
  Method: POST
  parameters:
    chain 链名称，如：Bitcoin | Fractal Bitcoin | Ethereum | BSC | BELL | TRON | TON | SOLANA | LITECOIN | DOGECOIN | BCH | BSV ...
    network 网络取值：testnet | mainnet
    sendAddress 发送地址
    toAddress 接收地址
    walletId  钱包id
    tokenName Token名称
    transHex 交易16进制数据，00001EADCD313832...
    amount 转账额度
    gasFee GasFee
    totalAmount 总额度
    contractAddress token合约地址（代币转账时传入）[Option]
    symbol 代币符号 取值：BTC | ETH | FB | BNB | TRX | BEL | WETH | USDT | USDC | ONE | HBTC | OKT | DAI | PEPE ...
    exInfo 异常信息（交易发送失败时返回的信息）
  ```

  响应：
  
    成功 `{"code":200,"msg":null,"data":null}`

### 7、查询实时币价

  该接口缓存5秒数据。

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

    查询速度更快，接口数据缓存时间5秒。

    支持币种如下（匹配币种信息的小写symbol。16进制长名称为Cat-20 Token，硬件钱包可忽略。）：

```
    1 | ethereum | fb | bitcoin | bellcoin | ewt | mtg | digg | gui | brick | flm | astro | enj3l | vmpx | 4ever | cre8 | spurs | bitboard | win | lqty | leo | trx3l | pts | strk | memerune | ctr | share | naos | kas3l | zbcn | mlp | mbl | rss3 | toke | metfi | buz | nibi | wnxm | egs | sys | sol | rpl | niifi | loot | lvn | taiko | eth5s | root | basex | hydra | labs | arpa3l | slrs | mkr3s | bankbrc | stos | plcu | supr | mot | phtr | gvr | dogin | pepecoin | xtag | zerozero | mmm | smartgame | pyth | cake | link3l | batch | epx | luffy | lus | trg | kon | not | vinu | xion | airdrop | tate | blur3s | senso | credit | whale | nub | xen | monky | notai | cname | zkx | say | obi | ddos | wrx | hpos10i | mis | rena | pcx | gxa | manta | every | hifi | vdt | ai | fomo | fic | onston | aave | ghc | w3gg | ggm | lsd | blin | polc | osmo | pzp | blok | strm | iota | openplatform | tusd | redo | hdv | xec3l | agld | sovrn | up | earn | msol | doge3s | gme | param | uma | carat | port | bome3s | defiland | opa | slp3l | patex | bio | opn1 | vext | pepe3l | xdb | blast | mons | cycon | eywa | o4dx | kaia | rlc | leash | ceur | zent | cere | punkai | hook | source | ortb | slp | rbn | order | aaa | kimchi | ride | iost | mobile | ar | crv | befe | trace | bath | kasta | revu | opium | mpc | ess | pvu | bubble | swarms | voya | farm | asm | lci | nbs | honey | xzc | slice | hotcross | udo | cti | ovr | eeg | nstr | tia3s | optimus | wsienna | oas | 20ex | skl | xgn | amp | alt | edg | aie | mong | wzm | mix | neiro | wbird | unfi | fire | wsdm | ogv | hibs | x | carv | neo | galfan | tio | foxy | elon | hxd | iq | oort | gvt | dome | ttt | link5s | fil3s | atom3l | tnsr | xzk | sti | etc3l | dmc | rim | jerry | urus | xtz | u2u | xpet | arks | xai | anml | crpt | trkx | klo | was | sion | luna3l | zec | mtl3s | cfx3s | lbl | nrn | kif | shib5l | lsd7 | alphr | gpt | kft | hsc | bch5l | safe | nest | velo | tari | swan | vic | nuls | sgb | trcl | esd | morpho | masa | ant | rtf | cudos | orbk | xlm3l | fud | ht | tokau | aiagent | revoland | umee | nebl | krl | orbi | nxd | sccp | bkn | safereum | rndx | mog | gala | logx | sats3l | fin | xpower | tita | kton | lime | crh | real | hlg | tonc | pearl | aped | pooh | stix | snm | fnf | propstoken | looks | ateam | xmr3s | ss20 | bzrx | taocat | frr | well | sail | lbr | redtoken | nlk | cho | cir | sashimi | naws | coti3l | orn | bri | spi | myria | eternal | berry | tia3l | pmon | sonar | hopr | taur | grokceo | zro | clanker | tshp | btc5s | loa | nap | zeus | bmi | nerd | andr | apn | flux | kube | okb3l | starl | apu | befi | kiba | wmt | mcash | mhunt | lemn | rly | ags | zinu | sandwich | sei3l | looks3s | posi | wlkn | kilt | zkj | strk3s | watt | fdusd | nals | tele | p00ls | ioen | hmstr | weeth | woo3l | fil3l | swash | duck2 | pgn | zkf | sclp | omni | koma | klv | wgrt | benqi | bsv | kai1 | dsync | bsw | pol | ong | cart | wag | xrp5l | opn | dydx | ctg | strk3l | lowb | tools | ethw | hxa | smurfcat | yfx | slm | polyx | skya | mars4 | tross | kas3s | ing | fra | auto | ltc5l | sync | czz | btmv1 | pcnt | lndx | nux | ferc | strong | eps | lrc | fame | ram | kan | atomarc | wing | bn | bat3s | gst3l | pax | lazycat | aisc | loop | deso | crbn | aln | hint | art | ole | bico | usde | bchsv | gamestop | link | rsv | mgkl | srm | cfx3l | six | okt | tama | realm | btcst | shdw | gov | akt | alpha | ada | plena | heart | eosbull | vrjam | dgb | aca | maps | pro | atd | alpine | ezswap | aixbt | diver | nom | zulu | kub | fst | egld | cookprotocol | hao | ach3l | atem | aidoge | mana3s | crp | cst | atr | lamb | night | care | doge | saito | gmmt | grt3l | spx | happy | btt | snap | pbtc35a | gorilla | pepe2 | erg | aimx | bake | miladycult | msu | rbls | quack | duck | pogai | tra | zex | cup | biop | golc | bls | rem | bzz3l | medusa | pnut3l | legion | op3l | drep | stardoge | janet | id | algo | 3ull | lkr | f3 | 10set | ctk | ape | shopnext | ladyf | onx | mobi | furucombo | deep | bcha3l | mql | ata | matic3l | von | ont3s | shr | aibb | poly | liquidus | walv | arca | dows | sfi | major | xseed | scr | acl | nuum | shib5s | slf | naft | btm3l | boba | mon | gmrx | poolx | vice | pbr | ghst | xch3s | gasdao | wbcd | snft1 | akv | kt | sbtc | grnd | npxs | xmark | cos | ecox | bond | vana | eth | xil | insc | xfi | hive | runecoin | bin | pols | uds | oklp | soul | evry | raca | hdro | meto | snpt | boson | creo | xcur | rbt | reach | velar | dis | tct | pump | hord | blade | shping | mina3l | cvx | konet | ace1 | ray3s | aurora | unn | ztg | bot | rbc | tomi | vega | ready | yf-dai | wings | lrc3l | axs5s | cwar | wen | idoodles | fourcoin | dhb | zrx | ethbear | arb | 0dog | ares | nexd | qbx | aergo | bora | loulou | bnt | iost3s | people3s | syn | ion | 3km | fight | bxc | bsw3s | omn | ftrb | tapt | hnb | dfi | teva | firo | gdao | xpll | htm | dlc | inv | rmrk | vtg | celo | fakeai | planet | bcd | phone | stt | yooshi | ihc | ena3s | orbs | ddim | iq50 | duko | byn | bscs | afc | neuro | gft | bch3l | mana | rating | xor | vr | ocn | usual | revo | smg | jensol | spume | el | avaai | skrt | 00 | frokai | msq | ondo | blaze | pera | w | dek | prism | neo3s | cats | ava | wnz | zbc | mina3s | om | evo | razor | props | kim | mask | fast | atlasdex | moz | knot | rdn | lemd | smole | wpay | cati | swftc | navi | mode | mother | snek | munity | propc | mart | miggles | ast | lavaswap | ton3l | bananas31 | eth3l | sd | gear | cream | ulu | btc3l | cryptofi | insn | pandora | ordi | bonus | cstr | pmg | kfc | astra | wif3l | sei3s | lnr | dadi | dogs | ena3l | iagon | bcug | sun | nfty | eth3s | ar3l | vader | cro | rice | cvc3l | dogi | mnemo | goat | sn | quick | lfw | pgc | vrt | kcal | boe | crts | gto | taproot | saas | gmat | gls | primal | avc | fida | axl | azero | popk | early | scf | elec | asd | pyusd | nuts | btg | aave3s | vee | crv3l | unq | muse | zec3s | cad | maneki | neiroeth | dot | lina | net | cfx | knj | uni3l | fpft | astr3s | tia | sdm | fdc | mts | box | breed | bu | bonded | eqx | crf | room | pika | petoshi | chr | pyme | etc3s | souls | nexg | mapo | viz | medx | shi | arn | ccar | cu | pundu | hrt | canto | mo | bnty | aura | vemp | tapprotocol | jam | life | usdn | neon | caga | ctc | hgpt | axs5l | asr | bxh | hapi | aog | occ | zil | vp | mir | revv | friend | patriot | kicks | rare | hget | fei | cqt | any | toma | combo | inj | gmm | rndr3s | iotx | deri | kok | ordi3s | dfnd | move | dsla | sonic | tpt | woo3s | efi | rdnt | metax | xrpbull | lion | thales | tbft | rari | iqt | uft | zelix | egame | ort | imx3l | lon | pace | ainn | gspi | pdex | zat | unio | bto | e4c | seele | hades | xrpbear | salt | mesa | yourai | pumlx | ramp | titan | rcn | atp | wat | sauce | arc | shib3l | fred | zec3l | front | gaypepe | agility | biso | kst | wld | vsx | filda | amc | ckb | lky | roomv2 | ach | bal3s | pros | lbk | cec | meld | catgold | hard | por | rjv | wfai | santos | city | yamv2 | bad | andy | gitcoin | slc | gas | bear | nektar | form | storm | lss | ref | bbq | neer | pig | yfdai | me | pi | tru | hsr | vra | fdt | mgg | pbx | apys | eliza | yesp | aly | hippop | blum | snx | toshi | umb | mine | ctt | pbux | ringai | xnl | bag | dnx | cgpt | cbl | lvly | stz | inter | ape3l | zerolend | mtl3l | abbc | bsv3l | dxgm | mooney | acm | moma | cgg | impt | bbf | mbox | juld | env | xvg | arpa3s | wmtx | blt | islm | open | ht3s | sca | rgame | wallet | kma | btf | pivx | ulti | loon | mooo | sns | gari | ligo | pix | sundog | near3s | rats | pet | nodl | cdt | zcx | cphr | sps | doge2 | supra | fps | chng | senc | c983s | cpan | zks | y8u | fsn | liquidium | jnt | xels | mao | gth | water | rizzmas | btc | jenner | pirate | wanko | lazio | defibox | mat | ame | psi | wwy | ted | kaia3l | rwa | bcdn | zeep | skm | ohm | deai | trb | dbr | hpb | swh | glmr3s | ipunks | beamx | unt | shx | bendog | metag | keep | xpress | base | mtl | dos | xar | k21 | idex | qash | ioi | nii | kingshib | aiswap | xmc | pha | arch | nftx | cela | cvaultcore | war | itsb | ntrn | gan | glizzy | fbx | tune | aleo | catch | sushi3l | lsk | ustc | ogpu | dlta | dafi | raze | nlc | vatreni | ghx | xeta | geta | logt | ragdoll | spellfire | ithaca | lgx | nrfb | empire | cfg | hod | wne | vci | cube | powr | naym | ornj | delfi | lba | joy | geod | tribe3l | pride | lmwr | aipump | gnx | srp | kct | caw | csas | ooe | sdn | champ | trvl | nbot | sb | hyve | mdt | nano | thep | agla | klay3l | people | ons | rune3s | movr | ever | prq | app | lunarlens | grin3s | ghny | arkm | duel | live | uni5s | sui3s | dgd | trumpcoin | osky | gitc | dal | arty | dili | amkt | prmx | tip | atolo | dfl | pokt | cheems | stox | pica | rite | flow | 1earth | clips | kai | aave3l | smx | skai | hsk | bigtime | nyan | xtz3s | ar3s | shiro | wiken | dc | eos | htx | num | pin | bsw3l | metis | mga | zsc | sunny | zcn | anc | unistake | doge5s | nu3s | sudo | keep3s | skt | g3 | corn | rei | kava | suku | olympus | cgv | pol3s | high | mice | betu | reign | nfm | cofix | thl | cs | prom | imayc | geek | wlth | btl | pendle | sols | zlk | sui3l | moxie | eva | purge | dfa | stg | fcon | fx | tara | cloud | xlm | vai | shib | dagx | psub | grin | bsv5s | ali | ttk | apepe | decentralized | kaby | grow | ftr | tst | svt | cgpu | snft | pop | drift | truf | mnt | suter | ost | doggo | fear | dia | para | mcrt | noob | prx | catheon | ton3s | peaq | snt | hmx | spo | dent | synt | maro | mnde | ruby | smt | catcoin | ethbull | iost3l | feg | goldencat | xcv | hoppy | zoon | pch | brise | escc | icp | forex | yop | yfii3s | ada3s | kick | dpet | orfy | raca3l | iht | sin | fncy | ssnc | saros | ltc5s | invite | bert | 1inch | elf | nord | omnia | enj3s | beam3l | gmwagmi | dope | acx | rose3s | alpa | pvp | bcn | emon | nct | knine | imx | noia | atm | etc | og | nu3l | nyzo | rin | xy | corgiai | nfe | lpool | arsw | awt | csix | gbpt | dust | poli | wozx | over | sfm | ens | babi | crt | eos5s | wx | lobo | qubic | tmai | eths | xwg | nblu | srg | zlw | vdr | purr | byin | fun | chz | dop | vela | gomd | altair | devt | libre | second | bacon | ibfk | zen | lmr | hbar | sats3s | taboo | uos | theta3s | chz3s | core | eurt | ams | wsi | roobee | perc | llt | kex | beam | artfi | doggy | rgt | bao | vlx | hnt | aki | ncn | hsf | cetus | scnsol | doai | v | goz | asto | bcha | link3s | go4 | cspr | ven | sake | bome | mdao | fyn | swgt | ald | aifun | icx | dune | mad | sqt | ads | suia | 4token | durev | ngl | flock | ipor | comp3s | ena | gtai | ooki | sugarb | mar3 | puff | ox | neo3l | flt | mudai | sushi3s | solo | mubi | bicity | ldo | four | bifif | fnsa | matic3s | mnry | trump | wxt | gomining | mv | gmee | oax | rfd | vsp | sov | orbr | nrv | lingo | mfer | cult | coq | flash | cusd | wsb | edu | mlk | ron | arcx | zerc | wusd | coti | ords | cro3s | bebe | bags | isk | yct | easy | pst | nu | agg | rfr | sero | ftt3s | altb | tlos | btcbear | egon | wzrd | apt3s | ars | abys | spay | tsuka | wsg | ppt | npc | hfun | pizabrc | sand3s | ski | sft | ipad | holdstation | sdex | bondly | paal | pepe3s | fly | rep | dock | dogedash | wagmigames | testtest10 | ath | fil | aiepk | dimo | cyber3s | mfootball | ait | aitech | bonk | cvc3s | clv | avax3l | hct | puffer | pft | aic | stbu | pnut | beam3s | crs | sponge | vda | ranker | mngo | artem | stchain | busd | wista | srt | mepad | band | gofx | vet3l | qlc | shibai | cel | liq | babybnb | rbtc | cow | agi | rektcoin | grail | one3s | ibayc | tnc | omg3s | bly | ladys | alpha3l | rifsol | moni | talent | mit | mxc | aevo | mco | pond | bitcat | wicc | sero3l | api3 | enno | ruff | trx3s | navx | iris | bal | 5ire | lunai | rune3l | vmt | ltc3l | mnz | dydx3l | gal3s | trala | lpt | satx | sxp | skl3l | looks3l | gat | sai | vrtx | els | xpnet | ust | steth | auction | ptc | wom | pnt | bdt | blankv2 | smty | sfty | smart | hot | theos | mith | dora | una | bs | next | omg3l | babyelon | paf | dck | buck | prcl | king | bp | alex | syno | hamster | slim | algo3s | meli | job | ejs | ol | coral | tai | stepg | badger | spec | woof | stsol | brct | zam | ilv | derp | waves | squid | ink | cbab | alice | pepebrc | dogemob | chkn | shft | coai | link5l | atlas | ztx | pool | ents | mft | fton | drops | gmx | kava3s | nas | ogn | totm | mini | fuel | tbe | shib2 | grin3l | cheq | lend | goal | timechrono | wex | zrc | mpi | nax | avalox | theta3l | btmx | vvs | lavita | atom | matrix | troll | gala3s | fwog | free | slg | seal | mkr3l | bst | loka3l | spell | memetoon | maha | aero | ygg | etn | pps | turbo | sbr | sylo | bts | vvaifu | op | oin | io | celt | mist | kmon | vv | kava3l | lrc3s | domi | uni5l | jst3l | bone | frin | glmr3l | woo | ht3l | kiteai | tsugt | naka | dgi | dark | ada3l | itrump | dnxc | elys | mavia | dio | glq | credi | uro | roa | pixfi | qnt | gno | dka | star | dg | obot | the | key | stx | babybonk | paw | shark | anc3s | kar | ocean | layer | sushi | gse | ivpay | seam | pixel | ultima | pot | bnsx | psg | pepper | vgx | xym | multi | beefi | raca3s | hbar3l | algo3l | pixelverse | gof | xch3l | cxt | cru | gxs | dehub | qtum | ip3 | lofi | max | gal | dcr | sop | memebrc | dvp | mmss | waves3s | pingo | dzoo | cvtx | thn | lrds | pundix | zix | mtos | seilor | steem | snk | gt | sqd | bry | kono | frbk | ont3l | cvp | trio | galo | vt | vet3s | pay | times | mrch | knft | teer | xt | hoge | man | grok | 3ac | barsik | icp3l | dot3s | portx | c983l | mswap | tet | grass | sei | moodengeth | vso | lyx | grt | eigen | merl | zkl | cell | skeb | rose | maga | music | zpt | vanry | runi | lm | gel | bal3l | gmt | loom | eth5l | qkc | red | ksm3l | scrt | ehash | stc | cspr3l | iron | dym | troy | roco | coreum | own | slk | yfv | tt | 1art | yfi3s | gold | volt | safemars | fti | runesx | btrst | pengu | keep3l | saitama | heu | ethfi | koko | swingby | off | lumia | eth2 | caps | her | gala5s | ginux | qsp | fis | dao | kip | ark | sos | wndr | mtr | sand3l | agrs | lcat | htr | cvc | drgn | dot5s | ousd | svl | sfp | loka | alice3s | cens | landshare | busy | df | catgpt | bees | 100x | dana | never | xtta | bor | dvi | lava | hmtt | hav | lemo | cbk | insp | peng | token | flx | bas | ag | eden | id3l | dmck | dorkl | joe | ccd | amb | fearnot | gtc | enj | doge3l | hns | blockasset | godl | gaia | safemoon | derc | tos | hana | avail | nvir | porto | lym | mdus | bells | omg | mtv | atom3s | degod | earnm | ban | squidgrow | spex | prare | acent | qbt | gmpd | lki | bdx | ksm | cros | pickle | klay3s | grt3s | wod | itgr | agri | kws | rekt | req | dx | rndr | nexo | gem | mdx | love | eul | jst3s | op3s | shrub | home | ola | kalm | nos | elu | brawl | tfuel | kat | kzen | ltc3s | burp | sakai | pack | argon | sui | clore | beans | bch5s | sfg | zone | place | ctrl | lrn | adapad | cybro | bifi | ham | smileai | baju | karate | glmr | dash3s | alch | lblock | iag | crown | dyp | dusk | fevr | kishu | alita | rose3l | ksm3s | ita | zzz | storj | for | xem | kine | ping | str | mcl | idea | mmt | psy | xrd | lunch | zen3s | sgr | tulip | push | vno | carbon | opul | ktn | apt | chz3l | ovo | trac | ctok | vent | bscpad | d2t | whypad | cspr3s | ppad | doe | ball | bsvbrc | l3p | wtc | mta | fora | mkr | rvc | cys | ton | taki | factr | twitfi | airtnt | l3 | fof | chax | cofi | eosbear | olv | bnx | deus | ray | dino | haha | mdf | arbi | hibiki | dog | orion | mc | acn | mua | comp | sct | t | kos | xec | wemix | qorpo | lev | asw | cro3l | klay | ice | srm3s | xec3s | gemai | onit | hero | forth | sdao | blendr | matter | sku | qanx | mln | issp | island | eclip | uno | fcd | doge5l | moo | wsm | spirit | stream | yam | mpl | btm | pyr | tcp | unit0 | bel | why | dhx | api33l | bome3l | noot | nift | des | hai | swop | aart | azur | nftfi | egg | lon3s | raft | muratiai | ez | lends | psb | jobai | autos | orca | zee | slp3s | arpa | blank | zaar | dxct | kas | wbtc | mana3l | atk | venom | sal | portal | zodi | gal3l | zap | ftn | lite | slnd | govi | asmatch | qi | cake3l | noa | xna | dmail | snow | mm | maf | armor | mer | juice | abt | ordi3l | brt | mplx | png | srm3l | zerebro | avt | jasmy3s | qtum3s | chex | vera | nym | conv | cobe | mengo | cwif | joystream | myt | pnk | wampl | spfc | gout | ogc | chat | cal | mp | bzz | mbx | mph | tomo | far | rad | netmind | flurry | akro | stik | soph | chain | mimir | math | aprs | bwld | orb | f2c | savg | axs3s | nett | fuse | larix | utk | nkn | hbar3s | mtd | thg | txt | oshi | mars | qtc | michi | boa | crv3s | cyberarena | ankr | swp | 1inch3s | pias | astr | dognft | aicode | tidal | snet | cook | fil6 | upi | insur | bcp | games | o3 | beer | xrwa | bkc | hln | basebearcute | bcx | twt | arb3l | brwl | near3l | black | ponke | fartcoin | trx | wbt | kpad | gmt3s | ops | blur | axs3l | pizza | mtrg | trias | supe | lyxe | aqdc | dogeswap | qtcon | btcs | aicell | gog | prt | teddy | mds | gst | rosn | coti3s | brkl | saga | ltc | oland | dash3l | fxs | majo | ubxs | goats | vara | 1on8 | dar | anavi | bacgames | ctrc | uld | clot | mojo | exvg | msi | aix | shill | chess | locg | drx | gns | bvt | yfii3l | angle | ome | goatseus | wojak | comp3l | mew | nbt | beyond | glm | mmpro | knc | alaya | fort | egld3l | senate | msn | gst3s | ftm3l | arnx | alcx | iazuki | baby | sparklet | tbtc | eosdac | copi | zkb | sfil | aax | nbp | degen | adf | kyl | audio | ampl3s | sqr | mirx | brett | ace | rif | g | dfy | arv | jto | sats | itamcube | umx | dash | mape | gyen | smtx | ratio | f | sharbi | milo | arrow | ad | dai | boring | mls | data | debt | kyve | cocos | pigcoin | grv | gnt | hippo | rex | oxt | gef | zeta | giga | mofi | xprt | myra | tap | hera | frog | rfuel | usdp | fnz | rvn | act | xdc | klima | rndr3l | bnb | smile | dydx3s | land | bonebone | tips | pip | depo | ae | hmt | smh | usbt | peoplefb | atoz | dot5l | drink | match | store | bsv3s | hip | caf | swrv | chicks | mbs | lai | ampl | magic | wif3s | shopx | orai | octo | ctsi | wit | oly | frax | hid | bepro | strp | bbl | oro | vidyx | sol3s | sidus | toms | jup | qtum3l | soon | mask3s | ftt3l | muc | ltd | evadore | scrat | lay3r | kekius | xrp5s | natix | tlm | agent | dcrn | alu | fiwa | watersol | baked | sc | bch | cweb | nsbt | ionx | rft | snift | rltm | ln | frm | fiu | turbos | vet | apx | fodl | juc | step | pew | kbox | nai | bgt | kima | ml | apt3l | alpaca | cat | andyeth | sweat | pando | icons | nwc | am | gq | joc | pai | pork | stpt | akita | xrp3l | turt | cateeth | air | dfyn | aqt | spa | bobo | mask3l | gf | ern | pym | mojito | shoe | cmp | xend | intr | alpha3s | rbnt | yozi | flr | woop | kgc | jasmy | space | soil | epk | gdt | ray3l | gzone | bcut | ns | oct | gala5l | sero3s | bat3l | celr | value | omi | well3 | jpg | crvusd | aark | opai | bcmc | suip | game2 | xrp3s | fan | tox | renbtc | lever | radar | bomb | waxl | b3x | kuma | fine | sol3l | aioz | adx | mob | ais | cher | zmt | dmr | craft | palm | catdog | bfc | elix | dtec | moon | myro | cover | stmx | item | gm | tem | vrx | goldminer | ceek | ledu | flip | kp3r | cheel | sand | rock | fitfi3s | aleph | rsc | memefi | fitfi3l | god | plspad | gigs | wifi | waves3l | nim | dmtr | juv | lit3l | prime | mean | koai | zig | api33s | xchng | uni | gard | landwolf | rdf | cpool | blue | vxt | xmon | yield | sln | arb3s | dsd | dv | tok | chapz | nftd | nsdx | ttc | tvk | lith | ai16z | ren | nftl | dms | dag | tribe3s | mda | cnns | 1cat | chillguy | brcst | cws | mana3 | spot | single | pol3l | gcoin | hech | moca | cyrus | puggy | sswp | ggg | void | bles | oxy | starheroes | stake | aptr | nada | skyrim | isp | btcbull | wnd | ring | pla | kbd | fyde | usdcmb | xdefi | kint | bxn | icp3s | sauber | zen3l | xcad | orc | xyo | ldo3l | mpt | lynx | grbe | mlt | wear | rifampicin | peipei | zk | eos5l | izi | piza | foxsy | ctx | metal | ampl3l | bbc | xch | dose | yin | lista | yfi | bank | bft | slerf | ask | bbt | vsc | xaut | rsr | scy | griffain | tenet | bnb3s | polk | loka3s | dogegov | kmno | ssv | karrat | dione | nif | arrr | luna3s | ats | mav | eq | grap | azy | ans | waxp | pstake | hegic | he | hivp | voxel | ete | rpk | talk | avax3s | udao | agb | yfii | ful | reelt | xnft | husd | jum | ape3s | bld | kin | catgirl | igu | swch | babydoge | gmt3l | vista | com | cty | kro | wam | ember | dodo | babyneiro | skill | strax | arg | egld3s | aga | like | cyber | cookie | blur3l | wncg | west | toncoin | swell | game | kap | pumpfun | dot3l | paid | pumpai | scarcity | orng | desci | galax | niko | elizasol | isky | upc | stn | neirocto | social | rage | myth | lon3l | ssx | dbc | bft1 | lat | mgpt | dpy | virtual | kart | mina | samo | farmland | toko | popcat | squad | liq1 | fury | don | xr | a5t | btc3s | velodrome | sersh | wild | cyber3l | nfp | c98 | movez | onc | aag | axis | autism | micro | sis | aprt | flokiceo | ela | stage | tdrop | axs | gum | fer | bgsc | tx20 | 1dollar | lgcy | phm | btc5l | skl3s | lun | egp | eml | slnv2 | usdc | vtho | gafi | bb | gamevirtual | hype | xet | niza | jgn | zil3s | doga | visr | rez | mcg | epik | tnt | ssg | sao | dhv | eos3s | gxe | dapp | gngl | mblk | indi | brn | billy | dpr | lit | tko | gala3l | wuf | clh | marsh | ethf | near | polydoge | bnc | sfund | ala | dom | kaia3s | sq3 | rune | perp | drac | dego | xtz3l | somm | hyco | lwa | bdp | gull | rook | ont | lunc | route | bnb3l | babygrok | xrune | defi | btm3s | moodeng | ese | cards | mcrn | emt | fet | bird | luna | rfox | etha | snx3l | roost | astr3l | psl | torn | sphri | etherparty | nvg | sny | uni3s | alice3l | rdex | qrdo | hello | sup | bdin | dmlg | qwan | bitci | octa | one | fox | send | pepe | blocx | bcha3s | adel | swap | xmr | uncommongoods | veri | imx3s | zil3l | bmon | fitfi | mny | mtn | intx | bully | ach3s | pnut3s | baobao | oddz | gs | mak | cre | meme | pit | clnd | bac | jst | lovely | tribe | unibot | floki | type | yfi3l | trade | xlm3s | time | bzz3s | gfi | bvm | orao | cummies | pnl | ufo | seat | hold | bc | mgt | swo | rect | ddd | tsl | via | trr | klaus | xed | port3 | rfrm | hc | pwar | syrup | npt | wxm | bongo | xcn | one3l | zf | mstar | sntr | tifi | lto | med | 88mph | anc3l | vc | jfi | bat | moov | atrs | block | scihub | tracai | snx3s | exrd | t23 | bd20 | vidy | benji | cirus | people3l | esg | elt | bpt | acs | vlxpad | uxlink | meowcat | wrt | boo | lien | satoshi | bamboo | wolf | chatai | bit | 1inch3l | ll | aeg | nftb | l1 | fio | pog | klap | buy | alph | polypad | obt | vts | lit3s | gob | play | wagyu | motg | bbank | wif | ass | lunr | asi | dechat | olt | ldo3s | okb | ftt | adp | gamestarter | daddy | xswap | ufi | mnw | dcb | gems | pkf | nsure | mco2 | ftm3s | uncx | nmt | skop | bccoin | strump | sway | pie | xava | a8 | dexe | emc | sxp3l | theta | savm | srk | yld | eos3l | met | poolz | fxf | ctp | opcat | cta | blz | metaldr | work | neat | xpla | bch3s | amazingteam | render | gens | go | mvl | igt | suiai | idv | usdd | niftsy | plato | hft | okb3s | avax | infra | rai | shib3s | usdg | zeum | super | clo | agix | coval | cate | perl | stnd | loe | psp | zkt | nmr | copycat | moda | undead | pine | dep | baseptl | phb | xrp | polyhedra | tao | raca4 | urolithina | bmex | jasmy3l | nft | amu | sxp3s | mint | xvs | susd | swth | wkc | biao | reap | kda | bsv5l | hit | cfi | evmos | xpr | vpr | white | banana | gec | knight | solr | matic | phil | zero | metan | ftm | falcons | zbu | reef | id3s | polis | cake3s | xmr3l | dks | devve | isme | peri | d9b4268df8aab74cc897e02292db0c1582e986adaf9c2b5296b804c3841e640b_0 | 38c9d877d571ae876f285ca8277c334ee0773d7c2f818f7bfda6899ea2cf833d_0 | 71f36fd43344f085b39413c1c7172bdbe712ef1995124add6fd1e6f626eaa481_0 | 816f112a565c28c0aabcc942afd073e799aef41a9d9e6b0a53bb812e34e4346f_0 | d482b3b6b364e276bb67dbf43eeb7d501a3bdabb31b855a2f6a78cb30c6d4bf6_0 | 6478ecb318edc63ed68cd869cadaa7d666556191de436b9d8e6715fb7a496d79_0 | 77ab996ff19fa4453e6ee7b7af55f1fb136030653b6d39928e9033e650160588_0 | 7bc1bdb7aebe4cc1c0209431ff1c1dcca49a95554d88b8bd054bd7b75755a71c_0 | 5b4ab3d424621e20b32a874fb47d36af0a9ebc65d59b93b0f7d85494bd87b6b5_0 | d08c7c847cd14edf0f9ab81a1e60d163dc8683176351ca3158bf2f663893c75f_0 | f6735096053f8e10f220e97ad0384b69c13e61077b5a40e5d65d758b31d9bc3f_0 | 028ae179783cd237f475ca1a58d5c8b3ecec3884862d337971fc168d5e92c16e_0 | 37f582619f870d362e6896a0ff03d9810ca922100e2fe23ec81ce98d74968dc9_0 | fb5ddc212410dbf7cbbc4e86dc37e4178298ddf3f417d3472899f02213318f69_0 | 145d8b18508777607043af8e0461d6880d985ff31223f470ba6da3327fd7d6a2_0 | 0c94c7ee539ba6d917e355928e55771b8c5b4c05421edb9ee932103525fae23c_0 | 34475c0c600acf665737ef4c8d97bade02e9c5472bcfc0be141184e244d7daaf_0 | 0673c7c86f87617e241ab500533382b052329f827cc959f642c7de4f9a6e5fa9_0 | 41d23ba71d444c95aca5c78e3ccb7fb8d4a745311f4c424be482ca5b7894cb64_0 | 3eb13baf45f4d8c0848b90ba49eaf335009dad9c1ddac03c1f817b830ef43c73_0 | 45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0 | ecf8ac3ea80ad1e96509ea4d7228bece1c02cae4ff7d423ab2df2a91ee899db7_0 | 250431e234b6e9a074888ff95beb3afcf9addc8b2602fa3c9406579b8ac31ec5_0 | 1568dc184fc47e6826a58a9927bbbc4372eae0c75b367d41dbc6153ebcc823a3_0 | 4406ec04dbac054687e338de90ba8139990d1386c23472db0e2d4bcf76029c71_0 | 4b0416abc4b9ff3c9c57396559f0d5cbb08ec24806f54c491f0c404bd884f509_0 | 878b57d27cc6c2871db587dcc3d5477fca28edb74fec96fe0bbcd9ecccf3d86e_0 | 3d0c9f3f7bbfb4029907825d3b44b3fe11b54a75e847a7a86cd8352c80b6b050_0 | 7d61a8976e5d6adf034af48271585957d32b74fa73327c0a090840f0b43d20d4_0 | 898c041af66887236f886bc81222c7d6f656f94b145485b2870d2dc493f42b95_0 | f68c5f5659236e0f4f46cff3f3b8e4957c7712848c37ae70267e48a7a6cd1ce1_0 | 2d8f7c56dc50784cfe25e4691c42e1e81ad59920f2f676a1e184647a75cef93b_0 | 64e69024c977138999af98f10a67702c944edd5d8829f8583e379395e290ed3b_0 | 39b37fd119c6ebbeca6a2ce6e48496245d33d3a747a179ae4aa57457e899499b_0 | 5f5d44c9f23216d26ef5e3df6035c46cde8d2a8c4e2d5c6bbcdfd707d5f8b6be_0 | d93955b26ca8538b51fe236a46fe55e043888392a61749448fc8afb345096dda_0 | 19473922dd288a795a781c40b4ecac973b8de705f6979aee6182d7ceaa73b256_0 | 72cf2b8a73647f51464d1b04108a04b2fb56bb2bf9d267c9fbccd1a91ab8f159_0 | a8719a7f32a0d0a22e7dd1997f269535939bda736cc492491270aa2d406baf41_0 | 033524b50705d5a948246b776509d5a1fdbe6d90807b34fc4c63eba6f9ade2c8_0 | 44a11aea72be92958aeee5e3a06c3e470fd2b84cd2aad473d8923ce8b463d9c2_0 | fcd85e257b6acf1e5198e626c450e54bdfa3d2449cbc31d54bf3ab7f866ac5f2_0 | 81eb831bbbc51944b409a731094fef97af1fbc753d95ae5821a71732142e4b51_0 | db0bcaf3efaf3624355d51a51d3f2053b286c803c0197addfabcdf6a1443d6f9_0 | db3d3defa717c26e4213d97b1b3e3c202c82549423695c8ffd87d9c72bf5b5ec_0 | 8b1593c47e414a370651d725dc1bcc6c62938c20ec9124fa32e76c59f6621fa0_0 | 59d566844f434e419bf5b21b5c601745fcaaa24482b8d68f32b2582c61a95af2_0 | ab4e6f860fa81a4ea309fcbb34154e1661f9062bbfec121b97af28293c2c8d72_0 | f673cf56bfb36b189d483fa2565cacbaaf694b921af201175fbda11fdc3164b9_0 | 4988cce8d35744fabe6124d708ac848a53d3dd5f772dab22d5fdca289daf25a8_0 | f29339845daefdcee20a543a66069e79730be8784ef5cdb8cb9ea16b752deb89_0 | 13e1adadc008590785f0a68d95f1f49fc17761b9d7a3bee84e15d9cf53255209_0 | 36f6a5358f4b2e0b25da25f67edfa381cd3decb465f7f78c05e28fd7c6ce79fd_0 | 9c21987275428817df091b20ec8bfeee44988d937c01753c49de9f12d10c3ed9_0 | 879a05fa2ae0ee81edb72110f136b9b9801382c06db01f0d743e02895836ba04_0 | bcf0701b49d2c93b7f22bceed188dab00f79f63ebd86d3e2aa1ffa706830192d_0 | c468e99ac3b533e503eac5ccf4f0e3362772f80cead8b7f71d802305d02f73d0_0 | 7d84f7aa61749152f862385407e94b93f5ae22a38ed4ead80f9380a151b3ccbc_0 | 8297dd8a16198687449bebbb0b420e93278af7bce062b99b0c4c05dfe6e019cf_0 | 305c480a7c9402bf5a20457e1363be7b3c3fb76ef23959cbca49f5373bbb00ab_0 | 3c33fcdb2813b8708b2491e9738e9b0d9427ed3cfee0cc25f28233fac974ad07_0 | 577a614cd2c9f376c68959c594e17d6c9159116df3377c2c52abe23bc30755ae_0 | 32e28115b22abff4cf79c6bf563619404dbbf7df85234ebb6c752fb5dcf1b689_0 | 66524b2aef0dcdb60a20a5e5f49101e3f078fd39bf382a5df7b2853271413faa_0 | e3a9ea32ffac8649297a083d7f198ef9e097191c6f39afdcca0e300a7ea5d8a0_0 | 30d4e067c2598d4b27e15e1947a4b313e40eca4725b8728aad5463396995f4b3_0 | 81192dec223416e66f4b80cbceebbe44114b129454e520f6e7d742b947dd5208_0 | 61b8dd18b759ee551c07e3f0c2ff40f4e80060d413c72c12dd16897b1f84de3f_0 | ab173e387776b80ee1aaf6b481cfd4e59206397375e515f727c77428c0f04557_0 | 7dd460587101e0a624710b001093c75c68ab17647c9d0555afcc03e2d9d41221_0 | 9c78497808d61b825d6168c2aa7befb69ca4b4985add20eb49802df884b1b384_0 | 9360971b4d3ec2c1e5fbda8990802028e91f7c705316a3440ea40220409f74c3_0 | acffd7d18ed4849b432a120b1c88902e50ec6c4c7f113a5903ee8ffeac6a5a93_0 | 5f0269c103284485964d8db6468502eff8a689febba47ecae91e5a813a4e50a9_0 | d1594975888bbd2a0935f0228a0634439436c1a5273690d73f555dc2b0df6a10_0 | f7de78be9577d2dbca45263f7f09d4033d75f8689b18b9edfbc4f1b42be60919_0 | cc1b4c7e844c8a7163e0fccb79a9ade20b0793a2e86647825b7c05e8002b9f6a_0 | 68795833e7a68453579b04a1d26aa5b3350e68f1e8387c28f9c42e828c698cba_0 | 3d76e5dc78d1c65c2c5aa1d6e141607fc946565c62e181784434bee06665e028_0 | 6b779fd5f1bb23882fe5336ffc85e9534edcd947039683a20650d7a39e35b2bb_0 | 4981d9ff330a68ac1d5d768dc2a495b39f1ba8c6af080b00735eb10e268b71f4_0 | usdt
```

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

  #### 12.4 获取活动信息

  获取活动时间、是否已经开始。

  ```
  path: /marketing-activity/2412?address={address}
  Method: GET
  parameters:
    address - String 钱包地址[Option]（用于查询是否是中奖者）
  ```

  Response：

  ```
  {
    "code": 200, 
    "msg": null,
    "data": {
        "beginDatetime": 1732723200000, // 开始时间 （UTC 0时区）
        "started": true, // 活动已经启动
        "img": "https://pbs.twimg.com/media/GdfA0Apa8AARrph?format=jpg&name=small", // 活动图片
        "sourceLink": "https://x.com/safnect",  // 活动教程中推文链接
        "endDatetime": 1733155200000, // 结束时间 （UTC 0时区）,
        "winner": true // 中奖者，true 是，false 否，null 未参与活动。（传递了address参数时返回地址是否是中奖者）
    }
  }
  ```

### 14 OKlink通用API接口

封装了OKlinkAPI查询的通用接口，

```
path: /bca/get
Method: GET
parameters:
  path - String OKLinkAPI路径，示例值：address/address-summary，transaction/token-transaction-detail
  ...
  其他参数参考oklink文档接口的参数
```

Response：

```
success:
{
  "code": 200, 
  "msg": null,
  "data": [
    ... datas
  ]
}
```

```
fail 500:
{
  "code": 500, // 服务器运行时异常
  "msg": "error message",
  "data": null
}
```

```
fail 701:
{
  "code": 701, // API返回的异常
  "msg": "error message",
  "data": null
}
```

### 15 NFT合集接口

#### 15.1 添加合集

添加合集信息

```
path: /coll-info/add
Method: POST
parameters:
  walletId - String 钱包id
  chain - String 链
  network - String 网络（mainnet | testnet）
  name - String 合集名称（长度限制100个字符）
  symbol - String 合集符号（长度限制10个字符）
  description - String 合集描述（长度限制1000个字符）
  address - String 钱包地址
```

Response：

```
success:
{
  "code": 200, 
  "msg": null,
  "data": "A62OEMYPRBNO4J" // 合集ID
}

fail:
{
  "code": 602,  // 601必填参数为空，602字符长度超限
  "msg": null,
  "data": null
}
```


#### 15.2 获取合集列表

获取合集列表，返回所有合集信息

```
path: /coll-info/get
Method: GET
parameters:
  address - String 钱包地址
  chain - String 链
  network - String 网络（mainnet | testnet）
```

Response：

```
success:
{
  "code": 200, 
  "msg": null,
  "data": [
      {
          "id": "A61C0ATSPJ8SY0", // 合集ID
          "chain": "FB",
          "network": "mainnet",
          "name": "qqdd",
          "symbol": "qqddsym",
          "description": "qqdddescription",
          "walletId": "111223213123",
          "createDatetime": 1734451896000,
          "bcCollId": "9244a89acbffa854ed11c87d8ade2aec969c91ce7efe98491afc3410ba6fe17a_0", // 链上的集合ID
          "ismint": false
      }
  ]
}
```

#### 15.3 铸造更新

铸造后更新合集铸造状态

```
path: /coll-info/mint-update
Method: POST
parameters:
  id - String 合集ID
  bcCollId - String 链上的集合ID
```

Response：

```
success:
{
  "code": 200, 
  "msg": null,
  "data": null
}
```

### 16 基础数据

#### 16.1 Token数据

根据网络和公链查询所有Token代币数据，客户端取到数据后可缓存在本地，每次打开App时请求一次，获取失败使用上一次的缓存数据。

```
path: /token-info/all
Method: POST
parameters:
  network - String 取值 testnet | mainnet
  chainName - String 公链名称（可选）
```

Response：

```
success:
{
  "code": 200, 
  "msg": null,
  "data": {
    "ETH": { // 公链标识
      "AGLD": { // 代币Symbol
        "contractAddress": "0x32353A6C91143bfd6C7d363B546e62a9A2489A20", // Token合约地址
        "fullName": "Adventure Gold", // Token全名
        "symbol": "AGLD", // 符号
        "img": "/token/images/lootnft_32.png", // Token符号
        "network": "mainnet", // 网络
        "totalSupply": "77,310,001", // 总供应
        "chainName": "ETH", // 链名称
        "decimals": 18, // 精度（最小单位）
        "sno": 255,
        "chain": 1, // 链ID
        "invisable": false,
        "amount": null,
        "priceUsd": null,
        "valueUsd": null
      },
      "LVM": {
        "contractAddress": "0x5BB15141bb6DeF6d2BafeED8ff84BF889c0C573B",
        "fullName": "LakeView",
        "symbol": "LVM",
        "img": "/token/images/lakeview_32.png",
        "network": "mainnet",
        "totalSupply": "1,000,000,000",
        "chainName": "ETH",
        "decimals": 18,
        "sno": 1102,
        "chain": 1,
        "invisable": false,
        "amount": null,
        "priceUsd": null,
        "valueUsd": null
      }
   }
}
```

### 17 获取历史交易记录

获取钱包的历史交易记录，按时间由近至远排列，支持多个钱包地址查询，可根据钱包地址、Network（testnet | mainnet）、chain（Bitcoin，Fractal Bitcoin，Ethereum...），token合约地址分页查询。

```
path: /trans/get
Method: GET
parameters:
  network - String 取值 testnet | mainnet
  addresses - String 钱包地址（多个以,分隔）
  chain - String 链名称（Bitcoin，Fractal Bitcoin，Ethereum...）【可选】
  contractAddress - String Token合约地址【可选】
  start - Number 分页开始记录数，默认值0【可选】
  limit - Number 分页每页记录行数，默认值20【可选】
```

Response：

```
success:
{
  "code": 200, 
  "msg": null,
  "data": [
    {
      "id": "A66O4IZ0LI1BBG",
      "chain": "Fractal Bitcoin", // 链名称 
      "network": "mainnet", // 网络
      "sendAddress": "bc1px8vatydhg6e6lrs6yz7rcpqx4r4k335vskkmjrd85kz4s9l5547sn42fff", // 发送方地址
      "toAddress": "bc1pzd3qdryjwcpx5sd5a8msf6xaskq0sedc6ud8tl0ruqdmwd7kqmwsadwdh4", // 接收方地址
      "walletId": "S193fd7f58b2-21909809", // 钱包ID
      "tokenName": "CAT-721:Qddd23",  // Token名称
      "transHex": "1496d82191cfa3477e591f8200e16c42dd0ea61d439f53c3451f323ffce8e57c_0:0",
      "amount": "5", // 转账额
      "gasFee": "2476916", // 网络费
      "totalAmount": "-2477249", // 总额
      "contractAddress": null, // 合约地址
      "sendTime": 1735096708000, // 发送时间
      "successed": true, // 发送成功
      "txid": "0xe21870f116e49ef6a2688578357f42b5b66976b6ffcb9111551049641ea3b6e1", // 链上交易Id
      "direction": 0, // 流水类型，1：转入，0：转出
      "confirmed": 1 // 交易是否已确认，1：是，0：否
    },
    {
      "id": "A662I3K3XP4DLI",
      "chain": "Fractal Bitcoin",
      "network": "mainnet",
      "sendAddress": "bc1px8vatydhg6e6lrs6yz7rcpqx4r4k335vskkmjrd85kz4s9l5547sn42fff",
      "toAddress": "bc1pzd3qdryjwcpx5sd5a8msf6xaskq0sedc6ud8tl0ruqdmwd7kqmwsadwdh4",
      "walletId": "S19045e1a9ed-15438365",
      "tokenName": "CAT-721:EECC",
      "transHex": "21ba7d6838d5c45c54645fcb5817ef8eccca540ca18e50614e217397d0aa9fc9_0:0",
      "amount": "2",
      "gasFee": "127376",
      "totalAmount": "-127709",
      "contractAddress": null,
      "sendTime": 1735017650000,
      "successed": true,
      "txid": "0xe21870f116e49ef6a2688578357f42b5b66976b6ffcb9111551049641ea3b6e1",
      "direction": 0,
      "confirmed": 0 
    }
  ]
}
```

### 18 上报钱包地址

在打开App、首页刷新、打开历史交易记录页面时，调用次接口上报钱包地址，服务器根据当前地址抓取链上的交易记录。

```
path: /report/address
Method: POST
parameters:
  network - String 取值 testnet | mainnet
  walletId - String 钱包ID
  accountIndex - Number 子账户号
  addressJson - String 所有的钱包地址Json格式字符串（JS库SFKey.getAllAddr(mnemonic)函数返回）
```

Response：

```
success:
{
  "code": 200, 
  "msg": null,
  "data": null
}
```

### 19 获取币列表

获取币列表数据，加载主界面币种列表数据。

```
path: /bca/coins
Method: GET
parameters:
  network - String 取值 testnet | mainnet
```

Response：

```
success:
{
  "code": 200,
  "msg": null,
  "data": [
    {
      "id": "A6EH37MW5ZE29O",
      "symbol": "BTC", // 币符号
      "icon": "https://storage.googleapis.com/safnect/icons/btc.png",  // 图标
      "network": "mainnet", // 网络
      "sno": 1, // 序号（列表已按正序排列）
      "chainId": 0,
      "visible": true 
    },
    {
      "id": "A6EH37MWMKQRK4",
      "symbol": "ETH",
      "icon": "https://storage.googleapis.com/safnect/icons/eth.png",
      "network": "mainnet",
      "sno": 2,
      "chainId": 1,
      "visible": true
    },
    {
      "id": "A6EH37MWMKRUU0",
      "symbol": "FB",
      "icon": "https://storage.googleapis.com/safnect/icons/fb.png",
      "network": "mainnet",
      "sno": 5,
      "chainId": 70000061,
      "visible": true
    },
    {
      "id": "A6EH37MWMKSVHG",
      "symbol": "BELL",
      "icon": "https://storage.googleapis.com/safnect/icons/bells.png",
      "network": "mainnet",
      "sno": 9,
      "chainId": null,
      "visible": true
    }
  ]
}
```
