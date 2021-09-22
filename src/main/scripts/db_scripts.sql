select *
from public.execution_result
where execution_result.sub_function_id in (
    select sub_function_id from execution_result where test_execution_id = 385 and execution_result_result = true
)
  and test_execution_id = 385
  and sub_function_id in (select sar_sub_functions.sub_function_id from sar_sub_functions where runtime like 'python%');

select *
from public.execution_result
where (test_execution_id = 235 or test_execution_id = 268)
  and sub_function_id in (select sar_sub_functions.sub_function_id from sar_sub_functions where runtime like 'node%')
  and execution_ssl_exception = true;

select distinct execution_result.sub_function_id
from execution_result
where test_execution_id = 86;


select sub_function_id, runtime
from sar_sub_functions
where sub_function_id in (select sub_function_id
                          from execution_result
                          where test_execution_id = 86
                            and execution_result_stdout like '%aws_xray_sdk%');


select *
from execution_result
where sub_function_id in (
    select sub_function_id from sar_sub_functions where runtime like 'python2%'
)
  and execution_ssl_exception = true;


select distinct sub_function_id
from execution_result
where test_execution_id = 385
  and sub_function_id not in (select distinct sub_function_id
                              from execution_result
                              where test_execution_id = 385
                                and execution_result_result = true)



select *
from public.execution_result
where execution_result.sub_function_id in (
    select distinct sub_function_id
    from execution_result
    where test_execution_id = 385
      and execution_result_result = true
)

  and test_execution_id = 385
  and sub_function_id in (select sar_sub_functions.sub_function_id from sar_sub_functions where runtime like 'python%');



select execution_result_script
from execution_result
where execution_timeout = true
  and test_execution_id = 420;



select distinct sub_function_id
from public.execution_result
where execution_result.test_execution_id = 552
  and execution_result.sub_function_id in (select sub_function_id
                                           from public.sar_sub_functions
                                           where sar_sub_functions.sub_function_id not in
                                                 (select distinct sub_function_id
                                                  from execution_result
                                                  where test_execution_id = 552
                                                    and (execution_result_result = true))
                                             and execution_cannot_find_module = true)
order by sub_function_id asc;



select *
from public.execution_result
where execution_result.test_execution_id = 596
  and execution_result.sub_function_id not in
      (select distinct sub_function_id
       from public.execution_result
       where test_execution_id = 596
         and execution_cannot_find_module = false)

select distinct sub_function_id
from execution_result
where test_execution_id = 601


select sum(deployment_count)
from sar_function_main;
select label, count(*)
from sar_function_main_labels
group by label;

create table sar_function_main_backup as
select *
from sar_function_main;

create table execution_result_08_09 as
select *
from execution_result;

select publisher_alias, count(*)
from sar_function_main
group by publisher_alias;

select deployment_count, name, publisher_alias
from sar_function_main
where sar_function_main.publisher_alias not like 'AWS%'
  and sar_function_main.publisher_alias not like 'Alexa%'
  and sar_function_main.publisher_alias not like 'Amazon%'
order by deployment_count desc;


select publisher_alias, sum(deployment_count) as s
from sar_function_main
where publisher_alias not like 'Alexa%'
  and publisher_alias not like 'AWS%'
  and not publisher_alias like 'Amazon%'

group by publisher_alias
order by s desc;


select sum(deployment_count) as s
from sar_function_main
where publisher_alias like 'Alexa%'
   or publisher_alias like 'AWS%'
   or publisher_alias like 'Amazon%';


select count(*) as s
from sar_function_main
where publisher_alias like 'Alexa%'
   or publisher_alias like 'AWS%'
   or publisher_alias like 'Amazon%';
select publisher_alias, count(*) as s
from sar_function_main
where publisher_alias not like 'Alexa%'
  and publisher_alias not like 'AWS%'
  and not publisher_alias like 'Amazon%'

group by publisher_alias
order by s desc;



select runtime, count(*)
from sar_sub_functions
where nofunctionfound = false
group by runtime;



select distinct mian_function_id
from sar_sub_functions
where example = true;

select distinct publisher_alias
from sar_function_main
where is_verified_author = true;


select sum(deployment_count)
from sar_function_main
where deployment_count > 10000;
select count(deployment_count)
from sar_function_main
where deployment_count > 10000;


select sum(c)
from (select name, count(*) as c
      from sar_function_main
      group by name
      having count(*) > 1) as nc;


select *
from sar_function_main
where home_page_url like '%github%'
  and error_clone_pull = false
order by home_page_url desc;


select mian_function_id, count(*)
from (select mian_function_id, runtime, count(*) as c
      from sar_sub_functions
      where runtime is not null
      group by mian_function_id, runtime) as sub
group by mian_function_id;


select runtime, count(*)
from sar_sub_functions
group by runtime

select sum(value)
from sonar_result
where metric = 'code_smells';


select id, name
from sar_function_main
where id in (
    select main_function_id
    from sonar_result
    where metric = 'security_hotspots'
      and value > 0
)


select distinct mian_function_id
from public.sar_sub_functions
where sar_sub_functions.sub_function_id in
      (select distinct sub_function_id from execution_result where test_execution_id = 604);


select distinct sub_function_id
from public.execution_result
where execution_result.sub_function_id in
      (select sar_sub_functions.sub_function_id
       from sar_sub_functions
       where mian_function_id in
             (select id
              from sar_function_main
              where home_page_url like '%ttps://github.com/aws-samples/serverless-app-examples%'))
  and test_execution_id = 552



select distinct sar_sub_functions.sub_function_id
from sar_sub_functions
where mian_function_id in
      (select id
       from sar_function_main
       where home_page_url like '%ttps://github.com/aws-samples/serverless-app-examples%')



select *
from public.sar_function_main
where id in (select distinct mian_function_id
             from sar_sub_functions
             where sub_function_id not in (select sub_function_id from execution_result where test_execution_id = 552))
  and home_page_url like 'https://github.com/aws-samples/serverless-app-examples%'

update execution_result
set test_execution_id = 552
where test_execution_id = 604;


select id, count(1)
from sar_function_main as m
         left join public.sar_sub_functions as s on m.id = s.mian_function_id
where home_page_url like '%github%'
  and not error_clone_pull
group by id;

select id, count(1)
from sar_function_main as m
         left join public.sar_sub_functions as s on m.id = s.mian_function_id
         left join execution_result as e on s.sub_function_id = e.sub_function_id
where home_page_url like '%github%'
  and not error_clone_pull
  and test_execution_id = 552
group by id


select *
from sar_function_main as m
         inner join sar_sub_functions as s on m.id = s.mian_function_id
where home_page_url like '%github%'
  and not error_clone_pull
  and not missing_sar_template
  and nofunctionfound = false
  and id not in (
    select m.id
    from sar_function_main as m
             join public.sar_sub_functions as s on m.id = s.mian_function_id
             join execution_result as e on s.sub_function_id = e.sub_function_id
--     where home_page_url like '%github%'
--       and not error_clone_pull
    where test_execution_id = 552
    group by m.id
)



select distinct id
from public.sar_function_main
where id in (select mian_function_id
             from public.sar_sub_functions
             where sar_sub_functions.sub_function_id in (select distinct sub_function_id
                                                         from execution_result
                                                         where execution_result_result = true
                                                           and test_execution_id = 552))



select is_verified_author
from public.sar_function_main
where id = (select mian_function_id from sar_sub_functions where sub_function_id = 9261)


select deployment_count
from public.sar_function_main
where id in (select mian_function_id
             from public.sar_sub_functions
             where sar_sub_functions.sub_function_id in
                   (select sub_function_id from execution_result where test_execution_id = 552))


select deployment_count, execution_billed_duration from sar_function_main inner join sar_sub_functions on id = mian_function_id
inner join execution_result er on sar_sub_functions.sub_function_id = er.sub_function_id
where test_execution_id = 552 and execution_billed_duration is not null;


select ssf.runtime, avg(execution_duration) from execution_result
inner join sar_sub_functions ssf on execution_result.sub_function_id = ssf.sub_function_id
where test_execution_id = 552
group by ssf.runtime;

select avg(execution_duration) from execution_result where test_execution_id = 552 and execution_result_result = true;

select avg(bd) from (select execution_billed_duration as bd from sar_function_main
inner join sar_sub_functions ssf on sar_function_main.id = ssf.mian_function_id
inner join execution_result er on ssf.sub_function_id = er.sub_function_id
where test_execution_id = 552 and execution_billed_duration is not null and execution_duration is not null
and execution_result_result = true
order by deployment_count desc limit 135) as foo;


select * from sar_function_main  order by deployment_count desc limit 135