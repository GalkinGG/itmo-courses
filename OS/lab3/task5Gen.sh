#!/bin/bash

echo $$ > .pid 

mkfifo pipe

while true
do
    read LINE
    echo "$LINE" > pipe
done
