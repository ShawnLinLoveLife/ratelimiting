/*
 * Copyright © 2015 SNLab and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.snlab.impl;

import com.google.common.collect.ImmutableList;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.FlowTableRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.MeterCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.AddMeterInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.AddMeterOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.service.rev130918.SalMeterService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.Meter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.band.type.band.type.DropBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.MeterBandHeadersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.MeterBandHeaderBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.meter.types.rev130918.meter.meter.band.headers.meter.band.header.MeterBandTypesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ratelimiting.rev150105.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.table.types.rev131026.TableRef;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class RatelimitingServiceImpl implements RatelimitingService {
    private static final Logger LOG = LoggerFactory.getLogger(RatelimitingServiceImpl.class);
    private SalFlowService salFlowService;
    private SalMeterService salMeterService;

    private AtomicLong flowIdInc = new AtomicLong(0);
    private AtomicLong meterIdInc = new AtomicLong(1);
    private AtomicLong flowCookieInc = new AtomicLong(0x3a00000000000000L);

    private final long DEFUALT_BAND_ID = 0;
    private final String DEFAULT_METER_NAME = "rate limiting";
    private final String DEFAULT_METER_CONTAINER = "rate limiting container";
    private final short DEFAULT_TABLD_ID = 0;
    private final int DEFAULT_PRIORITY = 10;
    private final int DEFAULT_HARD_TIME_OUT = 0;
    private final int DEFAULT_IDLE_TIME_OUT = 0;
    public RatelimitingServiceImpl(SalFlowService salFlowService, SalMeterService salMeterService) {
        this.salFlowService = salFlowService;
        this.salMeterService = salMeterService;
    }
    @Override
    public Future<RpcResult<SetUpRateLimitingOutput>> setUpRateLimiting(SetUpRateLimitingInput input) {
        SetUpRateLimitingOutputBuilder outputBuilder = new SetUpRateLimitingOutputBuilder();
        long meterId = meterIdInc.getAndIncrement();
        //　添加meter
        addMeter(input.getSwitchId(), input.getLimitedRate().longValue(), input.getBurstSize().longValue(), meterId);
        // 添加从src到dst的流
        addFlow(input.getSwitchId(), input.getSrcPort(), input.getDstPort(), meterId);
        // 添加从dst到src的流
        addFlow(input.getSwitchId(), input.getDstPort(), input.getSrcPort(), -1);
        outputBuilder.setErrorCode(ErrorCodeType.OK);
        return RpcResultBuilder.success(outputBuilder.build()).buildFuture();
    }

    private Future<RpcResult<AddMeterOutput>> addMeter(String switchId, long rate, long burstSize, long meterId) {
        // 建立一个meter对象
        DropBuilder dropBuilder = new DropBuilder();
        dropBuilder
                .setDropRate(rate)
                .setDropBurstSize(burstSize);

        MeterBandHeaderBuilder mbhBuilder = new MeterBandHeaderBuilder()
                .setBandType(dropBuilder.build())
                .setBandId(new BandId(DEFUALT_BAND_ID))
                .setMeterBandTypes(new MeterBandTypesBuilder()
                        .setFlags(new MeterBandType(true, false, false)).build())
                .setBandRate(rate)
                .setBandBurstSize(burstSize);

        List<MeterBandHeader> mbhList = new LinkedList<>();
        mbhList.add(mbhBuilder.build());

        MeterBandHeadersBuilder mbhsBuilder = new MeterBandHeadersBuilder()
                .setMeterBandHeader(mbhList);

        MeterBuilder meterBuilder = new MeterBuilder()
                .setFlags(new MeterFlags(true, true, false, false))
                .setMeterBandHeaders(mbhsBuilder.build())
                .setMeterId(new MeterId(meterId))
                .setMeterName(DEFAULT_METER_NAME)
                .setContainerName(DEFAULT_METER_CONTAINER);

        Meter meter = meterBuilder.build();

        // 生成一个指向某个交换机的路径
        InstanceIdentifier<Node> nodeIID =
                InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(new NodeId(switchId))).build();
        NodeRef nodeRef = new NodeRef(nodeIID);

        //　生成一个指向某个交换机上的meter的路径
        InstanceIdentifier<org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.Meter>
                meterIID =
                InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(new NodeId(switchId)))
                        .augmentation(FlowCapableNode.class)
                        .child(org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.meters.Meter.class, new MeterKey(new MeterId(meterId)))
                        .build();
        MeterRef meterRef = new MeterRef(meterIID);

        LOG.info("nodeRef: " + nodeRef.toString());
        LOG.info("meterRef: " + meterRef.toString());
        //　将这个meter对象放到对应交换机的datastore的路径上
        final AddMeterInputBuilder addMeterBuilder = new AddMeterInputBuilder(meter);
        addMeterBuilder
                .setNode(nodeRef)
                .setMeterRef(meterRef);
        LOG.info("AddMeterInput: " + addMeterBuilder.build());
        return salMeterService.addMeter(addMeterBuilder.build());
    }

    private Future<RpcResult<AddFlowOutput>> addFlow(String switchId, int inPort, int outPort, long meterId) {
        //　建立一个FlowBuilder
        FlowBuilder flowBuilder = new FlowBuilder();

        // 建立一个Match
        MatchBuilder matchBuilder = new MatchBuilder();
        matchBuilder
                .setInPort(new NodeConnectorId(switchId + ":" + String.valueOf(inPort)));
        Match match = matchBuilder.build();

        //　建立一个Instruction，本条Instruction说明这条流的output port是哪个
        Action outputToControllerAction = new ActionBuilder()
                .setOrder(0)
                .setAction(new OutputActionCaseBuilder()
                        .setOutputAction(new OutputActionBuilder()
                                .setMaxLength(0xffff)
                                .setOutputNodeConnector(new NodeConnectorId(switchId + ":" + outPort)) //
                                .build())
                        .build())
                .build();
        ApplyActions applyActions = new ApplyActionsBuilder().setAction(ImmutableList.of(outputToControllerAction))
                .build();
        Instruction applyActionsInstruction = new InstructionBuilder()
                .setOrder(0)
                .setInstruction(new ApplyActionsCaseBuilder()
                        .setApplyActions(applyActions)
                        .build())
                .build();

        //　建立一个Instructions，把第一个Instruction放入
        List<Instruction> instructionList = new LinkedList<>();
        instructionList.add(applyActionsInstruction);

        // 如果需要限流meterId > 0，建立第二个Instruction，本条Instruction说明这个流要应用哪个meter
        if (meterId > 0) {
            Instruction applyMeterInstruction = new InstructionBuilder()
                    .setOrder(1)
                    .setInstruction(new MeterCaseBuilder()
                            .setMeter(new org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.meter._case.MeterBuilder()
                                    .setMeterId(new MeterId(meterId)).build())
                            .build())
                    .build();
            instructionList.add(applyMeterInstruction);
        }

        Flow flow = flowBuilder
                .setMatch(match)
                .setInstructions(new InstructionsBuilder()
                        .setInstruction(instructionList)
                        .build())
                .setPriority(DEFAULT_PRIORITY)
                .setCookie(new FlowCookie(BigInteger.valueOf(flowCookieInc.getAndIncrement())))
                .build();

        // 生成一个指向某个交换机的路径
        InstanceIdentifier<Node> nodeIID =
                InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(new NodeId(switchId)))
                        .build();
        NodeRef nodeRef = new NodeRef(nodeIID);

        //　生成一个指向某个交换机上的table的路径
        InstanceIdentifier<Table> tableIID =
                InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(new NodeId(switchId)))
                        .augmentation(FlowCapableNode.class)
                        .child(Table.class, new TableKey(DEFAULT_TABLD_ID))
                        .build();
        FlowTableRef flowTableRef = new FlowTableRef(tableIID);

        //  生成一个指向某个交换机上的flow的路径
        InstanceIdentifier<Flow> flowIID =
                InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(new NodeId(switchId)))
                        .augmentation(FlowCapableNode.class)
                        .child(Table.class, new TableKey(DEFAULT_TABLD_ID))
                        .child(Flow.class, new FlowKey(new FlowId(String.valueOf(flowIdInc.getAndIncrement()))))
                        .build();
        FlowRef flowRef = new FlowRef(flowIID);

        LOG.info("nodeRef: " + nodeRef.toString());
        LOG.info("flowTableRef: " + flowTableRef.toString());
        LOG.info("flowRef: " + flowRef.toString());
        //　生成AddFlowInput
        final AddFlowInputBuilder addFlowInputBuilder = new AddFlowInputBuilder(flow);
        addFlowInputBuilder
                .setNode(nodeRef)
                .setFlowRef(flowRef)
                .setFlowTable(flowTableRef)
                .setHardTimeout(DEFAULT_HARD_TIME_OUT)
                .setIdleTimeout(DEFAULT_IDLE_TIME_OUT);
        return salFlowService.addFlow(addFlowInputBuilder.build());
    }
}