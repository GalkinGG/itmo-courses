#!/bin/bash

maxPid=0
currSize=0
maxSize=0
for pid in $(ps ax -o pid)
do
    if [[ -f /proc/$pid/status ]]
    then
        currSize=$(grep -i vmsize /proc/$pid/status | awk -F ':' '{print $2}' | awk '{print $1}')
        if [[ $currSize -ge $maxSize ]]
        then
            maxSize=$currSize
            maxPid=$pid
        fi
    fi
done
echo $maxPid : $maxSize
echo $(top -o VIRT -b -n 1 | head -n 8 | tail -n 1 | awk '{print $1 ":" $5}')
