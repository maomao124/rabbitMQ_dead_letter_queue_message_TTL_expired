package mao;

import com.rabbitmq.client.*;
import mao.tools.RabbitMQ;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Project name(项目名称)：rabbitMQ死信队列之消息TTL过期
 * Package(包名): mao
 * Class(类名): Consumer1
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/4/23
 * Time(创建时间)： 21:07
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class Consumer1
{
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机名称
    private static final String DEAD_EXCHANGE = "dead_exchange";


    public static void main(String[] args) throws IOException, TimeoutException
    {
        Channel channel = RabbitMQ.getChannel();
        //声明死信和普通交换机 类型为 direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明死信队列
        channel.queueDeclare("dead_queue", false, false, false, null);
        //绑定交换机
        channel.queueBind("dead_queue", DEAD_EXCHANGE, "key2");
        //正常队列绑定死信队列消息
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        map.put("x-dead-letter-routing-key", "key2");
        map.put("x-message-ttl", 10000);

        //声明正常队列
        channel.queueDeclare("normal-queue", false, false, false, map);
        channel.queueBind("normal-queue", NORMAL_EXCHANGE, "key1");

        System.out.println("开始接收消息");

        channel.basicConsume("normal-queue", true, new DeliverCallback()
        {
            @Override
            public void handle(String consumerTag, Delivery message) throws IOException
            {
                byte[] messageBody = message.getBody();
                String message1 = new String(messageBody, StandardCharsets.UTF_8);
                System.out.println("接收消息：" + message1);
            }
        }, new CancelCallback()
        {
            @Override
            public void handle(String consumerTag) throws IOException
            {
                System.out.println("取消");
            }
        });
    }
}
