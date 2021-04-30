begin;
delete from trace.path using (select commit_id as cid, project as p from run.straight where resultcode=1) as a where commit_id = cid and project = p and is_cross=false;
delete from trace.trace using (select commit_id as cid, project as p from run.straight where resultcode=1) as a where commit_id = cid and project = p and is_cross=false;
delete from test.result_straight using (select commit_id as cid, project as p from run.straight where resultcode=1) as a where commit_id = cid and project = p;
delete from test.straight using (select commit_id as cid, project as p from run.straight where resultcode=1) as a where commit_id = cid and project = p;
update run.straight set resultcode = 0 where resultcode=1;
commit;
