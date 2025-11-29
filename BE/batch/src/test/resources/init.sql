CREATE TABLE IF NOT EXISTS alarms
(
    id               BIGINT PRIMARY KEY,
    user_id          BIGINT  NOT NULL,
    is_active        BOOLEAN NOT NULL,
    day_of_the_weeks INT     NOT NULL,
    wake_up_time     TIME    NOT NULL,

    INDEX idx_user_id (user_id),
    INDEX idx_is_active (is_active),
    INDEX idx_day_of_weeks (day_of_the_weeks),
    INDEX idx_wake_up_time (wake_up_time),
    INDEX idx_user_active_time (user_id, is_active, wake_up_time),
    INDEX idx_modular_query (user_id, is_active, day_of_the_weeks, wake_up_time)
);

CREATE TABLE IF NOT EXISTS tags
(
    id   BIGINT PRIMARY KEY,
    word VARCHAR(100) NOT NULL,

    INDEX idx_word (word)
);

CREATE TABLE IF NOT EXISTS alarm_tags
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,

    FOREIGN KEY (alarm_id) REFERENCES alarms (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,

    UNIQUE KEY uk_alarm_tag (alarm_id, tag_id),
    INDEX idx_alarm_id (alarm_id),
    INDEX idx_tag_id (tag_id)
);

INSERT INTO tags (id, word)
VALUES (1, '사회'),
       (2, '정치'),
       (3, '경제'),
       (4, '스포츠'),
       (5, 'IT');

INSERT INTO alarms (id, user_id, is_active, day_of_the_weeks, wake_up_time)
VALUES (1, 1001, TRUE, 127, ADDTIME(CURTIME(), '00:40:00')),
       (2, 1002, TRUE, 127, ADDTIME(CURTIME(), '00:40:00')),
       (3, 1003, TRUE, 127, ADDTIME(CURTIME(), '00:40:00')),
       (4, 1004, TRUE, 127, ADDTIME(CURTIME(), '00:40:00')),
       (5, 1005, TRUE, 127, ADDTIME(CURTIME(), '00:40:00'));

INSERT INTO alarm_tags (alarm_id, tag_id)
VALUES (1, 1),
       (2, 3),
       (3, 4),
       (4, 1),
       (5, 2);