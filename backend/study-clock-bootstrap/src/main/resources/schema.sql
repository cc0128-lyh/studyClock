-- Increase icon column length for favicon URLs
ALTER TABLE IF EXISTS shortcuts ALTER COLUMN icon VARCHAR(500);
