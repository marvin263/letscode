-- 1. Create schema --
CREATE SCHEMA EI AUTHORIZATION ADMINISTRATOR;
COMMENT ON Schema EI IS 'Marvin''s own schema';
-- 注意：由于 单引号 要转义，所以's 必须用 两个单引号 来表示