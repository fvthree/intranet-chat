
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
[INFO] Copying 3 resources from src\main\resources to target\classes
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

2026-04-18T13:09:06.914+08:00  INFO 6212 --- [intranet-chat] [           main] c.intranet.chat.IntranetChatApplication  : Starting IntranetChatApplication using Java 21.0.10 with PID 6212 (D:\RJgit\intranet-chat\intranet-chat\backend\target\classes started by kadja in D:\RJgit\intranet-chat\intranet-chat\backend)
2026-04-18T13:09:06.916+08:00  INFO 6212 --- [intranet-chat] [           main] c.intranet.chat.IntranetChatApplication  : No active profile set, falling back to 1 default profile: "default"
2026-04-18T13:09:07.405+08:00  INFO 6212 --- [intranet-chat] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-04-18T13:09:07.405+08:00  INFO 6212 --- [intranet-chat] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data R2DBC repositories in DEFAULT mode.
2026-04-18T13:09:07.504+08:00  INFO 6212 --- [intranet-chat] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 94 ms. Found 1 R2DBC repository interface.
2026-04-18T13:09:08.842+08:00  INFO 6212 --- [intranet-chat] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoints beneath base path '/actuator'
2026-04-18T13:09:09.240+08:00  INFO 6212 --- [intranet-chat] [           main] org.flywaydb.core.FlywayExecutor         : Database: jdbc:postgresql://localhost:5433/intranet_chat (PostgreSQL 16.13)
2026-04-18T13:09:09.309+08:00  INFO 6212 --- [intranet-chat] [           main] o.f.core.internal.command.DbValidate     : Successfully validated 3 migrations (execution time 00:00.026s)
2026-04-18T13:09:09.348+08:00  INFO 6212 --- [intranet-chat] [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "public": 3
2026-04-18T13:09:09.351+08:00  INFO 6212 --- [intranet-chat] [           main] o.f.core.internal.command.DbMigrate      : Schema "public" is up to date. No migration necessary.
2026-04-18T13:09:09.486+08:00  INFO 6212 --- [intranet-chat] [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080 (http)
2026-04-18T13:09:09.505+08:00  INFO 6212 --- [intranet-chat] [           main] c.intranet.chat.IntranetChatApplication  : Started IntranetChatApplication in 2.937 seconds (process running for 3.219)
2026-04-18T13:09:15.289+08:00 ERROR 6212 --- [intranet-chat] [actor-tcp-nio-1] c.i.c.c.e.GlobalExceptionHandler         : Unhandled exception

org.springframework.security.oauth2.jwt.JwtEncodingException: An error occurred while attempting to encode the Jwt: Failed to select a JWK signing key
        at org.springframework.security.oauth2.jwt.NimbusJwtEncoder.selectJwk(NimbusJwtEncoder.java:134) ~[spring-security-oauth2-jose-6.4.4.jar:6.4.4]
        Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException:
Error has been observed at the following site(s):
        *__checkpoint Γçó Handler com.intranet.chat.auth.AuthController#login(LoginRequest) [DispatcherHandler]
Original Stack Trace:
                at org.springframework.security.oauth2.jwt.NimbusJwtEncoder.selectJwk(NimbusJwtEncoder.java:134) ~[spring-security-oauth2-jose-6.4.4.jar:6.4.4]
                at org.springframework.security.oauth2.jwt.NimbusJwtEncoder.encode(NimbusJwtEncoder.java:108) ~[spring-security-oauth2-jose-6.4.4.jar:6.4.4]
                at com.intranet.chat.auth.TokenService.createAccessToken(TokenService.java:34) ~[classes/:na]
                at com.intranet.chat.auth.AuthService.lambda$login$1(AuthService.java:39) ~[classes/:na]
                at reactor.core.publisher.FluxMap$MapSubscriber.onNext(FluxMap.java:106) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFilter$FilterSubscriber.onNext(FluxFilter.java:113) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFilter$FilterConditionalSubscriber.onNext(FluxFilter.java:247) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoNext$NextSubscriber.onNext(MonoNext.java:82) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxUsingWhen$UsingWhenSubscriber.onNext(FluxUsingWhen.java:348) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoFlatMap$FlatMapMain.onNext(MonoFlatMap.java:158) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onNext(FluxOnErrorResume.java:79) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Operators$MonoInnerProducerBase.complete(Operators.java:2864) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoSingle$SingleSubscriber.onComplete(MonoSingle.java:180) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.onComplete(Operators.java:2231) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxUsingWhen$UsingWhenSubscriber.deferredComplete(FluxUsingWhen.java:397) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxUsingWhen$CommitInner.onComplete(FluxUsingWhen.java:532) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.onComplete(Operators.java:2231) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxPeek$PeekSubscriber.onComplete(FluxPeek.java:260) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Operators$MultiSubscriptionSubscriber.onComplete(Operators.java:2231) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.onComplete(MonoIgnoreThen.java:210) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.onComplete(MonoIgnoreThen.java:210) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.pool.SimpleDequePool.maybeRecycleAndDrain(SimpleDequePool.java:547) ~[reactor-pool-1.1.2.jar:1.1.2]
                at reactor.pool.SimpleDequePool$QueuePoolRecyclerInner.onComplete(SimpleDequePool.java:788) ~[reactor-pool-1.1.2.jar:1.1.2]
                at reactor.core.publisher.Operators.complete(Operators.java:137) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoEmpty.subscribe(MonoEmpty.java:46) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Mono.subscribe(Mono.java:4576) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.pool.SimpleDequePool$QueuePoolRecyclerMono.subscribe(SimpleDequePool.java:901) ~[reactor-pool-1.1.2.jar:1.1.2]
                at reactor.core.publisher.MonoDefer.subscribe(MonoDefer.java:53) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.subscribeNext(MonoIgnoreThen.java:241) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.onComplete(MonoIgnoreThen.java:204) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxPeek$PeekSubscriber.onComplete(FluxPeek.java:260) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Operators.complete(Operators.java:137) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoEmpty.subscribe(MonoEmpty.java:46) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Mono.subscribe(Mono.java:4576) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.subscribeNext(MonoIgnoreThen.java:265) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen.subscribe(MonoIgnoreThen.java:51) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoDefer.subscribe(MonoDefer.java:53) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.subscribeNext(MonoIgnoreThen.java:241) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.onComplete(MonoIgnoreThen.java:204) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreElements$IgnoreElementsSubscriber.onComplete(MonoIgnoreElements.java:89) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandleFuseable$HandleFuseableSubscriber.onComplete(FluxHandleFuseable.java:239) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoSupplier$MonoSupplierSubscription.request(MonoSupplier.java:148) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandleFuseable$HandleFuseableSubscriber.request(FluxHandleFuseable.java:260) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreElements$IgnoreElementsSubscriber.onSubscribe(MonoIgnoreElements.java:72) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandleFuseable$HandleFuseableSubscriber.onSubscribe(FluxHandleFuseable.java:164) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoSupplier.subscribe(MonoSupplier.java:48) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Mono.subscribe(Mono.java:4576) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen$ThenIgnoreMain.subscribeNext(MonoIgnoreThen.java:265) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreThen.subscribe(MonoIgnoreThen.java:51) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.InternalMonoOperator.subscribe(InternalMonoOperator.java:76) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoDefer.subscribe(MonoDefer.java:53) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Mono.subscribe(Mono.java:4576) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxOnErrorResume$ResumeSubscriber.onError(FluxOnErrorResume.java:103) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoIgnoreElements$IgnoreElementsSubscriber.onError(MonoIgnoreElements.java:84) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxMap$MapSubscriber.onError(FluxMap.java:134) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFilter$FilterSubscriber.onError(FluxFilter.java:157) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFilter$FilterConditionalSubscriber.onError(FluxFilter.java:291) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxMap$MapConditionalSubscriber.onError(FluxMap.java:265) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Operators.error(Operators.java:198) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoError.subscribe(MonoError.java:53) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoDeferContextual.subscribe(MonoDeferContextual.java:55) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.InternalMonoOperator.subscribe(InternalMonoOperator.java:76) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoDefer.subscribe(MonoDefer.java:53) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.Mono.subscribe(Mono.java:4576) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxUsingWhen$UsingWhenSubscriber.onComplete(FluxUsingWhen.java:389) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapMain.checkTerminated(FluxFlatMap.java:850) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapMain.drainLoop(FluxFlatMap.java:612) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapMain.innerComplete(FluxFlatMap.java:898) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFlatMap$FlatMapInner.onComplete(FluxFlatMap.java:1001) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandle$HandleSubscriber.onComplete(FluxHandle.java:223) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.MonoFlatMapMany$FlatMapManyInner.onComplete(MonoFlatMapMany.java:261) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandleFuseable$HandleFuseableSubscriber.onComplete(FluxHandleFuseable.java:239) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxFilterFuseable$FilterFuseableConditionalSubscriber.onComplete(FluxFilterFuseable.java:391) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxContextWrite$ContextWriteSubscriber.onComplete(FluxContextWrite.java:126) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxPeekFuseable$PeekConditionalSubscriber.onComplete(FluxPeekFuseable.java:940) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxPeekFuseable$PeekConditionalSubscriber.onComplete(FluxPeekFuseable.java:940) ~[reactor-core-3.7.4.jar:3.7.4]
                at io.r2dbc.postgresql.util.FluxDiscardOnCancel$FluxDiscardOnCancelSubscriber.onComplete(FluxDiscardOnCancel.java:104) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
                at reactor.core.publisher.FluxDoFinally$DoFinallySubscriber.onComplete(FluxDoFinally.java:128) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxHandle$HandleSubscriber.onComplete(FluxHandle.java:223) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$BaseSink.complete(FluxCreate.java:465) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$BufferAsyncSink.drain(FluxCreate.java:871) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$BufferAsyncSink.complete(FluxCreate.java:819) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$SerializedFluxSink.drainLoop(FluxCreate.java:249) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$SerializedFluxSink.drain(FluxCreate.java:215) ~[reactor-core-3.7.4.jar:3.7.4]
                at reactor.core.publisher.FluxCreate$SerializedFluxSink.complete(FluxCreate.java:206) ~[reactor-core-3.7.4.jar:3.7.4]
                at io.r2dbc.postgresql.client.ReactorNettyClient$Conversation.complete(ReactorNettyClient.java:680) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
                at io.r2dbc.postgresql.client.ReactorNettyClient$BackendMessageSubscriber.emit(ReactorNettyClient.java:946) ~[r2dbc-postgresql-1.0.7.RELEASE.jar:1.0.7.RELEASE]
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
                at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:318) ~[netty-codec-4.1.119.Final.jar:4.1.119.Final]
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
