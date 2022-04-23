package mao;

import com.rabbitmq.client.*;
import mao.tools.RabbitMQ;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Project name(项目名称)：rabbitMQ死信队列之消息TTL过期
 * Package(包名): mao
 * Class(类名): Consumer2
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/4/23
 * Time(创建时间)： 21:31
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class Consumer2
{
    private static final String DEAD_EXCHANGE = "dead_exchange";

    public static void main(String[] args) throws IOException, TimeoutException
    {
        Channel channel = RabbitMQ.getChannel();
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        String deadQueue = "dead-queue";
        channel.queueDeclare(deadQueue, false, false, false, null);
        channel.queueBind(deadQueue, DEAD_EXCHANGE, "key2");
        System.out.println("开始接收消息");
        channel.basicConsume(deadQueue, true, new DeliverCallback()
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

            }
        });
    }
}
