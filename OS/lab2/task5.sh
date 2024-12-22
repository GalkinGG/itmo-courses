#!/bin/bash

divide() {
    echo $1 $2 | awk '{if ($2 > 0) print $1/$2; else print 0}'
}

./task4
sum=0
count=0
previousPPid=0
while IFS=: read -r pid ppid art
do
    if [[ ${ppid#*=} -ne $previousPPid ]]
    then
        echo Average_Running_Children_of_ParentID=$previousPPid is $(divide $sum $count)
        previousPPid=${ppid#*=}
        count=0
        sum=0
    fi
    sum=$(echo $sum ${art#*=} | awk '{print $1 + $2}')
    count=$((count+1))
    echo $pid : $ppid : $art
done < file4 > file5

if [[ $count -ne 0 ]]
then
    echo echo Average_Running_Children_of_ParentID=$previousPPid is $(divide $sum $count) >> file5
fi
