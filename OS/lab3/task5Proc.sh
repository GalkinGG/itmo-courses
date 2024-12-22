#!/bin/bash

res=1
mode="+"

stop() {
    kill $(cat .pid)
    killall tail
    rm pipe
    echo $res
    exit
}

(tail -f pipe) |
while true
do
    read LINE
    case $LINE in
        QUIT)
            echo "The script has finished working."
            stop
            ;;
        "*")
            mode="*"
            ;;
        "+")
            mode="+"
            ;;
        *)
            if [[ $(grep -o -E "^[-]{0,1}[1-9][0-9]{0,}$" <<< $LINE | wc -l) -gt 0 ]]
            then
                if [[ $mode == "+" ]]
                then
                    let res=$res+$LINE
                else
                    let res=$res*$LINE
                fi
            else
                echo "$LINE is incorrect input. Execution failed."
                stop
            fi
            ;;
    esac
done





