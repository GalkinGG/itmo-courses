#!bin/bash

byte_count() {
    for pid in $(ps ax -o pid)
    do
        if [[ $pid -ne 1 && -f /proc/$pid/io ]]
        then
            count=$(awk '/read_bytes:/ {print $2}' /proc/$pid/io)
            echo $pid $count
        fi
    done
}

byte_count > start
sleep 60
byte_count > end

while read -r pid size
do
    v1=$size
    v2=$(cat end | grep "^$pid" | awk '{print $2}')
    let diff=$v2-$v1
    if [[ $diff -ge 0 ]]
    then
        echo $pid : $(ps -o command -p $pid | tail -n 1) : $diff
    fi
done < start | sort -t ':' -rnk 3 | head -n 3
rm start end
