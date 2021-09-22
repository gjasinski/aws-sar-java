select distinct publisher_alias from sar_function_main
where is_verified_author = true;

-- deployment count desc
select deployment_count, name, publisher_alias
from sar_function_main
order by deployment_count desc;

-- deployment count not AWS
select deployment_count, name, publisher_alias
from sar_function_main
where sar_function_main.publisher_alias not like 'AWS%' and
        sar_function_main.publisher_alias not like 'Alexa%' and
        sar_function_main.publisher_alias not like 'Amazon%'
      order by deployment_count desc;


-- deployment count by author
select publisher_alias, sum(deployment_count) as s from sar_function_main
where
        publisher_alias not like 'Alexa%' and publisher_alias  not like 'AWS%' and  not publisher_alias like 'Amazon%'

group by publisher_alias order by s desc;

-- deployment count aws
select  sum(deployment_count) as s from sar_function_main where publisher_alias like 'Alexa%' or publisher_alias like 'AWS%' or publisher_alias like 'Amazon%';



-- number of AWS's functions
select count(*) as s from sar_function_main where publisher_alias like 'Alexa%' or publisher_alias like 'AWS%' or publisher_alias like 'Amazon%';

-- number of not AWS functions
select publisher_alias, count(*) as s from sar_function_main
where
        publisher_alias not like 'Alexa%' and publisher_alias  not like 'AWS%' and  not publisher_alias like 'Amazon%'

group by publisher_alias order by s desc;
