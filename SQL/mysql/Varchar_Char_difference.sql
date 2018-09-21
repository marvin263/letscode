
-- varchar 保留末尾空格；char删除末尾空格
DROP TABLE IF EXISTS `varchar_char`;
CREATE TABLE `varchar_char` (
`vchar` varchar(10) NOT NULL,
`baldChar` char(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into varchar_char() values ('baby1','baby1'),('    baby2','    baby2'),('baby3    ','baby3    ');

select concat("'", vchar, "'"),concat("'", baldChar, "'") from varchar_char;


-- PRIMARY 中，不能有NULL
-- UNIQUE  中，可以有多个NULL。在数据库中，NULL和它自己 并不相等
drop table if exists pktable;
CREATE TABLE `pktable` (
  `string_date` varchar(64),
  `dt` DATETIME NULL,
  PRIMARY KEY (`string_date`),
  UNIQUE (`dt`)
  );
insert into pktable(string_date, dt) values('a', NULL);
insert into pktable(string_date, dt) values('b', NULL);
-- 下面这句会出错。上面两句都OK，即 UNIQUE 的 dt中可以有多个NULL
insert into pktable(string_date, dt) values(NULL, NULL);
select * from pktable;


