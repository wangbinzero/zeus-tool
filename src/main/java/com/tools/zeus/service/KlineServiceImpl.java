package com.tools.zeus.service;

import com.mongodb.WriteResult;
import com.tools.zeus.entity.KlineDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KlineServiceImpl implements KlineService {
    private static final Logger LOG = LoggerFactory.getLogger(KlineServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public WriteResult updateKline(KlineDO klineDO) {

        Query query = new Query(Criteria.where("id").is(klineDO.getId()));
        KlineDO result = mongoTemplate.findOne(query, KlineDO.class);
        if (null != result) {
            LOG.info("更新K线");
            updateNew(query, klineDO);
        } else {
            LOG.info("新增K线");
            klineDO.setCreateTime(new Date().getTime());
            mongoTemplate.insert(klineDO);
        }
        return null;
    }

    @Override
    public void test() {
        LOG.info("测试");
    }


    private void updateNew(Query query, KlineDO klineDO) {
        Update update = new Update();
        update.set("amount", klineDO.getAmount());
        update.set("count", klineDO.getCount());
        update.set("open", klineDO.getOpen());
        update.set("close", klineDO.getClose());
        update.set("low", klineDO.getLow());
        update.set("high", klineDO.getHigh());
        update.set("vol", klineDO.getVol());
        update.set("id", klineDO.getId());
        update.set("updateTime", new Date().getTime());
        mongoTemplate.updateFirst(query, update, KlineDO.class);
    }


}
