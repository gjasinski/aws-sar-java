#!/bin/bash
grep -e exe -e node /home/ubuntu/stats/proc_results.log | awk '{ sum += $9; n++ } END { if (n > 0) print sum / n; }' >> /home/ubuntu/stats/proc_aggregated.log
