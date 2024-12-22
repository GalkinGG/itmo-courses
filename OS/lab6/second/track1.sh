#!/bin/bash

log=1.log
sz=$(<.size)

if [[ -f $log ]] 
then
    rm $log
fi

for ((n = 1; n <= 20; n++)) 
do
    echo "$n:" >> $log
    for ((k = 0; k < 10; k++)); do
        \time -f "%e" ./1b.sh $sz $n 2>>$log
    done
done