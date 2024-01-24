package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "notification_task")
@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    private String notification;
    @Column(name = "alarm_date")
    private LocalDateTime alarmDate;

    public NotificationTask (Long chatId, String notification, LocalDateTime alarmDate) {
        this.chatId = chatId;
        this.notification = notification;
        this.alarmDate = alarmDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(notification, that.notification) && Objects.equals(alarmDate, that.alarmDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, notification, alarmDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public LocalDateTime getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(LocalDateTime alarmDate) {
        this.alarmDate = alarmDate;
    }

    public NotificationTask() {
    }
}
