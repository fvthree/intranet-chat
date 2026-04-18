
D:\RJgit\intranet-chat\intranet-chat\backend>mvn spring-boot:run
[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------------< com.intranet:intranet-chat >---------------------
[INFO] Building intranet-chat 0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] >>> spring-boot:3.4.4:run (default-cli) > test-compile @ intranet-chat >>>
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ intranet-chat ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 6 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ intranet-chat ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ intranet-chat ---
[INFO] skip non existing resourceDirectory D:\RJgit\intranet-chat\intranet-chat\backend\src\test\resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ intranet-chat ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] <<< spring-boot:3.4.4:run (default-cli) < test-compile @ intranet-chat <<<
[INFO] 
[INFO] 
[INFO] --- spring-boot:3.4.4:run (default-cli) @ intranet-chat ---
[INFO] Attaching agents: []

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.4.4)

2026-04-18T16:33:24.002+08:00  INFO 21104 --- [intranet-chat] [           main] c.intranet.chat.IntranetChatApplication  : Starting IntranetChatApplication using Java 21.0.10 with PID 21104 (D:\RJgit\intranet-chat\intranet-chat\backend\target\classes started by kadja in D:\RJgit\intranet-chat\intranet-chat\backend)
2026-04-18T16:33:24.003+08:00  INFO 21104 --- [intranet-chat] [           main] c.intranet.chat.IntranetChatApplication  : No active profile set, falling back to 1 default profile: "default"
2026-04-18T16:33:24.503+08:00  INFO 21104 --- [intranet-chat] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-04-18T16:33:24.503+08:00  INFO 21104 --- [intranet-chat] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data R2DBC repositories in DEFAULT mode.
2026-04-18T16:33:24.605+08:00  INFO 21104 --- [intranet-chat] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 100 ms. Found 4 R2DBC repository interfaces.
2026-04-18T16:33:26.146+08:00  INFO 21104 --- [intranet-chat] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2026-04-18T16:33:26.539+08:00  INFO 21104 --- [intranet-chat] [           main] org.flywaydb.core.FlywayExecutor         : Database: jdbc:postgresql://localhost:5433/intranet_chat (PostgreSQL 16.13)
2026-04-18T16:33:26.592+08:00  INFO 21104 --- [intranet-chat] [           main] o.f.core.internal.command.DbValidate     : Successfully validated 6 migrations (execution time 00:00.026s)
2026-04-18T16:33:26.630+08:00  INFO 21104 --- [intranet-chat] [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "public": 6
2026-04-18T16:33:26.633+08:00  INFO 21104 --- [intranet-chat] [           main] o.f.core.internal.command.DbMigrate      : Schema "public" is up to date. No migration necessary.
2026-04-18T16:33:26.770+08:00  INFO 21104 --- [intranet-chat] [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080 (http)
2026-04-18T16:33:26.770+08:00  INFO 21104 --- [intranet-chat] [           main] c.intranet.chat.IntranetChatApplication  : Started IntranetChatApplication in 3.113 seconds (process running for 3.405)
2026-04-18T16:33:34.030+08:00 ERROR 21104 --- [intranet-chat] [actor-tcp-nio-5] c.i.c.c.e.GlobalExceptionHandler         : Unhandled exception

reactor.netty.channel.AbortedException: Connection has been closed BEFORE send operation
        at reactor.netty.channel.AbortedException.beforeSend(AbortedException.java:59) ~[reactor-netty-core-1.2.4.jar:1.2.4]
        Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException:
Error has been observed at the following site(s):
        *__checkpoint Γçó Handler com.intranet.chat.conversation.ConversationController#sendMessage(UUID, SendMessageRequest) [DispatcherHandler]
Original Stack Trace:
                at reactor.netty.channel.AbortedException.beforeSend(AbortedException.java:59) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.http.server.HttpServerOperations.then(HttpServerOperations.java:677) ~[reactor-netty-http-1.2.4.jar:1.2.4]
                at reactor.netty.ReactorNetty$OutboundThen.<init>(ReactorNetty.java:744) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.ReactorNetty$OutboundThen.<init>(ReactorNetty.java:733) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.NettyOutbound.then(NettyOutbound.java:358) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.channel.ChannelOperations.sendObject(ChannelOperations.java:314) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.NettyOutbound.sendObject(NettyOutbound.java:246) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession.send(ReactorNettyWebSocketSession.java:110) ~[spring-webflux-6.2.5.jar:6.2.5]
                at com.intranet.chat.realtime.RealtimeConnectionRegistry.lambda$sendToUser$1(RealtimeConnectionRegistry.java:49) ~[classes/:na]
                at reactor.core.publisher.FluxFlatMap$FlatMapMain.onNext(FluxFlatMap.java:388) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxIterable$IterableSubscription.slowPath(FluxIterable.java:335) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxIterable$IterableSubscription.request(FluxIterable.java:294) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapMain.onSubscribe(FluxFlatMap.java:373) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxIterable.subscribe(FluxIterable.java:201) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxIterable.subscribe(FluxIterable.java:83) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Mono.subscribe(Mono.java:4576) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapMain.onNext(FluxFlatMap.java:430) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFilter$FilterSubscriber.onNext(FluxFilter.java:113) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxMap$MapConditionalSubscriber.onNext(FluxMap.java:224) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxUsingWhen$UsingWhenSubscriber.onNext(FluxUsingWhen.java:348) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxConcatMapNoPrefetch$FluxConcatMapNoPrefetchSubscriber.innerNext(FluxConcatMapNoPrefetch.java:259) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxConcatMap$ConcatMapInner.onNext(FluxConcatMap.java:865) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxConcatMap$WeakScalarSubscription.request(FluxConcatMap.java:480) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.set(Operators.java:2367) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxConcatMapNoPrefetch$FluxConcatMapNoPrefetchSubscriber.onNext(FluxConcatMapNoPrefetch.java:202) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onNext(FluxOnErrorResume.java:79) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxUsingWhen$UsingWhenSubscriber.onNext(FluxUsingWhen.java:348) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapMain.tryEmit(FluxFlatMap.java:547) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapInner.onNext(FluxFlatMap.java:988) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandle$HandleSubscriber.onNext(FluxHandle.java:129) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onNext(MonoFlatMapMany.java:251) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandleFuseable$HandleFuseableSubscriber.onNext(FluxHandleFuseable.java:194) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFilterFuseable$FilterFuseableConditionalSubscriber.onNext(FluxFilterFuseable.java:337) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxContextWrite$ContextWriteSubscriber.onNext(FluxContextWrite.java:107) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxPeekFuseable$PeekConditionalSubscriber.onNext(FluxPeekFuseable.java:854) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxPeekFuseable$PeekConditionalSubscriber.onNext(FluxPeekFuseable.java:854) ~[reactor-core-3.7.4.jar:3.7.4]
                at io.r2dbc.postgresql.util.FluxDiscardOnCancel$FluxDiscardOnCancelSubscriber.onNext(FluxDiscardOnCancel.java:91) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
                at reactor.core.publisher.FluxDoFinally$DoFinallySubscriber.onNext(FluxDoFinally.java:113) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandle$HandleSubscriber.onNext(FluxHandle.java:129) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$BufferAsyncSink.drain(FluxCreate.java:880) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$BufferAsyncSink.next(FluxCreate.java:805) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$SerializedFluxSink.next(FluxCreate.java:163) ~[reactor-core-3.7.4.jar:3.7.4]
                at io.r2dbc.postgresql.client.ReactorNettyClient$Conversation.emit(ReactorNettyClient.java:696) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
                at io.r2dbc.postgresql.client.ReactorNettyClient$BackendMessageSubscriber.emit(ReactorNettyClient.java:948) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
                at io.r2dbc.postgresql.client.ReactorNettyClient$BackendMessageSubscriber.onNext(ReactorNettyClient.java:822) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
                at io.r2dbc.postgresql.client.ReactorNettyClient$BackendMessageSubscriber.onNext(ReactorNettyClient.java:728) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
                at reactor.core.publisher.FluxHandle$HandleSubscriber.onNext(FluxHandle.java:129) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxPeekFuseable$PeekConditionalSubscriber.onNext(FluxPeekFuseable.java:854) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxMap$MapConditionalSubscriber.onNext(FluxMap.java:224) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxMap$MapConditionalSubscriber.onNext(FluxMap.java:224) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.netty.channel.FluxReceive.drainReceiver(FluxReceive.java:292) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.channel.FluxReceive.onInboundNext(FluxReceive.java:401) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.channel.ChannelOperations.onInboundNext(ChannelOperations.java:435) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at reactor.netty.channel.ChannelOperationsHandler.channelRead(ChannelOperationsHandler.java:115) ~[reactor-netty-core-1.2.4.jar:1.2.4]
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:444) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:346) ~[netty-codec-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:333) ~[netty-codec-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:455) ~[netty-codec-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:290) ~[netty-codec-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:444) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1357) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:440) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:868) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:796) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:732) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:658) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:562) ~[netty-transport-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:998) ~[netty-common-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.1.119.Final.jar:4.1.119.Final]
                at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.119.Final.jar:4.1.119.Final]
                at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
