-- Add gemini_api_key to users table
ALTER TABLE users ADD COLUMN gemini_api_key VARCHAR(255) DEFAULT NULL;
