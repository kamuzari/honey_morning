/*
  [SQL 모듈화 통합 파일]
  1. init_environment       : 테이블 초기화 및 기초 데이터(Tag) 설정
  2. create_user_with_alarm : 유저 1명 + 알람 + 태그 생성 (단일 작업)
  3. seed_users_alarms_main : 전체 흐름 제어 (시간 계산 및 반복 호출)
*/

-- =============================================================================
-- [PART 1] 환경 초기화 프로시저
-- 역할: 기존 데이터를 모두 지우고, Tag 같은 기초 데이터를 세팅
-- =============================================================================
DELIMITER $$

DROP PROCEDURE IF EXISTS init_environment $$

CREATE PROCEDURE init_environment()
BEGIN

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE users;
TRUNCATE TABLE alarm_tags;
TRUNCATE TABLE alarms;

INSERT INTO tags (id, word)
VALUES (1, '사회'), (2, '정치'), (3, '경제'), (4, '스포츠'), (5, 'IT')
    ON DUPLICATE KEY UPDATE word = VALUES(word);

SET FOREIGN_KEY_CHECKS = 1;
END $$

DELIMITER ;


-- =============================================================================
-- [PART 2] 단일 유저 생성 프로시저
-- 역할: 외부에서 계산된 '시간'을 받아, 유저 1명과 알람, 태그를 생성합니다.
-- =============================================================================
DELIMITER $$

DROP PROCEDURE IF EXISTS create_user_with_alarm $$

CREATE PROCEDURE create_user_with_alarm(
    IN p_wake_up_time TIME
)
BEGIN
    DECLARE v_user_id BIGINT;
    DECLARE v_alarm_id BIGINT;
    DECLARE v_tag_cnt INT;

INSERT INTO users (username, password, nick_name, maximumStreak)
VALUES (
           CONCAT('user_', UUID_SHORT()),
           '{noop}password',
           CONCAT('nick_', UUID_SHORT()),
           0
       );
SET v_user_id = LAST_INSERT_ID();

INSERT INTO alarms (user_id, is_active, day_of_the_weeks, wake_up_time, repeat_frequency, repeat_interval)
VALUES (
           v_user_id,
           TRUE,
           127,
           p_wake_up_time,
           1,
           1
       );
SET v_alarm_id = LAST_INSERT_ID();
    SET v_tag_cnt = 1 + FLOOR(RAND() * 3);

INSERT INTO alarm_tags (alarm_id, tag_id)
SELECT v_alarm_id, id
FROM tags
ORDER BY RAND()
    LIMIT v_tag_cnt;

END $$

DELIMITER ;


-- =============================================================================
-- [PART 3] 메인 컨트롤러 프로시저
-- 역할: 루프를 돌며 시간을 계산하고, PART 2 프로시저를 호출하여 데이터를 생성합니다.
-- =============================================================================
DELIMITER $$

DROP PROCEDURE IF EXISTS seed_users_alarms_main $$

CREATE PROCEDURE seed_users_alarms_main(
    IN p_total_per_loop INT,    -- 시간대별 생성할 인원 수
    IN p_loop_count INT         -- 시간대 변경 횟수 (반복 횟수)
)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE j INT DEFAULT 0;
    
    DECLARE v_base_minutes INT DEFAULT 43; -- 기준 시작 분 (현재시간 + 43분)
    DECLARE v_current_wake_time TIME;      -- 계산된 알람 시간


WHILE j < p_loop_count DO

        SET v_current_wake_time = ADDTIME(CURTIME(), SEC_TO_TIME((v_base_minutes + j) * 60));
        
        SET i = 0;
        
        WHILE i < p_total_per_loop DO
            CALL create_user_with_alarm(v_current_wake_time);
            SET i = i + 1;
END WHILE;
        SET j = j + 1;
END WHILE;

END $$

DELIMITER ;

CALL init_environment();
-- =============================================================================
-- [PART 4] 이벤트 등록 (1분마다 실행)
-- =============================================================================

DELIMITER $$

DROP EVENT IF EXISTS scheduled_seed_event $$

CREATE EVENT scheduled_seed_event
ON SCHEDULE
    EVERY 1 MINUTE
    STARTS CURRENT_TIMESTAMP
DO
BEGIN
CALL seed_users_alarms_main(1000, 5);
END $$

DELIMITER ;

CALL seed_users_alarms_main(1000, 5);