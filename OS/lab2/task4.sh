#!/bin/bash

get_field() {
    cat /proc/$1/$2 | grep $3 | awk '{print $NF}'
}

divide() {
    echo $1 $2 | awk '{if ($2 > 0) print $1/$2; else print 0}'
}

calc_art() {
    sum=$(get_field $1 sched sum_exec_runtime)
    nr=$(get_field $1 sched nr_switches)
    echo $(divide $sum $nr)
}

for pid in $(ps ax -o pid)
do
    if [[ -f /proc/$pid/status ]]
    then
        echo ProcessId=$pid : Parent_ProcessID=$(get_field $pid status PPid) \
        : Average_Running_Time=$(calc_art $pid)
    fi
done | sort -t ':' -Vk2 > file4
