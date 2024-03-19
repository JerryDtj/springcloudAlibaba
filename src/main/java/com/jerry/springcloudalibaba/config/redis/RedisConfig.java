package com.jerry.springcloudalibaba.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * redis序列化方式由默认的JdkSerializationRedisSerializer改为jackson序列化方便读取
 * @author Jerry
 * @Date 2024/3/13 13:26
 *
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        //数据类型为空时,会直接取默认的序列化方式
        redisTemplate.setDefaultSerializer(RedisSerializer.json());
        return redisTemplate;
    }


}
