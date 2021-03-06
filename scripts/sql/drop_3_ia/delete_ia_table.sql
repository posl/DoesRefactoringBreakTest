begin;
delete from ia.directrefactoring using (select md_id as id from run.ia, ia.methoddefinition where run.ia.commit_id=ia.methoddefinition.commit_id and run.ia.resultcode=1) as a where cast(ia.directrefactoring.md_id as Integer)=id; 
delete from ia.annotations using (select md_id as id from run.ia, ia.methoddefinition where run.ia.commit_id=ia.methoddefinition.commit_id and run.ia.resultcode=1) as a where cast(ia.annotations.md_id as Integer)=id;
delete from ia.arguments using (select md_id as id from run.ia, ia.methoddefinition where run.ia.commit_id=ia.methoddefinition.commit_id and run.ia.resultcode=1) as a where cast(ia.arguments.md_id as Integer)=id;
delete from ia.changeline using (select md_id as id from run.ia, ia.methoddefinition where run.ia.commit_id=ia.methoddefinition.commit_id and run.ia.resultcode=1) as a where cast(ia.changeline.md_id as Integer)=id;
delete from ia.generics using (select md_id as id from run.ia, ia.methoddefinition where run.ia.commit_id=ia.methoddefinition.commit_id and run.ia.resultcode=1) as a where cast(ia.generics.md_id as Integer)=id;
delete from ia.indirectrefactoring using (select md_id as id from run.ia, ia.methoddefinition where run.ia.commit_id=ia.methoddefinition.commit_id and run.ia.resultcode=1) as a where cast(ia.indirectrefactoring.md_id as Integer)=id;
delete from ia.inherentrefactoring using (select md_id as id from run.ia, ia.methoddefinition where run.ia.commit_id=ia.methoddefinition.commit_id and run.ia.resultcode=1) as a where cast(ia.inherentrefactoring.md_id as Integer)=id; 
delete from ia.methoddefinition using (select commit_id as cid, project as p from run.ia where run.ia.resultcode=1) as a where commit_id=cid and project=p;
delete from ia.ia using (select commit_id as cid, project as p from run.ia where run.ia.resultcode=1) as a where commit_id=cid and project=p;
update run.ia set resultcode = 0, resultmessage = null where run.ia.resultcode=1;
commit;