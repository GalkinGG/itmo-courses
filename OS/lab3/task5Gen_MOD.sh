#!/bin/bash

switch_pipe() {
    echo "Enter pipe name:" > `tty`
    while true
    do
        read PIPE
        if [ ! -p $PIPE ]
        then
            echo "Pipe doesn't exist. Try again:" > `tty`
        else
            echo "Switched pipe to $PIPE" > `tty`
            break
        fi
    done
    echo $PIPE
}

echo $$ > .pid 

current_pipe=$(switch_pipe) 

while true
do
    read LINE
    case $LINE in
        SWITCH)
            current_pipe=$(switch_pipe)
            ;;
    
        *)
            echo "$LINE" > $current_pipe
            ;;
    esac
done