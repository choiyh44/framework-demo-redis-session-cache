# framework-demo-spring-redis-session-cache

redis를 두개 설정하여, 각각을 세션과 stomp pub/sub에 사용한다.

## redis 커넥션 2개 설정

application.yml에 redis접속정보 2개를 등록한다.
<pre>
<code>
redis-list:
  redis1:
    host: 127.0.0.1
    port: 6379
    lettuce:
      pool:
        min-idle: 1
        max-idle: 3
        max-active: 8
        max-wait: 1ms
      shutdown-timeout: 100ms
  redis2:
    host: 127.0.0.1
    port: 6380
    lettuce:
      pool:
        min-idle: 1
        max-idle: 3
        max-active: 8
        max-wait: 1ms
      shutdown-timeout: 100ms
</code>
</pre>

해당 접속 정보를 이용하여 Redis connection factory 2개를 설정한다.

<pre>
<code>

@Configuration
@ConfigurationProperties(prefix = "redis-list")
public class RedisConfig {
    private RedisProperties redis1;
    private RedisProperties redis2;
	...
    @Bean(name = "redis1LettuceConnectionFactory")
    @Primary
    public LettuceConnectionFactory Redis1LettuceConnectionFactory(ClientResources clientResources) {
        RedisStandaloneConfiguration redis1StandaloneConfiguration = getStandaloneConfig(redis1);
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources, redis1);
        return new LettuceConnectionFactory(redis1StandaloneConfiguration, clientConfig);
    }
   ...
    @Bean(name = "redis2LettuceConnectionFactory")
    public LettuceConnectionFactory Redis2LettuceConnectionFactory(ClientResources clientResources) {
        RedisStandaloneConfiguration redis1StandaloneConfiguration = getStandaloneConfig(redis2);
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources, redis2);
        return new LettuceConnectionFactory(redis1StandaloneConfiguration, clientConfig);
    }
}    
</code>
</pre>

## redis1을 세션에 사용

configuration 파일에 @EnableRedisHttpSession 어노테이션을 지정한다.

<pre>
<code>
@Configuration
@EnableRedisHttpSession
public class SessionConfig {

}
</code>
</pre>

해당 어노테이션이 지정되면 자동으로 @Primary Redis Connection Factory 가 세션서버로 사용된다.

[테스트 방법]
 
- p3x로 redis에 접속하여 redis 클리어 한 후 
- http://localhost:8080/sample/session 접속하면
- 세션이 생성되고, 세션에 name: testname 항목이 추가된다.

## [TODO] redis2를 cache에 사용

