package com.tools.zeus.service;

import com.mongodb.WriteResult;
import com.tools.zeus.entity.KlineDO;

public interface KlineService {

    WriteResult updateKline(KlineDO klineDO);

    void test();
}
