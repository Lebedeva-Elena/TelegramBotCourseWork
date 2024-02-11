package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @Mock
    private NotificationTaskRepository notificationTaskRepository;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;
    @Mock
    private TelegramBot telegramBot;


    @Test
    void saveEntity() {
        NotificationTask newTask = new NotificationTask(1L, "Привет", LocalDateTime.now());
        when(notificationTaskRepository.save(any())).thenReturn(newTask);
        NotificationTask result = notificationTaskRepository.save(newTask);
        assertEquals(newTask, result);
    }

    @Test
    void process() {
        when(update.message()).thenReturn(message);
        when(update.message().chat()).thenReturn(chat);
        when(update.message().chat().id()).thenReturn(1L);
        when(update.message().text()).thenReturn("Hello");
        notificationService.process(update);
        verify(telegramBot, times(1)).execute(any());
    }
}