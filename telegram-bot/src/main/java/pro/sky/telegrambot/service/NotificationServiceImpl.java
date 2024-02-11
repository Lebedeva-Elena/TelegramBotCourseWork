package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class NotificationServiceImpl implements NotificationService {
    private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    public NotificationServiceImpl(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    @Override
    public void process(Update update) {
        System.out.println(update);
        Long chatId = update.message().chat().id();
        String text = update.message().text();
        if (text.equals("/start")) {
            telegramBot.execute(new SendMessage(chatId, "Привет, мой забывчивый гость. " +
                    "Расскажи о своих предстоящих событиях и я позабочусь о том, чтобы ты не о них не забыл"));
            logger.info("Приветственное сообщение отправлено");
            return;

        }
        Matcher matcher = MESSAGE_PATTERN.matcher(text);
        if (matcher.find()) {
            String dateWithNotification = matcher.group(1);
            LocalDateTime alarmDate = LocalDateTime.parse(dateWithNotification, DATE_TIME_FORMATTER);
            String notification = matcher.group(3);
            NotificationTask notificationTask = new NotificationTask(chatId, notification, alarmDate);
            repository.save(notificationTask);
            telegramBot.execute(new SendMessage(chatId, "Напоминание сохранено, теперь ты можешь на меня положиться"));
            logger.info("Напоминание " + notificationTask.getNotification() + " cохранено");

        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Напиши дату в формате dd.MM.yyyy HH:mm и какое мероприятие тебя  ожидает");
            telegramBot.execute(sendMessage);
            logger.warn("Неверный формат сообщения");

        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    @Override
    public void sentNotification() {
        List<NotificationTask> tasks = repository.findByAlarmDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        for (int i = 0; i < tasks.size(); i++) {
            NotificationTask notificationTask = tasks.get(i);
            telegramBot.execute(new SendMessage(notificationTask.getChatId(), "Напоминаю о твоем событии " +
                    notificationTask.getNotification() + " в " + convertDateAndTime(notificationTask.getAlarmDate())));
            logger.info("Напоминание было отправлено");
        }
    }

    private String convertDateAndTime(LocalDateTime alarmDate) {
        return DATE_TIME_FORMATTER.format(alarmDate);

    }
}


