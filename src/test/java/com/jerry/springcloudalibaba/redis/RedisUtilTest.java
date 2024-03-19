package com.jerry.springcloudalibaba.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerry.springcloudalibaba.pojo.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class RedisUtilTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete("1");
        redisTemplate.delete(2);
        redisTemplate.delete("3");
        redisTemplate.delete("list");
        redisTemplate.delete("set");
        redisTemplate.delete("hash");
        redisTemplate.delete("zSet");
    }

    @Test
    public void setString(){
        User user = new User(1,"测试");
        redisTemplate.opsForValue().set("1",user);
        redisTemplate.opsForValue().set(2,"测试");
        redisTemplate.opsForValue().set("3","测试");
    }

    /**
     * 这个东西感觉就是一个双向链表.
     */
    @Test
    public void setList(){
        redisTemplate.delete("list");
        redisTemplate.opsForList().leftPushAll("list",new User(1,"1"),new User(2,"2"),new User(3,"3"));
        redisTemplate.opsForList().rightPush("list",new User(4,"4"));
        //这里会重复添加一个“1”
        redisTemplate.opsForList().leftPush("list",new User(1,"1"));
        User u = (User) redisTemplate.opsForList().index("list",2L);
        System.out.println(u.getId());
//        this.delete("list");
    }

    @Test
    public void setHash(){
        Map m = new HashMap();
        m.put("1",1);
        m.put("2",2);
        m.put("user1",new User(3,"name3"));
        m.put("delete",m.put(4,new User(4,"4")));
        redisTemplate.opsForHash().putAll("hash",m);
        redisTemplate.opsForHash().delete("hash","delete");
        Assert.isNull(redisTemplate.opsForHash().get("hash","delete"),"redis hash 取到了应该删除的值");
//        delete("hash");
    }

    @Test
    public void setSet() throws JsonProcessingException {
        redisTemplate.opsForSet().add("set",new User(1,"1"),new User(2,"2"),
                new User(3,"3"),new User(4,"4"),new User(5,"5"),new User(6,"6"));
        Assert.isTrue(0L==redisTemplate.opsForSet().add("set",new User(1,"1")),"redis set 添加了重复数据0");
        Assert.isTrue(1L==redisTemplate.opsForSet().add("set",new User(1,"2")),"redis set 未添加不重复数据1");
        Assert.isTrue(1L==redisTemplate.opsForSet().remove("set",new User(1,"2")),"redis set 未删除脏数据1");
        redisTemplate.opsForSet().add("set1",new User(1,"1"),new User(2,"2"),new User(3,"3"));
        ObjectMapper mapper = new ObjectMapper();
        //取差集
        Set differenceSet = redisTemplate.opsForSet().difference("set","set1");
        System.out.println("redis set 差集为:"+mapper.writeValueAsString(differenceSet));
        //取交集
        Set intersertSet = redisTemplate.opsForSet().intersect("set","set1");
        System.out.println("redis set 交集为:"+mapper.writeValueAsString(intersertSet));
        //取并集
        Set unionSet = redisTemplate.opsForSet().union("set","set1");
        System.out.println("redis set 并集为:"+mapper.writeValueAsString(unionSet));
        delete("set1");
    }

    @Test
    public void getZset() throws JsonProcessingException {
        redisTemplate.opsForZSet().add("zSet",new User(1,"1"),1D);
        redisTemplate.opsForZSet().add("zSet",new User(2,"2"),2D);
        redisTemplate.opsForZSet().add("zSet",new User(3,"3"),3D);
        redisTemplate.opsForZSet().add("zSet",new User(4,"4"),4D);
        redisTemplate.opsForZSet().add("zSet",new User(5,"5"),5D);
        redisTemplate.opsForZSet().add("zSet",new User(6,"6"),5D);
        ZSetOperations.TypedTuple t = redisTemplate.opsForZSet().popMax("zSet");
        Assert.isTrue(t.getScore()==5D,"redis zSet 最大分数取值错误");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("redis zSet 最大值:"+mapper.writeValueAsString(t.getValue()));
        System.out.println(mapper.writeValueAsString(redisTemplate.opsForZSet().range("zSet",2L,4L)));


    }

    public void getString(String key){
        Object value = redisTemplate.opsForValue().getAndDelete("1");
        System.out.println("value:"+value);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}

