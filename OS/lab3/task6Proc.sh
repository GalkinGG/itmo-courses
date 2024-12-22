#!/bin/bash

res=1

echo $$ > .pidProc

plusMode() {
    let res=$res+2
    echo $res
}

mulMode() {
    let res=$res*2
    echo $res
}

stop() {
    echo "Process stopped by generator"
    exit
}

trap 'plusMode' USR1
trap 'mulMode' USR2
trap 'stop' SIGTERM 

while true
do
    sleep 1
done
