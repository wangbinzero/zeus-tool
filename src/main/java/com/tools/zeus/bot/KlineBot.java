package com.tools.zeus.bot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.tools.zeus.entity.KlineDO;
import com.tools.zeus.service.KlineService;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KlineBot {

    private static final Logger LOG = LoggerFactory.getLogger(KlineBot.class);

    @Autowired
    private KlineService klineService;

    @Value("${huobi.global.url}")
    private String apiUrl;

    @Value("${huobi.symbol}")
    private String[] symbolList;

    @Value("${huobi.kline}")
    private String[] klineType;

    public void klineWork() throws InterruptedException {
        for (int i = 0; i < symbolList.length; i++) {
            for (int j = 0; j < klineType.length; j++) {
                StringBuilder sb = new StringBuilder(apiUrl);
                final String symbol = symbolList[i];
                final String period = klineType[j];
                sb.append("market/history/kline")
                        .append("?symbol=")
                        .append(symbol)
                        .append("&period=")
                        .append(period)
                        .append("&size=")
                        .append(2000);
                OkHttpClient client = new OkHttpClient.Builder().build();
                final Request request = new Request.Builder().url(sb.toString()).build();

                client.newCall(request).enqueue(new Callback() {
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        LOG.error("请求K线异常: symbol:[{}] period:[{}]", symbol, period);
                    }

                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String result = response.body().string();
                        LOG.info("请求K线成功: [{}]", result);
                        JSONObject json = JSON.parseObject(result);
                        writeMongo(json);
                    }
                });
                Thread.sleep(10000);
            }
        }
    }


    public void writeMongo(JSONObject json) {
        String ch = json.getString("ch");
        String[] str = ch.split("\\.");
        String symbol = str[1];
        String kType = str[3];
        String pair = symbol.split("usdt")[0].toUpperCase() + "/USDT";

        JSONArray jsonArray = json.getJSONArray("data");

        LOG.info("打印 array size:[{}]", jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = (JSONObject) jsonArray.get(i);
            LOG.info("打印元素: [{}], [{}]", ch, item.toString());
            KlineDO klineDO = JSON.parseObject(item.toString(), KlineDO.class);
            klineDO.setSymbol(symbol);
            klineDO.setkType(kType);
            klineDO.setType("kline");
            klineDO.setPair(pair);
            klineDO.setkTime(Integer.valueOf(klineDO.getId()));
            klineDO.setId(symbol + klineDO.getId() + kType);
            WriteResult result = klineService.updateKline(klineDO);
            //LOG.info("打印执行结果:[{}]",result.toString());
        }
//        jsonArray.stream().forEach(item -> {
//            LOG.info("打印元素: [{}], [{}]",ch, item.toString());
//            KlineDO klineDO = JSON.parseObject(item.toString(), KlineDO.class);
//            klineDO.setSymbol(symbol);
//            klineDO.setkType(kType);
//            klineDO.setType("kline");
//            klineDO.setPair(pair);
//            klineDO.setkTime(Integer.valueOf(klineDO.getId()));
//            klineDO.setId(symbol + klineDO.getId() + kType);
//            klineService.updateKline(klineDO);
//        });

    }
}
