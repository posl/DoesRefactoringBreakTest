begin;
delete from trace.path using (select commit_id as cid, project as p from run.cross where resultcode=1) as a where commit_id = cid and project = p and is_cross=true;
delete from trace.trace using (select commit_id as cid, project as p from run.cross where resultcode=1) as a where commit_id = cid and project = p and is_cross=true;
delete from test.result_cross using (select commit_id as cid, project as p from run.cross where resultcode=1) as a where commit_id = cid and project = p;
delete from test.cross using (select commit_id as cid, project as p from run.cross where resultcode=1) as a where commit_id = cid and project = p;
update run.cross set resultcode = 0 where resultcode=1;
commit;
