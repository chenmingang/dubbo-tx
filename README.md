
## tx模块提供dubbo的事务支持
简单事务传递，客户端发起事务方法请求，服务端开启事务，多服务端保持commit或rollback一致
### 服务端
- 在src/main/resources/META-INF/dubbo/internal文件夹下添加
com.alibaba.dubbo.rpc.Filter文件，配置项见doc中txServerFilter
- 配置service filter
<dubbo:service  interface="tx.ProviderService" ref="providerServiceImpl" timeout="10000"  filter="txServerFilter" retries="0"/>

### 消费端

- 在src/main/resources/META-INF/dubbo/internal文件夹下添加
com.alibaba.dubbo.rpc.Filter文件，配置项见doc中txConsumerFilter

- 配置reference filter
<dubbo:reference id="providerService" interface="tx.ProviderService" check="false" filter="txConsumerFilter"/>

- 在方法上添加注解
    @Tx
    @Override
    public void say() {}

- spring 配置注解拦截器
    <bean id="txFilter" class="tx.TxFilter" />
    <aop:config>
        <aop:pointcut id="pc" expression="execution(* tx.Service.*.*(..))"/>
        <aop:advisor advice-ref="txFilter" pointcut-ref="pc"/>
    </aop:config>
    <aop:aspectj-autoproxy proxy-target-class="true"  />
