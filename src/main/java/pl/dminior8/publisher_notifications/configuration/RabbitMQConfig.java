package pl.dminior8.publisher_notifications.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "notificationExchange";

    public static final String PUSH_QUEUE = "pushQueue";
    public static final String EMAIL_QUEUE = "emailQueue";

    public static final String PUSH_ROUTING_KEY = "notification.push";
    public static final String EMAIL_ROUTING_KEY = "notification.email";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue pushQueue() {
        return new Queue(PUSH_QUEUE, true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(pushQueue()).to(exchange()).with(PUSH_ROUTING_KEY);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(exchange()).with(EMAIL_ROUTING_KEY);
    }
}


