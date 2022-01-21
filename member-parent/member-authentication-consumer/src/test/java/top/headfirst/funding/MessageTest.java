package top.headfirst.funding;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aliyun.api.gateway.demo.util.HttpUtils;
import top.headfirst.funding.api.RedisRemoteService;
import top.headfirst.funding.constant.FundingConstant;
import top.headfirst.funding.util.ResultEntity;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageTest {

    @Autowired
    private RedisRemoteService redisRemoteService;

    private Logger logger = LoggerFactory.getLogger(MessageTest.class);

    @Test
    public void testSendMessage(){
        // 接口调用的 url 地址
        String host = "https://dfsns.market.alicloudapi.com";
        // 具体发送短信功能的地址
        String path = "/data/send_sms";
        // 请求方式
        String method = "POST";
        String appcode = "2f23a9a920d14ab69fcc21c214f4f76a";
        String code = null;
        String phone_number = null;
        String template_id = "TPL_0000";

        // 生成验证码
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            int random = (int) (Math.random() * 10);
            builder.append(random);
        }

        code = builder.toString();
        phone_number = "15207176491";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        // 要发送的验证码，也就是模板中会变化的部分
        bodys.put("content", "code:" + code);
        // 收短信的手机号
        bodys.put("phone_number", phone_number);
        // 模板编号
        bodys.put("template_id", template_id);

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            String reasonPhrase = statusLine.getReasonPhrase();

            logger.info("statusCode = " + statusCode);
            logger.info("reasonPhrase = " + reasonPhrase);

            // System.out.println(response.toString());
            // 如不输出json, 请打开这行代码，打印调试头部状态码。
            //  获取response的body
            logger.info(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRedisCode(){
        String phone_number = "";
        String key = FundingConstant.REDIS_CODE_PREFIX + phone_number;
        ResultEntity<String> resultEntity = redisRemoteService.getRedisStringValueByKeyRemote(key);
        String result = resultEntity.getResult();
        String redisCode = resultEntity.getData();

        System.out.println(key);
        System.out.println(result);
        System.out.println(redisCode);
        logger.debug(key);
        logger.debug(result);
        logger.debug(redisCode);
//        REDIS_CODE_PREFIX_
//        SUCCESS
//        1379
    }
}
