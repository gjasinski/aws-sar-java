#!/bin/bash
SUM_UP=0    
SUM_DOWN=0
filename=/home/ubuntu/stats/network_results.log
#grep -v 192.168.18.159 $filename | grep -v Adding | grep -v unknown | grep -v Refreshing | grep -v Ethernet | awk '{print $1}' | sort | uniq | sed '/^$/d' | while read connection
grep "^172.17." $filename | awk '{print $1}' | sort | uniq | sed '/^$/d' | while read connection
do
        RES_SUM_UP=$(grep "$connection" $filename | awk '{ sum += $3; n++ } END { if (n > 0) print sum / n; }')
        RES_SUM_DOWN=$(grep $connection $filename | awk '{ sum += $2; n++ } END { if (n > 0) print sum / n; }')
        echo $connection $RES_SUM_UP > /home/ubuntu/stats/net_results_up.log
        echo $connection $RES_SUM_DOWN > /home/ubuntu/stats/net_results_down.log
done

#    echo -n "$filename " >> cpu_results/result.log
#    cat $filename | awk '{ sum += $9; n++ } END { if (n > 0) print sum / n; }' >> cpu_results/result.log
