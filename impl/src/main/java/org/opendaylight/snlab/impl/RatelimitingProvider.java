/*
 * Copyright © 2015 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.snlab.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.SalMeterService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ratelimiting.rev150105.RatelimitingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RatelimitingProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(RatelimitingProvider.class);
    private BindingAwareBroker.RpcRegistration<RatelimitingService> ratelimitingService;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        // 获取SalFLowService，用于添加流表
        SalFlowService salFlowService = session.getRpcService(SalFlowService.class);
        // 获取SalMeterService，用于添加meter表
        SalMeterService salMeterService = session.getRpcService(SalMeterService.class);
        // 告诉系统我实现了一个RPC，并且将我需要的service的获取到
        ratelimitingService = session.addRpcImplementation(RatelimitingService.class,
                new RatelimitingServiceImpl(salFlowService, salMeterService));
        LOG.info("RatelimitingProvider Session Initiated");
    }

    @Override
    public void close() throws Exception {
        LOG.info("RatelimitingProvider Closed");
    }

}
