#!/bin/bash
kill -9 $(ps aux | grep nethogs | grep -v grep | awk '{print $2}')
kill -9 $(ps aux | grep top | grep -v grep | awk '{print $2}')
