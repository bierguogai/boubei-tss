
-- 清除多余的用户
delete from um_groupuser where userId in (select id from um_user u where u.id >= 269);
delete from um_roleuser where userId in (select id from um_user u where u.id >= 269);
delete from um_user  where id >= 269;
