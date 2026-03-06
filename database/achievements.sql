-- 5. Achievements Table
CREATE TABLE IF NOT EXISTS achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    criteria_type VARCHAR(50), -- e.g., 'TOTAL_FOCUS_TIME', 'STREAK', 'TASK_COMPLETED'
    criteria_value INT NOT NULL,
    icon_name VARCHAR(50), -- e.g., 'medal', 'star'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. User Achievements (Many-to-Many)
CREATE TABLE IF NOT EXISTS user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_achievement (user_id, achievement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Achievements
INSERT IGNORE INTO achievements (name, description, criteria_type, criteria_value, icon_name) VALUES 
('Novice Focus', 'Complete 25 minutes of focus', 'TOTAL_FOCUS_TIME', 25, 'medal'),
('Focus Master', 'Complete 1000 minutes of focus', 'TOTAL_FOCUS_TIME', 1000, 'trophy'),
('Consistency is Key', 'Reach a 3-day streak', 'STREAK', 3, 'calendar'),
('Task Crusher', 'Complete 10 tasks', 'TASK_COMPLETED', 10, 'finished');
